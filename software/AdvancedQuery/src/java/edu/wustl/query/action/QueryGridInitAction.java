package edu.wustl.query.action;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.tags.domain.TagItem;
import edu.wustl.common.velocity.VelocityManager;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.bizlogic.QueryDAO;
import edu.wustl.query.bizlogic.DashboardBizLogic;
import edu.wustl.query.bizlogic.QueryTagBizLogic;
import edu.wustl.query.dto.QueryDTO;
import edu.wustl.query.util.global.AQConstants;

public class QueryGridInitAction extends Action { 
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String pageOf = (String) request.getParameter(AQConstants.PAGE_OF);
		SessionDataBean sessionDataBean = (SessionDataBean) request.getSession().getAttribute(
				edu.wustl.common.util.global.Constants.SESSION_DATA);
		String tagId = (String) request.getParameter(AQConstants.TAGID_STRING);
		
		if (pageOf == null && tagId == null ){
			pageOf = "myQueries";	
		} else if (tagId != null){
			pageOf = "tagQueries";	
		}
		
		Collection<QueryDTO> queries = getQueries(pageOf, tagId, sessionDataBean);
		
		Map<String, Object> gridParams = new HashMap<String, Object>();
		gridParams.put("queryList", queries);
		gridParams.put("queryPageOf", pageOf);
		String responseString = VelocityManager.getInstance().evaluate(gridParams, "privilegeQueryGridTemplate.vm");
		response.getWriter().write(responseString);
		response.setContentType("text/xml");
		
		return null;
	}
	
	/**
	 * Gets the appropriate list of queries as per the request.
	 *
	 * @param pageOf page of
	 * @param sessionDataBean the session data bean
	 *
	 * @return queries collection
	 * @throws BizLogicException
	 */
	private Collection<QueryDTO> getQueries(String pageOf, String tagId, SessionDataBean sessionDataBean) 
	throws DAOException, BizLogicException
	{
		Collection<QueryDTO> queries = null;
		DashboardBizLogic dashboardBizLogic = new DashboardBizLogic();
		if (tagId != null) {
			queries = getQueriesByTag(tagId);
		} else if ("allQueries".equals(pageOf)) {
			queries = dashboardBizLogic.getAllQueries(sessionDataBean.getCsmUserId(), sessionDataBean.getUserName());
		} else if ("sharedQueries".equals(pageOf)) {
			queries = dashboardBizLogic.getSharedQueries(sessionDataBean.getUserName());
		} else if ("myQueries".equals(pageOf)) {
			queries = dashboardBizLogic.getMyQueries(sessionDataBean.getCsmUserId());
		}

		return queries;
	}
	
	private List<QueryDTO> getQueriesByTag(String tagIdStr)
	throws DAOException, BizLogicException
	{
		long tagId = Long.parseLong(tagIdStr);
		QueryTagBizLogic queryTagBizLogic= new QueryTagBizLogic();
		List<Long> queryIds = queryTagBizLogic.getQueryIds(tagId);
		List<QueryDTO> queryDTOList = new ArrayList<QueryDTO>();
		if (!queryIds.isEmpty()) {
			QueryDAO queryDAO = new QueryDAO();	
			queryDTOList = queryDAO.getQueriesById(queryIds);
		}
		return queryDTOList;
	}
}
