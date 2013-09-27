
package edu.wustl.query.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import edu.wustl.query.util.global.AQConstants;

/**
 * This action is called when user clicks on Search button after forming the query object.This class loads required grid data in session/request.
 * And then it forwards control to simpleSearchDataView.jsp.
 * @author deepti_shelar
 */
public class QueryGridViewAction extends Action
{

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HttpSession session = request.getSession();
		int pageNum = AQConstants.START_PAGE;
		request.setAttribute(AQConstants.PAGE_NUMBER, Integer.toString(pageNum));
		List dataList = (List) session.getAttribute(AQConstants.PAGINATION_DATA_LIST);
		request.setAttribute(AQConstants.PAGINATION_DATA_LIST, dataList);
		List<String> columnsList = (List<String>) session
				.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
		request.setAttribute(AQConstants.SPREADSHEET_COLUMN_LIST, columnsList);
		session.setAttribute(AQConstants.PAGINATION_DATA_LIST, null);
		String pageOf = request.getParameter(AQConstants.PAGEOF);
		request.setAttribute(AQConstants.HIDE_TREE_CHECK_VALUE, request.getSession().getAttribute(AQConstants.TREE_CHECK_VALUE));
		request.setAttribute(AQConstants.PAGEOF, pageOf);
		return mapping.findForward(AQConstants.SUCCESS);
	}
}
