package edu.wustl.common.query.authoriztion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.SharedQueryBean;
import edu.wustl.query.bizlogic.DashboardBizLogic;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.CsmUtility;
import edu.wustl.security.beans.SecurityDataBean;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.locator.SecurityManagerPropertiesLocator;
import edu.wustl.security.privilege.PrivilegeManager;
import edu.wustl.security.privilege.PrivilegeUtility;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.dao.ProtectionElementSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

/**
 * Class to create and insert protection elements for saved query.
 *
 * @author vijay_pande
 * @author deepti_shelar
 */
public class SavedQueryAuthorization
{

	/**
	 * Inserts authorization data.
	 * @param protectionObjects Set of objects for which protection elements has to insert
	 * @param bean Object of SharedQueryBean
	 * @param user logged in user
	 * @throws DAOException DAO Exception
	 */
	public void insertAuthData(Set<ParameterizedQuery> protectionObjects, SharedQueryBean bean, User user)
	throws DAOException
	{
		try
		{
			ParameterizedQuery query = (ParameterizedQuery) protectionObjects.iterator().next();
			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			privilegeManager.insertAuthorizationData(getAuthorizationData(query, user,null),
			protectionObjects, getDynamicGroups(user.getUserId().toString()), query.getObjectId());
			bean.setQuery(query);
			shareQuery(bean, query);
		}
		catch (Exception e)
		{
			ErrorKey errorKey = ErrorKey.getErrorKey("db.operation.error");
			throw new DAOException(errorKey, e, e.getMessage());
		}
	}

	/**
	 * Shares query : Populates security tables for protection group and PEs.
	 * @param bean SharedQueryBean
	 * @param query Parameterized Query
	 * @throws CSException CSException
	 * @throws SMException security manager exception
	 * @throws CSTransactionException exception
	 * @throws CSObjectNotFoundException exception
	 */
	public void shareQuery(SharedQueryBean bean, ParameterizedQuery query)
			throws CSException, SMException, CSTransactionException,
			CSObjectNotFoundException
	{
		String shareTo = bean.getShareTo();
		if ("all".equals(shareTo))
		{
			handleShareToAll(query);
		}
		else if("users".equals(shareTo))
		{
			handleShareToUsers(bean);
		}
		else if("roles".equals(shareTo))
		{
			handleShareToRoles(bean);
		}
	}

	/**
	 * Shares the query to selected roles.
	 * @param bean Object of SharedQueryBean
	 * @throws SMException Security Manager Exception
	 * @throws CSObjectNotFoundException CSObjectNotFoundException
	 * @throws CSTransactionException CSTransactionException
	 */
	private void handleShareToRoles(SharedQueryBean bean) throws SMException,
		CSObjectNotFoundException, CSTransactionException
	{
		String selectedRoles = bean.getSelectedRoles();
		PrivilegeUtility privilegeUtility = new PrivilegeUtility();
		String[] roles = selectedRoles.split(",");
		List<Long> userIds = new ArrayList<Long>();
		for (String role : roles)
		{
			String groupId = privilegeUtility.getGroupIdForRole(role);
			Set<User> users = privilegeUtility.getUserProvisioningManager().getUsers(groupId);
			for (User user : users)
			{
				userIds.add(user.getUserId());
			}
		}
		String[] ids = covertListToArray(userIds);
		setOwnersForQuery(bean.getQuery(), ids);
	}

	/**
	 * Converts the list of user identifiers to array.
	 * @param userIds List of user identifiers
	 * @return List of identifiers
	 */
	private String[] covertListToArray(List<Long> userIds)
	{
		String[] identifiers = new String[userIds.size()];
		int index=0;
		for (Long id : userIds)
		{
			identifiers[index] = id.toString();
			index++;
		}
		return identifiers;
	}

	/**
	 * Shares the query to list of selected users.
	 * @param bean Object of SharedQueryBean
	 * @throws SMException Security Manager Exception
	 * @throws CSTransactionException CSTransactionException
	 */
	private void handleShareToUsers(SharedQueryBean bean) throws SMException,
	CSTransactionException
	{
		StringBuffer selectedUserId = new StringBuffer();
		long[] selectedUserIds = bean.getProtocolCoordinatorIds();
		for(long userId:selectedUserIds)
		{
			selectedUserId.append(userId).append(',');
		}
		String[] userIds = selectedUserId.toString().split(",");
		setOwnersForQuery(bean.getQuery(), userIds);
	}

	/**
	 * Sets the users as the owners of the query. These users should be able to
	 * see the query.
	 * @param query Object of ParameterizedQuery
	 * @param userIds List of user identifiers
	 * @throws SMException Security Manager Exception
	 * @throws CSTransactionException CSTransactionException
	 */
	private void setOwnersForQuery(ParameterizedQuery query ,String[] userIds)
			throws SMException, CSTransactionException
	{
		PrivilegeUtility privilegeUtility = new PrivilegeUtility();
		UserProvisioningManager upManager = privilegeUtility.getUserProvisioningManager();
		ProtectionElement protectionElement = getProtectionElementForQuery(
				query, privilegeUtility);
		upManager.assignOwners(protectionElement.getProtectionElementId().toString(), userIds);
	}

	/**
	 * Shares the query to all user. This method creates a PE for query and
	 * assigns this PE to PG of PUBLIC_QUERY_PROTECTION_GROUP.
	 * @param query Object of ParameterizedQuery
	 * @throws CSException Common Security Exception
	 * @throws SMException Security Manager Exception
	 */
	private void handleShareToAll(ParameterizedQuery query)
			throws CSException, SMException
	{
		PrivilegeUtility privilegeUtility = new PrivilegeUtility();
		ProtectionElement protectionElement = getProtectionElementForQuery(
				query, privilegeUtility);
		//deAssignProtectionElements
		UserProvisioningManager upManager = privilegeUtility.getUserProvisioningManager();
		upManager.assignProtectionElement(
				AQConstants.PUBLIC_QUERY_PROTECTION_GROUP, protectionElement.getObjectId());
	}

	/**
	 * Returns the PE for passed query.
	 * @param query Object of ParameterizedQuery
	 * @param privilegeUtility Object of PrivilegeUtility
	 * @return protectionElement The protection element
	 * @throws SMException Security Manager Exception
	 */
	private ProtectionElement getProtectionElementForQuery(
			ParameterizedQuery query, PrivilegeUtility privilegeUtility)
			throws SMException
	{
		ProtectionElement protectionElement = new ProtectionElement();
		List<ProtectionElement> peList;//= new ArrayList<ProtectionElement>();

		protectionElement.setProtectionElementName(query.getObjectId());
		String appName = SecurityManagerPropertiesLocator.getInstance().getApplicationCtxName();
		protectionElement.setApplication(privilegeUtility.getApplication(appName));
		ProtectionElementSearchCriteria searchCriteria = new ProtectionElementSearchCriteria(
				protectionElement);
		peList = privilegeUtility.getUserProvisioningManager().getObjects(searchCriteria);
		if (peList != null && !peList.isEmpty())
		{
			protectionElement = peList.get(0);
		}
		return protectionElement;
	}

	/**
	 * Returns the array of dynamic groups.
	 * @param csmUserId CSM user id
	 * @return dynamicGroups string array of group identifiers
	 */
	private String[] getDynamicGroups(String csmUserId)
	{
		String[] dynamicGroups = new String[1];
		dynamicGroups[0] = CsmUtility.getUserProtectionGroup(csmUserId);

		return dynamicGroups;
	}

	/**
	 * This method returns collection of UserGroupRoleProtectionGroup
	 * objects that specify the user group protection group linkage
	 * through a role. It is also used to specify the groups the
	 * protection elements returned by this class should be added to.
	 * @param query object of ParameterizedQuery
	 * @param user logged in user
	 * @param role Role of user
	 * @return authorizationData
	 * @throws SMException Security Manager Exception
	 * @throws CSException Common Security Exception
	 */
	protected List<SecurityDataBean> getAuthorizationData(ParameterizedQuery query, User user,
			String role) throws SMException, CSException
	{
		List<SecurityDataBean> authorizationData = new ArrayList<SecurityDataBean>();
		Set<gov.nih.nci.security.authorization.domainobjects.User> group =
			new HashSet<gov.nih.nci.security.authorization.domainobjects.User>();
		group.add(user);

		String pgName = getUserProtectionGroup(user.getUserId().toString());
		SecurityDataBean securityDataBean = getSaveQuerySecurityBean(user.getUserId().toString(),
				group, pgName, role);
		authorizationData.add(securityDataBean);

		return authorizationData;
	}

	/**
	 * Creates a bean of CSM data.
	 * @param csmUserId CSM user id
	 * @param group group
	 * @param pgName Protection group name
	 * @param role Role of user
	 * @return securityDataBean
	 */
	private SecurityDataBean getSaveQuerySecurityBean(String csmUserId,
			Set<gov.nih.nci.security.authorization.domainobjects.User> group, String pgName,
			String role)
	{
		SecurityDataBean securityDataBean = new SecurityDataBean();
		securityDataBean.setUser(csmUserId);
		securityDataBean.setRoleName(role);
		//securityDataBean.setProtectionGroupName(pgName);
		securityDataBean.setProtGrpName(pgName);
		securityDataBean.setGroupName(getUserProtectionGroup(csmUserId));
		securityDataBean.setGroup(group);

		return securityDataBean;
	}

	/**
	 * Gets the PG for given user id.
	 * @param csmUserId CSM user id
	 * @return user protection group
	 */
	public String getUserProtectionGroup(String csmUserId)
	{
		return "User_" + csmUserId;
	}

	/**
	 * Returns the users to which this query is shared.
	 * @param query query
	 * @return set of users
	 * @throws SMException exception
	 * @throws CSObjectNotFoundException exception
	 */
	public Set<User> getSharedUsers(ParameterizedQuery query)
	throws SMException, CSObjectNotFoundException
	{
		Set<User> sharedUsers = new HashSet<User>();
		PrivilegeUtility privilegeUtility = new PrivilegeUtility();
		ProtectionElement protectionElement =
		getProtectionElementForQuery(query, privilegeUtility);
		if(protectionElement.getProtectionElementId() != null)
		{
			UserProvisioningManager upManager =
			privilegeUtility.getUserProvisioningManager();
			sharedUsers = upManager.getOwners
			(protectionElement.getProtectionElementId().toString());
		}
		return sharedUsers;
	}


	/**
	 * Removes the previous sharing.
	 * @param query the query
	 * @param csmUserId the CSM user id
	 * @throws SMException the SM exception
	 * @throws CSTransactionException the CS transaction exception
	 * @throws CSObjectNotFoundException the CS object not found exception
	 * @throws BizLogicException
	 */
	public void removePrevSharing(ParameterizedQuery query,String csmUserId)
				throws SMException, CSTransactionException,
				CSObjectNotFoundException, BizLogicException
	{
		PrivilegeUtility privilegeUtility = new PrivilegeUtility();
		ProtectionElement protectionElement =
		getProtectionElementForQuery(query, privilegeUtility);
		UserProvisioningManager upManager =
		privilegeUtility.getUserProvisioningManager();
		Set<ProtectionGroup> pgSet = new DashboardBizLogic().
		getPGsforQuery(query.getId().toString());
		for (ProtectionGroup protectionGroup : pgSet)
		{
			String pgName = protectionGroup.getProtectionGroupName();
			if(pgName.equals(AQConstants.PUBLIC_QUERY_PROTECTION_GROUP))
			{
				upManager.deAssignProtectionElements
				(AQConstants.PUBLIC_QUERY_PROTECTION_GROUP, protectionElement.getObjectId());
				break;
			}
		}
		removeOwnerForPE(query, upManager, protectionElement);
	}

	/**
	 * @param query query
	 * @param upManager upManager
	 * @param protectionElement protection element
	 * @throws SMException Security manager exception
	 * @throws CSObjectNotFoundException CSObjectNotFoundException
	 * @throws CSTransactionException CSTransactionException
	 */
	private void removeOwnerForPE(ParameterizedQuery query,
			UserProvisioningManager upManager, ProtectionElement protectionElement)
			throws SMException, CSObjectNotFoundException,
			CSTransactionException
	{
		Set<User> sharedUsers = getSharedUsers(query);
		if(!sharedUsers.isEmpty())
		{
			String[] userIds = new String[0];
			upManager.assignOwners(protectionElement.getProtectionElementId().toString(), userIds);
			upManager.modifyProtectionElement(protectionElement);
		}
	}
}
