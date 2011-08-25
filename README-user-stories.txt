Data / business rules stuff we're fairly sure of:
-------------------------------------------------
- The site tracks policies and lets users vote on them.
- Policies have a name, description, justification, web links, other attributes.
- Users receive a vote "salary" income every week, maybe referred to as "pollies" as if it's a currency. I'll just call them "votes" in this description.
- Users can allocate their unallocated votes to any policies on the site.
- Users can create policies. There is a cost to do this, in the form of a mandated minimum number of unallocated votes the user must have and must allocate to their newly created policy.
- There is a fixed set of portfolios (formerly known as "categories") - tax, education, health etc.
- Policies belong to one portfolio, set by policy creator.
- Users can assign any number of tags (freeform text) to policies.
- Users can add policies to a "watch list" shown in their user profile.
- Each policy has forum-style threaded comments.
- Each portfolio has forum-style threaded comments.
- Policies, tags and portfolios each track their current total number of votes received from all users, and the number of votes in the last 3 days.
- Site gives prominence to the highest-ranked policies - this is the main "goal" of the voting.
- Site shows trending tags and portfolios.
- Unregistered/not-logged-in users can view most pages - view policies, comments, ranking information etc, and can make comments (IP and cookie tracked) - they just can't vote on anything or create policies.
- Most pages should have social media links, Share This etc.
- Some events will generate dismissable messages/notifications for each user, shown at login and on their profile - things like "Your policy Foo got to number 1!" or "Your XXX was renamed / deleted by moderator due to X" or "User XXX suggested you retire your policy in favour of policy YYY".
- Some events will email users.


Data / business rules stuff possibly open to design variation:
--------------------------------------------------------------
- Users can withdraw their previously-allocated votes from a policy, but there may be a penalty - you only get some percentage of votes back.
- Policies can be "retired" by their creator, returning votes to the users, with recommendation to vote on another policy instead.
- Policies can optionally be associated with a political party, and the site may allow displaying things by party.
- Tags can be merged, deleted and renamed (possibly only by moderators).
- Site may show history of rankings as well as current data (can wait till a bit later)


Main functionality:
-------------------
Front page:
    Winning policies: one or both of these, with strong emphasis for the top ones:
        List of top few policies ranked by votes received in the last 3 days.
        List of top few policies ranked by total votes ever.
    Hot/trending portfolios:
        List of top few portfolios ranked by votes in the last 3 days to policies in this portfolio
    Hot/trending tags:
        List (or tag cloud) of top few tags ranked by votes in the last 3 days to policies with this tag

Activity: user profile.
    Show current "account balance" of unallocated votes.
    Show summary of this user's current vote allocations to policies.
    Show summary of policies this user has created.
    Show watch list of policies.
    Show "user notifications" and allow them to be dismissed.

Activity: user viewing their current vote allocation and assigning their votes to policies.
    Show policies this user is currently voting on, the votes currently allocated to each, plus the policies they are not currently voting on, and let the user assign their unallocated votes and see the change.
        This needs to be focussed on the competition element - how many more votes are needed to move the policy up one level in the 3-day rankings, how many votes it is ahead of the next one down, etc.
    Possibly: users withdrawing their previously-allocated votes from policies.
        This should be separate from the "adding votes" function, due to the "withdrawal penalty"; users need to be notified of the cost before they do it.

Activity: user creating a new policy.
    Show current "account balance" of unallocated votes.
    If balance is sufficient, allow creating a policy:
        Enter name, description, web links, other fields.
        Select portfolio, add tags, set initial vote allocation.

Activity: browse / search policies.
    Show list of policies.
        Filter/sort/navigate by:
          - name
          - total or 3-day vote ranking
          - tags (one or multiple)
          - portfolio
          - party (maybe)

Activity: browse / search portfolios.
    Show list of portfolios.
        Filter/sort/navigate by:
          - name
          - total or 3-day vote ranking across all policies in this portfolio.

Activity: browse / search tags.
    Show list or tag cloud.
        Filter/sort/navigate by:
          - name
          - total or 3-day vote ranking across all policies with this tag.

Activity: view policy details.
    Show policy name, description and other fields.
    Show portfolio and party.
    Show tags currently assigned, allow them to be edited.
    Show related policies (based on common tags).
    Show current number of votes allocated by all users.
    Show current number of votes allocated by logged-in user.
    Show current ranking - by total votes ever, by last 3-day votes.
    Maybe show neighbouring policies - next highest/lowest in the ranking.
    Show and add comments for this policy.
    Share / promote on Facebook etc.

Activity: view portfolio.
    Show portfolio name and description.
    Show total or 3-day vote ranking across all policies in this portfolio.
    List policies in this portfolio.
    Show and add comments for this portfolio.

Activity: view tag.
    Show tag name, allow editing.
    Show total or 3-day vote ranking across all policies with this tag.
    List policies with this tag.
    Delete tag / merge tags (moderator only?)
    Maybe show related tags - those that occur together.

Activity: retire policy.
    When viewing a policy that the logged-in user created, allow retiring it and specifying a suggested alternative to vote on.

Activity: user registration
    Absolutely minimal form for first page, to reduce the barrier to entry: just enter email address (must be site-unique) and submit (sends email confirm link which they must click before they can vote on anything, but need not do so to complete registration).
    Once they've submitted that, must choose a username (site unique) and password.
    Optionally, can also enter real name and whatever other profile info.

Activity: user login
    Usual stuff - username or email, plus password; forgot password link.


Less critical:
--------------
Activity: viewing history of vote rankings by policy, tag, portfolio etc.


Admin interfaces, design/layout less important:
-----------------------------------------------
Activity: moderation functions for admin users:
    Moderate comments
    Moderate tags
    Moderate created policies

Activity: user administration.

Activity: user viewing their history of vote allocations, comments, etc.
