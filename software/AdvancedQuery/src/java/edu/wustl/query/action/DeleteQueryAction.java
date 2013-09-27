
package edu.wustl.query.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.query.bizlogic.QueryDAO;
import edu.wustl.query.util.global.AQConstants;

/**
 * Action Handler for Deleting Saved query from database
 * @author deepti_shelar
 */
public class DeleteQueryAction extends Action
{
	/**
	 * This method deletes the query from database.
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
		String queryIdStr = request.getParameter(AQConstants.QUERYS_ID);	 
		Long queryId = Long.parseLong(queryIdStr);
		JSONObject arrayObj = new JSONObject();
		if (queryId != null)
		{
			try
			{
				QueryDAO queryDAO = new QueryDAO(); 
				queryDAO.deleteQuery(queryId);
				queryDAO.deleteQueryTagItem(queryId);				
				arrayObj.put("success", ApplicationProperties.getValue("query.deletedSuccessfully.message"));
			}
			catch (BizLogicException daoException)
			{	
				arrayObj.put("error", ApplicationProperties.getValue("query.errorondelete.message"));
				throw new BizLogicException(null, daoException, "DAOException: while deleting query");	
			}
			finally{
				response.flushBuffer();
				response.getWriter().write(arrayObj.toString());
				response.setContentType("text/html");	
				return null;	 
			}
		}
		return null;	
	}
}
