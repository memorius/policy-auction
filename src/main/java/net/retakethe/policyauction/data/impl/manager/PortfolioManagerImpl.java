package net.retakethe.policyauction.data.impl.manager;

import java.util.Collections;
import java.util.List;

import me.prettyprint.hector.api.query.QueryResult;
import net.retakethe.policyauction.data.api.PortfolioManager;
import net.retakethe.policyauction.data.api.dao.PortfolioDAO;
import net.retakethe.policyauction.data.api.exceptions.NoSuchPortfolioException;
import net.retakethe.policyauction.data.api.types.PortfolioID;
import net.retakethe.policyauction.data.impl.dao.PortfolioDAOImpl;
import net.retakethe.policyauction.data.impl.query.api.ColumnSlice;
import net.retakethe.policyauction.data.impl.query.api.KeyspaceManager;
import net.retakethe.policyauction.data.impl.query.api.OrderedRows;
import net.retakethe.policyauction.data.impl.query.api.RangeSlicesQuery;
import net.retakethe.policyauction.data.impl.query.api.Row;
import net.retakethe.policyauction.data.impl.query.api.SliceQuery;
import net.retakethe.policyauction.data.impl.schema.Schema;
import net.retakethe.policyauction.data.impl.schema.column.NamedColumn;
import net.retakethe.policyauction.data.impl.schema.timestamp.MillisTimestamp;
import net.retakethe.policyauction.data.impl.types.PortfolioIDImpl;
import net.retakethe.policyauction.util.CollectionUtils;
import net.retakethe.policyauction.util.Functional;
import net.retakethe.policyauction.util.Functional.Filter;
import net.retakethe.policyauction.util.Functional.SkippedElementException;

/**
 * @author Nick Clarke
 */
public class PortfolioManagerImpl extends AbstractDAOManagerImpl implements PortfolioManager {

    public PortfolioManagerImpl(KeyspaceManager keyspaceManager) {
        super(keyspaceManager);
    }

    @Override
    public PortfolioID makePortfolioID(String idString) {
        return new PortfolioIDImpl(idString);
    }

    @Override
    public PortfolioDAO getPortfolio(PortfolioID portfolioID) throws NoSuchPortfolioException {
        List<NamedColumn<PortfolioID, MillisTimestamp, String, ?>> list = CollectionUtils.list(
                (NamedColumn<PortfolioID, MillisTimestamp, String, ?>) Schema.PORTFOLIOS.NAME,
                (NamedColumn<PortfolioID, MillisTimestamp, String, ?>) Schema.PORTFOLIOS.DESCRIPTION);
        SliceQuery<PortfolioID, MillisTimestamp, String> query =
                Schema.PORTFOLIOS.createSliceQuery(getKeyspaceManager(), portfolioID, list);

        QueryResult<ColumnSlice<MillisTimestamp, String>> queryResult = query.execute();

        ColumnSlice<MillisTimestamp, String> cs = queryResult.get();

        String name;
        try {
            name = getNonNullColumn(cs, Schema.PORTFOLIOS.NAME);
        } catch (NoSuchColumnException e) {
            throw new NoSuchPortfolioException(portfolioID);
        }

        String description;
        try {
            description = getNonNullColumn(cs, Schema.PORTFOLIOS.DESCRIPTION);
        } catch (NoSuchColumnException e) {
            throw new RuntimeException("Invalid portfolio data for ID '" + portfolioID + "'", e);
        }

        return new PortfolioDAOImpl(portfolioID, name, description);
    }

    @Override
    public List<PortfolioDAO> getAllPortfolios() {
        List<NamedColumn<PortfolioID, MillisTimestamp, String, ?>> list = CollectionUtils.list(
                (NamedColumn<PortfolioID, MillisTimestamp, String, ?>) Schema.PORTFOLIOS.NAME,
                (NamedColumn<PortfolioID, MillisTimestamp, String, ?>) Schema.PORTFOLIOS.DESCRIPTION);
        RangeSlicesQuery<PortfolioID, MillisTimestamp, String> query =
                Schema.PORTFOLIOS.createRangeSlicesQuery(getKeyspaceManager(), list);
        // TODO: must not be smaller than the actual number of items else we have to do paging,
        //       but it actually allocates an array this size so don't set Integer.MAX_VALUE.
        //       This will do for now, we're not likely to have many portfolios.
        query.setRowCount(1000);

        QueryResult<OrderedRows<PortfolioID, MillisTimestamp, String>> result = query.execute();

        OrderedRows<PortfolioID, MillisTimestamp, String> orderedRows = result.get();
        if (orderedRows == null) {
            return Collections.emptyList();
        }

        return Functional.filter(orderedRows.getList(),
                new Filter<Row<PortfolioID, MillisTimestamp, String>, PortfolioDAO>() {
                    @Override
                    public PortfolioDAO filter(Row<PortfolioID, MillisTimestamp, String> row)
                            throws SkippedElementException {
                        ColumnSlice<MillisTimestamp, String> cs = row.getColumnSlice();
                        if (cs == null) {
                            throw new SkippedElementException();
                        }

                        String name;
                        try {
                            name = getNonNullColumn(cs, Schema.PORTFOLIOS.NAME);
                        } catch (NoSuchColumnException e) {
                            // Tombstone row
                            throw new SkippedElementException();
                        }

                        String description;
                        try {
                            description = getNonNullColumn(cs, Schema.PORTFOLIOS.DESCRIPTION);
                        } catch (NoSuchColumnException e) {
                            throw new RuntimeException("Invalid portfolio data for ID '" + row.getKey() + "'", e);
                        }

                        return new PortfolioDAOImpl(row.getKey(), name, description);
                    }
                });
    }
}
