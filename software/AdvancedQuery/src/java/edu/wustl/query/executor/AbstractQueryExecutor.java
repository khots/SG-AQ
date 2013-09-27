/**
 * <p>Title: Query Executor Class </p>
 * <p>Description:  AbstractQueryExecutor class is a base class which contains code
 * to execute the sql query to get the results from database. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author prafull_kadam
 * @version 1.00
 * Created on June 29, 2007
 */

package edu.wustl.query.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.QueryParams;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.security.QueryCsmCache;
import edu.wustl.query.security.QueryCsmCacheManager;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.SecurityManager;

/**
 * @author prafull_kadam
 * Query Executor class, for executing SQL on database & getting corresponding results.
 * This is abstract class, sql execution implementation for specific type of database
 * should be implemented in the derived class of this class.
 */
public abstract class AbstractQueryExecutor
{
	/**
	 * logger Logger - Generic logger.
	 */
	protected static org.apache.log4j.Logger logger = Logger.getLogger(SecurityManager.class);

	/**
	 * AQConstants required for forming/changing SQL.
	 */
	protected static final String SELECT_CLAUSE = "SELECT";

	/**
	 * AQConstants required for forming/changing SQL.
	 */
	protected static final String FROM_CLAUSE = "FROM";

	/**
	 * AQConstants required for db error.
	 */
	private static final String DB_ERROR = "db.operation.error";

	/**
	 * String to be displayed.
	 */
	private static final String ABSTRACT_QUERY = "AbstractQueryExecutor :";
	/**
	 * Holds the reference to connection object.
	 */
	//protected Connection connection;
	/**
	 * Holds reference to the statement object.
	 *//*
	protected PreparedStatement stmt = null;*/
	/**
	 * Holds reference to the resultSet object.
	 */
	protected ResultSet resultSet = null;
	/**
	 * The SQL to be executed.
	 */
	protected String query;
	/**
	 * Holds reference to the SessionDataBean object.
	 */
	protected SessionDataBean sessionDataBean;
	/**
	 * Booleans variables required for query execution like security check etc.
	 */
	protected boolean isSecureExecute, hasConditionOnIdentifiedField;

	/**
	 * Boolean variable, will have value true if call is made to get sublist of
	 * the total query result by passing StartIndex & totalNoOfRecords.
	 */
	protected boolean getSublistOfResult;
	/**
	 * Map of QueryResultObjectData, used for security checks & handle identified data.
	 */
	protected Map queryResultObjectDataMap;
	/**
	 * Start index in the Query Resultset. & no of records to fetch from the query result.
	 */
	protected int startIndex, noOfRecords;

	/**
	 * Method to get the Query executor instance.
	 * This will return instance of the query executor object depending upon Variables.databaseName value.
	 * @return The instance of the Query executor object.
	 *//*
	public AbstractQueryExecutor getInstance()
	{
		if (Variables.databaseName.equals(AQConstants.MYSQL_DATABASE))
		{
			return new MysqlQueryExecutor();
		}
		else if(Variables.databaseName.equals(AQConstants.ORACLE_DATABASE))
		{
			return new OracleQueryExecutor();
		}
	}
*/
	/**
	 * To get the Query execution results.
	 * @param query The SQL to be executed.
	 * @param connection The JDBC connection object.
	 * @param sessionDataBean The reference to SessionDataBean object.
	 * @param isSecureExecute security check parameter.
	 * @param hasConditionOnIdentifiedField security check parameter.
	 * @param queryResultObjectDataMap Map of QueryResultObjectData.
	 * @param startIndex Start index in the Query Result set.
	 * @param noOfRecords no of records to fetch from the query result.
	 * @return The Query Execution results.
	 * @throws DAOException if there an error occurs while executing query.
	 * @throws SMException Security Manager Exception
	 */
	public final PagenatedResultData getQueryResultList(String query, Connection connection,
			SessionDataBean sessionDataBean, boolean isSecureExecute,
			boolean hasConditionOnIdentifiedField, Map queryResultObjectDataMap, int startIndex,
			int noOfRecords) throws DAOException, SMException
	{
		this.query = query;
		this.sessionDataBean = sessionDataBean;
		this.isSecureExecute = isSecureExecute;
		this.hasConditionOnIdentifiedField = hasConditionOnIdentifiedField;
		this.queryResultObjectDataMap = queryResultObjectDataMap;
		this.startIndex = startIndex;
		this.noOfRecords = noOfRecords;
		getSublistOfResult = startIndex != -1; // this will be used, when its
		//required to get sublist of the result set.
		/**
		 * setting noOfRecords = Integer.MAX_VALUE, if All records are expected from result.
		 * see getListFromResultSet method
		 */
		if (!getSublistOfResult)
		{
			this.noOfRecords = Integer.MAX_VALUE;
		}
		JDBCDAO jdbcDAO = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		PagenatedResultData pagenatedResultData = null;
		try
		{
			jdbcDAO = DAOConfigFactory.getInstance().getDAOFactory(appName).getJDBCDAO();
			jdbcDAO.openSession(null);
			pagenatedResultData = createStatemtentAndExecuteQuery(jdbcDAO);
			logger.debug("Query Execution on MySQL Completed...");
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey(DB_ERROR);
			throw new DAOException(errorKey,sqlExp,ABSTRACT_QUERY);
		}
		finally
		{
			try
			{
				jdbcDAO.closeSession();
			}
			catch (DAOException ex)
			{
				logger.error(ex.getMessage(), ex);
				ErrorKey errorKey = ErrorKey.getErrorKey(DB_ERROR);
				throw new DAOException(errorKey,ex,ABSTRACT_QUERY);
			}
		}
		return pagenatedResultData;
	}

	/**
	 * @param queryParams QueryParams object
	 * @return pagenatedResultData Pagenated Result Data
	 * @throws DAOException DAOException
	 * @throws SMException Security Manager Exception
	 */
	public PagenatedResultData getQueryResultList(QueryParams queryParams) throws DAOException, SMException
	{
		query = queryParams.getQuery();
		sessionDataBean = queryParams.getSessionDataBean();
		isSecureExecute = queryParams.isSecureToExecute();
		hasConditionOnIdentifiedField = queryParams.isHasConditionOnIdentifiedField();
		queryResultObjectDataMap = queryParams.getQueryResultObjectDataMap();
		startIndex = queryParams.getStartIndex();
		noOfRecords = queryParams.getNoOfRecords();
		getSublistOfResult = startIndex != -1; // this will be used, when it required to get sublist of the result set.
		  //setting noOfRecords = Integer.MAX_VALUE, if All records are expected from result. see getListFromResultSet method
		if (!getSublistOfResult)
		{
			noOfRecords = Integer.MAX_VALUE;
		}
		String appName = CommonServiceLocator.getInstance().getAppName();
		JDBCDAO jdbcDAO = null;
		PagenatedResultData pagenatedResultData = null;
		try
		{
			jdbcDAO = DAOConfigFactory.getInstance().getDAOFactory(appName).getJDBCDAO();
			jdbcDAO.openSession(null);
			pagenatedResultData = createStatemtentAndExecuteQuery(jdbcDAO);
			logger.debug("Query Execution on MySQL Completed...");
		}
		catch (SQLException sqlExp)
		{
			logger.error(sqlExp.getMessage(), sqlExp);
			ErrorKey errorKey = ErrorKey.getErrorKey(DB_ERROR);
			throw new DAOException(errorKey,sqlExp,ABSTRACT_QUERY);
		}
		finally
		{
			try
			{
				if(resultSet != null)
				{
					jdbcDAO.closeStatement(resultSet);
				}
				jdbcDAO.closeSession();
			}
			catch (DAOException ex)
			{
				logger.error(ex.getMessage(), ex);
				ErrorKey errorKey = ErrorKey.getErrorKey(DB_ERROR);
				throw new DAOException(errorKey,ex,ABSTRACT_QUERY);
			}
		}
		return pagenatedResultData;
	}

	/**
	 * This method will create Statement object, execute the query & return the query Results,
	 *  which will contain the Result list based on start index & page size & total no. of records
	 *  that query can return.
	 * Subclasses of this class must provide its own implementation for executing the query & getting results.
	 * @param jdbcDAO JDBCDAO object
	 * @return The reference to PagenatedResultData object, which will have pagenated data list
	 * & total no. of records that query can return.
	 * @throws SQLException SQLException
	 * @throws DAOException DAOException
	 * @throws SMException Security Manager Exception
	 */
	protected abstract PagenatedResultData createStatemtentAndExecuteQuery(JDBCDAO jdbcDAO)
	throws SQLException, SMException, DAOException;

	/**
	 * To process the ResultSet object & create Results in the format List<List<String>>.
	 * @param jdbcDAO JDBCDAO object
	 * @return The Result list.
	 * @throws SQLException SQLException
	 * @throws SMException Security Manager Exception
	 * @throws DAOException DAOException
	 */
	protected List getListFromResultSet(JDBCDAO jdbcDAO) throws SQLException, SMException, DAOException
	{
 		ResultSetMetaData metaData = resultSet.getMetaData();

		boolean isLongKeyOfMap = false;
		if(queryResultObjectDataMap!=null && !queryResultObjectDataMap.isEmpty())
		{
			isLongKeyOfMap = isLongKeyOfMap(isLongKeyOfMap);
		}
		int columnCount = metaData.getColumnCount();

		/**
		 * Name: Prafull
		 * Reviewer: Aarti
		 * Bug: 4857,4865
		 * Description: Changed Query modification logic for Oracle.
		 * For oracle queries extra rownum is added in SELECT clause as last attribute
		 *  in SELECT clause of query for paginated results,
		 * so no need to process that extra row num column.
		 * @see edu.wustl.common.dao.queryExecutor.
		 * AbstractQueryExecutor#putPageNumInSQL(java.lang.String,int,int)
		 */
		columnCount = getColumnCount(columnCount,getSublistOfResult);
		int recordCount = 0;
		List list = new ArrayList();
		QueryCsmCacheManager cacheManager = new QueryCsmCacheManager(jdbcDAO);
		QueryCsmCache cache = cacheManager.getNewCsmCacheObject();
		/**
		 * noOfRecords will hold value = Integer.MAX_VALUE when All records are expected from result.
		 */
		while (resultSet.next() && recordCount < noOfRecords)
		{
			List aList = new ArrayList();
			// Srinath: rewrote to use resultSet getters of correct type.
			for (int i = 1; i <= columnCount; i++)
			{
                populateListToFilter(metaData, aList, i);
            }
			if(!isLongKeyOfMap && queryResultObjectDataMap!=null)
			{
				//Aarti: If query has condition on identified data then check user's permission
				//on the record's identified data.
				//If user does not have privilege don't add the record to results list
				//bug#1413
				if (AQConstants.SWITCH_SECURITY && hasConditionOnIdentifiedField && isSecureExecute)
				{
					boolean hasPrivilegeOnIdentifiedData = cacheManager.
					hasPrivilegeOnIdentifiedDataForSimpleSearch(sessionDataBean,
							queryResultObjectDataMap,aList, cache);
					if (!hasPrivilegeOnIdentifiedData)
					{
						continue;
					}
				}
				//Aarti: Checking object level privileges on each record
				if (AQConstants.SWITCH_SECURITY && isSecureExecute)
				{
					filterDataForSimpleSearch(cacheManager, cache, aList);
				}
			}
			else
			{
				if (AQConstants.SWITCH_SECURITY && hasConditionOnIdentifiedField && isSecureExecute)
				{
					boolean hasPrivilegeOnIdentifiedData =cacheManager.hasPrivilegeOnIdentifiedData(sessionDataBean, queryResultObjectDataMap, aList,cache);
					if (!hasPrivilegeOnIdentifiedData)
					{
						continue;
					}
				}

				//Aarti: Checking object level privileges on each record
				if (AQConstants.SWITCH_SECURITY && isSecureExecute)
				{
					filterDataForAdvancedSearch(cacheManager, cache, aList);
				}
			}
			list.add(aList);
			recordCount++;
		}
		return list;
	}

	/**
	 * @param isLongKeyOfMap isLongKeyOfMap
	 * @return flag
	 */
	private boolean isLongKeyOfMap(boolean isLongKeyOfMap)
	{
		boolean flag = isLongKeyOfMap;
		Iterator mapIterator = queryResultObjectDataMap.keySet().iterator();
		while(mapIterator.hasNext())
		{
			if (mapIterator.next() instanceof Long)
			{
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * @param metaData metaData
	 * @param aList list
	 * @param counter counter
	 * @throws SQLException Exception
	 */
	private void populateListToFilter(ResultSetMetaData metaData,
			List aList, int counter) throws SQLException
	{
		Object retObj;
		switch (metaData.getColumnType(counter))
		{
		    case Types.CLOB :
		        retObj = resultSet.getObject(counter);
		        break;
		    case Types.DATE :
		    case Types.TIMESTAMP :
		        retObj = resultSet.getTimestamp(counter);
		        if (retObj == null)
		        {
		            break;
		        }
		        SimpleDateFormat formatter = new SimpleDateFormat(CommonServiceLocator.getInstance().getTimeStampPattern());
		        retObj = formatter.format((java.util.Date) retObj);
		        break;
		    default :
		        retObj = resultSet.getObject(counter);
		        if (retObj != null)
		        {
		            retObj = retObj.toString();
		        }
		}
		if (retObj == null)
		{
		    aList.add("");
		}
		else
		{
		    aList.add(retObj);
		}
	}

	/**
	 * @param cacheManager cacheManager
	 * @param cache cache
	 * @param aList list
	 * @throws SMException Security Manager Exception
	 */
	private void filterDataForSimpleSearch(QueryCsmCacheManager cacheManager,
			QueryCsmCache cache, List aList) throws SMException {
		if (sessionDataBean != null & sessionDataBean.isSecurityRequired())
		{
			//call filterRowForSimpleSearch of method of csm cache manager changed for csm-query performance issue.
			cacheManager.filterRowForSimpleSearch(sessionDataBean,queryResultObjectDataMap, aList,cache );
		}
	}

	/**
	 * @param cacheManager cacheManager
	 * @param cache cache
	 * @param aList list
	 * @throws SMException Security Manager Exception
	 */
	private void filterDataForAdvancedSearch(QueryCsmCacheManager cacheManager,
			QueryCsmCache cache, List aList) throws SMException {
		if (sessionDataBean != null & sessionDataBean.isSecurityRequired())
		{
			//Supriya :call filterRow of method of csm cache manager changed
			//for csm-query performance issue.
			cacheManager.filterRow(sessionDataBean, queryResultObjectDataMap, aList,cache );
		}
	}

	/**
	 * @param columnCount columnCount
	 * @param getSublistOfResult getSublistOfResult
	 * @return countVal countVal
	 */
	private int getColumnCount(int columnCount,boolean getSublistOfResult)
	{
		String appName=CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		int countVal = columnCount;
		if("ORACLE".equals(daofactory.getDataBaseType()) && getSublistOfResult)
		{
			countVal = columnCount - 1;
		}
		return countVal;
	}

	/**
	 * To form the SQL query to get the count of the records for the given query.
	 * @param originalQuery the SQL string
	 * @return The SQL query to get the count of the records for the given originalQuery.
	 */
	protected String getCountQuery(String originalQuery)
	{
		return "Select count(*) from (" + originalQuery + ") alias";
	}

	public abstract void deleteQueryTempViews(String tempViewName);

}
