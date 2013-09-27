
package edu.wustl.query.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.query.flex.dag.DAGConstant;
import edu.wustl.query.util.global.AQConstants;

public class QueryAction extends Action
{

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session = request.getSession();
		session.removeAttribute(AQConstants.QUERY_OBJECT);
		session.removeAttribute(AQConstants.SELECTED_COLUMN_META_DATA);
		session.removeAttribute(AQConstants.IS_SAVED_QUERY);
		session.removeAttribute(AQConstants.IS_SIMPLE_SEARCH);
		session.removeAttribute(DAGConstant.ISREPAINT);
		session.removeAttribute(DAGConstant.TQUIMAP);
		session.removeAttribute(DAGConstant.JQUIMAP);
		session.removeAttribute(AQConstants.EXPORT_DATA_LIST);
		session.removeAttribute(AQConstants.ENTITY_IDS_MAP);
		session.removeAttribute("savedQuery");
		session.removeAttribute("savedQueryProcessed");

		//Added a Default session data bean......Need to be removed when there query will have login
		SessionDataBean sessionBean = (SessionDataBean) session
				.getAttribute(AQConstants.SESSION_DATA);
		if (sessionBean == null)
		{
			Long userId = Long.valueOf(1);
			final String ipAddress = request.getRemoteAddr();
			SessionDataBean sessionData = new SessionDataBean();
			sessionData.setUserName("admin@admin.com");
			sessionData.setIpAddress(ipAddress);
			sessionData.setUserId(userId);
			sessionData.setFirstName("admin@admin.com");
			sessionData.setLastName("admin@admin.com");
			sessionData.setAdmin(true);
			sessionData.setSecurityRequired(false);
			session.setAttribute(AQConstants.SESSION_DATA, sessionData);
		}

		return mapping.findForward(edu.wustl.query.util.global.AQConstants.SUCCESS);
	}

}
