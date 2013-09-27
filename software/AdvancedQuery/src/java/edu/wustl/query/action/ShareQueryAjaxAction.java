package edu.wustl.query.action;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.common.dynamicextensions.ui.util.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.query.bizlogic.ShareQueryBizLogic;

/**
 * This class will be called for AJAX calls.
 *
 */
public class ShareQueryAjaxAction extends SecureAction
{

	/**
	 * @param mapping ActionMapping object
	 * @param form ActionForm object
	 * @param request HttpServletRequest object
	 * @param response HttpServletResponse object
	 * @throws Exception Exception
	 * @return ActionForward
	 */
	@Override
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String limit = request.getParameter("limit");
		String query = request.getParameter("query");
		String start = request.getParameter("start");

		Integer limitFetch = Integer.parseInt(limit);
		Integer startFetch = Integer.parseInt(start);

		JSONArray jsonArray = new JSONArray();
		JSONObject mainJsonObject = new JSONObject();

		Integer total = limitFetch + startFetch;

		List users = getUsers();

		List<NameValueBean> coordinators = new ArrayList<NameValueBean>();
		populateQuerySpecificNameValueBeansList(coordinators, users, query);
		mainJsonObject.put("totalCount", coordinators.size());
		request.setAttribute(Constants.SELECTED_VALUES, coordinators);

		for (int i = startFetch; i < total && i < coordinators.size(); i++)
		{
			JSONObject jsonObject = new JSONObject();
			Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();

			if (query == null
					|| coordinators.get(i).getName().toLowerCase(locale).contains(
							query.toLowerCase(locale)) || query.length() == 0)
			{
				jsonObject.put("id", coordinators.get(i).getValue());
				jsonObject.put("field", coordinators.get(i).getName());
				jsonArray.put(jsonObject);
			}
		}
		mainJsonObject.put("row", jsonArray);
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(mainJsonObject.toString());
		return null;
	}

	/**
	 * Returns the user list present in the system.
	 * @throws BizLogicException Biz logic exception
	 * @return users List of users
	 */
	private List getUsers() throws BizLogicException
	{
		ShareQueryBizLogic bizLogic = new ShareQueryBizLogic();
		return bizLogic.getCSMUsers();
	}

	/**
	 * This method populates name value beans list as per query,
	 * i.e. word typed into the auto-complete drop-down text field.
	 * @param querySpecificNVBeans querySpecificNVBeans
	 * @param users List of users
	 * @param query query
	 */
	private void populateQuerySpecificNameValueBeansList(List<NameValueBean> querySpecificNVBeans,
			List users, String query)
	{
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();

		for (Object obj : users)
		{
			NameValueBean nvb = (NameValueBean) obj;

			if (nvb.getName().toLowerCase(locale).contains(query.toLowerCase(locale)))
			{
				querySpecificNVBeans.add(nvb);
			}
		}
	}
}
