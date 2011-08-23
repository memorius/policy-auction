package net.retakethe.policyauction.data.impl.dao;

import net.retakethe.policyauction.data.api.dao.VoteSalaryPayment;

import org.joda.time.LocalDate;

/**
 * @author Nick Clarke
 */
public class VoteSalaryPaymentImpl implements VoteSalaryPayment {
    private static final long serialVersionUID = 0L;

    private final long votes;
    private final LocalDate date;

    public VoteSalaryPaymentImpl(LocalDate date, long votes) {
        this.date = date;
        this.votes = votes;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public long getVotes() {
        return votes;
    }
}