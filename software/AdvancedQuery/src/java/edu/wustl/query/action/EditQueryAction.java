package edu.wustl.query.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.query.bizlogic.DashboardBizLogic;
import edu.wustl.query.flex.dag.DAGConstant;
import edu.wustl.query.util.global.AQConstants;

/**
 * When the user clicks on 'edit' icon on the query dash board, this action gets called.
 * @author pooja_deshpande
 *
 */
public class EditQueryAction extends Action
{
	/**
	 *@param mapping Object of ActionMapping
	 *@param form Object of ActionForm
	 *@param request Object of HttpServletRequest
	 *@param response Object of HttpServletResponse
	 *@throws Exception Exception object
	 *@return ActionForward actionForward
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String queryId = request.getParameter("queryId");
		DashboardBizLogic bizLogic = new DashboardBizLogic();
		IParameterizedQuery query = bizLogic.getQueryById(Long.valueOf(queryId));
		HttpSession session = request.getSession();
		session.setAttribute(AQConstants.IS_SAVED_QUERY,AQConstants.TRUE);
		session.setAttribute(DAGConstant.QUERY_OBJECT,query);
		session.setAttribute(AQConstants.TREE_CHECK_VALUE, query.getShowTree());
		return mapping.findForward(edu.wustl.query.util.global.AQConstants.SUCCESS);
	}
}