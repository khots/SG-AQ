
package edu.wustl.query.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.bizlogic.ValidateQueryBizLogic;
import edu.wustl.query.util.global.AQConstants;

/**
 * When the user searches or saves a query , the query is checked for the conditions like DAG should not be empty , is there
 * at least one node in view on define view page and does the query contain the main object. If all the conditions are satisfied
 * further process is done else corresponding error message is shown.
 *
 * @author shrutika_chintal
 *
 */
public class ValidateQueryAction extends Action
{

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		String buttonClicked = request.getParameter(AQConstants.BUTTON_CLICKED);
		String dataKey = request.getParameter(AQConstants.UPDATE_SESSION_DATA);
		HttpSession session = request.getSession();
		if (dataKey != null && dataKey.equals(AQConstants.UPDATE_SESSION_DATA))
		{
			String message = (String) session
					.getAttribute(AQConstants.VALIDATE_MSG_ORDERING);
			String isListEmpty = (String) session.getAttribute(AQConstants.IS_LIST_EMPTY);

			message = setBlankMessage(message, isListEmpty);
			response.setContentType("text/html");
			response.getWriter().write(message);
		}
		else
		{
			IParameterizedQuery query = (IParameterizedQuery) session
			.getAttribute(AQConstants.QUERY_OBJECT);
			ValidateQueryBizLogic vBizLogic = new ValidateQueryBizLogic();
			String validationMessage = vBizLogic.getValidationMessage(request,
					query);
			updateResponse(response, buttonClicked, validationMessage);
		}
		request.getSession().setAttribute("showTree", ((CategorySearchForm) form)
				.isShowTree());
		return null;
	}

	/**
	 * @param message message
	 * @param isListEmpty to determine if list is empty
	 * @return message
	 */
	private String setBlankMessage(String message, String isListEmpty)
	{
		String blankMessage = message;
		if ((isListEmpty != null && isListEmpty.equals(AQConstants.FALSE)) || message == null)
		{
			blankMessage = " "; //if empty string is returned mac+safari gives problem and if message is set to null mozilla gives problem.
		}
		return blankMessage;
	}

	/**
	 * @param response response
	 * @param buttonClicked if button is clicked
	 * @param validationMessage message
	 * @throws IOException IOException
	 */
	private void updateResponse(HttpServletResponse response,
			String buttonClicked, String validationMessage) throws IOException
	{
		if(validationMessage == null)
		{
			response.getWriter().write(buttonClicked);
		}
		else
		{
			response.setContentType("text/html");
			response.getWriter().write(validationMessage);
		}
	}
}
