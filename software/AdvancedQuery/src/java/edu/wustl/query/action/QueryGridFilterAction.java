package edu.wustl.query.action;

import java.io.IOException;
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
import org.json.JSONArray;
import org.json.JSONObject;

import edu.common.dynamicextensions.domain.AttributeTypeInformation;
import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domain.StringAttributeTypeInformation;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.bizlogic.QueryOutputSpreadsheetBizLogic;
import edu.wustl.query.bizlogic.SpreadsheetDenormalizationBizLogic;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

public class QueryGridFilterAction extends SecureAction
{
	private static final Logger LOGGER = Logger.getCommonLogger(QueryGridFilterAction.class);
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{		
		HttpSession session = request.getSession();	
		Integer pageNum = Integer.parseInt ((String)request.getParameter(AQConstants.PAGE_NUMBER));
		Integer fetchRecordSize = Integer.parseInt((String)session.getAttribute(AQConstants.FETCH_RECORD_SIZE));
		
		request.setAttribute(AQConstants.PAGE_NUMBER, pageNum.toString());
		//session.setAttribute(AQConstants.RESULTS_PER_PAGE, recordsPerPage.toString());
		SelectedColumnsMetadata selectedColMetadata =(SelectedColumnsMetadata)session
															.getAttribute(AQConstants.SELECTED_COLUMN_META_DATA);
				
		QuerySessionData querySessionData = (QuerySessionData) session.getAttribute(Constants.QUERY_SESSION_DATA);
		QueryDetails queryDetailsObj = new QueryDetails(session);
		String oldquery = querySessionData.getSql();
		querySessionData.setSql(getQuery(session, request, oldquery,queryDetailsObj));
				
		boolean isContPresent = new QueryOutputSpreadsheetBizLogic().isContainmentPresent(queryDetailsObj.getQuery());;		
		List dataList = new ArrayList();
		try{
			dataList = Utility.getPaginationDataList(request, getSessionData(request), 
					fetchRecordSize, pageNum, querySessionData, selectedColMetadata.isDefinedView());
			
			/*if(isContPresent && queryDetailsObj.getQuery().getConstraints().size() != 1 
					&& !queryDetailsObj.getQuery().getIsNormalizedResultQuery())
			{
				SpreadsheetDenormalizationBizLogic  denormalizationBizLogic = new SpreadsheetDenormalizationBizLogic();
				Map<String,Object> exportDetailsMap = denormalizationBizLogic.scanIQuery
															(queryDetailsObj, dataList, selectedColMetadata, querySessionData);
				if(!exportDetailsMap.isEmpty())
				{
					dataList = (List<List<String>>)exportDetailsMap.get("dataList");
					List<String>colList = (List<String>)exportDetailsMap.get("headerList");
					session.setAttribute(AQConstants.SPREADSHEET_COLUMN_LIST, colList);
				}
			}*/
		} catch(Exception e){
			LOGGER.error("Exception while executing query: ", e);
		}
		//session.setAttribute(AQConstants.DENORMALIZED_LIST, dataList);
		session.setAttribute(AQConstants.TOTAL_RESULTS, querySessionData.getTotalNumberOfRecords());
		List<String> columnList = (List<String>) session.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
		String json = Utility.getGridDataJson(dataList, columnList, request);
		response.getWriter().write(json);
		
		// set original query
		querySessionData.setSql(oldquery);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private String getQuery(HttpSession session, HttpServletRequest request, String oldquery, QueryDetails queryDetailsObj)
	{		
		StringBuilder query = new StringBuilder();
		Map<String, String> columnNameVsAliasMap = queryDetailsObj.getColumnNameVsAliasMap();
		query.append(oldquery.substring(0, oldquery.lastIndexOf("where") + 6));
		try {
			String jsonString = (String) request.getParameter("Data");			
			JSONObject jsonObj = new JSONObject(jsonString);
			JSONArray columns = jsonObj.getJSONArray("columns");
			JSONArray values = jsonObj.getJSONArray("values");
			String sortColumn = jsonObj.getString("sortColumn");
			String sortDir = jsonObj.getString("sortDir");
			
			Map<String, String> paramsMap = new HashMap<String, String>();
			
			for(int i = 0; i < columns.length(); i++) {
				paramsMap.put(columns.getString(i).split("_")[0], values.getString(i));
			}
			
			List<OutputTreeDataNode> rootOutputTreeNodeList = (List<OutputTreeDataNode>) session
								.getAttribute(AQConstants.SAVE_TREE_NODE_LIST);
			List<QueryOutputTreeAttributeMetadata> attributes = new ArrayList<QueryOutputTreeAttributeMetadata>();
			
			for(OutputTreeDataNode node: rootOutputTreeNodeList) { 
				attributes.addAll(node.getAttributes());
				for(OutputTreeDataNode child: QueryModuleUtil.getInViewChildren(node)) {
					attributes.addAll(child.getAttributes());
				}
			}
			
			String orderBy = "";
			
			for(QueryOutputTreeAttributeMetadata attr: attributes){
				AttributeTypeInformation type = (AttributeTypeInformation) attr.getAttribute().getAttributeTypeInformation();
				String value = paramsMap.get(attr.getDisplayName());
				if(value != null && !value.equals("")){  
					if(type instanceof StringAttributeTypeInformation) {
						query.append("UPPER(").append(columnNameVsAliasMap.get(attr.getColumnName())).append(") LIKE '%")
							.append(value.toUpperCase()).append("%' and ");						
					} else if(type instanceof BooleanAttributeTypeInformation){ 						
						if(!value.matches("[0-9]+")){
							value = "true".contains(value.toLowerCase())? "true": value ;
							value = "false".contains(value.toLowerCase())? "false": value ;
						}
						
						query.append(columnNameVsAliasMap.get(attr.getColumnName())).append(" = ")
							.append(value).append(" and ");						
					} 
				}
				
				if(attr.getDisplayName().equalsIgnoreCase(sortColumn)){
					orderBy = columnNameVsAliasMap.get(attr.getColumnName()) + " " + sortDir + ", ";
				}
			}	
			
			query.append(oldquery.substring(oldquery.lastIndexOf("where")+ 5));		
			if(!orderBy.equals("")) {
				int index = query.lastIndexOf("ORDER BY");
				query.insert(index + 9, orderBy);
			}
			session.setAttribute(AQConstants.QUERY_WITH_FILTERS, query.toString());
			LOGGER.out.error("New query : "+ query.toString());
		} catch(Exception e ){
			LOGGER.out.error("Exception found while creating query :", e);
		}
		return query.toString();
	}	
}
