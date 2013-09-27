
package edu.wustl.query.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Utility;

/**
 * This is a action called when user clicks on search button from addlimits .jsp. The result data lists are already
 * stored in session through an applet action ViewSearchResultsAction. This class just forwards the control to ViewSearchResults.jsp.
 * @author deepti_shelar
 *
 */
public class ViewSearchResultsJSPAction extends Action
{

	/**
	 * This method loads the data required for ViewSearchResults.jsp
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward actionForward
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String target = AQConstants.SUCCESS;
		CategorySearchForm actionForm = (CategorySearchForm) form;
		String nextOperation = actionForm.getNextOperation();
		if (nextOperation != null && nextOperation.equalsIgnoreCase(AQConstants.SHOW_ERROR_PAGE))
		{
			target = AQConstants.FAILURE;
		}
		
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
		return mapping.findForward(target);
	}
}
