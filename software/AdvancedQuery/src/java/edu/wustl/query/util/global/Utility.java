	
package edu.wustl.query.util.global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.common.dynamicextensions.domain.AttributeTypeInformation;
import edu.common.dynamicextensions.domain.NumericAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.common.beans.QueryResultObjectData;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.utils.QueryUtility;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.bizlogic.CommonQueryBizLogic;
import edu.wustl.query.bizlogic.QueryOutputSpreadsheetBizLogic;
import edu.wustl.query.executor.AbstractQueryExecutor;
import edu.wustl.query.executor.MysqlQueryExecutor;
import edu.wustl.query.executor.OracleQueryExecutor;
import edu.wustl.query.htmlprovider.HtmlUtility;
import edu.wustl.query.htmlprovider.ParseXMLFile;
import edu.wustl.query.security.QueryCsmCacheManager;
import edu.wustl.query.util.querysuite.QueryModuleUtil;


public class Utility //extends edu.wustl.common.util.Utility
{
	private static org.apache.log4j.Logger logger = LoggerConfig.getConfiguredLogger(Utility.class);
	
	private static String pattern = "MM-dd-yyyy";

	/**
	 * Returns the formatted object compatible to grid format.
	 * @param object object
	 * @return obj The formatted object
	 */
	public static Object toNewGridFormat(Object object)
	{		
		object = edu.wustl.common.util.Utility.toGridFormat(object);
		if (object instanceof String)
		{
			object = object.toString().replaceAll(",", " ");
			object = object.toString().replaceAll("&", "and");
		}

		return object;
	}
 
	/**
	 * Executes SQL through JDBC and returns the list of records.
	 * @param sql SQL to be fired
	 * @return list list<string>
	 * @throws DAOException DAOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public static List executeSQL(String sql, LinkedList<ColumnValueBean> columnValueBean) throws DAOException, ClassNotFoundException
	{
		String appName=CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDao = daofactory.getJDBCDAO();

		jdbcDao.openSession(null);
		List list;
		if(columnValueBean == null)
		{
			list = jdbcDao.executeQuery(sql);
		}
		else
		{
			list = jdbcDao.executeQuery(sql, null, columnValueBean);
		}
		jdbcDao.closeSession();
		return list;
	}

	/**
	 * Adds the attribute values in the list in sorted order and returns the
	 * list containing the attribute values in proper order.
	 * @param dataType -
	 *            data type of the attribute value
	 * @param value1 -
	 *            first attribute value
	 * @param value2 -
	 *            second attribute value
	 * @return List containing value1 and value2 in sorted order
	 */
	public static List<String> getAttributeValuesInProperOrder(String dataType, String value1, String value2)
	{
		String attributeValue1 = value1;
		String attributeValue2 = value2;
		List<String> attributeValues = new ArrayList<String>();
		if (dataType.equalsIgnoreCase(EntityManagerConstantsInterface.DATE_ATTRIBUTE_TYPE))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			try
			{
				Date date1 = dateFormat.parse(value1);
				Date date2 = dateFormat.parse(value2);
				if (date1.after(date2))
				{
					attributeValue1 = value2;
					attributeValue2 = value1;
				}
			}
			catch (ParseException e)
			{
				Logger.out.error("Can not parse the given date in " +
					"getAttributeValuesInProperOrder() method :"+ e.getMessage());
				Logger.out.info(e.getMessage(), e);
			}
		}
		else
		{
			if (dataType.equalsIgnoreCase(EntityManagerConstantsInterface.INTEGER_ATTRIBUTE_TYPE)
			|| dataType.equalsIgnoreCase(EntityManagerConstantsInterface.LONG_ATTRIBUTE_TYPE))
			{
				if (Long.parseLong(value1) > Long.parseLong(value2))
				{
					attributeValue1 = value2;
					attributeValue2 = value1;
				}
			}
			else
			{
				if((dataType.equalsIgnoreCase(EntityManagerConstantsInterface.
				DOUBLE_ATTRIBUTE_TYPE))&& (Double.parseDouble(value1) >
				Double.parseDouble(value2)))
				{
					attributeValue1 = value2;
					attributeValue2 = value1;
				}
			}
		}
		attributeValues.add(attributeValue1);
		attributeValues.add(attributeValue2);
		return attributeValues;
	}

	/**
	 *
	 * @param row List of rows in the grid
	 * @param hyperlinkColMap Hyper Link Column Map
	 * @param index index
	 * @return obj object
	 */
	public static Object toNewGridFormatWithHref(List<String> row,
			Map<Integer, QueryResultObjectData> hyperlinkColMap, int index)
	{
		Object obj = row.get(index);
		if (obj instanceof String)
		{
			obj = toNewGridFormat(obj);
			QueryResultObjectData queryResObjData = hyperlinkColMap.get(index);

			if (queryResObjData != null)// This column is to be shown as
			// hyperlink.
			{
				if (obj == null || obj.equals(""))
				{
					obj = "NA";
				}

				/**
				 * Name : Prafull Bug ID: 4223 Patch ID: 4223_1 Description:
				 * Edit User: password fields empty & error on submitting new
				 * password Added PageOf Attribute as request parameter in the
				 * link.
				 */
				String aliasName = queryResObjData.getAliasName();
				String link = "SimpleSearchEdit.do?"
						+ edu.wustl.common.util.global.Constants.TABLE_ALIAS_NAME +
						"=" + aliasName + "&" + AQConstants.IDENTIFIER + "="
						+ row.get(queryResObjData.getIdentifierColumnId()) + "&"
						+ AQConstants.PAGE_OF + "="
						+ Variables.aliasAndPageOfMap.get(aliasName);
				/**
				 * bug ID: 4225 Patch id: 4225_1 Description : Passing a
				 * different name to the popup window
				 */
				String onclickStr = " onclick=javascript:NewWindow('" + link
						+ "','simpleSearch','800','600','yes') ";
				String hrefTag = "<a class='normalLink' href='#'" +
				onclickStr + ">" + obj + "</a>";
				// String hrefTag = "<a href='"+ link+ "'>"+obj+"</a>";
				obj = hrefTag;
			}
		}
		return obj;
	}

	/**
	 * Replaces the escape characters with the original special characters (i.e. single/double quotes)
	 * @param dataList dataList
	 * @return newList
	 */
	public static List<List<String>> getFormattedOutput(List<List<String>> dataList)
	{
		List<List<String>> newList = new ArrayList<List<String>>();
		List<String> rowList;
		for(int i=0;i<dataList.size();i++)
		{
			rowList = new ArrayList<String>();
			List<String> row = dataList.get(i);
			for(int j=0;j<row.size();j++)
			{
				populateInternalRow(rowList, row, j);
			}
			newList.add(rowList);
		}
		return newList;
	}

	/**
	 * Populates the row.
	 * @param rowList rowList
	 * @param row row
	 * @param index j
	 */
	private static void populateInternalRow(List<String> rowList,
			List<String> row, int index)
	{
		String data = row.get(index);
		if(data != null && (data.contains("&#39") || data.contains("&#34")))
		{
			data = DynamicExtensionsUtility.getUnEscapedStringValue(data);
		}
		rowList.add(data);
	}
	/**
	 * This method creates a comma separated string of numbers representing
	 * column width.
	 * @param columnNames List of column names
	 * @return colWidth Comma separated column widths
	 */
	public static String getColumnWidth(List columnNames)
	{
		String colWidth = getColumnWidth((String) columnNames.get(0));
		StringBuffer columnWidth = new StringBuffer(colWidth);

		for (int col = 1; col < columnNames.size(); col++)
		{
			String columnName = (String) columnNames.get(col);
			columnWidth.append(',').append(getColumnWidth(columnName));
		}
		return columnWidth.toString();
	}

	/**
	 * Get column width of the grid.
	 * @param columnName column name
	 * @return columnWidth column width
	 */
	private static String getColumnWidth(String columnName)
	{		
		String columnWidth;
		if ("ID".equals(columnName.trim()))
		{
			columnWidth = "0";
		}
		else if ("".equals(columnName.trim()))
		{
			columnWidth = "20";
		}
		else
		{
			columnWidth = "100";
		}
		return columnWidth;
	}

	/**
	 * limits the title of the saved query to 125 characters to avoid horizontal
	 * scroll bar.
	 * @param title -
	 *            title of the saved query (may be greater than 125 characters)
	 * @return - query title up to 125 characters
	 */

	public static String getQueryTitle(String title)
	{
		String multilineTitle;
		if (title.length() <= AQConstants.CHARACTERS_IN_ONE_LINE)
		{
			multilineTitle = title;
		}
		else
		{
			multilineTitle = title.substring(0, AQConstants.CHARACTERS_IN_ONE_LINE) + ".....";
		}
		return multilineTitle;
	}

	/**
	 * returns the entire title to display it in tooltip.
	 * @param title -
	 *            title of the saved query
	 * @return tool tip string
	 */
	public static String getTooltip(String title)
	{
		String tooltip = title.replaceAll("'", AQConstants.SINGLE_QUOTE_ESCAPE_SEQUENCE);
		// escape sequence for '
		return tooltip;
	}

	/**
	 * @param request Object of HttpServletRequest
	 * @param sessionData A data bean that contains information related to user logged in.
	 * @param recordsPerPage To specify records to be displayed per page
	 * @param pageNum page number
	 * @param querySessionData QuerySessionData object
	 * @return paginationDataList paginationDataList
	 * @throws DAOException DAO Exception
	 */
	public static List getPaginationDataList(HttpServletRequest request,
			SessionDataBean sessionData, int recordsPerPage, int pageNum,
			QuerySessionData querySessionData, boolean isDefineView) throws DAOException
	{
		List paginationDataList;
		querySessionData.setRecordsPerPage(recordsPerPage);
		int startIndex = recordsPerPage * (pageNum - 1);
		recordsPerPage = startIndex+ recordsPerPage;
		CommonQueryBizLogic qBizLogic = new CommonQueryBizLogic();

		List<String> columnsList = (List<String>) request.getSession()
								.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
		PagenatedResultData pagenatedResult = qBizLogic.execute(sessionData, querySessionData,
				startIndex, columnsList.size());
		paginationDataList = pagenatedResult.getResult();
		String isSimpleSearch = (String) request.getSession().getAttribute(
				AQConstants.IS_SIMPLE_SEARCH);
		if (isSimpleSearch == null || (!isSimpleSearch.equalsIgnoreCase(AQConstants.TRUE)))
		{
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap = querySessionData
					.getQueryResultObjectDataMap();
			if (queryResultObjectDataBeanMap != null)
			{
				populateSessionData(request, paginationDataList,
						queryResultObjectDataBeanMap, isDefineView);
			}
		}
		
		return paginationDataList;
	}

	/**
	 * @param request request
	 * @param paginationDataList data list
	 * @param queryResultObjectDataBeanMap map
	 */
	private static void populateSessionData(HttpServletRequest request,
			List paginationDataList,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap, boolean isDefineView)
	{
		for (Iterator<Long> beanMapIterator = queryResultObjectDataBeanMap.keySet()
				.iterator(); beanMapIterator.hasNext();)
		{
			Long identifier = beanMapIterator.next();
			QueryResultObjectDataBean bean =
				queryResultObjectDataBeanMap.get(identifier);
			if (bean.isClobeType())
			{
				List<String> columnsList = (List<String>) request.getSession()
						.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
				QueryOutputSpreadsheetBizLogic queryBizLogic =
					new QueryOutputSpreadsheetBizLogic();
				Map<Integer, Integer> fileTypMnEntMap =
					queryBizLogic.updateSpreadSheetColumnList(columnsList,
								queryResultObjectDataBeanMap, isDefineView);
				Map exportMetataDataMap = QueryOutputSpreadsheetBizLogic.
				updateDataList(paginationDataList, fileTypMnEntMap);
				request.getSession().setAttribute(AQConstants.ENTITY_IDS_MAP,
					exportMetataDataMap.get(AQConstants.ENTITY_IDS_MAP));
				request.getSession().setAttribute(AQConstants.EXPORT_DATA_LIST,
					exportMetataDataMap.get(AQConstants.EXPORT_DATA_LIST));
				break;
			}
		}
	}

	public static String getGridDataJson(List dataList, List columnList, HttpServletRequest request)
	{
		try {
			HttpSession session = request.getSession();	
			int  pageNum = Integer.parseInt ((String)request.getAttribute(AQConstants.PAGE_NUMBER));
			int totalResult = ((Integer) session.getAttribute(AQConstants.TOTAL_RESULTS)).intValue();
			Integer recordsPerPage = Integer.parseInt((String)session.getAttribute(AQConstants.RESULTS_PER_PAGE));
			int pos = recordsPerPage * (pageNum - 1);	
			List columnLabels = new ArrayList(columnList);
			columnLabels.add(0, "");
			
			Map gridData = new HashMap();
			gridData.put("total_count", 0);
			gridData.put("pos", pos);
			gridData.put("rows", getGridData(dataList, pos));			
			
			JSONObject json = new JSONObject();
			json.put("columns", columnLabels);
			json.put("columnType", getColumnTypes(session, columnLabels));
			json.put("gridData", gridData);
			
			return json.toString();			
		} catch(Exception e) {
			logger.error("Exception found while creating json", e);			
		}
		
		return "{}";
	}
	
	/**
	 * Set the grid data.
	 * @param dataList DataList
	 * @param columnList List of columns
	 * @param request Object of HttpServletRequest
	 */
	public static void setGridData(List dataList, List columnList, HttpServletRequest request)
	{
		request.setAttribute("gridDataJson", getGridDataJson(dataList, columnList, request));		
		request.setAttribute("colTypes", getcolTypes(dataList));
		columnList.add(0, ""); // added for checkbox
		request.setAttribute("colWidth", getColumnWidth(columnList));
		columnList.remove(0);
			
		int index = 0;
		StringBuffer hiddenColNos = new StringBuffer();
		if(columnList != null)
		{
			for(int col = 0; col < columnList.size(); col++)
			{
				index = populateHiddenColNos(columnList, col, index, hiddenColNos);
			}
		}
		request.setAttribute("hiddenColumnNumbers", hiddenColNos.toString());
	}

	/**
	 * @param columnList columnList
	 * @return isWidthInPercent
	 */
	private static boolean isWidthInPercent(List columnList)
	{
		boolean isWidthInPercent;
		if( columnList.size() < AQConstants.TEN)
		{
			isWidthInPercent = true;
		}
		else
		{
			isWidthInPercent = false;
		}
		return isWidthInPercent;
	}

	/**
	 * @param columnList columnList
	 * @param col column
	 * @param counter counter
	 * @param hiddenColNos hiddenColNos
	 * @return index
	 */
	private static int populateHiddenColNos(List columnList, int col,
			int counter, StringBuffer hiddenColNos)
	{
		int index = counter;
		if (columnList.get(col).toString().trim().equals("ID") ||
		columnList.get(col).toString().trim().equals("Status")
		|| columnList.get(col).toString().trim().equals("Site Name")||
		columnList.get(col).toString().trim().equals("Report Collection Date"))
		{
			hiddenColNos.append("hiddenColumnNumbers["+index+"] = ").append(col).append(';');
			index++;
		}
		return index;
	}
	
	/**
	 * Get column width.
	 * @param columnList columnList
	 * @param isWidthInPercent isWidthInPercent
	 * @return colWidth The column width
	 */
	public static String getcolWidth(List columnList, boolean isWidthInPercent)
	{
		StringBuffer colWidth = new StringBuffer("\"");
		
		if(columnList != null)
		{
			String fixedColWidth;
			if(isWidthInPercent)
			{
				fixedColWidth = String.valueOf(AQConstants.HUNDRED/columnList.size());
			}
			else
			{
				fixedColWidth = "100";
			}
			for(int col = 0; col < (columnList.size()-1); col++)
			{
				colWidth.append(fixedColWidth).append(',');
			}
			colWidth.append(fixedColWidth);
		}
		colWidth.append("\"");
		return colWidth.toString();
	}

	/**
	 * Get column types.
	 * @param dataList dataList
	 * @return colTypes The column types
	 */
	public static String getcolTypes(List dataList)
	{
		StringBuffer colTypes= new StringBuffer();
		colTypes.append('"');
		colTypes.append(Variables.prepareColTypes(dataList, true));
		colTypes.append('"');
		return colTypes.toString();
	}

	/**
	 *
	 * @param dataList DataList
	 * @return myData myData
	 */
	private static JSONArray getGridData(List dataList, Integer pos)
	{		
		JSONArray rows = new JSONArray();
		if(dataList == null){
			return rows;
		}
		for (int i = 0; i < dataList.size(); i++){ 
			List row = (List)dataList.get(i);
			
			JSONArray data = new JSONArray();			
			data.put("");
			for (Object obj: row){	
				data.put(Utility.toNewGridFormat(obj));
			}
			
			Map dataRow = new HashMap();			
			dataRow.put("id", pos + i);
			dataRow.put("data", data);			
			rows.put(dataRow);
		}
			
		return rows;
	}
	
	
	@SuppressWarnings("unchecked")
	private static List getColumnTypes(HttpSession session, List columnList) {
		List<OutputTreeDataNode> rootOutputTreeNodeList = (List<OutputTreeDataNode>) session
											.getAttribute(AQConstants.SAVE_TREE_NODE_LIST);
		List<QueryOutputTreeAttributeMetadata> attributes = new ArrayList<QueryOutputTreeAttributeMetadata>();
		
		for(OutputTreeDataNode node: rootOutputTreeNodeList) { 
			attributes.addAll(node.getAttributes());
			for(OutputTreeDataNode child: QueryModuleUtil.getInViewChildren(node)) {
				attributes.addAll(child.getAttributes());
			}
		}
				
		String[] columnTypes = new String[columnList.size()];
		columnTypes[0] = "ch";
		
		for(QueryOutputTreeAttributeMetadata attr: attributes){
			AttributeTypeInformation type = (AttributeTypeInformation) attr.getAttribute().getAttributeTypeInformation();
			String colName = attr.getDisplayName();
			int index = columnList.indexOf(attr.getDisplayName());
			if(index != -1) {
				String attrType =  type.getClass().getSimpleName();
				if(type instanceof NumericAttributeTypeInformation ) {
					attrType = "NumericAttributeTypeInformation";
				}
				setDataTypeInArray(columnList, index, columnTypes, colName, attrType);
			}
		}
		
		/*StringBuilder filter = new StringBuilder("[\"ch\"");
		for(String type: columnTypes) {			
			filter.append(", \"").append(type).append("\"");
		}
		filter.append("]");*/
		List typeList = Arrays.asList(columnTypes);
		
		return typeList;
	}
	
	private static void setDataTypeInArray(List columnList, int index, String[] columnTypes, String colName, String type ) {
		columnTypes[index] =  type;
		int i = 1;
		while((index = columnList.indexOf(colName + "_" + i)) != -1) {
			columnTypes[index] =  type;
			i++;
		}
	}
	/** Added By Rukhsana
     * Added list of objects on which read denied has to be checked while filtration of result
     * for csm-query performance.
     * A map that contains entity name as key and sql to get Main_Protocol_Object
     * (Collection protocol, Clinical Study) Ids for that entity id as value for csm-query performance.
     * Reading the above values from a properties file to make query module application independent
     */
	
    public static void setReadDeniedAndEntitySqlMap()
    {
        List<String> readDeniedObjList = new ArrayList<String>();
        Map<String,String> entityCSSqlMap = new HashMap<String, String>();
        String mainPrtclClassNm="";
        String validatorClassNm ="";
        String mainProtocolQuery="";
        String appName = CommonServiceLocator.getInstance().getAppHome();
        File file = new File(appName+ System.getProperty("file.separator")+"WEB-INF"+
        		System.getProperty("file.separator")+"classes"+System.getProperty("file.separator")
        		+edu.wustl.security.global.Constants.CSM_PROPERTY_FILE);
        FileInputStream inputStream = null;
        if(file.exists())
        {
           Properties csmPropertyFile = new Properties();
           try
           {
        	    inputStream = new FileInputStream(file);
                csmPropertyFile.load(inputStream);
                mainPrtclClassNm = csmPropertyFile.getProperty
                (edu.wustl.security.global.Constants.MAIN_PROTOCOL_OBJECT);
                validatorClassNm = csmPropertyFile.getProperty
                (edu.wustl.security.global.Constants.VALIDATOR_CLASSNAME);
                mainProtocolQuery = csmPropertyFile.getProperty
                (AQConstants.MAIN_PROTOCOL_QUERY);
                populateEntityCSSqlMap(readDeniedObjList, entityCSSqlMap,
						csmPropertyFile);
            }
            catch (FileNotFoundException e)
            {
                Logger.out.debug("csm.properties not found");
                Logger.out.info(e.getMessage(), e);
            }
            catch (IOException e)
            {
                Logger.out.debug("Exception occured while reading csm.properties");
                Logger.out.info(e.getMessage(), e);
            }
            finally
            {
            	closeFile(inputStream);
            }
           edu.wustl.query.util.global.Variables.mainProtocolObject = mainPrtclClassNm;
           edu.wustl.query.util.global.Variables.queryReadDeniedObjectList.addAll(readDeniedObjList);
           edu.wustl.query.util.global.Variables.entityCPSqlMap.putAll(entityCSSqlMap);
           edu.wustl.query.util.global.Variables.validatorClassname = validatorClassNm;
           edu.wustl.query.util.global.Variables.mainProtocolQuery = mainProtocolQuery;
        }
    }

    /**
     * Close the Input Stream.
     * @param inputStream inputStream
     */
	private static void closeFile(FileInputStream inputStream)
	{
		if(inputStream != null)
		{
			try
			{
				inputStream.close();
			}
			catch (IOException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

    /**
     * @param readDeniedObjList list of read denied objects
     * @param entityCSSqlMap map
     * @param csmPropertyFile property file
     */
	private static void populateEntityCSSqlMap(List<String> readDeniedObjList,
			Map<String, String> entityCSSqlMap, Properties csmPropertyFile)
	{
		String readdenied = csmPropertyFile.getProperty
		(edu.wustl.security.global.Constants.READ_DENIED_OBJECTS);
		String [] readDeniedObjects=readdenied.split(",");
		for(int i=0;i<readDeniedObjects.length;i++)
		{
		      readDeniedObjList.add(readDeniedObjects[i]);
		      if(csmPropertyFile.getProperty(readDeniedObjects[i])!=null)
		      {
		          entityCSSqlMap.put(readDeniedObjects[i],csmPropertyFile.getProperty
		        		  (readDeniedObjects[i]));
		      }
		}
	}

    /**
     * @param objName Object name
     * @param identifier Identifier
     * @param sessionDataBean A data bean that contains information related to user logged in
     * @return cpIdsList List of CollectionProtocol identifiers
     */
	  public static List getCPIdsList(String objName, Long identifier, SessionDataBean sessionDataBean)
	    {
	        List cpIdsList = new ArrayList();
	        if (objName != null && !objName.equalsIgnoreCase
	        		(edu.wustl.query.util.global.Variables.mainProtocolObject))
	        {
	            String cpQuery = QueryCsmCacheManager.getQueryStringForCP
	            (objName, Integer.valueOf(identifier.toString()));
	            JDBCDAO jdbcDao = null;

	            String appName=CommonServiceLocator.getInstance().getAppName();
	            IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
	            try
	            {
	                jdbcDao = executeCPQuery(sessionDataBean, cpIdsList,
							cpQuery, daofactory);
	            }
	            catch (Exception e)
	            {
	                return null;
	            }
	            finally
	            {
	                closeSession(jdbcDao);
	            }
	        }
	        else
	        {
	            cpIdsList.add(identifier);
	        }
	        return cpIdsList;
	    }

	  	/**
	  	 * @param jdbcDao jdbcDao
	  	 */
		private static void closeSession(JDBCDAO jdbcDao)
		{
			try
			{
				if(jdbcDao != null)
				{
					jdbcDao.closeSession();
				}
			}
			catch (DAOException e)
			{
				logger.error(e.getMessage(), e);
			}
		}

	  	/**
	  	 * @param sessionDataBean bean
	  	 * @param cpIdsList list
	  	 * @param cpQuery query
	  	 * @param daofactory DAO
	  	 * @return jdbcDao
	  	 * @throws DAOException DAOException
	  	 */
		private static JDBCDAO executeCPQuery(SessionDataBean sessionDataBean,
				List cpIdsList, String cpQuery, IDAOFactory daofactory)
				throws DAOException
		{
			JDBCDAO jdbcDao;
			jdbcDao = daofactory.getJDBCDAO();
			jdbcDao.openSession(sessionDataBean);

			List<Object> list = jdbcDao.executeQuery(cpQuery);
			if (list != null && !list.isEmpty())
			{
			    populateCPIdsList(cpIdsList, list);
			}
			return jdbcDao;
		}

	  /**
	   * @param cpIdsList list
	   * @param list list
	   */
	private static void populateCPIdsList(List cpIdsList, List<Object> list)
	{
		for(Object obj : list)
		{
		    List list1 = (List)obj;
		    cpIdsList.add(Long.valueOf(list1.get(0).toString()));
		}
	}
	  /**
	     * To check whether there is condition on identifier field or not.
	     * @param query the reference to the Query Object.
	     * @return true if there is any condition on the Identified attribute, else returns false.
	     */
	    public static boolean isConditionOnIdentifiedField(IQuery query)
	    {
	    	boolean isCondnOnIdField = false;
	        Map<IExpression, Collection<ICondition>> allSelectedConditions = QueryUtility
	                .getAllSelectedConditions(query);
	        Collection<Collection<ICondition>> values = allSelectedConditions.values();
	        for (Collection<ICondition> conditions : values)
	        {
	            for (ICondition condition : conditions)
	            {
	                Boolean isCondnOnIdAttr = condition.getAttribute().getIsIdentified();
	                
	                if (Boolean.TRUE.equals(isCondnOnIdAttr))
	                {
	                	isCondnOnIdField = true;
	                	break;
	                }
	            }
	            if(isCondnOnIdField)
	            {
	            	break;
	            }
	        }
	        return isCondnOnIdField;
	    }

	    /**
	     * Returns the Query Executor depending upon the database (MySql/Oracle).
	     * @return queryExecutor The queryExecutor
	     */
	    public static AbstractQueryExecutor getQueryExecutor()
	    {
	        AbstractQueryExecutor queryExecutor;

	        String appName = CommonServiceLocator.getInstance().getAppName();
	        IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
	        if("MYSQL".equalsIgnoreCase(daoFactory.getDataBaseType()))
	        {
	            queryExecutor = new MysqlQueryExecutor();
	        }
	        else
	        {
	            queryExecutor = new OracleQueryExecutor();
	        }
	        return queryExecutor;
	    }
	    /**
	     * Forms display name for attribute as className : attribute name.
	     * @param attribute AttributeInterface
	     * @return columnDisplayName
	     */
	    public static String getDisplayNameForColumn(AttributeInterface attribute)
	    {
	        StringBuffer columnDisplayName = new StringBuffer();
	        String className = parseClassName(attribute.getEntity().getName());
	        String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute.getName());
	        columnDisplayName.append(className).append(" : ").append(attributeLabel);
	        return columnDisplayName.toString();
	    }

	    /**
	     * @param fullyQualifiedName Fully Qualified Name of the class.
	     * @return fullyQualifiedName The parsed class name
	     */
	    public static String parseClassName(String fullyQualifiedName)
	    {
	    	String className = fullyQualifiedName;
	        try
	        {
	            className = fullyQualifiedName.substring(fullyQualifiedName
	                    .lastIndexOf('.') + 1);
	        }
	        catch (Exception e)
	        {
	        	Logger.out.error("Error in parsing the class name");
	        }
	        return className;
	    }
	    /**
	     * @param entity entity for which Primary Key list is required
	     * @return primaryKeyList primary key list
	     */
	    public static List<String> getPrimaryKey(EntityInterface entity)
	    {
	        Collection<TaggedValueInterface> taggedValueCollection = entity.getTaggedValueCollection();

	        List<String> primaryKeyList = new ArrayList<String>();
	        for (TaggedValueInterface tag : taggedValueCollection)
	        {
	            if (AQConstants.PRIMARY_KEY_TAG_NAME.equals(tag.getKey()))
	            {
	                StringTokenizer stringTokenizer = new StringTokenizer(tag.getValue(), ",");
	                while(stringTokenizer.hasMoreTokens())
	                {
	                    primaryKeyList.add(stringTokenizer.nextToken());
	                }
	            }
	        }
	        return primaryKeyList;
	    }

	    /**
	     * Method to generate SQL for Node.
	     * @param parentNodeId parent node id which contains data
	     * @param tableName temporary table name
	     * @param parentIdColumnName parent node ID column names (, separated)
	     * @param selectSql SQL
	     * @param idColumnOfCurrentNode id column name (, separated) of current node
	     * @return SQL for current node
	     */
	    public static String getSQLForNode(String parentNodeId, String tableName,
	            String parentIdColumnName, String selectSql, String idColumnOfCurrentNode)
	    {
	        selectSql = selectSql + AQConstants.FROM + tableName;
	        if (parentNodeId != null)
	        {
	            selectSql = selectSql + AQConstants.WHERE + " (";
	            StringTokenizer stringTokenizerParentID = new StringTokenizer(parentIdColumnName, ",");
	            StringTokenizer stringTokenizerParentNodeID = new StringTokenizer(parentNodeId,
	                    AQConstants.PRIKEY_ATTR_SEPARATOR);
	            while (stringTokenizerParentID.hasMoreElements())
	            {
	                selectSql = selectSql + stringTokenizerParentID.nextElement() + " = '"
	                        + stringTokenizerParentNodeID.nextElement() + "' " + LogicalOperator.And
	                        + " ";
	            }
	            StringTokenizer stringTokenizerIDofCurrentNode = new StringTokenizer(
	                    idColumnOfCurrentNode, ",");
	            while (stringTokenizerIDofCurrentNode.hasMoreElements())
	            {
	                selectSql = selectSql + " " + stringTokenizerIDofCurrentNode.nextElement() + " "
	                        + RelationalOperator.getSQL(RelationalOperator.IsNotNull) + " "
	                        + LogicalOperator.And;
	            }
	            if (selectSql.substring(selectSql.length() - AQConstants.THREE).equals
	            		(LogicalOperator.And.toString()))
	            {
	                selectSql = selectSql.substring(0, selectSql.length() - AQConstants.THREE);
	            }
	            selectSql = selectSql + ")";
	        }
	        return selectSql;
	    }


	/**
	 * @param feature feature
	 * @return <CODE>true</CODE> feature is used,
	 * <CODE>false</CODE> otherwise
	 */
	public static boolean checkFeatureUsage(String feature)
	{
		ResourceBundle appResources = ResourceBundle.getBundle("ApplicationResources");
		String isFeatureUsed = appResources.getString(feature);

		boolean hasUsage;

		if (isFeatureUsed == null || "".equals(isFeatureUsed) || !"false".equals(isFeatureUsed))
		{
			hasUsage = true;
		}
		else
		{
			hasUsage = false;
		}
		return hasUsage;
	}

	  /**
     * It will update the given condition to the empty condition.
     *
     * @param condition
     *            condition to be updated.
     */
    public static void updateConditionToEmptyCondition(final ICondition condition)
    {
        try
        {
            final String operator = getDefaultOperatorForAttribute(condition
                    .getAttribute());
            condition.setRelationalOperator(RelationalOperator
                    .getOperatorForStringRepresentation(operator));
            condition.getValues().clear();
            if (condition.getRelationalOperator().getStringRepresentation()
                    .equalsIgnoreCase(AQConstants.BETWEEN))
            {
                condition.addValue("");
                condition.addValue("");
            }
            else
            {
                condition.addValue("");
            }
        }
        catch (final CheckedException e)
        {
        	logger.error(e.getMessage(), e);
        }
    }

    /**
     * This method will return the Default operator which should be used to form
     * the Empty conditions.
     *
     * @param attribute
     *            for which the operator is needed.
     *
     * @return the operator.
     *
     * @throws CheckedException
     *             exception.
     */
    private static String getDefaultOperatorForAttribute(final AttributeInterface attribute)
            throws CheckedException
    {
        String operator;
        InputStream inputStream = Utility.class.getClassLoader()
										.getResourceAsStream(AQConstants.DYNAMIC_UI_XML);
        final ParseXMLFile parseFile = ParseXMLFile.getInstance(inputStream);
            operator = HtmlUtility.getConditionsList(attribute, parseFile).get(0);

        return operator;
    }
    
    public static Map<String,String> splitQuery(String sql){
    	int fromIndex = sql.indexOf("from");
		String select = sql.substring(15, fromIndex).trim(); //15 is a index upto SELECT DISTINCT 
		String [] columns = select.split(", ");	
		
		Map<String, String> columnsVsAlias = new HashMap<String, String>();
		int lastindex = 0;
		for(String column : columns){
			String names[] = column.split("Column");
			if(names.length == 2 && names[1].matches("[0-9]+")){
				columnsVsAlias.put("Column" + names[1], names[0]);
				lastindex = sql.indexOf(column) + column.length();
			}else {
				String temp = sql.substring(lastindex + 1, fromIndex).trim();
				columnsVsAlias.put("temporal", temp);
				break;
			}
		}
		return columnsVsAlias;
    }
    
    public static String generateHiddenIds(Set<String>tableAliasNames, String columnsInSql)
    {
    	StringBuffer hiddenIdColumns = new StringBuffer();
    	if(tableAliasNames != null || ! tableAliasNames.isEmpty()){
    		int index = 0;
    		for (String tableAliasName : tableAliasNames){
    			String value = tableAliasName+".IDENTIFIER";
    			if(columnsInSql.indexOf(value) < 0){
    				hiddenIdColumns.append(value+" "+"Id"+index+", ");
    				index++;
    			}
    		}
    	}
    	return hiddenIdColumns.toString();
    }
    
    public static String getTableAliasName(EntityInterface entity){
    	String tableName = entity.getTableProperties().getName();
		String entityAlias = edu.wustl.common.util.Utility.removeSpecialCharactersFromString(tableName);
        if (entityAlias.length() > 26)
        {
        	entityAlias = entityAlias.substring(0, 26);
        }
        
        return entityAlias;
    }
}