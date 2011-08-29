package net.retakethe.policyauction.data.api.dao;

import java.io.Serializable;

import org.joda.time.LocalDate;

/**
 * @author Nick Clarke
 */
public interface VoteSalaryPaymentDAO extends Serializable {

    LocalDate getDate();

    long getVotes();

}
