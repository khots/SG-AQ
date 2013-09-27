
package edu.wustl.query.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONArray;
import org.json.JSONException;

import edu.wustl.common.action.SecureAction;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.util.ExportReport;
import edu.wustl.common.util.SendFile;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.actionForm.QueryAdvanceSearchForm;
import edu.wustl.query.bizlogic.ExportQueryBizLogic;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.querysuite.QueryDetails;

public class SpreadsheetExportAction extends SecureAction
{
	/**
	 * This class exports the data of a spreadsheet to a file.
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward object
	 */
	
	private ExportReport report;
	
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		HttpSession session = request.getSession();
		
		List<List<String>> exportList = new ArrayList<List<String>>();
		if(session.getAttribute(AQConstants.SELECTED_COLUMN_META_DATA) != null)
		{
			exportList = getExportList(session);
		}
				
		//Extracting map from form bean which gives the serial numbers of selected rows
		List<String> idIndexList = new ArrayList<String>();
		Map<Integer, List<String>> entityIdsMap = (Map<Integer, List<String>>) session
		.getAttribute(AQConstants.ENTITY_IDS_MAP);

		generateSpreadsheetData(form, response, request, session, exportList, idIndexList,
				entityIdsMap);
		

		return null;
	}

	/**
	 * @param form
	 * @param response 
	 * @param request
	 * @param session
	 * @param exportList 
	 * @param idIndexList
	 * @param entityIdsMap
	 * @throws DAOException
	 * @throws MultipleRootsException
	 * @throws IOException 
	 */
	protected void generateSpreadsheetData(ActionForm form, HttpServletResponse response, 
			HttpServletRequest request, HttpSession session, List<List<String>> exportList, 
			List<String> idIndexList, Map<Integer, List<String>> entityIdsMap)
		throws DAOException, MultipleRootsException, IOException 
	{
		String isChkAllAcrossAll = request.getParameter(AQConstants.CHECK_ALL_ACROSS_ALL_PAGES);
	
		List<String> columnList = (List<String>) session.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
		SelectedColumnsMetadata selectedColumnsMetadata =
			(SelectedColumnsMetadata)session.getAttribute(AQConstants.SELECTED_COLUMN_META_DATA);
		QueryDetails queryDetails = new QueryDetails(session);
	
		//List<List<String>> dataList = getDataList(request,session,isChkAllAcrossAll,selectedColumnsMetadata, queryDetails);
		
		int actualNoOfRec = 0;
		boolean isDefinedView = false;
		if(selectedColumnsMetadata != null)
		{
			actualNoOfRec = selectedColumnsMetadata.getActualTotalRecords();
			isDefinedView = selectedColumnsMetadata.isDefinedView();
		}
		String pageNo = request.getParameter(AQConstants.PAGE_NUMBER);
		if (pageNo != null)
		{
			request.setAttribute(AQConstants.PAGE_NUMBER, pageNo);
		}
		int pageNum = Integer.valueOf(pageNo);
		int recordsPerPage;
		if (isChkAllAcrossAll != null
				&& isChkAllAcrossAll.equalsIgnoreCase("true"))
		{
			if(actualNoOfRec == 0)
			{
				String totalRecords = (String) session.getAttribute(AQConstants.FETCH_RECORD_SIZE);
				recordsPerPage = Integer.parseInt(totalRecords);
				pageNum = 1;
			}
			else
			{
				recordsPerPage = actualNoOfRec; 
			}
		}
		else
		{
			if(actualNoOfRec ==0)
			{
			    String recordsPerPageStr = (String) session.getAttribute(AQConstants.RESULTS_PER_PAGE);
				recordsPerPage= Integer.parseInt(recordsPerPageStr); ;
			}
			else
			{
				QuerySessionData querySessionData = (QuerySessionData)session.getAttribute(AQConstants.QUERY_SESSION_DATA);
				recordsPerPage = querySessionData.getRecordsPerPage();
			}
		}
		QuerySessionData querySessionData = (QuerySessionData) session 
				.getAttribute(edu.wustl.common.util.global.Constants.QUERY_SESSION_DATA);
		
		if(session.getAttribute(AQConstants.QUERY_WITH_FILTERS) != null) {
			querySessionData.setSql((String)session.getAttribute(AQConstants.QUERY_WITH_FILTERS));
		}
		
		session.setAttribute("COLUMNS_LIST", columnList);	
		
		//boolean isDefineView = false;
		/*if(queryDetails.getQuery()!=null && !queryDetails.getQuery().getIsNormalizedResultQuery() && selectedColumnsMetadata != null)
		{
			IExpression rootExpression = queryDetails.getQuery().getConstraints().getJoinGraph().getRoot();
			if(!queryDetails.getQuery().getConstraints().getJoinGraph().getChildrenList(rootExpression).isEmpty())
			{
				isDefineView = true;
				if(isChkAllAcrossAll != null && isChkAllAcrossAll.equalsIgnoreCase("true"))
				{
					QuerySessionData querySessionData = (QuerySessionData)session.getAttribute(AQConstants.QUERY_SESSION_DATA);
					querySessionData.setRecordsPerPage((Integer) session.getAttribute(AQConstants.TOTAL_RESULTS));
					SpreadsheetDenormalizationBizLogic  denormalizationBizLogic =
						new SpreadsheetDenormalizationBizLogic();
					Map<String,Object> exportDetailsMap = denormalizationBizLogic.scanIQuery
					(queryDetails, dataList, selectedColumnsMetadata, querySessionData);
					dataList = (List<List<String>>)exportDetailsMap.get("dataList");
					columnList = (List<String>)exportDetailsMap.get("headerList");
				}
				else
				{
					List<List<String>> finalDataList = new ArrayList<List<String>>();
					dataList = (List<List<String>>) session.getAttribute(AQConstants.DENORMALIZED_LIST);
					for (int counter = 0; counter < obj.length; counter++)
					{
						int indexOf = obj[counter].toString().indexOf("_") + 1;
						int index = Integer.parseInt(obj[counter].toString().substring(indexOf));
						List<String> list = dataList.get(index);
						finalDataList.add(list);
					}
					dataList = finalDataList;
				}
			}
		}
		if(!isDefineView)
		{
			List tmpColumnList = new ArrayList();
			List tmpDataList = populateTemporaryList(columnList, dataList,tmpColumnList);
			columnList = tmpColumnList;
			dataList = tmpDataList;
		}*/
		//Mandar 06-Apr-06 Bugid:1165 : Extra ID columns end. Adding first row(column names) to exportData
		
		
		if(isChkAllAcrossAll != null && isChkAllAcrossAll.equalsIgnoreCase("true"))
		{
			QuerySessionData clone = createCloneQuerySessionData(querySessionData);
			modifySql(clone, queryDetails, session);
			int dataSize = recordsPerPage;
			int pageNumber = 1; 
			while(dataSize == recordsPerPage)
			{
				List<List<String>> dataList = new ArrayList<List<String>>();
				List<List<String>> subExportList = new ArrayList<List<String>>();
				dataList =	Utility.getPaginationDataList(request, getSessionData(request),
						recordsPerPage, pageNumber, querySessionData, isDefinedView);
				dataSize = dataList.size();				
				pageNumber ++;		

				dataList = Utility.getFormattedOutput(dataList);
				subExportList = addDataToExportList(form, isChkAllAcrossAll,subExportList, 
						columnList, dataList, idIndexList, entityIdsMap);
				
				exportAndSend(request, response, session, subExportList, idIndexList, entityIdsMap, !(dataSize == recordsPerPage));
			}
		} else {
			//List<List<String>> dataList = new ArrayList<List<String>>();
			/*dataList =	Utility.getPaginationDataList(request, getSessionData(request),
					recordsPerPage, pageNum, querySessionData, isDefinedView);
			exportList = addDataToExportList(form, isChkAllAcrossAll,exportList, 
					columnList, dataList, idIndexList, entityIdsMap);*/
			try{
				exportList.add(columnList);
				exportList.addAll(parseJson(request));
				exportAndSend(request, response, session, exportList, idIndexList, entityIdsMap, true);
			}catch(Exception e){
				
			}
		}
		
	}
	
	private List<List<String>> addDataToExportList(ActionForm form, String isChkAllAcrossAll,
			List<List<String>> exportList, List<String> columnList, List<List<String>> dataList, 
			List<String> idIndexList, Map<Integer, List<String>> entityIdsMap)
	{
		QueryAdvanceSearchForm searchForm = (QueryAdvanceSearchForm) form;
		Map map = searchForm.getValues();
		Object[] obj = map.keySet().toArray();
		boolean isDefineView = false;
		exportList.add(columnList);
		int columnsSize = columnList.size();

		if (isDefineView || (isChkAllAcrossAll != null && isChkAllAcrossAll.equalsIgnoreCase("true")))
		{
			for (int counter = 0; counter < dataList.size(); counter++)
			{
				List<String> list = dataList.get(counter);
				if(isDefineView)
				{
					exportList.add(list);
				}
				else
				{
					List<String> subList = list.subList(0, columnsSize);
					exportList.add(subList);
				}
				populateIndexList(idIndexList, entityIdsMap, counter);
			}
		}
		else
		{
			for (int counter = 0; counter < obj.length; counter++)
			{
				String index = obj[counter].toString().split("_")[1];
				if(index.length() > 2){
					index = index.substring(1);
				}				
				int id = Integer.parseInt(index);
				List<String> list = dataList.get(id);
				List<String> subList = list.subList(0, columnsSize);
				populateIndexList(idIndexList, entityIdsMap, id);
				exportList.add(subList);
			}

		}
		return exportList;
	}

	/**
	 * @param session session
	 * @return exportList
	 * @throws MultipleRootsException MultipleRootsException
	 */
	protected List<List<String>> getExportList(HttpSession session)
			throws MultipleRootsException
	{
		IParameterizedQuery query = (IParameterizedQuery) session.getAttribute(AQConstants.QUERY_OBJECT);
		List<List<String>> exportList = new ArrayList<List<String>>();
		if(query != null)
		{
			ExportQueryBizLogic exportBizLogic = new ExportQueryBizLogic();
			exportBizLogic.exportDetails(query,exportList);
		}
		return exportList;
	}

	/**
	 * @param idIndexList index list
	 * @param entityIdsMap map
	 * @param counter counter
	 */
	private void populateIndexList(List<String> idIndexList,
			Map<Integer, List<String>> entityIdsMap, int counter)
	{
		if (entityIdsMap != null && !entityIdsMap.isEmpty())
		{
			List<String> entityIdList = entityIdsMap.get(counter);
			idIndexList.addAll(entityIdList);
		}
	}

	/**
	 * @param columnList column List
	 * @param dataList dataList
	 * @param tmpColumnList column list
	 * @return tmpDataList
	 */
	private List populateTemporaryList(List<String> columnList,
			List<List<String>> dataList, List tmpColumnList)
	{
		int idColCount = setIdColumnCount(columnList);
		// remove ID columns
		for (int cnt = 0; cnt < (columnList.size() - idColCount); cnt++)
		{
			tmpColumnList.add(columnList.get(cnt));
		}
		// datalist filtration for ID data.
		List tmpDataList = new ArrayList();
		for (int dataListCnt = 0; dataListCnt < dataList.size(); dataListCnt++)
		{
			List tmpList = dataList.get(dataListCnt);
			List tmpNewList = new ArrayList();
			for (int cnt = 0; cnt < (tmpList.size() - idColCount); cnt++)
			{
				tmpNewList.add(tmpList.get(cnt));
			}
			tmpDataList.add(tmpNewList);
		}
		return tmpDataList;
	}

	/**
	 * @param columnList column List
	 * @return idColCount
	 */
	private int setIdColumnCount(List<String> columnList)
	{
		int idColCount = 0;
		for (int cnt = 0; cnt < columnList.size(); cnt++)
		{
			String columnName = columnList.get(cnt);
			Logger.out.debug(columnName + " : " + columnName.length());
			if (columnName.trim().equalsIgnoreCase("ID"))
			{
				idColCount++;
			}
		}
		return idColCount;
	}
	/**
	 *
	 * @param response response
	 * @param session session
	 * @param exportList exportList
	 * @param idIndexList idIndexList
	 * @param entityIdsMap entityIdsMap
	 * @throws IOException IOException
	 */
	private void exportAndSend(HttpServletRequest request,HttpServletResponse response,
			HttpSession session, List<List<String>> exportList,
			List<String> idIndexList, Map<Integer, List<String>> entityIdsMap, boolean downloadFile)
			throws IOException
	{		
		String appName = CommonServiceLocator.getInstance().getAppHome();
		String path = appName + System.getProperty("file.separator");
		String csvfileName = path + AQConstants.SEARCH_RESULT; 
		String zipFileName = path + session.getId() + AQConstants.ZIP_FILE_EXTENSION;
		String fileName = path + session.getId() + AQConstants.CSV_FILE_EXTENSION;
		
		if (entityIdsMap != null && !entityIdsMap.isEmpty())
		{	
			report = new ExportReport(path, csvfileName, zipFileName);
			report.writeDataToZip(exportList, AQConstants.DELIMETER, idIndexList);
			SendFile.sendFileToClient(response, zipFileName, AQConstants.EXPORT_ZIP_NAME,
					"application/download");
		}
		else
		{
			if(report == null){
				report = new ExportReport(fileName);
			}
			report.writeData(exportList, AQConstants.DELIMETER);
			if(downloadFile){
				report.closeFile();
				report = null;
				SendFile.sendFileToClient(response, fileName, AQConstants.SEARCH_RESULT,
						"application/download");
			}	
		}
	}	
	
	private void modifySql(QuerySessionData querySessionData, QueryDetails queryDetails, HttpSession session){
		Map<String,String> aliasMap = queryDetails.getColumnNameVsAliasMap();
		List<OutputTreeDataNode> rootOutputTreeNodeList = (List<OutputTreeDataNode>) session
				.getAttribute(AQConstants.SAVE_TREE_NODE_LIST);
	    String columnAlias = null;
		for (QueryOutputTreeAttributeMetadata attrMeta: rootOutputTreeNodeList.get(0).getAttributes())
		{
			if (attrMeta.getAttribute().getName().equals(AQConstants.IDENTIFIER) )
			{
				columnAlias = aliasMap.get(attrMeta.getColumnName());
				break;
			}
		}
		String sql = querySessionData.getSql();		
		
		if (columnAlias != null){
			if(sql.indexOf(columnAlias) < 0){
			    sql = "SELECT DISTINCT "+ columnAlias +", "+ sql.substring(15);
			}
			sql = sql+ " ORDER BY " + columnAlias;
		}
		querySessionData.setSql(sql);	
	}
	
	private QuerySessionData createCloneQuerySessionData(QuerySessionData querySessionData){
		QuerySessionData clone = new QuerySessionData();
		clone.setSql(querySessionData.getSql());
		clone.setTotalNumberOfRecords(querySessionData.getTotalNumberOfRecords());
		clone.setRecordsPerPage(querySessionData.getRecordsPerPage());
		clone.setSecureExecute(querySessionData.isSecureExecute());
		clone.setHasConditionOnIdentifiedField(querySessionData.isHasConditionOnIdentifiedField());
		clone.setQueryResultObjectDataMap(querySessionData.getQueryResultObjectDataMap());
		return clone;
	}
	
	private List<List<String>> parseJson(HttpServletRequest request) throws JSONException
	{
		List<List<String>> dataList = new ArrayList<List<String>>();
		
		String jsonDataStr = request.getParameter("jsonData");
		JSONArray jsonArray = new JSONArray(jsonDataStr);
		
		for(int i = 0; i < jsonArray.length(); i++){ 
			String row =  jsonArray.getString(i);
			row = row.replace("[", "").replace("]", "");
			String [] cols = row.split(",") ;
			List<String> colList = new ArrayList<String>(Arrays.asList(cols));
			dataList.add(colList);
		}
		
		return dataList;
	}
}
