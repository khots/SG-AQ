
package edu.wustl.query.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.query.actionForm.SaveQueryForm;
import edu.wustl.query.bizlogic.BizLogicFactory;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * @author chetan_patil
 * @created September 14, 2007, 9:53:15 AM
 */
public class FetchAndExecuteQueryAction extends Action
{

	/**
	 * This method is used to fetch and execute query.
	 * @param actionMapping mapping
	 * @param actionForm form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward actionForward
	 */
	public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String target = AQConstants.FAILURE;

		try
		{
			SaveQueryForm saveQueryForm = (SaveQueryForm) actionForm;
			Long queryId = saveQueryForm.getQueryId();
			IBizLogic bizLogic = BizLogicFactory.getInstance().getBizLogic
			(AQConstants.QUERY_INTERFACE_BIZLOGIC_ID);
			Object object = bizLogic.retrieve(ParameterizedQuery.class.getName(), queryId);

			if (object == null)
			{
				setActionError(request, "No result found.");
			}
			else
			{
				IParameterizedQuery parameterizedQuery = (ParameterizedQuery) object;
				HttpSession session = request.getSession();
				session.setAttribute(AQConstants.QUERY_OBJECT, parameterizedQuery);
				String errorMessage = QueryModuleUtil.executeQuery(request, parameterizedQuery);
				if ("".equals(errorMessage))
				{
					target = AQConstants.SUCCESS;
				}
				else if (errorMessage.equalsIgnoreCase(AQConstants.TREE_ND_LMT_EXCEED_REC))
				{
					target = AQConstants.TREE_ND_LMT_EXCEED_REC;
				}
				else
				{
					setActionError(request, errorMessage);
				}
			}
		}
		catch (NumberFormatException exc)
		{
			setActionError(request, "Query identifier is not valid.");
		}
		return actionMapping.findForward(target);
	}
	/**
	 * Sets error messages.
	 * @param request request object
	 * @param errorMessage error message
	 */
	private void setActionError(HttpServletRequest request, String errorMessage)
	{
		ActionErrors errors = new ActionErrors();
		ActionError error = new ActionError("errors.item", errorMessage);
		errors.add(ActionErrors.GLOBAL_ERROR, error);
		saveErrors(request, errors);
	}

}
