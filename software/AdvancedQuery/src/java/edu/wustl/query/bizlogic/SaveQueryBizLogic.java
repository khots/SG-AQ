/**
 *
 */

package edu.wustl.query.bizlogic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.wustl.cab2b.common.queryengine.ICab2bQuery;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.bizlogic.IQueryBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.query.authoriztion.SavedQueryAuthorization;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.util.EmailClient;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.SharedQueryBean;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.UserCache;
import edu.wustl.query.util.querysuite.DAOUtil;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

/**
 * @author chetan_patil
 * @created Sep 13, 2007, 7:39:46 PM
 */
public class SaveQueryBizLogic extends DefaultQueryBizLogic implements IQueryBizLogic
{

	private static org.apache.log4j.Logger logger = LoggerConfig
			.getConfiguredLogger(QueryBizLogic.class);

	/**
	 * It will insert the Query object in the database.
	 * @param query
	 * @param sessionDataBean
	 * @param shareQuery
	 * @throws DAOException
	 * @throws BizLogicException
	 * @throws DAOException
	 */
	public void saveQuery(IParameterizedQuery query, SessionDataBean sessionDataBean,
			SharedQueryBean bean) throws BizLogicException
	{
		HibernateDAO hibernateDao = null;
		try
		{
			hibernateDao = DAOUtil.getHibernateDAO(sessionDataBean);
			insert(query, hibernateDao);
			hibernateDao.commit();
			handleQuerySharing(query, sessionDataBean, bean);
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			if (hibernateDao != null)
			{
				try
				{
					DAOUtil.closeHibernateDAO(hibernateDao);
				}
				catch (DAOException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	/**
	 * Shares the query to users
	 * @param query query
	 * @param sessionDataBean session data
	 * @param bean share query details
	 * @throws BizLogicException exception
	 */
	private void handleQuerySharing(IParameterizedQuery query,
			SessionDataBean sessionDataBean, SharedQueryBean bean)
			throws BizLogicException
	{
		HashSet<ParameterizedQuery> protectionObjects = new HashSet<ParameterizedQuery>();
		protectionObjects.add((ParameterizedQuery) query);
		String csmUserId = sessionDataBean.getCsmUserId();
		User user = UserCache.getUser(csmUserId);//getUserById(csmUserId);
		SavedQueryAuthorization savedQueryAuth = new SavedQueryAuthorization();
		try
		{
			savedQueryAuth.insertAuthData(protectionObjects, bean,
					user);
			if(bean.getProtocolCoordinatorIds() != null){
				sendSharedQueryEmailNotification(user, bean.getProtocolCoordinatorIds(), query); 
			}
		}
		catch (DAOException e)
		{
			String message = "error while sharing this query, however this query is saved " +
					"and will be visible to you only";
			logger.error(e.getMessage()+ "_ "+message, e);
			throw new BizLogicException(null,e,"SMException : error while sharing this query, however this query is saved " +
					"and will be visible to you only");		
		}  
	}

	private void sendSharedQueryEmailNotification(User currentUser, long[] protocolCoordinatorIds, 
			IParameterizedQuery query) throws BizLogicException  
	{
		 Map<String,Object> contextMap = new HashMap<String,Object>();
		 contextMap.put("user", currentUser);
		 contextMap.put("sharedQuery", query);
			 
		 for (Long sUserId : protocolCoordinatorIds)
		 {
			 User sharedUser = UserCache.getUser(sUserId.toString()); 
			 contextMap.put("sharedUser", sharedUser);
			 EmailClient.getInstance().sendEmail( 
					 AQConstants.SHARE_QUERY_EMAIL_TEMPL,
					 new String[]{ sharedUser.getEmailId() },
					 contextMap);
		 }
	}
	/**
	 * Returns the user object by id
	 * @param csmUserId id of the user
	 * @return User object
	 * @throws BizLogicException exception
	 */
	public User getUserById(String csmUserId) throws BizLogicException
	{
		User user = null;
		try
		{
			ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
			user = securityManager.getUserById(csmUserId);
		}
		catch (SMException e)
		{
			logger.error(e.getMessage(), e);
			throw new BizLogicException(null,e,"SMException : error while getting user from security manager");
		}
		return user;
	}
	/**
	 * Returns the Role of the user
	 * @param sessionDataBean session data
	 * @return role name
	 */
	protected String getRole(SessionDataBean sessionDataBean)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method returns the IQuery object
	 * @param queryId id
	 * @return Iquery object
	 * @throws BizLogicException exception
	 */
	public IQuery getQuery(Long queryId) throws BizLogicException
	{
		IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		IBizLogic bizLogic = factory.getBizLogic(AQConstants.QUERY_INTERFACE_BIZLOGIC_ID);
		final List<IQuery> queryList = bizLogic.retrieve(ParameterizedQuery.class.getName(),
				AQConstants.IDENTIFIER, queryId);
		IQuery query = null;
		if (queryList != null && !queryList.isEmpty())
		{
			query = queryList.get(0);
		}
		return query;
	}
	/**
	 * This methods updates the query in the database which is already saved
	 * @param query IQuery
	 * @throws BizLogicException exception
	 */
	public void updateQuery(IQuery query, SessionDataBean sessionDataBean,
			SharedQueryBean sharedQueryBean) throws BizLogicException
	{
		IParameterizedQuery parameterizedQuery = populateParameterizedQueryData(query);
		parameterizedQuery.setId(query.getId());

		updateSavedQueries(parameterizedQuery, sessionDataBean, sharedQueryBean);
	}
	/**
	 * It will update the query object in the dataBase
	 * @param query query
	 * @param sessionDataBean session data
	 * @param sharedQueryBean bean
	 * @throws BizLogicException exception
	 */
	public void updateSavedQueries(IParameterizedQuery query, SessionDataBean sessionDataBean,
			SharedQueryBean sharedQueryBean) throws BizLogicException
	{
		HibernateDAO hibernateDao = null;
		try
		{
			hibernateDao = DAOUtil.getHibernateDAO(sessionDataBean);
			super.update(hibernateDao, query);
			hibernateDao.commit();
			SavedQueryAuthorization auth = new SavedQueryAuthorization();
			auth.removePrevSharing((ParameterizedQuery)query, sessionDataBean.getCsmUserId());
			sharedQueryBean.setQuery((ParameterizedQuery)query);
			auth.shareQuery(sharedQueryBean, (ParameterizedQuery)query);
		}
		catch (Exception e)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new BizLogicException(errorKey, e, e.getMessage());
		}
		finally
		{
			try
			{
				DAOUtil.closeHibernateDAO(hibernateDao);
			}
			catch (DAOException e)
			{
				throw new BizLogicException(e);
			}
		}
	}

	/**
	 * Populates query data.
	 * @param query = IQuery
	 * @return query IParameterizedQuery
	 */
	public IParameterizedQuery populateParameterizedQueryData(IQuery query)
	{
		IParameterizedQuery originalQuery=(IParameterizedQuery)query;
		if(query instanceof ICab2bQuery)
		{
			originalQuery = QueryObjectFactory.createParameterizedQuery(query);
		}
		return originalQuery;
	}
	/**
	 * Sets the bean values
	 * @param queryObject query
	 * @param userId logged in user
	 * @return Shared Query bean
	 * @throws BizLogicException exception
	 */
	public SharedQueryBean getSharingDetailsBean(IQuery queryObject) throws BizLogicException
	{
		SharedQueryBean bean = new SharedQueryBean();
		DashboardBizLogic bizLogic = new DashboardBizLogic();
		Set<ProtectionGroup> pgSet = bizLogic.getPGsforQuery(queryObject.getId().toString());
		populateBean(pgSet,bean);
		String shareTo = bean.getShareTo();
		boolean isSharedToAll;
		if(shareTo != null && shareTo.equals(AQConstants.ALL))
		{
			isSharedToAll = true;
		}
		else
		{
			isSharedToAll = false;
		}
		long[] sharedUsers;
		if(!isSharedToAll)
		{
			sharedUsers = getSharedUsers((ParameterizedQuery)queryObject);
			if(sharedUsers.length == 0)
			{
				bean.setShareTo(AQConstants.NONE);
			}
			else
			{
				bean.setShareTo(AQConstants.USERS);
				bean.setProtocolCoordinatorIds(sharedUsers);
			}
		}
		return bean;
	}
	/**
	 * Returns the list of users to whom the query is shared.
	 * @param queryObject query
	 * @return array of user id's
	 * @throws BizLogicException exception
	 */
	private long[] getSharedUsers(ParameterizedQuery queryObject) throws BizLogicException
	{
		long[] selectedUserIds = null;
		SavedQueryAuthorization savedQueryAuth = new SavedQueryAuthorization();
		try
		{
			Set<User> sharedUsers = savedQueryAuth.getSharedUsers(queryObject);
			selectedUserIds = new long[sharedUsers.size()];
			int index = 0;
			for (User user : sharedUsers)
			{
				selectedUserIds [index] = user.getUserId();
				index++;
			}
		}
		catch (SMException e)
		{
			logger.error(e.getMessage(), e);
			throw new BizLogicException(null,e,"SMException : error while" +
			" getting users to whom the query is shared : query id is :"+queryObject.getId());
		}
		catch (CSObjectNotFoundException e)
		{
			logger.error(e.getMessage(), e);
			throw new BizLogicException(null,e,"SMException : error while" +
			" getting users to whom the query is shared : query id is :"+queryObject.getId());
		}
		return selectedUserIds;
	}

	/**
	 * This method sets the bean for csmUser id and whether the query is shared to all
	 * @param pgSet set of protection groups
	 * @param bean sharedQueryBean
	 */
	private void populateBean(Set<ProtectionGroup> pgSet, SharedQueryBean bean)
	{
		if(pgSet !=null)
		{
			for (ProtectionGroup protectionGroup : pgSet)
			{
				String pgName = protectionGroup.getProtectionGroupName();
				if(pgName.equals(AQConstants.PUBLIC_QUERY_PROTECTION_GROUP))
				{
					bean.setShareTo(AQConstants.ALL);
				}
				else
				{
					String[] pgNameSplit = pgName.split(AQConstants.UNDERSCORE);
					String csmUserId = pgNameSplit[1];
					bean.setCsmUserId(csmUserId);
				}
			}
		}
	}
}