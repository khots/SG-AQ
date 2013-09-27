/*
package edu.wustl.query.action;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.actionForm.QueryLoginForm;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.security.global.ProvisionManager;
import edu.wustl.security.global.Roles;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import edu.wustl.security.privilege.PrivilegeManager;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.authorization.domainobjects.User;

public class QueryLoginAction extends Action
{

	*//**
	 * Overrides the execute method of Action class.
	 * Initializes the various drop down fields in Institute.jsp Page.
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws IOException I/O exception
	 * @throws ServletException servlet exception
	 * @return value for ActionForward object
	 *//*
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException
	{
		if (form == null)
		{
			Logger.out.debug("Form is Null");
			return (mapping.findForward(AQConstants.FAILURE));
		}

		HttpSession prevSession = request.getSession();
		if (prevSession != null)
			prevSession.invalidate();

		QueryLoginForm loginForm = (QueryLoginForm) form;
		Logger.out.info("Inside Login Action, Just before validation");

		String loginName = loginForm.getLoginName();
		String password = loginForm.getPassword();

		try
		{
			//	boolean loginOK = SecurityManager.getInstance(LoginAction.class).login(loginName, password);
			ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
			boolean loginOK = securityManager.login(loginName, password);

			if (loginOK)
			{
				boolean isAdminUser = false;
				boolean isSecureExecute = true;
				UserProvisioningManager userProvisioningManager = ProvisionManager.getInstance()
						.getUserProvisioningManager();
				//UserProvisioningManager userProvisioningManager = SecurityManager.getInstance(LoginAction.class).getUserProvisioningManager();
				User csmUser = userProvisioningManager.getUser(loginName);

				//	        	DefaultBizLogic defaultBizLogic = new DefaultBizLogic();
				//            	defaultBizLogic.cachePrivileges(loginName);

				PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
				privilegeManager.getPrivilegeCache(loginName);

				Set groups = userProvisioningManager.getGroups(String.valueOf(csmUser.getUserId()));
				gov.nih.nci.security.authorization.domainobjects.Role role = SecurityManagerFactory
						.getSecurityManager().getUserRole(csmUser.getUserId());
				String userRole = role.getName();

				//SecurityManager.getInstance(LoginAction.class).getUserGroup(csmUser.getUserId());
				String ipAddress = request.getRemoteAddr();
				if (userRole.equalsIgnoreCase(edu.wustl.security.global.Constants.ADMINISTRATOR))
				{
					isAdminUser = true;
				}

				if (userRole.equalsIgnoreCase(Roles.ADMINISTRATOR)
						|| userRole.equalsIgnoreCase(Roles.SUPERVISOR))
				{
					isSecureExecute = false;
				}

				SessionDataBean sessionData = new SessionDataBean();
				sessionData.setUserName(loginName);
				sessionData.setIpAddress(ipAddress);
				sessionData.setUserId(csmUser.getUserId());
				sessionData.setCsmUserId(csmUser.getUserId().toString());
				sessionData.setFirstName(csmUser.getFirstName());
				sessionData.setLastName(csmUser.getLastName());
				sessionData.setSecurityRequired(isSecureExecute);
				sessionData.setAdmin(isAdminUser);

				HttpSession session = request.getSession(true);
				session.setAttribute(AQConstants.SESSION_DATA, sessionData);

				return mapping.findForward(AQConstants.SUCCESS);
			}
			else
			{
				handleError(request, "errors.incorrectLoginNamePassword");
				return mapping.findForward(AQConstants.FAILURE);
			}
		}
		catch (Exception e)
		{
			Logger.out.info("Exception: " + e.getMessage(), e);
			handleError(request, "errors.incorrectLoginNamePassword");
			return (mapping.findForward(AQConstants.FAILURE));
		}
	}

	private void handleError(HttpServletRequest request, String errorKey)
	{
		ActionErrors errors = new ActionErrors();
		errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(errorKey));
		//Report any errors we have discovered
		if (!errors.isEmpty())
		{
			saveErrors(request, errors);
		}
	}
}
*/