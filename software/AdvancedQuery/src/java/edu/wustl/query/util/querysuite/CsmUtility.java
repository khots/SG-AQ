
package edu.wustl.query.util.querysuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.bizlogic.BizLogicFactory;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.privilege.PrivilegeUtility;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.dao.ProtectionElementSearchCriteria;
import gov.nih.nci.security.dao.ProtectionGroupSearchCriteria;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;

/**
 * CSM Utility class.
 */
final public class CsmUtility
{
	private CsmUtility()
	{
		super();
	}
	/**
	 * Logger.
	 */
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(CsmUtility.class);

	/**
	 * Get list of query protection elements.
	 * @param queryId queryId
	 * @return peList
	 * @throws SMException Security Manager Exception
	 */
	public static List<ProtectionElement> getQueryPEs(String queryId) throws SMException
	{
		PrivilegeUtility privilegeUtility = new PrivilegeUtility();
		ProtectionElement pElement = new ProtectionElement();
		pElement.setObjectId(ParameterizedQuery.class.getName() + "_"+queryId);
		ProtectionElementSearchCriteria searchCriteria = new ProtectionElementSearchCriteria(pElement);
		return privilegeUtility.getObjects(searchCriteria);
	}

	/**
	 * Gets the user protection group.
	 * @param csmUserId String
	 * @return user protection group
	 */
	public static String getUserProtectionGroup(String csmUserId)
	{
		return "User_" + csmUserId;
	}

	/**
	 * @return Collection<Long>
	 * @throws DAOException DAOException
	 */
	public static Collection<Long> getParameterizedQueryCollection() throws DAOException
	{
		HibernateDAO hibernateDAO = null;
		try
		{
			hibernateDAO = DAOUtil.getHibernateDAO(null);
			return hibernateDAO.executeQuery("select id from "
					+ IParameterizedQuery.class.getName() + "  order by  id desc");
		}
		finally
		{
			DAOUtil.closeHibernateDAO(hibernateDAO);
		}
	}

	/**
	 * To Retrieve Queries.
	 * @param ids Collection<Long>
	 * @param queryNameLike queryNameLike
	 * @return  Collection<IParameterizedQuery>
	 * @throws DAOException DAOException
	 */
	public static Collection<IParameterizedQuery> retrieveQueries(Collection<Long> ids, String queryNameLike)
	throws DAOException
	{
		List<Long> idList = new ArrayList<Long>(ids);
		Collections.sort(idList,Collections.reverseOrder());
		Collection<IParameterizedQuery> queries = new ArrayList<IParameterizedQuery>();
		IBizLogic bizLogic = BizLogicFactory.getInstance().getBizLogic(
				AQConstants.QUERY_INTERFACE_BIZLOGIC_ID);
		for (Long queryId : idList)
		{
			List<IParameterizedQuery> queryList;
			try
			{
				queryList = bizLogic.retrieve(ParameterizedQuery.class
						.getName(), "id", queryId);
				if (queryList != null && !queryList.isEmpty())
				{
					IParameterizedQuery pQuery = queryList.get(0);
					queries.add(pQuery);
				}
			}
			catch (BizLogicException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
		return queries;
	}


	/**
	 * Returns the shared queries Id's list.
	 * @param userName name of the user
	 * @return shared queries Id's list.
	 * @throws SMException instance of SMException
	 */
	public static Collection<Long> getSharedQueryIdList(String userName) throws SMException
	{
		Collection<Long> collection = new ArrayList<Long>();
		List<ProtectionElement> peList = getQueryPEs("*");
		if (peList != null && !peList.isEmpty())
		{
			PrivilegeUtility privUtil = new PrivilegeUtility();
			for (ProtectionElement pElement : peList)
			{

				if(privUtil.getUserProvisioningManager().checkOwnership(userName, pElement.getObjectId()))
				{
					String[] objectId = pElement.getObjectId().split("_");
					Long pQueryId = Long.valueOf(objectId[1]);
					collection.add(pQueryId);
				}
			}
		}
		return collection;
	}

	/**
	 * Will returns the queries ID's list.
	 * @param userPG user protection group name
	 * @return queries ID's list
	 * @throws SMException instance of SMException
	 * @throws CSObjectNotFoundException instance of CSObjectNotFoundException
	 */
	public static Collection<Long> getQueriesIdList(String userPG) throws SMException, CSObjectNotFoundException
	{
		Collection<Long> queriesIdList = new ArrayList<Long>();
		ProtectionGroup protectionGroup;
		List<ProtectionGroup> groups = getProtectionGroupByName(userPG);
		if(!groups.isEmpty())
		{
			protectionGroup= groups.get(0);
			Set<ProtectionElement> protectionElements = null;
			PrivilegeUtility utility = new PrivilegeUtility();
			UserProvisioningManager manager;
			manager = utility.getUserProvisioningManager();
			protectionElements = manager.getProtectionElements(protectionGroup.getProtectionGroupId().toString());

			queriesIdList = getQueriesIds(protectionElements);
		}
		return queriesIdList;
	}



	/**
	 * Returns the List of Protection groups
	 * @param userPG name of the user protection group
	 * @return List of Protection groups
	 * @throws SMException instance of SMException
	 */
	private static List<ProtectionGroup> getProtectionGroupByName(String userPG) throws SMException
	{
		PrivilegeUtility privUtil = new PrivilegeUtility();
		ProtectionGroup protectionGroup = new ProtectionGroup();
		protectionGroup.setProtectionGroupName(userPG);
		ProtectionGroupSearchCriteria criteria = new ProtectionGroupSearchCriteria(protectionGroup);

		return privUtil.getObjects(criteria);
	}

	/**
	 * Returns collection of queries ID's.
	 * @param protectionElements set of ProtectionElements
	 * @return collection of queries ID's.
	 */
	private static Collection<Long> getQueriesIds(Set<ProtectionElement> protectionElements)
	{
		Collection<Long> queriesIdList = new ArrayList<Long>();
		if (protectionElements != null && !protectionElements.isEmpty())
		{
			for (ProtectionElement pElement : protectionElements)
			{
				String[] objectId = pElement.getObjectId().split("_");
				Long pQueryId = Long.valueOf(objectId[1]);
				queriesIdList.add(pQueryId);
			}
		}
		return queriesIdList;
	}
}
