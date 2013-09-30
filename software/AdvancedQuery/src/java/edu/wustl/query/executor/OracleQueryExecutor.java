/**
 * <p>Title: Query Executor Class for Oracle database</p>
 * <p>Description:  MysqlQueryExecutor class is a class which contains code to execute the sql query to get the results from Oracle database. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author prafull_kadam
 * @version 1.00
 * Created on July 11, 2007
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
 * Query Executor class implementation for Oracel database.
 * The Logic implemented for executing query is:
 * - Modify Query to get only required no of record
 * - Iterate on all result set
 * - Fire another query of type "select count(*) from table" to get the totalNoOfRecords
 */
public class OracleQueryExecutor extends AbstractQueryExecutor
{
	/**
	 * This method will
	 * - modify the SQL, by adding the rownum conditions, to get the required number of records starting from startIndex.
	 * - create statement object by executing modified SQL query & get the results. It will modify SQL if value of startIndex is -1, elseit will return all the query results.
	 * - Fire another "Select Count(*)..." type SQL to get the total number of records that given sql can return.
	 * - return the Results.
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
		} 
		// modify the SQL by adding rownum condition if required.
		if (getSublistOfResult)
		{
			sqlToBeExecuted = new StringBuilder();
			sqlToBeExecuted.append(putPageNumInSQL(query, startIndex, noOfRecords));
		}
		
		if (cpSql != null){
			String[] sql = sqlToBeExecuted.toString().split("where");
			sqlToBeExecuted = new StringBuilder(sql[0]).append("where").append(cpSql).append(sql[1]);
		}

		// execute the modified query & get Results

		resultSet = jdbcDAO.getQueryResultSet(sqlToBeExecuted.toString());
		List list = getListFromResultSet(jdbcDAO);
		return new PagenatedResultData(list, totalFetchedRecords);
	}
	/**
	 * To modify the SQL, to get the required no. of records with the given offset from the query.
	 * For query like "Select id, first_name from catissue_participant where id > 0 order by id" will be modified as follows:
	 * For Oracle: "Select * from (Select qry.*, rownum rn From (Select rownum rn, id, first_name from catissue_participant where id > 0 order by id) qry where rownum <= lastindex) where rn > startIndex"
	 * For MySQL : "Select id, first_name from catissue_participant where id > 0 order by id limit startIndex, noOfRecords"
	 * @param sql The SQL to be executed on database
	 * @param startIndex The offset, or the starting index.
	 * @param noOfRecords The totalnumber of records to fetch from the query.
	 * @return The modified SQL.
	 */
	protected String putPageNumInSQL(String sql, int startIndex, int noOfRecords)
	{
		StringBuffer newSql = new StringBuffer(80);
		/*if (Variables.databaseName.equals(AQConstants.MYSQL_DATABASE))
		{
			// Add limit clause for the MYSQL case
			newSql.append(sql).append(" Limit ").append(startIndex).append(" , ").append(
					noOfRecords);
		}
		else
		{*/
			/**
			 * Name: Prafull
			 * Reviewer: Aarti
			 * Bug: 4857,4865
			 * Description: Changed Query modification logic for Oracle.
			 *
			 * forming new query, by using original query as inner query & adding rownum conditions in outer query.
			 */
			newSql.append(SELECT_CLAUSE).append(" * ").append(FROM_CLAUSE).append(" (").append(
					SELECT_CLAUSE).append(" qry.*, ROWNUM rn ").append(FROM_CLAUSE).append(" (")
					.append(sql).append(") qry WHERE ROWNUM <= ").append(startIndex + noOfRecords)
					.append(") WHERE rn > ").append(startIndex);
			// Another approach to form simillar query by putting both rownum conditions in the outer query.
			//			newSql.append(SELECT_CLAUSE).append(" * ").append(FROM_CLAUSE).append(" (").append(SELECT_CLAUSE)
			//			.append(" qry.*, ROWNUM rn ").append(FROM_CLAUSE).append(" (").append(sql).append(") qry ) WHERE rn BETWEEN ")
			//			.append(startIndex+1).append(" AND ").append(startIndex + noOfRecords);;

		//}
		return newSql.toString();
	}

	@Override
	public void deleteQueryTempViews(String tempViewName)
	{
		// TODO Auto-generated method stub
		String getViewNamesQuery = "select tname from tab where tname like '"+tempViewName+"%' and TABTYPE like 'VIEW'";
		String deleteViewQuery = " drop view  ";
		try
		{
			JDBCDAO jdbcDAO = DAOUtil.getJDBCDAO(null);
			final List<List<String>> resultList = jdbcDAO.executeQuery(getViewNamesQuery, new ArrayList<ColumnValueBean>());
			for(List<String> record : resultList)
			{

					jdbcDAO.executeUpdate(deleteViewQuery + record.get(0));
					//delete this view

			}
			jdbcDAO.closeSession();

		}
		catch (DAOException ex) {
			logger.debug("Could not delete the Query Module Search temporary table:"+ ex.getMessage(), ex);
		}


	}


}
