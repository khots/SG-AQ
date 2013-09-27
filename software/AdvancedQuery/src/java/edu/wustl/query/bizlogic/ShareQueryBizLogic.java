package edu.wustl.query.bizlogic;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.query.actionForm.SaveQueryForm;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.SecurityManagerFactory;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShareQueryBizLogic
{
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(ShareQueryBizLogic.class);
	public List<User> getUsers()
	{
		List<User> users = null;
		try
		{
			users = SecurityManagerFactory.getSecurityManager().getUsers();
		}
		catch (SMException e)
		{
			logger.error(e.getMessage(), e);
		}
		return users;
	}
	/**
	 * Returns the list of NameValueBeans with name as "LastName,FirstName"
	 * and value as systemtIdentifier, of all users who are not disabled.
	 * @return the list of NameValueBeans with name as "LastName,FirstName"
	 * and value as systemtIdentifier, of all users who are not disabled.
	 * @throws BizLogicException BizLogic exception
	 */
	public List getCSMUsers() throws BizLogicException
	{
		//Retrieve the users whose activity status is not disabled.
		List users;
		try
		{
			users = SecurityManagerFactory.getSecurityManager().getUsers();
		}
		catch (SMException e)
		{
			throw new BizLogicException(null,e,"Exception in getting users through SecurityManager");
		}

		List nameValuePairs = new ArrayList();
		nameValuePairs.add(new NameValueBean(Constants.SELECT_OPTION, String
				.valueOf(Constants.SELECT_OPTION_VALUE)));

		// If the list of users retrieved is not empty.
		if (!users.isEmpty())
		{
			// Creating name value beans.
			for (int i = 0; i < users.size(); i++)
			{
				gov.nih.nci.security.authorization.domainobjects.User user =
				(gov.nih.nci.security.authorization.domainobjects.User) users.get(i);
				NameValueBean nameValueBean = new NameValueBean(); // NOPMD - Instantiating NameValueBean in loop
				nameValueBean.setName(user.getLastName() + ", " + user.getFirstName());
				nameValueBean.setValue(String.valueOf(user.getUserId()));
				nameValuePairs.add(nameValueBean);
			}
		}
		Collections.sort(nameValuePairs);
		return nameValuePairs;
	}

	/**
	 * Get selected users.
	 * @param savedQueryForm savedQueryForm
	 * @return coordinators
	 */
	public List<NameValueBean> getSelectedUsers(SaveQueryForm savedQueryForm) throws BizLogicException
	{
		List<NameValueBean> coordinators = new ArrayList<NameValueBean>();
		long[] protocolCoordIds = savedQueryForm.getProtocolCoordinatorIds();
		List users;
		users = getCSMUsers();
		if (protocolCoordIds != null && protocolCoordIds.length > 0)
		{
			List<Long> prtCordIds = new ArrayList<Long>();

			addProtocolIds(protocolCoordIds, prtCordIds);
			populateCoordinatorList(coordinators, users, prtCordIds);
		}
		return coordinators;
	}

	/**
	 * @param protocolCoordIds protocol coordinator id's
	 * @param prtCordIds array
	 */
	private void addProtocolIds(long[] protocolCoordIds, List<Long> prtCordIds)
	{
		for (int i = 0; i < protocolCoordIds.length; i++)
		{
			prtCordIds.add(protocolCoordIds[i]);
		}
	}

	/**
	 * @param coordinators coordinators
	 * @param users users
	 * @param prtCordIds protocol coordinator id's
	 */
	private void populateCoordinatorList(List<NameValueBean> coordinators,
			List users, List<Long> prtCordIds)
	{
		for (Object object : users)
		{
			NameValueBean nameValueBean = (NameValueBean) object;
			if (prtCordIds.contains(Long.parseLong(nameValueBean.getValue())))
			{
				coordinators.add(nameValueBean);
			}
		}
	}
}
