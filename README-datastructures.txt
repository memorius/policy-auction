TODO: changes needed:
---------------------
- Removing votes from a policy has a cost: you only get 50% of the votes back.
  How do we represent this?

- 3-day rolling "new votes" list.
  Set TTL to make all votes for a given day expire at the same time - i.e. 3 days minus time since start of day.
  When do we write items? Similar consistency issues to the main policies table?

- Comments on policies.

- Policy categories: fixed set of items: Education, Tax, Health etc.

- Policies are in exactly one category, set at creation. But editable I guess?

- Policy tags. User (and possibly other people?) can assign any number of tags to a policy.
  - Users can create tags with any text, they're added to a central list.
    (tag name -> tag ID, as for usernames; allows renaming; same tricks as with username to prevent duplicate inserts: write with decreasing timestamp, read back again before using.)
  - Users with "moderate_tags" role can:
    - delete tags
    - merge one tag into another
    - rename tags
  - Need efficient way to list all policies with a given tag, and also all policies which have a given set of tags.
    (for "related policies" feature)

- "hot categories" data (trending categories) - categories plus some kind of activity metric.

- "hot tags" data (trending tags) - tags plus some kind of activity metric.

- Tag cloud: tags plus number-of-policies-tagged.

- Users can have watchlist of policies.

- User profile needs to provide:
  You have N unspent pollies.
  You have created N policies.
  You have voted for N policies.

- Anyone can create new policies, but it costs you X votes. How do we represent that? Just assign votes to it immediately, or special case?

- Featured policies on front page.

- "Report this" feature. For policies; for comments; maybe also tags?

- Policy pages need to show current ranking (within the 3-day window), neighbouring policies in the ranking, and difference from the one above and below - .g. "N votes needed to beat <nearest policy>".
  How to calculate this?



Background on Cassandra data model:
-----------------------------------
Cassandra's structure is "column families" - tables with rows looked up by a unique key, with each row containing any number of "columns", which are name->value pairs storing a single value - i.e. each row is a Map<ColumnName, Value>. Very large numbers of columns per row (e.g. millions) are OK.

Columns in a row are stored in sorted order. Writes insert or delete one or more columns from a row. Reads retrieve single columns from a row, or ordered ranges of columns.

Writes to a single row are very fast. Reads from a single row are a little slower but usually only milliseconds.

Reads across all rows are slow (table scan across whole cluster!) and return results in random order - best avoided except for background processes or unless the number of rows is small.

There is also a "super column family" structure: values in the row are "super columns" instead of "columns", and each one holds multiple name->value pairs (subcolumns) instead of a single value: i.e. each row is a Map<SuperColumnName, <Map<SubColumnName, Value>>>. These should be kept smaller - large numbers of supercolumns are OK, but small numbers of subcolumns for each.


Background on Cassandra consistency behavior:
---------------------------------------------
Cassandra is a timestamp-based eventual-consistency system with no check-and-write operations, so some care is needed for accurate vote counting, and to prevent loopholes such as a user assigning their votes in parallel from different browser sessions, and problems such as delayed writes (e.g. due to a node getting a write backlog during compaction) making counts inaccurate.

There is no locking, no transactions: reads across multiple rows aren't isolated from concurrent writes, and writes to multiple rows are independent and could fail part-way through. Also, as I understand it, multi-column writes are atomic within each row, but not transactionally isolated from concurrent reads of that row: i.e. when writing columns 1 and 2, a concurrent read may see the new column 1 but the old column 2. This needs care in some cases - can't have dependent data in separate columns if it's written more than once and must be atomically updated.

Cassandra's writes use a timestamp mechanism; the "timestamp" for each write to each column is an arbitrary client-supplied 64-bit integer, and when there is a write to an existing column, the write is applied only if the timestamp is greater than that of the column's old value. For example, if you set timestamps to be client current time, then newest write eventually wins; if you set (0 - client current time), oldest write eventually wins; or you can use some other app-defined integer value. This resolution process happens separately for each column in an update. It applies for deletes too. There's no distinction between "create" and "update" writes: "delete" followed by "write" will re-create the deleted column if the write timestamp is newer.

Consistency levels: it's tunable; when we have only one node it's not an issue, but once we have three or more, we'll configure for strong consistency, because it removes many of the issues with stale data. We'll use:
  - Replication Factor 3 (or more)
  - Write Consistency: quorum (2, if 3 nodes)
  - Read Consistency quorum (2, if 3 nodes)
This means a write followed by a read will always see the data it's just written; this design assumes this constraint. We'll still have some failure tolerance - we can operate with any one node (for a given key's replica set) down or unresponsive, but reads will fail if two are down. (Writes will also fail with two nodes down in some conditions, depending on number of nodes in cluster - hinted handoff may allow it to keep working if there are four nodes total hence still two active.)

Indexing: Cassandra's secondary indexes are not intended for high-uniqueness indexed values (e.g. username -> userID), but for aggregation queries where there are many rows with the same value; queries against a secondary index have to contact ALL nodes in the cluster, as I understand it. So where we have fields with high uniqueness that we want to index, we make our own lookup column families - this then only has to contact READ_CONSISTENCY nodes.


Cassandra column families:
--------------------------
Pseudocode. '...' means a variable series of columns with names varying by date or id.

Re. use of super column families: for user_policy_votes and policy_new_votes, we can either use supercolumns or a concatenated value in a single column; it's important these updates be atomic, so the latter may be necessary for that: need to check whether supercolumn timestamps and write visibility apply to the individual subcolumns.

users: (key = <userID> : timeUUID) {
    username: text
    email: text
    password_hash: binary
    first_name: text
    last_name: text
    show_real_name: boolean
    vote_salary_last_paid_timestamp: time
    vote_salary_<date> ...: int (never changed once written)
    role_XXX: [nothing]
    maybe other user data as needed - login history, password reset mechanism, etc.
}

policies: (key = <policyID> : timeUUID) {
    active: boolean
    short_name: text
    description: text
    link ...:
    party: <partyID>
    owner: <userID>
    last_edit_date: date
    total_votes: int
    finalized_votes: int:timeUUID
}

user_policy_votes: (key = <userID> : timeUUID) { - super column family
    votes_<prev_version> ... {
        version: timeUUID
        votes_<policyID> ... : increment:newtotal (ints)
        cassandra timestamp copied from pending_votes.
    },
    pending_votes_<version> {
        prev_version: timeUUID
        votes_<policyID> ... : increment:newtotal (ints)
        cassandra timestamp = (- now) so oldest wins
    }
}

policy_new_votes: (key = <policyID> : timeUUID) { - super column family
    <prev_version> ...: {
        vote_increment: int (can be -/0/+)
        userid: <userID>
        version: timeUUID
        cassandra timestamp: from user_policy_votes.pending_votes, so oldest wins
    }
}

policies_vote_history: (key = <policyID>_<date>) {
    timeUUID ...: {
        userid: <userID>
        vote_increment: int (can be -/+, never 0)
        new_vote_total: int
    }
}

parties: (key = <partyID> : timeUUID) {
    active: boolean
    short_name: text
    description: text
}

users_by_name: (key = <username> : text) {
    user_id: <userID>
    registered_timestamp: timeUUID
}

policy_ranking: (key = hour (or minute, or whatever)) {
    <votecount>_<policyID> ...: short_name
}

misc["active-policies"]: {
    <policyName> ...: <policyID>
}

misc["active-parties"]: {
    <partyName> ...: <partyID>
}

misc["webapp-instances"]: {
    <ipaddr> ...: (nothing), TTL say 5 mins
}

misc["voting-config"]: {
    vote_finalize_delay_seconds: int
    ranking_window_days: int (3)
    user_vote_salary_frequency_days: int (7)
    user_vote_salary_increment: int (100)
    vote_cost_to_create_policy: int (100)
}

log: (key = timeUUID) {
    server_ipaddr: <string> (secondary indexed)
    something...
}


Explanation of data design and usage:
-------------------------------------
users:
  - Uniqueness and indexing of username is handled separately, see users_by_name below.
  - User vote salary is in separate timestamp-named columns, added to every week. These are never modified once written - i.e. we don't subtract when allocating votes, we just calculate the difference of (total salary - current policy vote allocations). Can either have a background process to add these columns weekly, or just update whenever we look at that user's record.
  - password_hash: bcrypt hash of password.
  - Roles: columns present to indicate role_admin, role_edit_policies, role_edit_users etc. Secondary indexes on these for user config screens.
  - Other user data as needed - last login etc.

policies:
  Policy current config and calculated totals.
  - total_votes: cached count that resulted from the last policy_new_votes change. Any operation that changes policy_current_votes will then read back, calculate and write this column, with the write timestamp set to the time we started the read back. Also, we'll have a periodic background process (say, every few minutes) that recalculates it - last resort protection against failures in the recalc following any given update.
  - finalized_votes: This is the count of all the votes that have been "finalized" and copied to "policies_vote_history", i.e. they are old enough that we can be sure that all writes have arrived and conflicts are resolved. This provides the base value to which increments in policy_new_votes are added when recalculating policies.total_votes. The timeUUID is the "version" from the latest vote included in the count - in a single column because it's critical they're always updated atomically together.

user_policy_votes:
  Each row is the user vote history for a single user across all policies. Change in votes and new total votes per policy for this user is in policyid-named columns in each record. The structure is designed to cope - in the absence of a locking mechanism - with multiple vote submits by the same user, by detecting conflicting writes and garbage-collecting any child updates whose parent writes lost the conflict resolution.
  - New user accounts receive a single votes_000000 entry with zero vote allocations.
  - When reading data for user to edit vote allocations, it includes "prev_version" - this is used to chain the records together.
  - When user saves their new allocation, we write a pending_votes entry, with "version" set to a new timeUUID, and "prev_version" set to the basis data we previously read. Then we propagate this information to policy_new_votes for all the policies so it can be efficiently used to calculate total_votes and go into the aggregate history. That's a distributed write, so could fail at any step; a background process will clean this up, and pending_votes entries stay there until one or other process successfully completes those writes and the following total_votes recalc.
  - The Cassandra timestamp on these updates will be DECREASING with time; this means that when there are conflicting updates in user_policy_votes.votes_ and in policy_new_votes, the first update will eventually win.
  - Once written to policy_new_votes, the pending entry is written as a user_policy_votes.votes_<prev_version> supercolumn, with timestamp set the same as the pending item, then the pending supercolumn is deleted.
  - Note the 'version' and 'prev' items are deliberately inverted between pending and non-pending: the pending ones are intended to NOT collide until propagated to policy_new_votes, so that the conflict resolution can occur there when the vote counting happens; the non-pending ones collide here and the oldest one wins.
  - To read the user's current vote allocation, taking into account conflict resolution, read all the user_policy_votes.votes_ supercolumns, organize by version -> prev_version, and find the newest version that has an unbroken chain of extant prev_version links. If there's a conflict, the chain will be broken for the newer ones whose basis lost the conflict resolution, because they collide since they write the same votes_<prev_version> item. Then, ignore allocations to any policies that have since been deleted / made inactive.
  - For efficiency, the conflicted orphans could eventually be deleted, and we could keep a timestamp of prev_version up to which we have resolved everything.
  - Propagating to policy_new_votes: see below.

policy_new_votes:
  This records vote allocation changes as users add them. It provides "distributed vote counting" - allows efficient recalculation of current total votes for a policy by reading only one row.
  - Records are created from user_policy_votes.pending_votes, one for every policy, including those receiving no change in votes - zero-increment ones are still needed for correct conflict resolution.
  - All columns come from the corresponding user_policy_votes.pending_votes_ record.
  - Records remain here for a while (configurable in "misc["config"]) until we can be certain we have all the data for that time, even in the event of node failure and recovery. Until then we can still make vote counts, but it's possible they might change later.
  - To calculate the current vote allocation for a policy:
    - Read policies.finalized_votes -> count and boundary timeUUID. Start with this count.
    - Read ALL columns in policy_new_votes.
    - Iterate, discarding any items whose "version" column is older than boundary timeUUID: these are already counted
      and will shortly get archived. Put the rest into a map by "version" column.
    - Iterate the map:
      - if the "prev_version" value (column name) is older than the boundary timeUUID, item passes conflict resolution,
        because its parent has been finalized. Add the increment to the total vote count.
      - else look up the "prev_version" value in the map and follow the chain back repeatedly:
        - If we reach an entry whose "prev_version" value is not in the map but is older than the boundary timeUUID, the whole chain is consistent; add the increment for the child value we started with to the total.
        - If we reach an entry whose "prev_version" value is not in the map but is NOT older than the boundary timeUUID, then the whole chain has been superseded - one of the parents failed conflict resolution. Don't add the value for the child value we started with to the total. Additionally, issue a delete for it.
    All this is not as bad as it sounds; mostly there will be very short chains or single items, and we only have to do all this recalculation when we save vote changes.
    - Write result to policies.total_votes, with the write timestamp set to the time we started the read.
    - Write a column to policy_ranking.

policies_vote_history:
  History of "finalized" votes. Grouped into a row per date for convenient retrieval.
  Every so often, records from policy_new_votes whose "version" (not "prev_version") is old enough (misc["voting-config"].vote_finalize_delay_seconds) are:
  - copied to the appropriate row of policies_vote_history
    We discard any zero values, and we follow the same duplicate-history rules as described under policy_new_votes, except that we actually discard (by not copying them) the duplicate records.
  - added to policies.finalized_votes plus its timestamp is updated to that of the newest "version" (not "prev_version") that we copied
  - deleted from policy_new_votes
  Provided the write to "finalized_votes" is done before the deletes, this is safe if it happens to get run by multiple threads in parallel.

parties:
  Assuming we want to track policies by political party, this is the list of parties.

users_by_name:
  This is an index for records in "users" by username.
  - Avoiding duplicate concurrent registration of usernames:
    - To create a user account, we first write to users_by_name, without setting user_id, and with the timestamp on the write transaction set to a DECREASING value, -now. This way later writes will never overwrite earlier ones. Then we read back and check that the registered_timestamp is the same as we just wrote. If it is, we "won" the username and can set the user_id column. If it's not, someone else already has it.

policy_ranking:
  Allows rapid retrieval of current sorted ranking for the front page, and keeps historical record of rankings.
  It means we only need a one-row read instead of a read for each policy record.
  If the read comes back empty (first request in a time interval), then caller reads all active policies, writes values for them, then reads again.
  Write a column to it every time we update at a policy's total_votes.
  Reader just retrieves all columns, iterates into a LinkedHashMap {policyID -> [count, timestamp]}, discards all but the newest-timestamped for any policyID that appears twice and issues a column delete for each old value.

misc:
  Contains various single-row stuff: quick-lookups to avoid the need to iterate all keys on every request, and provide things in sorted order; runtime and config data:

  misc["active-policies"]:
  - list of all active policy IDs, updated whenever they're added/disabled.
    Key is whatever we want to sort by - the name, or party+name, or date, or something.
    Value can be the ID, or make it a supercolumn with more data as needed.
    Probably pretty much every request will read this.

  misc["active-parties"]:
  - list of all active party IDs, updated whenever they're added/disabled.
    As above, key is whatever we want to sort by, value is whatever we need in the list view.

  misc["webapp-instances"]:
  - each webapp's background runner reinserts its value every (say) 2 mins.
    The column TTL ensures the entry expires after a while if the server dies.
    Main purpose is to allow each background runner to easily determine how many webapp nodes are in the cluster, so it can set frequencies of background tasks accordingly, and maybe other monitoring info as needed.

  misc["voting-config"]:
  - current_votes_finalize_delay: seconds to keep items in policy_new_votes waiting for conflict resolution before background process "finalizes" them as above and archives to policies_vote_history. Probably hours or days. Don't accept submit of vote allocations if older than this, make them re-read the data.

log:
  We can use Cassandra for some of the logging from the webapps, system activity records, etc.


Other stuff:
------------
Activities that do things with policies (e.g. show/edit user votes for policies) must check that the policyID hasn't been deleted / made inactive. Checking in misc["active-policies"] should generally be enough. There's no locking so it's impossible to make setting the policy inactive behave as an instant cutoff; the best we could do is have a timestamp and retrospectively undo stuff. Probably not a big deal... and if we design for it, we can do things like cache the list of active policies for a few minutes in each webapp.

Similar considerations with deleted users.


Background operations:
----------------------
Each instance of the webapp will have a background scheduler thread (using Quartz library or similar) that runs periodic tasks. (We could use Hadoop on top of Cassandra, but as I understand it, that needs "special" job control nodes which are single points of failure.) So that we have no single point of failure and can work with any number of webapp instances, tasks are run on all nodes, maybe with somewhat randomized scheduling according to the current number of webapp instances, and tasks are designed to be idempotent, i.e. they can safely be run multiple times or in parallel, and duplicate writes / deletes won't matter.

Tasks:

- Occasionally (maybe a few times a day), archive from policy_new_votes to policies_vote_history as described above, according to current_votes_finalize_delay, and update finalized baseline counts in policies.

- Periodically look for user_policy_votes records with pending_votes columns, and re-do/complete the writes to policy_new_votes, and the recalc of policies.total_votes, then move the user_policy_votes.pending_votes column to a user_policy_votes.votes column.

- At least once every policy_ranking interval: update policy_ranking[now] from policies.total_votes across all policies. This makes reasonably sure there's a record for each interval as long as one or more servers is up, and corrects current rankings if any writes to this row get lost. (Whether we need a hard guarantee of having a record for each interval depends on intended data analysis; if we do, we'll need some other way to calculate it.)

- Every few minutes, recalc policies.total_votes for each one (last-resort protection against failures during recalc after updates to policy_new_votes). TODO: is that still needed given the use of user_policy_votes and pending_votes? The pending entry will stay until a successful write-and-recalc. The main issue is the amount of time it will take to correct it.


Questions remaining:
--------------------
- Deleted/retired policies: how to handle? Just mark as "active=false", and have user unallocated votes calculation ignore these? Note that once made inactive, they can never be activated again, because the user can then immediately assign the votes to something else.

- Merging two policies: what do we do with the votes?
  - Still thinking about this, but probably we'll leave the old policy records in the system, and mark them as "active=false", create a new policy, and set an "ancestor_policy" field on the new ones and a "merge_child" field on the ones.
  - What about pre-merge votes in the history?
    - Just leave them against the old policy records? The UI / analytics code may need to be merge-aware.
    - Add the two policy's totals together and put in the new policy's records? but this is "rewriting history" and may present consistency issues across the merge.
  - What about users' current vote allocations at the time of the merge?
    We'd like to set the new votes to the sum of the old votes, but can we do this atomically such that it's safe against users concurrently changing their vote allocations? Maybe; needs thought. Or, we can just leave the votes which were against the old policies as unallocated until the user comes back and allocates them somewhere; but this heavily disadvantages the new policy compared to the old ones.

- Detection of duplicate accounts. Do we require users to have unique email addresses? What else?


Detailed spec for each operation:
---------------------------------
TODO - finish this

User:
    add user:
      username uniqueness check
      create user record with initial salary balance
      create intitial zero vote record


User vote allocation:
    add new periodic vote bundle (weekly job, or on demand when user record read):
      write:
        vote_salary_last_paid = now,
        new column users.vote_salary_<timestamp> = NEW_VOTES_PER_WEEK

    read total unallocated votes:

    allocate votes to policy:

    unallocate votes from policy:

    delete user:

Policy:
    list current ranking:

Party:

Background operations:
