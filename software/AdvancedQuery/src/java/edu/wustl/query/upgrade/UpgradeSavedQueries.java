package edu.wustl.query.upgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.common.query.authoriztion.SavedQueryAuthorization;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.beans.SharedQueryBean;
import edu.wustl.query.bizlogic.DashboardBizLogic;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeUtility;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSTransactionException;

public final class UpgradeSavedQueries
{
	private UpgradeSavedQueries()
	{}

	private static org.apache.log4j.Logger logger = LoggerConfig
			.getConfiguredLogger(UpgradeSavedQueries.class);

	/**
	 * @param args
	 * @throws CSTransactionException CSTransactionException
	 * @throws DAOException DAOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public static void main(String args[]) throws CSTransactionException
	{
		if (args.length == 0)
		{
			throw new RuntimeException(
				"insufficient arguments to this program : "
					+ "Upgrade saved queries, expecting value for" +
							" gov.nih.nci.security.configFile ");
		}
		else
		{
			logger.info("args[0] ......" + args[0]);
			System.setProperty("gov.nih.nci.security.configFile", args[0]);
		}
		logger.info("In UpgradeSavedQueries");
		try
		{
			setOwner(args);
		}
		catch (SMException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (DAOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Set the owner from the argument passed.
	 * @param args args
	 * @throws DAOException DAOException
	 * @throws SMException SMException
	 * @throws CSTransactionException CSTransactionException
	 */
	private static void setOwner(String[] args) throws DAOException,
			SMException, CSTransactionException
	{
		DashboardBizLogic dashboardBizLogic = new DashboardBizLogic();
		Collection<IParameterizedQuery> queries = dashboardBizLogic.getAllQueriesForUpgrade();

		if (queries.isEmpty())
		{
			logger
				.info("In UpgradeSavedQueries : NO QUERIES TO UPGRADE.....");
		}
		else
		{
			String owner = null;
			if(args.length == 2)
			{
				owner = args[1];
			}
			shareToAll(queries,owner);
		}
	}

	/**
	 * @param queries queries
	 * @param owner owner
	 * @throws SMException SMException
	 * @throws CSTransactionException CSTransactionException
	 */
	private static void shareToAll(Collection<IParameterizedQuery> queries, String owner)
			throws SMException, CSTransactionException
	{
		User admin = null;
		if (owner != null)
		{
			admin = getUserByName(owner);
		}
		if (admin == null)
		{
			admin = getValidAdminUser();
		}
		for (IParameterizedQuery parameterizedQuery : queries)
		{
			logger.info("QUERY ID : " + parameterizedQuery.getId()
					+ " | QUERY NAME : " + parameterizedQuery.getName());
			HashSet<ParameterizedQuery> protectionObjects = new HashSet<ParameterizedQuery>();
			protectionObjects.add((ParameterizedQuery) parameterizedQuery);
			SavedQueryAuthorization savedQuery = new SavedQueryAuthorization();
			SharedQueryBean bean = new SharedQueryBean();
			bean.setShareTo(AQConstants.ALL);
			try
			{
				savedQuery.insertAuthData(protectionObjects, bean, admin);
			}
			catch (DAOException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("SAVED QUERIES UPGRADED SUCCESSFULLY....");
	}

	/**
	 * @return admin
	 * @throws SMException Security Manager Exception
	 */
	private static User getValidAdminUser() throws SMException
	{
		User admin;
		List<User> adminUsers = getAdminUsers();
		PrivilegeUtility privilegeUtility = new PrivilegeUtility();
		UserProvisioningManager upManager = privilegeUtility
				.getUserProvisioningManager();
		List<ProtectionGroup> protectionGroups = upManager
				.getProtectionGroups();
		admin = getValidUser(adminUsers, protectionGroups);
		if(admin == null)
		{
			throw new RuntimeException("User not found with Admin role and a valid protection group");
		}
		else
		{
			logger.info("Old saved queries assigned to Admin user : "+admin.getName());
		}
		return admin;
	}

	/**
	 * @param adminUsers adminUsers
	 * @param protectionGroups protectionGroups
	 * @return valid user
	 * @throws SMException SMException
	 */
	private static User getValidUser(List<User> adminUsers,
			List<ProtectionGroup> protectionGroups) throws SMException
	{
		SavedQueryAuthorization savedQueryAith = new SavedQueryAuthorization();
		User validAdminUser = null;
		for (User admin : adminUsers)
		{
			String userProtectionGroup = savedQueryAith
					.getUserProtectionGroup(admin.getUserId().toString());
			for (ProtectionGroup pg : protectionGroups)
			{
				if (userProtectionGroup.equalsIgnoreCase(pg
						.getProtectionGroupName()))
				{
					validAdminUser = admin;
					break;
				}
			}
			if(validAdminUser != null)
			{
				break;
			}
		}
		return validAdminUser;
	}

	/**
	 * @param name name
	 * @return user with specified name
	 * @throws SMException SMException
	 */
	private static User getUserByName(String name) throws SMException
	{
		ISecurityManager securityManager = SecurityManagerFactory
				.getSecurityManager();
		return securityManager.getUser(name);
	}

	/**
	 * @return list of users who are administrators.
	 * @throws SMException SMException
	 */
	private static List<User> getAdminUsers() throws SMException
	{
		List<User> adminUsers = new ArrayList<User>();
		ISecurityManager securityManager = SecurityManagerFactory
				.getSecurityManager();
		List<User> users = securityManager.getUsers();
		for (User user : users)
		{
			Role role = securityManager.getUserRole(Long.valueOf(user.getUserId()));
			if (role.getName().equalsIgnoreCase(
					edu.wustl.security.global.Constants.ROLE_ADMIN))
			{
				adminUsers.add(user);
			}
		}
		return adminUsers;
	}
}
