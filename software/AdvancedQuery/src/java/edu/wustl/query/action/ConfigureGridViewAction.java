
package edu.wustl.query.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.bizlogic.DefineGridViewBizLogic;
import edu.wustl.query.bizlogic.QueryOutputSpreadsheetBizLogic;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.querysuite.QueryCSMUtil;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * When user is done with selecting the columns to be shown in results, this action is invoked.
 * This action creates meta data for selected columns and keeps it in session for further reference.
 *
 * @author deepti_shelar
 *
 */
public class ConfigureGridViewAction extends SecureAction
{
	/**
	 * creates meta data for selected columns and keeps it in session for further reference.
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward actionForward
	 */
	@SuppressWarnings("unchecked")
	protected ActionForward executeSecureAction(ActionMapping mapping,
		ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception
	{
		CategorySearchForm categorySearchForm = (CategorySearchForm) form;
		HttpSession session = request.getSession();		
		QuerySessionData querySessionData = (QuerySessionData) session.getAttribute(AQConstants.QUERY_SESSION_DATA);
		QueryDetails queryDetailsObj = new QueryDetails(session);
		IQuery query = (IQuery)session.getAttribute(AQConstants.QUERY_OBJECT);		
		query.setIsNormalizedResultQuery(categorySearchForm.getNormalizedQuery());
		SelectedColumnsMetadata selectedColumnsMetadata =
			(SelectedColumnsMetadata)session.getAttribute(AQConstants.SELECTED_COLUMN_META_DATA);
		OutputTreeDataNode currentSelectedObject = selectedColumnsMetadata.getCurrentSelectedObject();
				
		session.removeAttribute(AQConstants.EXPORT_DATA_LIST);
		session.removeAttribute(AQConstants.ENTITY_IDS_MAP);
		session.removeAttribute(AQConstants.QUERY_WITH_FILTERS);
		
		int fetchRecordSize = Integer.parseInt((String) session.getAttribute(AQConstants.FETCH_RECORD_SIZE));
		boolean hasConditionOnIdentifiedData = Utility.isConditionOnIdentifiedField(query);		
		String sql = querySessionData.getSql();
		
		Map spreadSheetDataMap = new HashMap();
		List<String> definedColumnsList = new ArrayList<String>();
		Map<String, String> specimenMap = new HashMap<String, String>();
		DefineGridViewBizLogic defineGridViewBizLogic = new DefineGridViewBizLogic();
		QueryOutputSpreadsheetBizLogic queryOutputSpreadsheetBizLogic = new QueryOutputSpreadsheetBizLogic();
		Map<Long, QueryResultObjectDataBean> queryResultObjecctDataMap = null;
		List<NameValueBean> selectedCNVBList = categorySearchForm.getSelColNVBeanList();
		String operation = request.getParameter(AQConstants.OPERATION);
		
		if (operation == null || operation.equalsIgnoreCase(AQConstants.FINISH))
		{
			selectedColumnsMetadata.setDefinedView(true);			
			defineGridViewBizLogic.getSelectedColumnsMetadata(categorySearchForm,
					queryDetailsObj, selectedColumnsMetadata, query.getConstraints());						
		}
		else if (operation.equalsIgnoreCase(AQConstants.BACK))
		{
			queryResultObjecctDataMap = populateMapForBackOperation(session, selectedColumnsMetadata, spreadSheetDataMap);
			selectedCNVBList = selectedColumnsMetadata.getSelColNVBeanList();
			categorySearchForm.setSelColNVBeanList(selectedCNVBList);
			definedColumnsList = (List<String>)session.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
			spreadSheetDataMap.put(AQConstants.SPREADSHEET_COLUMN_LIST, definedColumnsList);
			querySessionData = queryOutputSpreadsheetBizLogic.getQuerySessionData(queryDetailsObj,
					fetchRecordSize, 0, spreadSheetDataMap, sql, queryResultObjecctDataMap,
					hasConditionOnIdentifiedData, selectedColumnsMetadata);
		}
		else if (operation.equalsIgnoreCase(AQConstants.RESTORE))
		{			
			selectedColumnsMetadata.setDefinedView(false);
			defineGridViewBizLogic.getColumnsMetadataForSelectedNode(currentSelectedObject, selectedColumnsMetadata, query.getConstraints());
			selectedColumnsMetadata.setSelectedOutputAttributeList(new ArrayList<IOutputAttribute>());
			selectedCNVBList = null;			
		}
		
		if(queryResultObjecctDataMap == null) {
			queryResultObjecctDataMap = new HashMap<Long, QueryResultObjectDataBean>();
			Map<String, IOutputTerm> outputTermsColumns =
						(Map<String, IOutputTerm>)session.getAttribute(AQConstants.OUTPUT_TERMS_COLUMNS);
			String nodeData = getClickedNodeData(sql);
			StringBuffer selectedColumnNames = new StringBuffer();
			definedColumnsList = defineGridViewBizLogic.getSelectedColumnList(categorySearchForm,
					selectedColumnsMetadata.getSelectedAttributeMetaDataList(),selectedColumnNames,
					queryResultObjecctDataMap, queryDetailsObj, outputTermsColumns, nodeData, specimenMap);
			spreadSheetDataMap.put(AQConstants.SPREADSHEET_COLUMN_LIST, definedColumnsList);
			String sqlForSelectedColumns = defineGridViewBizLogic.createSQLForSelectedColumn(selectedColumnNames.toString(), sql);
			querySessionData = queryOutputSpreadsheetBizLogic.getQuerySessionData(queryDetailsObj,
					fetchRecordSize, 0, spreadSheetDataMap, sqlForSelectedColumns,
					queryResultObjecctDataMap, hasConditionOnIdentifiedData, selectedColumnsMetadata);
			spreadSheetDataMap.put(AQConstants.DEFINE_VIEW_RESULT_MAP, queryResultObjecctDataMap);
			spreadSheetDataMap.put(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP, queryResultObjecctDataMap);
			if(!operation.equalsIgnoreCase(AQConstants.RESTORE)) {
				selectedCNVBList = categorySearchForm.getSelColNVBeanList();				
			}
		}
		
		if(spreadSheetDataMap.get(AQConstants.SPREADSHEET_COLUMN_LIST) == null)
		{
			spreadSheetDataMap.put(AQConstants.SPREADSHEET_COLUMN_LIST, definedColumnsList);
		}
		QueryCSMUtil.setRequestAttr(request, specimenMap);
		selectedColumnsMetadata.setSelColNVBeanList(selectedCNVBList);
		selectedColumnsMetadata.setCurrentSelectedObject(currentSelectedObject);
		spreadSheetDataMap.put(AQConstants.QUERY_SESSION_DATA, querySessionData);
		spreadSheetDataMap.put(AQConstants.SELECTED_COLUMN_META_DATA, selectedColumnsMetadata);
		spreadSheetDataMap.put(AQConstants.MAIN_ENTITY_MAP, queryDetailsObj.getMainEntityMap());
		if(selectedColumnsMetadata.isDefinedView())
		{
			request.setAttribute(AQConstants.PAGINATION_DATA_LIST, spreadSheetDataMap.get(AQConstants.SPREADSHEET_DATA_LIST));
			//session.setAttribute(AQConstants.DENORMALIZED_LIST, spreadSheetDataMap.get(AQConstants.SPREADSHEET_DATA_LIST));
		}
		QueryModuleUtil.setGridData(request, spreadSheetDataMap);
		Utility.setGridData((List) spreadSheetDataMap.get(AQConstants.SPREADSHEET_DATA_LIST),
					(List)spreadSheetDataMap.get(AQConstants.SPREADSHEET_COLUMN_LIST), request);
		return mapping.findForward(AQConstants.SUCCESS);
	}

	/**
	 * @param session session
	 * @param selectedColumnsMetadata selectedColumnsMetadata
	 * @param spreadSheetDataMap spreadSheetDataMap
	 * @return queryResultObjecctDataMap
	 */
	@SuppressWarnings("unchecked")
	private Map<Long, QueryResultObjectDataBean> populateMapForBackOperation(
			HttpSession session,
			SelectedColumnsMetadata selectedColumnsMetadata,
			Map spreadSheetDataMap)
	{
		Map<Long, QueryResultObjectDataBean> queryResultObjecctDataMap =
			(Map<Long, QueryResultObjectDataBean>)session.getAttribute(AQConstants.DEFINE_VIEW_RESULT_MAP);
		spreadSheetDataMap.put(AQConstants.DEFINE_VIEW_RESULT_MAP, queryResultObjecctDataMap);
		if(!selectedColumnsMetadata.isDefinedView())
		{
			selectedColumnsMetadata.setDefinedView(false);
			queryResultObjecctDataMap = (Map<Long, QueryResultObjectDataBean>)
													session.getAttribute(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP);
			spreadSheetDataMap.put(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP, queryResultObjecctDataMap);
		}
		return queryResultObjecctDataMap;
	}

	/**
	 * Get the data for the node that the user clicks.
	 * @param sql query
	 * @return clickedData clickedData
	 */
	private String getClickedNodeData(String sql)
	{
		int index = sql.lastIndexOf('=');
		String clickedData = "";
		if(index != -1)
		{
			clickedData = sql.substring(index + 1, sql.length());
		}
		return clickedData;
	}
}
