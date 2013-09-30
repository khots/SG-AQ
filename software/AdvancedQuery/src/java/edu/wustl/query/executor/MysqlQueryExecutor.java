/**
 * <p>Title: Query Executor Class for MySQL database</p>
 * <p>Description:  MysqlQueryExecutor class is a class which contains code to execute the sql query to get the results from MYSQL database. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author prafull_kadam
 * @version 1.00
 * Created on June 29, 2007
 */

package edu.wustl.query.executor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.util.querysuite.DAOUtil;
import edu.wustl.security.exception.SMException;

/**
 * @author prafull_kadam
 * Query Executor class implementation for MySQL database.
 * The Logic implemented for executing query is:
 * - Execute Query
 * - Set Resultset absolute to startIndex
 * - Iterate on Result for given no. of records
 * - Call resultset.last() & get the cursor position for totalNoOfRecords
 */
public class MysqlQueryExecutor extends AbstractQueryExecutor
{
	/**
	 * This method will
	 * - create statement object by executing SQL query, create scrollable resultset
	 * - moves the cursor position to the startIndex, depending upon its value. if value of startIndex is -1, it will return all the query results.
	 * - after getting required number of records, move cursor to last record & get the cursor position index.
	 * -	 return the Results.
	 * @return The reference to PagenatedResultData object, which will have pagenated data list & total no. of records that query can return.
	 * @throws DAOException
	 * @throws SMException
	 * @see edu.wustl.common.dao.queryExecutor.AbstractQueryExecutor#createStatemtentAndExecuteQuery()
	 */
	protected PagenatedResultData createStatemtentAndExecuteQuery(JDBCDAO jdbcDAO) throws SQLException, SMException, DAOException
	{
		StringBuilder sqlToBeExecuted = new StringBuilder(query);		
		String cpSql = checkCPBasedAuthentication(jdbcDAO);		
		if(cpSql != null && cpSql.isEmpty()){
			return new PagenatedResultData(new ArrayList(), totalFetchedRecords);
		} else if(cpSql != null){
			String[] sql = sqlToBeExecuted.toString().split("where");
			sqlToBeExecuted = new StringBuilder(sql[0]).append("where").append(" ").
					append(cpSql).append(sql[1]);
		}
		
		if (getSublistOfResult)
		{
			sqlToBeExecuted.append(" Limit ")
				.append(startIndex).append(" , ").append(noOfRecords).toString();
		}
		
		resultSet = jdbcDAO.getQueryResultSet(sqlToBeExecuted.toString());
		List list = getListFromResultSet(jdbcDAO); // get the result set.
		return new PagenatedResultData(list, totalFetchedRecords);
	}

	@Override
	public void deleteQueryTempViews(String tempViewName)
	{
		// TODO Auto-generated method stub	
		String getViewNamesQuery = " show full tables like '"+tempViewName+"%'";
		StringBuffer deleteViewQuery = new StringBuffer(" drop view if exists ");
		try
		{
			JDBCDAO jdbcDAO = DAOUtil.getJDBCDAO(null);
			final List<List<String>> resultList = jdbcDAO.executeQuery(getViewNamesQuery, new ArrayList<ColumnValueBean>());
			for(List<String> record : resultList)
			{
				if("VIEW".equalsIgnoreCase(record.get(1)))
				{
					jdbcDAO.executeUpdate(deleteViewQuery.append(record.get(0)).toString());
					//delete this view
				}
			}
			jdbcDAO.closeSession();

		}
		catch (DAOException ex) {
			logger.debug("Could not delete the Query Module Search temporary table:"+ ex.getMessage(), ex);
		}
	}
}
