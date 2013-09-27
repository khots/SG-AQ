package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.dao.util.DAOUtility;
import edu.wustl.query.dto.QueryDTO;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.UserCache;
import edu.wustl.query.util.querysuite.CsmUtility;
import edu.wustl.query.util.querysuite.DAOUtil;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeUtility;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

/**
 * This class contains all the methods required for Query dash-board.
 * @author deepti_shelar
 *
 */
public class DashboardBizLogic extends DefaultQueryBizLogic
{

	private static final org.apache.log4j.Logger LOGGER = LoggerConfig
	.getConfiguredLogger(DashboardBizLogic.class);
 
	public User getQueryOwner(String queryId) throws BizLogicException
	{
		Set<ProtectionGroup> pgSet = getPGsforQuery(queryId);
		return getOwnerOfTheQuery(pgSet);
	}
	/**
	 * Returns PGs for given query.
	 * @param queryId id of the query
	 * @return set of PGs
	 * @throws BizLogicException exception
	 */
	public Set<ProtectionGroup> getPGsforQuery(String queryId)
			throws BizLogicException
	{
		Set<ProtectionGroup> pgSet = null;
		try
		{
			List<ProtectionElement> queryPEs = CsmUtility.getQueryPEs(queryId);
			if (queryPEs != null && !queryPEs.isEmpty())
			{
				ProtectionElement pElement = queryPEs.get(0);
				PrivilegeUtility privUtil = new PrivilegeUtility();
				pgSet = privUtil.getUserProvisioningManager().getProtectionGroups(
						pElement.getProtectionElementId().toString());
			}
		}
		catch (SMException e)
		{
			throw new BizLogicException(null,e,
					"Error while getting owner for query to be shown on dashboard");
		}
		catch (CSObjectNotFoundException e)
		{
			throw new BizLogicException(null,e,
					"Error while getting owner for query to be shown on dashboard");
		}
		return pgSet;
	}
	/**
	 * Returns the owner of the query.
	 * @param pgSet set of protection groups
	 * @return User
	 * @throws BizLogicException exception
	 */
	private User getOwnerOfTheQuery(Set<ProtectionGroup> pgSet)
			throws BizLogicException
	{
		String ownerId = getOwnerPG(pgSet);
		if(ownerId == null)
		{
			ownerId = "1";
		}
		return UserCache.getUser(ownerId);
	}

	/**
	 * returns the user id from protection group list.
	 * @param pgSet set of PGs
	 * @return String user id
	 */
	private String getOwnerPG(Set<ProtectionGroup> pgSet)
	{
		String userId = null;
		if(pgSet !=null)
		{
			for (ProtectionGroup protectionGroup : pgSet)
			{
				String pgName = protectionGroup.getProtectionGroupName();

				if(!pgName.equals(AQConstants.PUBLIC_QUERY_PROTECTION_GROUP))
				{
					userId = pgName.substring(pgName.indexOf('_')+1);
					break;
				}
			}
		}
		return userId;
	}

	/**
	 * Returns all queries.
	 * @return list of all queries
	 * @throws DAOException DAOException
	 */
	public Collection<IParameterizedQuery> getAllQueries() 
	throws DAOException
	{
		HibernateDAO hibernateDAO = null;
		hibernateDAO = DAOUtil.getHibernateDAO(null);
		return hibernateDAO.executeQuery("from " + IParameterizedQuery.class.getName());
	}

	/**
	 * Gets all queries from the DB.
	 * @return collection of queries
	 * @throws DAOException exception
	 */
	public Collection<IParameterizedQuery> getAllQueriesForUpgrade() throws DAOException
	{
		Collection<IParameterizedQuery> allQueries =
			DAOUtility.getInstance().executeHQL(DAOUtility.GET_PARAM_QUERIES_DETAILS);
		 return allQueries;
	}

	/**
	 * Returns true if the passes user is super-administrator.
	 * @param csmUserId user id
	 * @return true/false
	 * @throws BizLogicException exception
	 */
	private boolean ifSuperAdminUser(String csmUserId) throws BizLogicException
	{
		boolean isSuperAdmin = false;
		try
		{
			ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
			Role role = securityManager.getUserRole(Long.valueOf(csmUserId));
			if(role.getName().equalsIgnoreCase(edu.wustl.security.global.Constants.ROLE_ADMIN))
			{
				isSuperAdmin = true;
			}
		}
		catch (SMException e)
		{
			throw new BizLogicException(null,e,"Sm exception: while getting role for an user");
		}
		return isSuperAdmin;
	}

	/**
	 * @param tokenizer StringTokenizer
	 * @return time
	 */
	private static String getAppropriateTime(StringTokenizer tokenizer)
	{
		String time;
		String hours;
		String minutes;
		tokenizer.nextToken();
		time = tokenizer.nextToken();
		time = time.substring(0, time.lastIndexOf(':'));
		hours = time.substring(0, time.lastIndexOf(':'));
		minutes = time.substring(time.lastIndexOf(':')+1, time.length());
		if(Integer.parseInt(hours)>AQConstants.TWELVE)
		{
			time = getPMTime(hours, minutes);
		}
		else if(Integer.parseInt(hours)==0)
		{
			time = "12:"+minutes+" "+AQConstants.AM_CONSTANT;
		}
		else if(Integer.parseInt(hours)==AQConstants.TWELVE)
		{
			StringBuffer tempTime = new StringBuffer(time);
			tempTime.append(' ').append(AQConstants.PM_CONSTANT);
			time = tempTime.toString();
		}
		else
		{
			StringBuffer tempTime = new StringBuffer(time);
			tempTime.append(' ').append(AQConstants.AM_CONSTANT);
			time = tempTime.toString();
		}
		return time;
	}

	/**
	 * @param hours hours
	 * @param minutes minutes
	 * @return time
	 */
	private static String getPMTime(String hours, String minutes)
	{
		StringBuffer time = new StringBuffer();
		int hrs = Integer.parseInt(hours)-AQConstants.TWELVE;
		if(hrs<AQConstants.TEN)
		{
			time.append('0').append(hrs).append(':').append(minutes).
			append(' ').append(AQConstants.PM_CONSTANT);
		}
		else
		{
			time.append(hrs).append(':').append(minutes).append(' ').
			append(AQConstants.PM_CONSTANT);
		}
		return time.toString();
	}

	/**
	 * Return query object by id.
	 * @param queryId id
	 * @return query object
	 * @throws DAOException exception
	 */
	public IParameterizedQuery getQueryById(Long queryId) throws DAOException
	{
//		CsmUtility util = new CsmUtility();
		IParameterizedQuery query = null;
		List<Long> queryIds = new ArrayList<Long>();
		queryIds.add(queryId);
		Collection<IParameterizedQuery> queries = CsmUtility.retrieveQueries(queryIds, "");
		for (IParameterizedQuery parameterizedQuery : queries)
		{
			query = parameterizedQuery;
		}
		return query;
	}

	/**
	 * @param data data
	 * @return columnValueBean
	 */
	private static LinkedList<ColumnValueBean> populateColumnValueBean(LinkedList<Object> data)
	{
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		ColumnValueBean bean = null;
		for(Object object : data)
		{
			bean = new ColumnValueBean(object.toString(), object);
			columnValueBean.add(bean);
		}
		return columnValueBean;
	}

	/**
	 * Will returns all queries(shared/created) of the user with given CSM user ID.
	 * @param csmUserId CSM user id of the user
	 * @param userName user name
	 * @return all queries(shared/created) of the user with given CSM user ID.
	 * @throws BizLogicException instance of BizLogicException
	 */
	public Collection<QueryDTO> getAllQueries(String csmUserId, String userName) 
	throws BizLogicException
	{
		List<QueryDTO> queries = new ArrayList<QueryDTO>();
		try {
			QueryDAO queryDAO = new QueryDAO();
			if(ifSuperAdminUser(csmUserId)) {
				queries= queryDAO.getAllQueries();	 
			}
			else {
				queries = getSharedQueries(userName);
				Collection<QueryDTO> myQueries = getMyQueries(csmUserId);
				if(myQueries != null) {
					queries.addAll(myQueries);
				}
			}
		} catch (DAOException e) {
			LOGGER.error(e.getMessage(),e);
			throw new BizLogicException(null,e,"DAOException: while Retrieving queries for SuperAdmin");
		}

		return queries;
	}

	/**
	 * Will Returns all the shared queries on which the user with given CSM user ID has access.
	 * @param csmUserId CSM user Id
	 * @param userName user name
	 * @return all the shared queries on which the user with given CSM user ID has access.
	 * @throws BizLogicException instance of BizLogicException
	 * @throws DAOException 
	 */
	public List<QueryDTO> getSharedQueries(String userName) throws BizLogicException, DAOException
	{
		List<QueryDTO> queries = new ArrayList<QueryDTO>();
		try {
			Collection<Long> publicQueryIdList = CsmUtility.getQueriesIdList(AQConstants.PUBLIC_QUERY_PROTECTION_GROUP);
			Collection<Long> sharedQueryIdList = CsmUtility.getSharedQueryIdList(userName);
			Set<Long> queriesIdSet = new HashSet<Long>();
			queriesIdSet.addAll(publicQueryIdList);
			queriesIdSet.addAll(sharedQueryIdList);

			QueryDAO queryDAO = new QueryDAO();
			queries = queryDAO.getQueriesById(queriesIdSet);
		} catch (CSObjectNotFoundException e) {
			LOGGER.error(e.getMessage(),e);
			throw new BizLogicException(null,e,e.getMessage());
		} catch (SMException e) {
			LOGGER.error(e.getMessage(),e);
			throw new BizLogicException(null,e,e.getMessage());
		}
		return queries;
	}

	/**
	 * This will return all queries created by the user with given CSM user Id.
	 * @param csmUserId CSM user id
	 * @return all queries created by the user with given CSM user Id
	 * @throws BizLogicException instance of BizLogicException
	 * @throws DAOException 
	 */
	public Collection<QueryDTO> getMyQueries(String csmUserId) throws BizLogicException, DAOException
	{
		Collection<QueryDTO> queries = new ArrayList<QueryDTO>();
		String userPG = CsmUtility.getUserProtectionGroup(csmUserId);
		try {
			Collection<Long> myQueriesIdList = CsmUtility.getQueriesIdList(userPG);
			QueryDAO queryDAO = new QueryDAO();
			queries = queryDAO.getQueriesById(myQueriesIdList);
		} catch (SMException e) {
			LOGGER.error(e.getMessage(),e);
			throw new BizLogicException(null,e,e.getMessage());
		} catch (CSObjectNotFoundException e) {
			LOGGER.error(e.getMessage(),e);
			throw new BizLogicException(null,e,e.getMessage());
		}
		return queries;
	}
}
