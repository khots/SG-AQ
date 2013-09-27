/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */
package edu.wustl.common.query.security;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.global.Constants;
import edu.wustl.security.global.ProvisionManager;
import edu.wustl.security.locator.SecurityManagerPropertiesLocator;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.exceptions.CSException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class extends SecurityManager of SecurityManager project.
 * @author deepti_shelar
 *
 */
public class QuerySecurityManager extends edu.wustl.security.manager.SecurityManager
{
	/**
	 * Gets the user role.
	 * @param userID long id of the user.
	 * @throws SMException Security Manager Exception
	 * @return role
	 */
	public Role getUserRole(long userID) throws SMException
	{
		Role role = null;
		try
		{
			ProvisionManager pvManager = ProvisionManager.getInstance();
			UserProvisioningManager upManager = pvManager
					.getUserProvisioningManager();
			Set<Group> groups = upManager.getGroups(String.valueOf(userID));
			String applicationCtxName = SecurityManagerPropertiesLocator
					.getInstance().getApplicationCtxName();
			Map<String, String> groupRoleMap = getGroupRoleMap();
			Iterator<Group> iter = groups.iterator();
			while (iter.hasNext())
			{
				Group group = (Group) iter.next();
				String applicationName = group.getApplication()
						.getApplicationName();
				if (applicationName.equals(applicationCtxName))
				{
					String groupName = groupRoleMap.get(group.getGroupName());
					role = upManager
							.getRoleById(pvManager.getRoleID(groupName));
				}
			}
		}
		catch (CSException e)
		{
			Logger.out.debug("Unable to get roles: Exception: "
					+ e.getMessage());
			throw new SMException(ErrorKey.getErrorKey("sm.operation.error"),
					e, "Exception while getting user role");
		}
		return role;
	}
	/**
	 * Loads the map for Group name as a key and Role name as value.
	 * @return map of group name v/s role name
	 */
	private Map<String, String> getGroupRoleMap()
	{
		Map<String, String> groupRoleMap = new HashMap<String, String>();
		groupRoleMap.put(ADMIN_GROUP, Constants.ROLE_ADMIN);
		groupRoleMap.put(SUPERVISOR_GROUP, Constants.SUPERVISOR_ROLE);
		groupRoleMap.put(TECHNICIAN_GROUP, Constants.TECHNICIAN_ROLE);
		groupRoleMap.put(PUBLIC_GROUP, Constants.PUBLIC_ROLE);
		return groupRoleMap;
	}
}
