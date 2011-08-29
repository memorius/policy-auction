package net.retakethe.policyauction.data.impl.dao;

import net.retakethe.policyauction.data.api.dao.VoteSalaryPaymentDAO;

import org.joda.time.LocalDate;

/**
 * @author Nick Clarke
 */
public class VoteSalaryPaymentDAOImpl implements VoteSalaryPaymentDAO {
    private static final long serialVersionUID = 0L;

    private final long votes;
    private final LocalDate date;

    public VoteSalaryPaymentDAOImpl(LocalDate date, long votes) {
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