
/**
 *
 */

package edu.wustl.query.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.util.global.Constants;
import edu.wustl.metadata.util.DyExtnObjectCloner;
import edu.wustl.query.bizlogic.CreateQueryObjectBizLogic;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * @author chetan_patil
 * This action process the input from the UI and executes parameterized query with the input values.
 * @created September 14, 2007, 9:53:15 AM
 */
public class ExecuteQueryAction extends Action
{
	/**
	 * This method executes the saved query.
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
		ActionForward actionForward = null;

		boolean flag = true;
		cleanUpSession(request);
		HttpSession session = request.getSession();
		IParameterizedQuery query = (IParameterizedQuery) session.getAttribute(AQConstants.QUERY_OBJECT);
		IParameterizedQuery cloneQuery = new DyExtnObjectCloner().clone(query);
		String conditionstr = request.getParameter("conditionList");
		String rhsList = request.getParameter(AQConstants.STR_TO_FORM_TQ);
		request.setAttribute(AQConstants.HIDE_TREE_CHECK_VALUE, request.getSession().getAttribute(AQConstants.TREE_CHECK_VALUE));
		session.setAttribute(AQConstants.IS_SAVED_QUERY, AQConstants.TRUE);
		Map<Integer, ICustomFormula> cFIndexMap = (Map<Integer, ICustomFormula>) session
				.getAttribute(AQConstants.CUSTOM_FORMULA_INDEX_MAP);
		session.removeAttribute(AQConstants.CUSTOM_FORMULA_INDEX_MAP);
		if (conditionstr != null)
		{
			CreateQueryObjectBizLogic bizLogic = new CreateQueryObjectBizLogic();
			String errorMessage = bizLogic.setInputDataToQuery(conditionstr, cloneQuery
					.getConstraints(), null, cloneQuery);
			errorMessage += bizLogic.setInputDataToTQ(cloneQuery,
					AQConstants.EXECUTE_QUERY_PAGE, rhsList, cFIndexMap);
			if (errorMessage.trim().length() > 0)
			{
				//request.setAttribute("queryId", query.getId());
				actionForward = getActionForward(actionMapping, request, errorMessage);
				flag = false;
			}
		}
		if(flag)
		{
			session.setAttribute(AQConstants.QUERY_OBJECT, query);
			actionForward = executeQuery(actionMapping, request, cloneQuery);
		}
		return actionForward;
	}

	/**
	 * @param actionMapping
	 * @param request
	 * @param query
	 * @param errorMessage
	 * @return
	 */
	private ActionForward getActionForward(ActionMapping actionMapping, HttpServletRequest request,
			 String errorMessage)
	{
		ActionForward actionForward;
		String target;
		saveActionErrors(request, errorMessage);
		target = AQConstants.INVALID_CONDITION_VALUES;


		actionForward = actionMapping.findForward(target);
		return actionForward;
	}

	/**
	 * @param request
	 * @param errorMessage
	 */
	private void saveActionErrors(HttpServletRequest request, String errorMessage)
	{
		List<String> xssViolationErrors =
			(ArrayList<String>)request.getAttribute(Constants.VIOLATING_PROPERTY_NAMES);
		if(xssViolationErrors!=null && xssViolationErrors.size() == 1
					&& xssViolationErrors.contains("queryString"))
		{
			request.removeAttribute(Constants.VIOLATING_PROPERTY_NAMES);
		}
		ActionErrors errors = new ActionErrors();
		ActionError error = new ActionError("errors.item", errorMessage);
		errors.add(ActionErrors.GLOBAL_ERROR, error);
		saveErrors(request, errors);
	}

	/**
	 * Execute the query.
	 * @param actionMapping actionMapping
	 * @param request request
	 * @param cloneQuery cloneQuery
	 * @return actionForward
	 */
	private ActionForward executeQuery(ActionMapping actionMapping,
			HttpServletRequest request,
			IParameterizedQuery cloneQuery)
	{
		String target;// = AQConstants.FAILURE;
		ActionForward actionForward;
		String errorMessage = QueryModuleUtil.executeQuery(request, cloneQuery);  

		if (errorMessage == null || errorMessage.isEmpty()) 
		{
			target = AQConstants.SUCCESS;
			HttpSession session = request.getSession();
			session.removeAttribute(AQConstants.QUERY_WITH_FILTERS);
			request.setAttribute(AQConstants.PAGE_NUMBER, Integer.toString(1));
			List dataList = (List) session.getAttribute(AQConstants.PAGINATION_DATA_LIST);
			request.setAttribute(AQConstants.PAGINATION_DATA_LIST, dataList);		
			List<String> columnsList = (List<String>) session.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
			request.setAttribute(AQConstants.SPREADSHEET_COLUMN_LIST, columnsList);
			session.setAttribute(AQConstants.PAGINATION_DATA_LIST, null);		
			request.setAttribute(AQConstants.PAGEOF, "pageOfQueryModule");			
			Utility.setGridData(dataList, columnsList, request);			
		}
		else
		{
			target = AQConstants.FAILURE;
			saveActionErrors(request, errorMessage);
		}		
		
		actionForward =  actionMapping.findForward(target);
		return actionForward;
	}
	/**
	 * Cleans up data from session.
	 * @param request request object
	 */
	private void cleanUpSession(HttpServletRequest request)
	{
		HttpSession session = request.getSession();
		session.removeAttribute(AQConstants.SELECTED_COLUMN_META_DATA);
		session.removeAttribute(AQConstants.EXPORT_DATA_LIST);
		session.removeAttribute(AQConstants.ENTITY_IDS_MAP);
		session.removeAttribute(AQConstants.AUDIT_EVENT_ID);
	}
}
