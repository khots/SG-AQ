
package edu.wustl.query.util.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.util.global.AQConstants;

public class QueryCoreSessionListener implements HttpSessionListener
{
	private static org.apache.log4j.Logger logger = LoggerConfig.getConfiguredLogger(QueryCoreSessionListener.class);
	/**
	 * This method is called when session is created.
	 */
	public void sessionCreated(HttpSessionEvent arg0)
	{

	}
	/**
	 * this method is called when session is destroyed.
	 */
	public void sessionDestroyed(HttpSessionEvent arg0)
	{
		HttpSession session = arg0.getSession();
		SessionDataBean sessionData = (SessionDataBean) session
				.getAttribute(AQConstants.SESSION_DATA);

		if (sessionData != null)
		{
			cleanUp(sessionData, (String) session.getAttribute(AQConstants.RANDOM_NUMBER));
		}

		// To remove PrivilegeCache from the session, requires user LoginName
		// Singleton instance of PrivilegeManager

		/**
		 * Commented out by Baljeet
		 */
		/*if(sessionData != null)
		{
			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			privilegeManager.removePrivilegeCache(sessionData.getUserName());
		}*/
	}
	/**
	 * Deletes temporary tables created.
	 * @param sessionData session data
	 * @param randomNumber random number
	 */
	private void cleanUp(SessionDataBean sessionData, String randomNumber)
	{
		String appName=CommonServiceLocator.getInstance().getAppName();
		String tempTableNameForQuery = AQConstants.TEMP_OUPUT_TREE_TABLE_NAME
				+ sessionData.getUserId() + randomNumber;
		try
		{
		    IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
            JDBCDAO jdbcDao = daoFactory.getJDBCDAO();
			jdbcDao.openSession(sessionData);
			jdbcDao.deleteTable(tempTableNameForQuery);
			jdbcDao.closeSession();
		}
		catch (DAOException ex)
		{
			logger.error(ex.getMessage());
		}
	}
}
