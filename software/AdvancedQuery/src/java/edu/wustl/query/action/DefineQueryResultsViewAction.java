
package edu.wustl.query.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.QueryTreeNodeData;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.bizlogic.DefineGridViewBizLogic;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryDetails;

/**
 * This action is invoked when user clicks on Define View button from the results screen. This will open a page where user can select the attributes
 * for which he / she wants to see records.
 *
 * @author deepti_shelar
 */
public class DefineQueryResultsViewAction extends Action
{

	/**
	 * This method loads DefineGridResultsView jsp.This code forms a tree which contains all class names present in query
	 * and all its attributes , from which user can select desired attributes to be shown in grid.
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
		CategorySearchForm searchForm = (CategorySearchForm) form;

		HttpSession session = request.getSession();
		QueryDetails queryDetailsObj = new QueryDetails(session);
		SelectedColumnsMetadata selColMetadata = (SelectedColumnsMetadata) session
				.getAttribute(AQConstants.SELECTED_COLUMN_META_DATA);
		List<NameValueBean> prevSelColNVBList = selColMetadata
				.getSelColNVBeanList();
		if (!selColMetadata.isDefinedView())
		{
			prevSelColNVBList = null;
		}
		OutputTreeDataNode curSelObj = selColMetadata
				.getCurrentSelectedObject();
		request.setAttribute(AQConstants.categorySearchForm, searchForm);

		//Map<Long,OutputTreeDataNode> uniqueIdNodesMap = (Map<Long,OutputTreeDataNode>) session.getAttribute(Constants.ID_NODES_MAP);
		List<QueryTreeNodeData> treeDataVector = new ArrayList<QueryTreeNodeData>();
		DefineGridViewBizLogic defineGridViewBL = new DefineGridViewBizLogic();
		defineGridViewBL.createTree(searchForm, queryDetailsObj, treeDataVector,
				curSelObj, prevSelColNVBList);
		List<NameValueBean> selectedCNVBList = searchForm
				.getSelColNVBeanList();
		searchForm.setNormalizedQuery(queryDetailsObj.getQuery().getIsNormalizedResultQuery());
		selColMetadata.setSelColNVBeanList(selectedCNVBList);
		session.setAttribute(AQConstants.SELECTED_COLUMN_META_DATA, selColMetadata);
		session.setAttribute(AQConstants.SEL_COL_NVB_LST,
				selectedCNVBList);
		session.setAttribute(AQConstants.TREE_DATA, treeDataVector);
		request.setAttribute("isSpecPresent", request.getParameter("isSpecPresent"));
		return mapping.findForward(AQConstants.SUCCESS);
	}
}
