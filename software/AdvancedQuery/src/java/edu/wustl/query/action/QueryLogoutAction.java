
package edu.wustl.query.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.query.util.global.AQConstants;

public class QueryLogoutAction extends SecureAction
{

	/**
	 * @param mapping object of ActionMapping
	 * @param form object of ActionForm
	 * @param request object of HttpServletRequest
	 * @param response object of HttpServletResponse
	 * @throws Exception generic exception
	 */
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		HttpSession session = request.getSession();

		//Delete Advance Query table if exists
		SessionDataBean sessionData = getSessionData(request);
		//Advance Query table name with userID attached
		String tempTableName = AQConstants.QUERY_RESULTS_TABLE + "_" + sessionData.getUserId();

		//JDBCDAO jdbcDao = (JDBCDAO)DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDao = daoFactory.getJDBCDAO();
		jdbcDao.openSession(sessionData);
		jdbcDao.deleteTable(tempTableName);
		jdbcDao.closeSession();

		session.invalidate();

		return mapping.findForward(AQConstants.SUCCESS);
	}
}
