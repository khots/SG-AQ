
package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.QueryTreeNodeData;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.impl.OutputAttribute;
import edu.wustl.common.util.Utility;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryCSMUtil;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.TemporalColumnUIBean;

/**
 * This bizlogic is called when user wants to define columns to be shown for grid results.
 * @author deepti_shelar
 *
 */
public class DefineGridViewBizLogic
{

	/**
	 * This list denotes the selected columns in a session.
	 */
	private List<NameValueBean> selectedColList;

	/**
	 * @return the selectedColList
	 */
	public List<NameValueBean> getSelectedColList()
	{
		return selectedColList;
	}

	/**
	 * @param selectedColList the selectedColList to set
	 */
	public void setSelectedColList(List<NameValueBean> selectedColList)
	{
		this.selectedColList = selectedColList;
	}

	/**
	 * returns class name when passed a OutputTreeDataNode.
	 * @param node OutputTreeDataNode
	 * @return String className
	 */
	public String getClassName(OutputTreeDataNode node)
	{
		String className = null;
		if (node != null)
		{
			IOutputEntity outputEntity = node.getOutputEntity();
			EntityInterface deEntity = outputEntity.getDynamicExtensionsEntity();
			className = deEntity.getName();
			className = Utility.parseClassName(className);
		}
		return className;
	}

	/**
	 * Creates tree structure data required for defining grid view.
	 * @param categorySearchForm action form
	 * @param queryDetailsObj Object of QueryDetails
	 * @param treeDataVector vector to store tree data
	 * @param currentSelectedObj Object of OutputTreeDataNode
	 * @param prevSelCNVBList List of NameValueBean
	 */
	public void createTree(CategorySearchForm categorySearchForm, QueryDetails queryDetailsObj,
			List<QueryTreeNodeData> treeDataVector, OutputTreeDataNode currentSelectedObj,
			List<NameValueBean> prevSelCNVBList)
	{
		this.selectedColList = prevSelCNVBList;
		if (!queryDetailsObj.getUniqueIdNodesMap().isEmpty())
		{
			addRootNode(treeDataVector);
			Set keySet = queryDetailsObj.getUniqueIdNodesMap().keySet();
			Iterator iterator = keySet.iterator();
			Object identifier;
			OutputTreeDataNode node;
			while (iterator.hasNext())
			{
				identifier = iterator.next();
				node = null;
				if (identifier instanceof String)
				{
					String nodeId = identifier.toString();
					node = queryDetailsObj.getUniqueIdNodesMap().get(nodeId);
					if(node.isInView())
					{
					  addClassAndAttributeNodes(treeDataVector, node, categorySearchForm,
  					currentSelectedObj);
					}
				}
			}
		}
	}

	/**
	 * Adds root node to the tree, its a just a label node saying classes present in query.
	 * @param treeDataVector  vector to store tree data
	 */
	private void addRootNode(List<QueryTreeNodeData> treeDataVector)
	{
		QueryTreeNodeData classTreeNode = new QueryTreeNodeData();
		String rootNodeId = AQConstants.ROOT;
		classTreeNode.setIdentifier(rootNodeId);
		classTreeNode.setObjectName(rootNodeId);
		String rootDiplayName = AQConstants.CLASSES_PRESENT_IN_QUERY;
		classTreeNode.setDisplayName(rootDiplayName);
		classTreeNode.setParentIdentifier(AQConstants.ZERO_ID);
		classTreeNode.setParentObjectName("");
		treeDataVector.add(classTreeNode);
	}

	/**
	 * This method adds class node to the tree.
	 * And calls addAttributeNodes method to add attribute nodes to tree.
	 * @param treeDataVector  vector to store tree data
	 * @param node OutputTreeDataNode node to be added in tree
	 * @param categorySearchForm action form
	 * @param currentSelectedObject  OutputTreeDataNode
	 */
	private void addClassAndAttributeNodes(List<QueryTreeNodeData> treeDataVector,
			OutputTreeDataNode node, CategorySearchForm categorySearchForm,
			OutputTreeDataNode currentSelectedObject)
	{
		long selectedObjectId = currentSelectedObject.getId();
		long nodeId = node.getId();
		String uniqueNodeId = node.getUniqueNodeId();
		IOutputEntity outputEntity = node.getOutputEntity();
		EntityInterface dynamicExtensionsEntity = outputEntity.getDynamicExtensionsEntity();
		String className = dynamicExtensionsEntity.getName();
		className = Utility.parseClassName(className);
		String classDisplayName = Utility.getDisplayLabel(className);
		String treeClassNodeId = AQConstants.CLASS + AQConstants.UNDERSCORE + uniqueNodeId
				+ AQConstants.UNDERSCORE + className;
		QueryTreeNodeData classTreeNode = new QueryTreeNodeData();
		classTreeNode.setIdentifier(treeClassNodeId);
		classTreeNode.setObjectName(className);
		classTreeNode.setDisplayName(classDisplayName);
		classTreeNode.setParentIdentifier(AQConstants.ROOT);
		classTreeNode.setParentObjectName("");
		treeDataVector.add(classTreeNode);
		boolean isSelectedObject = false;
		if (selectedObjectId == nodeId)
		{
			isSelectedObject = true;
			if (selectedColList == null)
			{
				categorySearchForm.setCurrentSelectedNodeInTree(treeClassNodeId);
			}
		}
		addAttributeNodes(treeDataVector, className, treeClassNodeId, categorySearchForm, node
				.getAttributes(), isSelectedObject);
	}

	/**
	 * Adds attribute nodes to tree.
	 * @param treeDataVector vector to store tree data
	 * @param className String class name
	 * @param treeClassNodeId parent id to add to tree
	 * @param categorySearchForm action form
	 * @param attributeMetadataList  Meta data to be added
	 * @param isSelectedObject whether the object is selected
	 */
	private void addAttributeNodes(List<QueryTreeNodeData> treeDataVector, String className,
			String treeClassNodeId, CategorySearchForm categorySearchForm,
			List<QueryOutputTreeAttributeMetadata> attributeMetadataList, boolean isSelectedObject)
	{
		//List<QueryOutputTreeAttributeMetadata> attributeMetadataList = node.getAttributes();
		List<NameValueBean> selectedColumnNameValue = new ArrayList<NameValueBean>();
		AttributeInterface attribute;
		String attributeName;
		String attributeDisplayName;
		String attributeWithClassName;
		String treeAttributeNodeId;
		QueryTreeNodeData attributeTreeNode;
		NameValueBean nameValueBean;
		for (QueryOutputTreeAttributeMetadata attributeMetadata : attributeMetadataList)
		{
			attribute = attributeMetadata.getAttribute();
			attributeName = attribute.getName();
			attributeDisplayName = Utility.getDisplayLabel(attributeName);
			attributeWithClassName = attributeMetadata.getDisplayName();
			treeAttributeNodeId = attributeMetadata.getUniqueId();
			attributeTreeNode = new QueryTreeNodeData();
			attributeTreeNode.setIdentifier(treeAttributeNodeId);
			attributeTreeNode.setObjectName(attributeName);
			attributeTreeNode.setDisplayName(attributeDisplayName);
			attributeTreeNode.setParentIdentifier(treeClassNodeId);
			attributeTreeNode.setParentObjectName(className);
			treeDataVector.add(attributeTreeNode);
			nameValueBean = new NameValueBean();
			nameValueBean.setName(attributeWithClassName);
			nameValueBean.setValue(treeAttributeNodeId);
			selectedColumnNameValue.add(nameValueBean);
		}
		if (selectedColList != null)
		{
			categorySearchForm
					.setSelColNVBeanList(selectedColList);
		}
		else if (isSelectedObject)
		{
			categorySearchForm.setSelColNVBeanList(selectedColumnNameValue);
		}
	}

	/**
	 * returns list of selected columns.
	 * @param categorySearchForm The categorySearchForm
	 * @param queryDetailsObj QueryDetails object
	 * @param  selectedColumnsMetadata Object of SelectedColumnsMetadata
	 * @param constraints Constraints on query
	 */
	public void getSelectedColumnsMetadata(CategorySearchForm categorySearchForm,
			QueryDetails queryDetailsObj, SelectedColumnsMetadata selectedColumnsMetadata,
			IConstraints constraints)
	{
		String[] selectedColumnIds = categorySearchForm.getSelectedColumnNames();
		List<QueryOutputTreeAttributeMetadata> attribureMetadataList =
			new ArrayList<QueryOutputTreeAttributeMetadata>();
		List<IOutputAttribute> outputAttributeList = new ArrayList<IOutputAttribute>();
		List<NameValueBean> selectedColumnNameValue = new ArrayList<NameValueBean>();
		IOutputAttribute attr;
		String columnId;
		//String[] split;
		String expressionId;
		OutputTreeDataNode outputTreeDataNode;
		int lenOfCIds = selectedColumnIds.length;
		for (int i = 0; i < lenOfCIds; i++)
		{
			attr = null;
			columnId = selectedColumnIds[i];
			String[] split = columnId.split(AQConstants.EXPRESSION_ID_SEPARATOR);
			expressionId = split[AQConstants.INDEX_PARAM_ZERO];
			outputTreeDataNode = getMatchingOutputTreeDataNode(queryDetailsObj, expressionId);
			if (outputTreeDataNode != null)
			{
				List<QueryOutputTreeAttributeMetadata> attributes = outputTreeDataNode
						.getAttributes();
				NameValueBean nameValueBean;
				for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
				{
					if (columnId.equals(attributeMetaData.getUniqueId()))
					{
						attribureMetadataList.add(attributeMetaData);
						attr = new OutputAttribute(constraints.getExpression
						(outputTreeDataNode.getExpressionId()),
						attributeMetaData.getAttribute());
						outputAttributeList.add(attr);
						nameValueBean = new NameValueBean(
						attributeMetaData.getDisplayName(),
						attributeMetaData.getUniqueId());
						selectedColumnNameValue.add(nameValueBean);
						break;
					}
				}
			}
		}
		selectedColumnsMetadata.setSelectedAttributeMetaDataList(attribureMetadataList);
		selectedColumnsMetadata.setSelectedOutputAttributeList(outputAttributeList);
		selectedColumnsMetadata.setSelColNVBeanList(selectedColumnNameValue);
	}

	/**
	 * Returns the matching OutputTreeDataNode, compares expression id.
	 * @param queryDetailsObj QueryDetails object
	 * @param expressionId string id
	 * @return OutputTreeDataNode node
	 */
	private OutputTreeDataNode getMatchingOutputTreeDataNode(QueryDetails queryDetailsObj,
			String expressionId)
	{
		OutputTreeDataNode outputTreeDataNode = null;
		Set<String> keySet = queryDetailsObj.getUniqueIdNodesMap().keySet();
		Iterator<String> iterator = keySet.iterator();
		Object next;
		while (iterator.hasNext())
		{
			next = iterator.next();
			if (next instanceof String)
			{
				outputTreeDataNode = queryDetailsObj.getUniqueIdNodesMap().get(next);
				if (Integer.valueOf(outputTreeDataNode.getExpressionId()).toString().equals(
						expressionId))
				{
					break;
				}
			}
		}
		return outputTreeDataNode;
	}

	/**
	 *
	 * @param categorySearchForm CategorySearchForm object
	 * @param selectedAttributeMetaDataList List of QueryOutputTreeAttributeMetadata objects
	 * @param selectedColumnNames Selected column names
	 * @param queryResultObjecctDataMap queryResultObjecctDataMap
	 * @param queryDetailsObj QueryDetails object
	 * @param outputTermsColumns Map containing details of output terms
	 * @param nodeData node data
	 * @return definedColumnsList The selected column list
	 */
	public List<String> getSelectedColumnList(CategorySearchForm categorySearchForm,
			List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList,
			StringBuffer selectedColumnNames,
			Map<Long, QueryResultObjectDataBean> queryResultObjecctDataMap,
			QueryDetails queryDetailsObj, Map<String, IOutputTerm> outputTermsColumns,
			String nodeData,Map<String, String> specimenMap)
	{
		queryResultObjecctDataMap.clear();
		List<String> definedColumnsList = new ArrayList<String>();
		List<NameValueBean> selectedColumnNameValue = new ArrayList<NameValueBean>();
		Map<String, String> columnNameVsAliasMap= queryDetailsObj.getColumnNameVsAliasMap();
		QueryResultObjectDataBean queryResulObjectDataBean = null;

		List<EntityInterface> defineViewNodeList = new ArrayList<EntityInterface>();
		int columnIndex = 0;
		int totalFileTypeAttributes = 0;
		Iterator<QueryOutputTreeAttributeMetadata> iterator = selectedAttributeMetaDataList
				.iterator();
		QueryOutputTreeAttributeMetadata element;
		while (iterator.hasNext())
		{
			element = iterator.next();
			queryResulObjectDataBean = queryResultObjecctDataMap.get(element.getTreeDataNode()
					.getId());
			if (queryResulObjectDataBean == null)
			{
				queryResulObjectDataBean = QueryCSMUtil.getQueryResulObjectDataBean(element
						.getTreeDataNode(), queryDetailsObj);
				queryResultObjecctDataMap.put(element.getTreeDataNode().getId(),
						queryResulObjectDataBean);
				defineViewNodeList.add(element.getTreeDataNode().getOutputEntity()
						.getDynamicExtensionsEntity());
			}

			if (element.getAttribute().getIsIdentified() != null
					&& element.getAttribute().getIsIdentified())
			{
				queryResulObjectDataBean.getIdentifiedDataColumnIds().add(columnIndex);
			}
			if (AQConstants.IDENTIFIER.equals(element.getAttribute().getName()))
			{
				/*if (queryResulObjectDataBean.isMainEntity())
				{*/
					queryResulObjectDataBean.setMainEntityIdentifierColumnId(columnIndex);
				/*}
				else
				{*/
					queryResulObjectDataBean.setEntityId(columnIndex);
				//}
			}
			if (AQConstants.FILE.equalsIgnoreCase(element.getAttribute().getDataType()))
			{
				queryResulObjectDataBean.setClobeType(true);
				Map beanMetadataMap = queryResulObjectDataBean
						.getFileTypeAtrributeIndexMetadataMap();
				int fileTypeIndex = columnIndex + totalFileTypeAttributes;
				beanMetadataMap.put(fileTypeIndex, element);
				queryResulObjectDataBean.setFileTypeAtrributeIndexMetadataMap(beanMetadataMap);
				totalFileTypeAttributes++;
			}
			else
			{
				queryResulObjectDataBean.getObjectColumnIds().add(columnIndex);
				selectedColumnNames.append(columnNameVsAliasMap.get(element.getColumnName())+" "+ element.getColumnName());
				selectedColumnNames.append(AQConstants.DELIMETER);
				definedColumnsList.add(element.getDisplayName());
				columnIndex++;
			}
			NameValueBean nameValueBean = new NameValueBean(element.getDisplayName(), element
					.getUniqueId());
			selectedColumnNameValue.add(nameValueBean);
		}
		Iterator<Long> mapItr = queryResultObjecctDataMap.keySet().iterator();
		String sql = "";
		if (selectedColumnNames.lastIndexOf(AQConstants.DELIMETER) != -1)
		{
			sql = selectedColumnNames.substring(0, selectedColumnNames
					.lastIndexOf(AQConstants.DELIMETER));
		}
		if (!outputTermsColumns.isEmpty())
		{
			QueryOutputSpreadsheetBizLogic gridBizLogic = new QueryOutputSpreadsheetBizLogic();
			IConstraints constraints = queryDetailsObj.getQuery().getConstraints();
			TemporalColumnUIBean temporalColumnUIBean = new TemporalColumnUIBean(null, sql,
					definedColumnsList, outputTermsColumns, columnIndex, constraints);
			queryResulObjectDataBean.setTqColumnMetadataList
			(gridBizLogic.modifySqlForTemporalColumns(temporalColumnUIBean, queryDetailsObj,
					nodeData));
			//sql = temporalColumnUIBean.getSql();
			sql = sql +", "+ columnNameVsAliasMap.get("temporal");
			columnIndex = temporalColumnUIBean.getColumnIndex();
		}
		selectedColumnNames.replace(0, selectedColumnNames.length(), sql);
		while (mapItr.hasNext())
		{
			queryResulObjectDataBean = queryResultObjecctDataMap.get(mapItr.next());
//			if (queryResulObjectDataBean.getMainEntityIdentifierColumnId() == -1)
			{
				Map<EntityInterface, Integer> entityIdIndexMap =
					new HashMap<EntityInterface, Integer>();
				sql = QueryCSMUtil.updateEntityIdIndexMap(queryResulObjectDataBean, columnIndex,
						sql, defineViewNodeList, entityIdIndexMap, queryDetailsObj,specimenMap);
				selectedColumnNames.replace(AQConstants.ARGUMENT_ZERO, selectedColumnNames
						.length(), sql);
				if (queryResulObjectDataBean.isMainEntity())
				{
					EntityInterface entity = queryResulObjectDataBean.getEntity();
					Integer integer = queryResulObjectDataBean.
					getEntityIdIndexMap().get(entity);
					queryResulObjectDataBean.setMainEntityIdentifierColumnId(integer);
				}
				else
				{
					EntityInterface mainEntity = queryResulObjectDataBean.getMainEntity();
					if (queryResulObjectDataBean.getEntityIdIndexMap().get(mainEntity) != null)
					{
						queryResulObjectDataBean.setMainEntityIdentifierColumnId
						(queryResulObjectDataBean.getEntityIdIndexMap().get(mainEntity));
					}
				}
			}
		}
		categorySearchForm.setSelColNVBeanList(selectedColumnNameValue);
		return definedColumnsList;
	}

	/**
	 * @param columnsInSql Selected columns
	 * @param sql formed for newly defined columns
	 * @return String query
	 */
	public String createSQLForSelectedColumn(String columnsInSql, String sql)
	{
		//String columnsInSql = selectedColumnNames.toString();
		/*int indexOfSelectDistict = sql.indexOf("select");
		int selectDistictLength = "select".length();
		int indexOfFrom = sql.indexOf(AQConstants.FROM);
		int indexOfWhere = sql.indexOf(AQConstants.WHERE);
		int indexOfIs = sql.indexOf(" is ");
		StringBuffer newSql = new StringBuffer();
		newSql.append(sql.substring(indexOfSelectDistict, selectDistictLength));
		newSql.append(' ');
		newSql.append(columnsInSql);
		newSql.append(sql.substring(indexOfFrom));
		if(newSql.indexOf("ORDER BY") == -1)
		{
			newSql.append(" ORDER BY").append(sql.substring(indexOfWhere+6, indexOfIs));
		}*/
		
		String newSql = "SELECT DISTINCT "+ columnsInSql + " " + sql.substring(sql.indexOf("from"));
		return newSql;
	}

	/**
	 * returns list of selected columns.
	 * @param selectedColumnsMetadata Object of SelectedColumnsMetadata
	 * @param outputTreeDataNode Object of OutputTreeDataNode
	 * @param constraints The constraints of the query
	 * @return SelectedColumnsMetadata SelectedColumnsMetadata
	 */
	public SelectedColumnsMetadata getColumnsMetadataForSelectedNode(
			OutputTreeDataNode outputTreeDataNode, SelectedColumnsMetadata selectedColumnsMetadata,
			IConstraints constraints)
	{
		if (outputTreeDataNode != null)
		{
			List<IOutputAttribute> selectedOutputAttributeList = new ArrayList<IOutputAttribute>();
			List<QueryOutputTreeAttributeMetadata> attributes = outputTreeDataNode.getAttributes();
			selectedColumnsMetadata.setSelectedAttributeMetaDataList(attributes);
			if (selectedColumnsMetadata.isDefinedView())
			{
				AttributeInterface attribute;
				OutputAttribute attr;
				for (QueryOutputTreeAttributeMetadata metadata : attributes)
				{
					attribute = metadata.getAttribute();
					attr = new OutputAttribute(constraints.getExpression(outputTreeDataNode
							.getExpressionId()), attribute);
					selectedOutputAttributeList.add(attr);
				}
				selectedColumnsMetadata.setSelectedOutputAttributeList
				(selectedOutputAttributeList);
			}
		}
		return selectedColumnsMetadata;
	}

	/**
	 *
	 * @param values Collection of OutputTreeDataNode
	 * @param selectedAttributeList selected attribute list
	 * @param selectedColumnsMetadata Object of SelectedColumnsMetadata
	 */
	public void getSelectedColumnMetadataForSavedQuery(Collection<OutputTreeDataNode> values,
			List<IOutputAttribute> selectedAttributeList,
			SelectedColumnsMetadata selectedColumnsMetadata)
	{
		//Collection<OutputTreeDataNode> values = uniqueIdNodesMap.values();
		List<QueryOutputTreeAttributeMetadata> attribureMetadataList
			= new ArrayList<QueryOutputTreeAttributeMetadata>();
		Set newSet = new HashSet<QueryOutputTreeAttributeMetadata>();
		List<NameValueBean> selectedColumnNameValue = new ArrayList<NameValueBean>();
		for(OutputTreeDataNode node:values)
		{
			for(IOutputAttribute outAttr :selectedAttributeList)
			{
				EntityInterface dynamicExtensionsEntity =
					node.getOutputEntity().getDynamicExtensionsEntity();
				while(dynamicExtensionsEntity != null)
				{
					if(outAttr.getAttribute().getEntity().getId().equals(dynamicExtensionsEntity.getId()))
					{
						populateAttributeMetadata(attribureMetadataList, newSet,
								selectedColumnNameValue, node, outAttr);
						break;
					}
					else
					{
						dynamicExtensionsEntity = dynamicExtensionsEntity.getParentEntity();
					}
				}
			}
		}
		selectedColumnsMetadata.setSelectedAttributeMetaDataList(attribureMetadataList);
		selectedColumnsMetadata.setSelColNVBeanList(selectedColumnNameValue);
	}

	/**
	 * @param attribureMetadataList attribureMetadataList
	 * @param newSet newSet
	 * @param selectedColumnNameValue selectedColumnNameValue
	 * @param node node
	 * @param outAttr outAttr
	 */
	private void populateAttributeMetadata(
			List<QueryOutputTreeAttributeMetadata> attribureMetadataList,
			Set newSet, List<NameValueBean> selectedColumnNameValue,
			OutputTreeDataNode node, IOutputAttribute outAttr)
	{
		List<QueryOutputTreeAttributeMetadata> attributes = node.getAttributes();
		for(QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
		{
			if(outAttr.getAttribute().equals(attributeMetaData.getAttribute()))
			{
				boolean add = newSet.add(attributeMetaData);
				NameValueBean nameValueBean;
				if(add)
				{
					attribureMetadataList.add(attributeMetaData);
					nameValueBean = new NameValueBean(attributeMetaData
							.getDisplayName(), attributeMetaData.getUniqueId());
					selectedColumnNameValue.add(nameValueBean);
					break;
				}
			}
		}
	}
}
