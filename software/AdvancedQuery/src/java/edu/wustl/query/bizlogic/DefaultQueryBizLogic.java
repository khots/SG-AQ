package edu.wustl.query.bizlogic;



import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.bizlogic.IQueryBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.util.querysuite.AdvanceQueryDAO;

/**
 *
 */

public class DefaultQueryBizLogic extends DefaultBizLogic implements IQueryBizLogic
{
	/**
	 * constructor initialized with default application name.
	 */
	public DefaultQueryBizLogic()
	{
		//application name need to update in
		//query.properties as well as application resource.properties
		super(AdvanceQueryDAO.getInstance().getAppName());

	}
	/**
	 *
	 * This method return true if authorized user.
	 * @param dao DAO object.
	 * @param domainObject Domain object.
	 * @param sessionDataBean  SessionDataBean object.
	 * @throws BizLogicException generic BizLogic Exception
	 * @return true if authorized user.
	 */

	//@see edu.wustl.common.bizlogic.IBizLogic#
	// isAuthorized(edu.wustl.common.dao.AbstractDAO, java.lang.Object, edu.wustl.common.beans.SessionDataBean)
	public boolean isAuthorized(DAO dao, Object domainObject, SessionDataBean sessionDataBean)
			throws BizLogicException
	{
//		boolean isAuthorized = false;
//		if(sessionDataBean != null && sessionDataBean.isAdmin())
//		{
			return true;
//		}
//		return isAuthorized;
	}


	/**
	 * @param sessionDataBean A data bean that contains information related to user logged in.
	 * @param querySessionData QuerySessionData object
	 * @param startIndex startIndex
	 * @throws DAOException DAOException
	 * @return object of PagenatedResultData
	 */
	public PagenatedResultData execute(SessionDataBean sessionDataBean,
			QuerySessionData querySessionData, int startIndex) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List executeSQL(String sql) throws DAOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAliasName(String columnName, Object columnValue) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Set getAllTableNames(String aliasName, int forQI) throws DAOException,
			ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttributeType(String columnName, String aliasName) throws DAOException,
			ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List getColumnNames(String value) throws DAOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List getColumnNames(String aliasName, boolean defaultViewAttributesOnly)
			throws DAOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName(String aliasName) throws DAOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayNamebyTableName(String tableName) throws DAOException,
			ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List getMainObjectsOfQuery() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Set getNextTableNames(String prevValue) throws DAOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map getPivilegeTypeMap()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map getQueryObjectNameTableNameMap()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List getRelatedTableAliases(String aliasName) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map getRelationData()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getSpecimenTypeCount(String specimenType, JDBCDAO jdbcDAO) throws DAOException,
			ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getTableIdFromAliasName(String aliasName) throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> getTotalSummaryDetails() throws DAOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void initializeQueryData()
	{
		// TODO Auto-generated method stub

	}

	public void insertQuery(String sqlQuery, SessionDataBean sessionData) throws DAOException,
			ClassNotFoundException
	{
		// TODO Auto-generated method stub

	}

	public void insertQueryForMySQL(String sqlQuery, SessionDataBean sessionData, JDBCDAO jdbcDAO)
			throws DAOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub

	}

	public void insertQueryForOracle(String sqlQuery, SessionDataBean sessionData, JDBCDAO jdbcDAO)
			throws DAOException, ClassNotFoundException, SQLException, IOException
	{
		// TODO Auto-generated method stub

	}

	public List setColumnNames(String aliasName) throws DAOException, ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Set setTablesInPath(Long parentTableId, Long childTableId) throws DAOException,
			ClassNotFoundException
	{
		// TODO Auto-generated method stub
		return null;
	}

}