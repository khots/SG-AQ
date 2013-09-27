/**
 *
 */

package edu.wustl.query.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.query.util.global.AQConstants;

/**
 * @author supriya_dankh
 *
 */
public class QueryMessageAction extends Action
{
	/**
	 * This method loads the UI for add limits section.
	 * This UI is the replaced with the div data with the help of Ajax script.
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
		String message = (String) request.getSession().getAttribute(
				AQConstants.NO_MAIN_OBJECT_IN_QUERY);
		StringBuffer contents = new StringBuffer();
		contents = contents.append("<li><font color='blue' family='arial,helvetica,verdana,sans-serif'>")
		.append(message).append("</font></li>");
		message = contents.toString();
		request.getSession().removeAttribute(AQConstants.NO_MAIN_OBJECT_IN_QUERY);
		response.setContentType("text/html");
		response.getWriter().write(message);
		return null;
	}
}
