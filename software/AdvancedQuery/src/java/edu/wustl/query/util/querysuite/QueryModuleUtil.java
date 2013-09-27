
package edu.wustl.query.util.querysuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.common.dynamicextensions.domaininterface.AbstractMetadataInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.Utility;

/**
 * This is an utility class to provide methods required for query interface.
 * @author deepti_shelar
 */
/**
 * @author vijay_pande
 *
 */
final public class QueryModuleUtil
{

	/**
	 * Default constructor.
	 */
	private QueryModuleUtil()
	{
	}

	/**
	 * Takes data from the map and generates out put data accordingly so that
	 * spreadsheet will be updated.
	 *
	 * @param spreadSheetDtmap
	 *            map which holds data for columns and records.
	 * @return this string consists of two strings separated by '&', first part
	 *         is for column names to be displayed in spreadsheet and the second
	 *         part is data in the spreadsheet.
	 */
	public static String prepareOutputSpreadsheetDataString(final Map spreadSheetDtmap)
	{
		List<List<String>> dataList = (List<List<String>>) spreadSheetDtmap
				.get(AQConstants.SPREADSHEET_DATA_LIST);
		StringBuffer dataStr = new StringBuffer();
		String columns = getColumnsAsString(spreadSheetDtmap);
		dataStr.append(columns).append('&');
		getGridDetails(dataList, dataStr);
		return dataStr.toString();
	}

	/**
	 * @param dataList
	 *            dataList
	 * @param dataStr
	 *            dataStr
	 */
	private static void getGridDetails(List<List<String>> dataList, StringBuffer dataStr)
	{
		StringBuffer gridStrBuff = new StringBuffer();
		Object gridObj;
		String gridStr;
		for (List<String> row : dataList)
		{
			for (Object columnData : row)
			{
				gridObj = Utility.toNewGridFormat(columnData);
				gridStr = gridObj.toString();
				gridStrBuff.append(gridStr);
				gridStrBuff.append(',');
			}
			dataStr.append('|').append(gridStrBuff.toString());
		}
	}

	/**
	 * This will get the column's data as String format.
	 *
	 * @param spreadSheetDtmap
	 *            spreadSheetDtmap
	 * @return String
	 */
	private static String getColumnsAsString(final Map spreadSheetDtmap)
	{
		List columnsList = (List) spreadSheetDtmap.get(AQConstants.SPREADSHEET_COLUMN_LIST);
		String columns = columnsList.toString();
		columns = columns.replace("[", "");
		columns = columns.replace("]", "");
		return columns;
	}

	/**
	 * Forms select part of the query.
	 *
	 * @param attributes
	 *            attributes
	 * @param queryDetailsObj
	 *            queryDetailsObj
	 * @param queryResultObjectDataBean
	 *            queryResultObjectDataBean
	 * @return String having all column names for select part.
	 */
	public static Map<String, String> getColumnNamesForSelectpart(
			List<QueryOutputTreeAttributeMetadata> attributes, QueryDetails queryDetailsObj,
			QueryResultObjectDataBean queryResultObjectDataBean)
	{
		String columnNames;
		String idColumnName = null;
		String dspNameColName = null;
		String index = null;
		Map<String, String> attrNameColumn = new HashMap<String, String>();
		String columnName;
		int columIndex = 0;
		AttributeInterface attribute;
		List<Integer> objectColumnIdsVector = new ArrayList<Integer>();
		List<Integer> idvector = new ArrayList<Integer>();
		boolean isTagPresentEntity = isTagPresent(attributes.get(0).getAttribute().getEntity());
		boolean incrementColIndex = true;
		Map<String, String> columnNamePositionMap = new HashMap<String, String>();
		for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
		{
			attribute = attributeMetaData.getAttribute();
			String attributePosition = isTagPresentOnAttribute(attribute);

			columnName = attributeMetaData.getColumnName();

			if (!isTagPresentEntity && idColumnName != null && dspNameColName != null)
			{
				break;
			}
			if (AQConstants.IDENTIFIER.equals(attribute.getName()))
			{
				idColumnName = columnName;
				queryResultObjectDataBean.setMainEntityIdentifierColumnId(0);
				queryResultObjectDataBean.setEntityId(0);
				objectColumnIdsVector.add(0);
				columIndex++;
			}
			else
			{
				populateAttrColumnMap(attrNameColumn, columnName, attribute, isTagPresentEntity);
				if (attributePosition != null)
				{
					columnNamePositionMap.put(attributePosition, columnName);
					index = columnName.substring(AQConstants.COLUMN_NAME.length(), columnName
							.length());
					if (attribute.getIsIdentified() != null)
					{
						idvector.add(1);
						idvector.add(AQConstants.TWO);
					}
					objectColumnIdsVector.add(1);
					objectColumnIdsVector.add(AQConstants.TWO);
					queryResultObjectDataBean.setIdentifiedDataColumnIds(idvector);
					if (incrementColIndex)
					{
						columIndex++;
						incrementColIndex = false;
					}
				}
				else if (!isTagPresentEntity
						&& isPresentInArray(attribute.getName(), AQConstants.ATTR_NAME_TREENODE_LBL))
				{
					dspNameColName = columnName;
					index = columnName.substring(AQConstants.COLUMN_NAME.length(), columnName
							.length());
					if (attribute.getIsIdentified() != null)
					{
						idvector.add(1);
					}
					objectColumnIdsVector.add(1);
					queryResultObjectDataBean.setIdentifiedDataColumnIds(idvector);
					columIndex++;
				}
			}
		}
		queryResultObjectDataBean.setObjectColumnIds(objectColumnIdsVector);

		dspNameColName = updateDisplayColumnName(dspNameColName, attrNameColumn,
				isTagPresentEntity, columnNamePositionMap);
		columnNames = getColumnNames(idColumnName, dspNameColName);
		Map<String, String> colNameIndexMap = populateColumnIndexMap(queryResultObjectDataBean,
				columnNames, index);
		return colNameIndexMap;
	}

	/**
	 * @param queryResultObjectDataBean
	 *            queryResultObjectDataBean
	 * @param columnNames
	 *            columnNames
	 * @param index
	 *            index
	 * @return colNameIndexMap
	 */
	private static Map<String, String> populateColumnIndexMap(
			QueryResultObjectDataBean queryResultObjectDataBean, String columnNames, String index)
	{
		if (queryResultObjectDataBean.getIdentifiedDataColumnIds().size() != 0)
		{
			queryResultObjectDataBean.setHasAssociatedIdentifiedData(true);
		}
		Map<String, String> colNameIndexMap = new HashMap<String, String>();
		colNameIndexMap.put(AQConstants.COLUMN_NAMES, columnNames);
		colNameIndexMap.put(AQConstants.INDEX, index);
		return colNameIndexMap;
	}

	/**
	 * @param attrNameColumn
	 *            attrNameColumn
	 * @param columnName
	 *            columnName
	 * @param attribute
	 *            attribute
	 * @param isTagPresentEntity
	 *            isTagPresentEntity
	 */
	private static void populateAttrColumnMap(Map<String, String> attrNameColumn,
			String columnName, AttributeInterface attribute, boolean isTagPresentEntity)
	{
		if (isTagPresentEntity && attribute.getName().equalsIgnoreCase("birthDate")
				|| attribute.getName().equalsIgnoreCase("deathDate"))
		{
			attrNameColumn.put(attribute.getName(), columnName);
		}
	}

	/**
	 * @param displayName
	 *            displayName
	 * @param attrNameColumn
	 *            attrNameColumn
	 * @param isTagPresentEntity
	 *            isTagPresentEntity
	 * @param columnNamePositionMap
	 *            columnNamePositionMap
	 * @return dspNameColName
	 */
	private static String updateDisplayColumnName(String displayName,
			Map<String, String> attrNameColumn, boolean isTagPresentEntity,
			Map<String, String> columnNamePositionMap)
	{
		StringBuffer dspNameColName = getDisplayColName(displayName);
		if (isTagPresentEntity)
		{
			dspNameColName = new StringBuffer(getColDisplayName(displayName, columnNamePositionMap));
			if (!attrNameColumn.isEmpty())
			{
				String columnNameForBirthDate = attrNameColumn.get("birthDate");
				String columnNameForDeaththDate = attrNameColumn.get("deathDate");
				if (dspNameColName == null)
				{
					dspNameColName = new StringBuffer();
					dspNameColName.append(columnNameForBirthDate).append(", ").append(
							columnNameForDeaththDate);
				}
				else
				{
					dspNameColName.append(", ").append(columnNameForBirthDate).append(", ").append(
							columnNameForDeaththDate);
				}
			}
		}
		return getReturnValue(dspNameColName);
	}

	/**
	 * @param dspNameColName
	 *            dspNameColName
	 * @return returnValue
	 */
	private static String getReturnValue(StringBuffer dspNameColName)
	{
		String returnValue = null;
		if (dspNameColName != null)
		{
			returnValue = dspNameColName.toString();
		}
		return returnValue;
	}

	/**
	 * @param displayName
	 *            displayName
	 * @return dspNameColName
	 */
	private static StringBuffer getDisplayColName(String displayName)
	{
		StringBuffer dspNameColName = null;
		if (displayName != null)
		{
			dspNameColName = new StringBuffer(displayName);
		}
		return dspNameColName;
	}

	/**
	 * @param idColumnName
	 *            idColumnName
	 * @param dspNameColName
	 *            dspNameColName
	 * @return columnNames
	 */
	private static String getColumnNames(String idColumnName, String dspNameColName)
	{
		String columnNames;
		if (dspNameColName == null)
		{
			columnNames = idColumnName;
		}
		else
		{
			columnNames = idColumnName + " , " + dspNameColName;
		}
		return columnNames;
	}

	/**
	 * Gets the display name for column.
	 *
	 * @param dspNameColName
	 *            name
	 * @param columnNamePositionMap
	 *            map
	 * @return string display name
	 */
	private static String getColDisplayName(String displayColumnName,
			Map<String, String> columnNamePositionMap)
	{
		String dspNameColName = displayColumnName;
		Set<String> keySet = columnNamePositionMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		if (iterator.hasNext())
		{
			String position1 = iterator.next();
			if (iterator.hasNext())
			{
				dspNameColName = setDisplayColumnName(dspNameColName, columnNamePositionMap,
						iterator, position1);
			}
		}
		return dspNameColName;
	}

	private static String setDisplayColumnName(String displayColumnName,
			Map<String, String> columnNamePositionMap, Iterator<String> iterator, String position1)
	{
		StringBuffer dspNameColName = getDisplayColName(displayColumnName);
		String position2;
		position2 = iterator.next();
		if (Integer.parseInt(position1) < Integer.parseInt(position2))
		{
			if (dspNameColName == null)
			{
				dspNameColName = new StringBuffer();
				dspNameColName.append(columnNamePositionMap.get(position1)).append(", ").append(
						columnNamePositionMap.get(position2));
			}
			else
			{
				dspNameColName.append(columnNamePositionMap.get(position1)).append(", ").append(
						columnNamePositionMap.get(position2));
			}
		}
		else
		{
			if (dspNameColName == null)
			{
				dspNameColName = new StringBuffer();
				dspNameColName.append(columnNamePositionMap.get(position2)).append(", ").append(
						columnNamePositionMap.get(position1));
			}
			else
			{
				dspNameColName.append(columnNamePositionMap.get(position2)).append(", ").append(
						columnNamePositionMap.get(position1));
			}
		}
		return dspNameColName.toString();
	}

	/**
	 * Check if the tag is present.
	 *
	 * @param metadaInterface
	 *            metadaInterface
	 * @return <CODE>true</CODE> if tag is present, <CODE>false</CODE> otherwise
	 */
	private static boolean isTagPresent(AbstractMetadataInterface metadaInterface)
	{
		boolean isTagPresent = false;
		Collection<TaggedValueInterface> taggedValueCollection = metadaInterface
				.getTaggedValueCollection();
		for (TaggedValueInterface tagValue : taggedValueCollection)
		{
			if (tagValue.getKey().equals("resultview"))
			{
				isTagPresent = true;
				break;
			}
		}
		return isTagPresent;
	}

	/**
	 * Checks if tag is present on attribute.
	 *
	 * @param metadaInterface
	 *            metadaInterface
	 * @return <CODE>true</CODE> if tag is present, <CODE>false</CODE> otherwise
	 */
	private static String isTagPresentOnAttribute(AbstractMetadataInterface metadaInterface)
	{
		String isTagPresent = null;
		Collection<TaggedValueInterface> taggedValueCollection = metadaInterface
				.getTaggedValueCollection();
		for (TaggedValueInterface tagValue : taggedValueCollection)
		{
			if (tagValue.getKey().equals("resultview"))
			{
				isTagPresent = tagValue.getValue();
				break;
			}
		}
		return isTagPresent;
	}

	/**
	 * Returns true if the attribute name can be used to form label for tree
	 * node.
	 *
	 * @param objectName
	 *            objectName
	 * @param stringArray
	 *            stringArray
	 * @return true if the attribute name can be used to form label for tree
	 *         node otherwise returns false
	 */
	public static boolean isPresentInArray(String objectName, String[] stringArray)
	{
		boolean isPresentInArray = false;
		int strLen = stringArray.length;
		for (int i = 0; i < strLen; i++)
		{
			String name = stringArray[i];
			if (name.equals(objectName))
			{
				isPresentInArray = true;
			}
		}
		return isPresentInArray;
	}

	/**
	 * This is used to set the default selections for the UI components when the
	 * screen is loaded for the first time.
	 *
	 * @param actionForm
	 *            form bean
	 * @return CategorySearchForm form bean
	 */
	public static CategorySearchForm setDefaultSelections(CategorySearchForm actionForm)
	{
		if (actionForm.getClassChecked() == null)
		{
			actionForm.setClassChecked(AQConstants.ON_STRING);
		}
		if (actionForm.getAttributeChecked() == null)
		{
			actionForm.setAttributeChecked(AQConstants.ON_STRING);
		}
		if (actionForm.getPermissibleValuesChecked() == null)
		{
			actionForm.setPermissibleValuesChecked(AQConstants.OFF);
		}
		if (actionForm.getIncludeDescriptionChecked() == null)
		{
			actionForm.setIncludeDescriptionChecked(AQConstants.OFF);
		}
		// TODO check if null and then set the value of selected.
		// Bug #5131: Commenting until the Concept Code search is fixed
		// actionForm.setSelected(Constants.TEXT_RADIOBUTTON);
		actionForm.setTextField("");
		actionForm.setPermissibleValuesChecked(AQConstants.OFF);
		return actionForm;
	}

	/**
	 * When passes treeNumber , this method returns the root node of that tree.
	 *
	 * @param queryDetailsObj
	 *            queryDetailsObj
	 * @param treeNo
	 *            number of tree
	 * @return root node of the tree
	 */
	public static OutputTreeDataNode getRootNodeOfTree(QueryDetails queryDetailsObj, String treeNo)
	{
		OutputTreeDataNode rootNodeOfTree = null;
		// List<OutputTreeDataNode> rootOpTreeNodeLst
		// =queryDetailsObj.getRootOutputTreeNodeList();

		// TODO ONLY ONE NODE IN THIS LIST
		// for (OutputTreeDataNode rootNode :
		// queryDetailsObj.getRootOutputTreeNodeList())
		// {
		OutputTreeDataNode rootNode = queryDetailsObj.getRootOutputTreeNodeList().get(0);
		// TODO rootNode.isInView is equivalent to rootNode.getTreeNo >=0; fix
		// this.
		if (rootNode.isInView())
		{
			// if (rootNode.getTreeNo() == Integer.valueOf(treeNo))
			// {
			rootNodeOfTree = rootNode;
			// }
		}
		else
		{
			List<OutputTreeDataNode> inViewChildList = QueryModuleUtil.getInViewChildren(rootNode);
			for (OutputTreeDataNode childNode : inViewChildList)
			{
				if (childNode.getTreeNo() == Integer.valueOf(treeNo))
				{
					rootNodeOfTree = childNode;
					break;
				}
			}
		}
		return rootNodeOfTree;
	}

	public static List<OutputTreeDataNode> getInViewChildren(OutputTreeDataNode parentNode)
	{
		List<OutputTreeDataNode> childList = new ArrayList<OutputTreeDataNode>();

		for (OutputTreeDataNode node : parentNode.getChildren())
		{
			childList.add(node);
			childList.addAll(getInViewChildren(node));
		}

		return childList;
	}

	/**
	 * Returns column name of nodes id when passed a node to it.
	 *
	 * @param node
	 *            {@link OutputTreeDataNode}
	 * @return String id Columns name
	 */
	public static String getParentIdColumnName(OutputTreeDataNode node)
	{
		String getParenIdColName = null;
		if (node != null)
		{
			List<QueryOutputTreeAttributeMetadata> attributes = node.getAttributes();
			for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
			{
				AttributeInterface attribute = attributeMetaData.getAttribute();
				if (AQConstants.IDENTIFIER.equals(attribute.getName()))
				{
					String sqlColumnName = attributeMetaData.getColumnName();
					getParenIdColName = sqlColumnName;
				}
			}
		}
		return getParenIdColName;
	}

	/**
	 * Sets required data for grid.
	 *
	 * @param request
	 *            HTTPRequest
	 * @param spreadSheetDtmap
	 *            Map to store spreadsheet data
	 */
	public static void setGridData(HttpServletRequest request, Map spreadSheetDtmap)
	{
		int pageNum = AQConstants.START_PAGE;
		SelectedColumnsMetadata selColumnsMdata = (SelectedColumnsMetadata) spreadSheetDtmap
				.get(AQConstants.SELECTED_COLUMN_META_DATA);
		HttpSession session = request.getSession();
		request.setAttribute(AQConstants.PAGE_NUMBER, Integer.toString(pageNum));
		QuerySessionData querySessionData = (QuerySessionData) spreadSheetDtmap
				.get(AQConstants.QUERY_SESSION_DATA);
		int totalNoOfRecords = querySessionData.getTotalNumberOfRecords();
		List<List<String>> dataList = (List<List<String>>) spreadSheetDtmap
				.get(AQConstants.SPREADSHEET_DATA_LIST);
		request.setAttribute(AQConstants.PAGINATION_DATA_LIST, dataList);
		List columnsList = (List) spreadSheetDtmap.get(AQConstants.SPREADSHEET_COLUMN_LIST);
		if (columnsList != null)
		{
			session.setAttribute(AQConstants.SPREADSHEET_COLUMN_LIST, columnsList);
		}
		session.setAttribute(AQConstants.TOTAL_RESULTS, Integer.valueOf(totalNoOfRecords));
		session.setAttribute(AQConstants.QUERY_SESSION_DATA, querySessionData);
		session.setAttribute(AQConstants.SELECTED_COLUMN_META_DATA, selColumnsMdata);
		session.setAttribute(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP, spreadSheetDtmap
				.get(AQConstants.QUERY_REASUL_OBJECT_DATA_MAP));
		session.setAttribute(AQConstants.DEFINE_VIEW_RESULT_MAP, spreadSheetDtmap
				.get(AQConstants.DEFINE_VIEW_RESULT_MAP));
		session.setAttribute(AQConstants.MAIN_ENTITY_MAP, spreadSheetDtmap
				.get(AQConstants.MAIN_ENTITY_MAP));
		String pageOf = request.getParameter(AQConstants.PAGEOF);
		if (pageOf == null)
		{
			pageOf = AQConstants.PAGE_OF_QUERY_MODULE;
		}
		request.setAttribute(AQConstants.PAGEOF, pageOf);
	}

	/**
	 * Checks if rule is present in DAG.
	 *
	 * @param query
	 *            query
	 * @return <CODE>true</CODE> if rule is present, <CODE>false</CODE>
	 *         otherwise
	 */
	public static boolean checkIfRulePresentInDag(IQuery query)
	{
		boolean isRulePresentInDag = false;

		if (query != null)
		{
			IConstraints constraints = query.getConstraints();
			for (IExpression expression : constraints)
			{
				if (expression.containsRule())
				{
					isRulePresentInDag = true;
					break;
				}
			}
		}
		return isRulePresentInDag;
	}

	/**
	 * Method to call searchQuery and to set appropriate error message.
	 *
	 * @param request
	 *            object of HttpServletRequest
	 * @param parameterizedQuery
	 *            object of IParameterizedQuery
	 * @return errorMessage String value for errorMessage
	 */
	public static String executeQuery(HttpServletRequest request, IQuery parameterizedQuery)
	{
		boolean isRulePresentInDag = checkIfRulePresentInDag(parameterizedQuery);
		QueryModuleError errorCode;
		QueryModuleSearchQueryUtil qMSearchQuery = new QueryModuleSearchQueryUtil(request,
				parameterizedQuery);
		if (isRulePresentInDag)
		{
			errorCode = qMSearchQuery.searchQuery();
		}
		else
		{
			errorCode = QueryModuleError.EMPTY_DAG;
		}
		return getErrorMessage(parameterizedQuery, errorCode);
	}

	/**
	 * This method returns the appropriate error message depending upon the
	 * error code.
	 *
	 * @param parameterizedQuery
	 *            query
	 * @param errorCode
	 *            code
	 * @return String error message
	 */
	private static String getErrorMessage(IQuery parameterizedQuery, QueryModuleError errorCode)
	{
		String errorMessage;
		switch (errorCode)
		{
			case EMPTY_DAG :
				errorMessage = "<li><font color='blue' family='arial,helvetica,verdana,sans-serif'>"
						+ ApplicationProperties.getValue("query.empty.dag") + "</font></li>";
				break;
			case MULTIPLE_ROOT :
				errorMessage = "<li><font color='red'> "
						+ ApplicationProperties.getValue("errors.executeQuery.multipleRoots")
						+ "</font></li>";
				break;
			case NO_RESULT_PRESENT :
				errorMessage = "<li><font color='green' family='arial,helvetica,verdana,sans-serif'>"
						+ ((IParameterizedQuery) parameterizedQuery).getName()
						+ " : "
						+ ApplicationProperties.getValue("query.zero.records.present")
						+ "</font></li>";
				break;
			case SQL_EXCEPTION :
			case DAO_EXCEPTION :
			case CLASS_NOT_FOUND :
				errorMessage = ApplicationProperties.getValue("errors.executeQuery.genericmessage");
				break;
			case RESULTS_MORE_THAN_LIMIT :
				errorMessage = AQConstants.TREE_ND_LMT_EXCEED_REC;
				break;
			case INVALID_METADATA:
				errorMessage = ApplicationProperties.getValue("invalid.metadata.message");
				break;
			default :
				errorMessage = "";
		}
		return errorMessage;
	}

	/**
	 * It will generate random number.
	 *
	 * @param session
	 *            session
	 * @return String
	 */
	public static String generateRandomNumber(HttpSession session)
	{
		String randomNumber;
		if (session.getAttribute(AQConstants.RANDOM_NUMBER) == null)
		{
			int number = (int) (Math.random() * 100000);
			randomNumber = AQConstants.UNDERSCORE + Integer.toString(number);
			session.setAttribute(AQConstants.RANDOM_NUMBER, randomNumber);
		}
		else
		{
			randomNumber = (String) session.getAttribute(AQConstants.RANDOM_NUMBER);
		}
		return randomNumber;
	}
}