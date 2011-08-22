package net.retakethe.policyauction.data.impl.dao;

import java.io.Serializable;

import org.apache.tapestry5.json.JSONObject;

public final class PolicyVoteRecord implements Serializable {
    private static final long serialVersionUID = 0L;

    private static final String VOTE = "vote";
    private static final String VOTE_TOTAL = "voteTotal";
    private static final String PENALTY = "penalty";
    private static final String PENALTY_TOTAL = "penaltyTotal";

    private long voteIncrement;
    private long voteTotal;
    private long penalty;
    private long penaltyTotal;

    public PolicyVoteRecord() {
        this(0, 0, 0, 0);
    }
    
    public PolicyVoteRecord(long voteIncrement, long voteTotal, long penalty, long penaltyTotal) {
        this.voteIncrement = voteIncrement;
        this.voteTotal = voteTotal;
        this.penalty = penalty;
        this.penaltyTotal = penaltyTotal;
    }

    public PolicyVoteRecord(JSONObject json) {
        this(longOrZero(json, VOTE),
             longOrZero(json, VOTE_TOTAL),
             longOrZero(json, PENALTY),
             longOrZero(json, PENALTY_TOTAL));
    }

    public JSONObject toJSON() {
        JSONObject o = new JSONObject();
        putIfNonZero(o, VOTE, voteIncrement);
        putIfNonZero(o, VOTE_TOTAL, voteTotal);
        putIfNonZero(o, PENALTY, penalty);
        putIfNonZero(o, PENALTY_TOTAL, penaltyTotal);
        return o;
    }

    /**
     * Create human-readable string for debugging
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PolicyVoteRecord [voteIncrement=").append(this.voteIncrement).append(", voteTotal=")
                .append(this.voteTotal).append(", penalty=").append(this.penalty).append(", penaltyTotal=")
                .append(this.penaltyTotal).append("]");
        return builder.toString();
    }

    /**
     * Get the change in votes since loaded.
     */
    public long getVoteIncrement() {
        return this.voteIncrement;
    }

    /**
     * Add to or subtract from the change in votes since loaded, and update total votes accordingly.
     */
    public void addVoteIncrement(long increment) {
        this.voteIncrement += increment;
        this.voteTotal += increment;
    }

    public long getVoteTotal() {
        return this.voteTotal;
    }

    public long getPenalty() {
        return this.penalty;
    }

    /**
     * Add to or subtract from the change in penalty votes since loaded, and update total penalty votes accordingly.
     */
    public void addVotePenalty(long penaltyIncrement) {
        this.penalty += penaltyIncrement;
        this.penaltyTotal += penaltyIncrement;
    }

    public long getPenaltyTotal() {
        return this.penaltyTotal;
    }

    private static long longOrZero(JSONObject json, String key) {
        return json.has(key) ? json.getLong(key) : 0;
    }
    
    private static void putIfNonZero(JSONObject json, String key, long value) {
        if (value != 0) {
            json.put(key, Long.valueOf(value));
        }
    }
}