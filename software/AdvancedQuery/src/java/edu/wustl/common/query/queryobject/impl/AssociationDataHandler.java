package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.wustl.common.query.queryobject.impl.RecordProcessor.TreeCell;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.query.util.global.Utility;

public class AssociationDataHandler
{
	/**
	 * This method will update the Maps needed for generating the Header
	 * & data list.
	 * @param denormalizedLst denormalizationMap
	 * @param rootExp rootExp
	 * @param dataHandler dataHandler
	 * @param selectedColumnsMetadata
	 * @param mapIndex mapIndex
	 */
	public Table<TreeCell> updateRowDataList(final List<Map<OutputAssociationColumn,Object>> denormalizedLst, final IExpression rootExp, QueryExportDataHandler dataHandler, SelectedColumnsMetadata selectedColumnsMetadata)
	{
		final QueryHeaderData queryDataEntity = new QueryHeaderData(rootExp.getQueryEntity().getDynamicExtensionsEntity(), rootExp);
		final List<RecordProcessorNode<TreeCell>> nodes =
			generateQueryDatamap(denormalizedLst, queryDataEntity, null, dataHandler,selectedColumnsMetadata);
		final RowAppenderProcNode<TreeCell> root = new RowAppenderProcNode<TreeCell>();
        root.addChildren(nodes);
        return root.getTable(TreeCell.EMPTY_CELL);
	}

	/**
	 * This method will update the maps required for exporting the data
	 * (i.e. the data to be shown in the spreadsheet).
	 * @param denormalizedList denormalizedList
	 * @param queryDataEntity queryDataCont
	 * @param dataHandler dataHandler
	 * @param selectedColumnsMetadata
	 * @param entityVsDataList entityVsDataList
	 * @param dataCnt dataCnt
	 */
	private List<RecordProcessorNode<TreeCell>> generateQueryDatamap(
			final List<Map<OutputAssociationColumn, Object>> denormalizedList,
			final QueryHeaderData queryDataEntity, final TreeCell parent, QueryExportDataHandler dataHandler, SelectedColumnsMetadata selectedColumnsMetadata)
	{
        final List<RecordProcessorNode<TreeCell>> res = new ArrayList<RecordProcessorNode<TreeCell>>();
        for (Map<OutputAssociationColumn, Object> rec : denormalizedList)
        {
        	final TreeCell cell = new TreeCell(queryDataEntity, rec);
            cell.setParentCell(parent);
            final RecordProcessorNode<TreeCell> recordProcNode = new RecordProcessorNode<TreeCell>(cell);
            res.add(recordProcNode);

            for (Map.Entry<OutputAssociationColumn, Object> entry : rec.entrySet())
            {
                if (entry.getKey().getAbstractAttr() instanceof AssociationInterface)
                {
                    processAssociations(cell, recordProcNode, entry, dataHandler,selectedColumnsMetadata);
                }
            }
        }
        return res;
	}

	/**
	 * To process the association attribute i.e. retrieves the inner map and
	 * gives call to generateQueryDatamap(...) method.
	 * @param cell cell
	 * @param recordProcNode recordProcNode
	 * @param entry entry
	 * @param dataHandler dataHandler
	 * @param selectedColumnsMetadata
	 */
	private void processAssociations(final TreeCell cell,
			final RecordProcessorNode<TreeCell> recordProcNode,
			final Map.Entry<OutputAssociationColumn, Object> entry, QueryExportDataHandler dataHandler, SelectedColumnsMetadata selectedColumnsMetadata)
	{
		final AssociationInterface association = (AssociationInterface) entry.getKey().getAbstractAttr();
		AbstractTableProcessorNode<TreeCell> childNode;
		if (association.getAssociationType() == AssociationType.CONTAINTMENT)
		{
		    childNode = new ColumnAppenderProcNode<TreeCell>();
		}
		else
		{
		    childNode = new RowAppenderProcNode<TreeCell>();
		}
		recordProcNode.addChild(childNode);
		final List<Map<OutputAssociationColumn, Object>> childRecs =
			(List<Map<OutputAssociationColumn, Object>>) entry.getValue();
		IExpression expression = entry.getKey().getTgtExpression();
		final QueryHeaderData queryHeaderData = new QueryHeaderData(expression.getQueryEntity().getDynamicExtensionsEntity(),
					expression);
		final List<Map<OutputAssociationColumn, Object>> newChildList =
			dataHandler.updateTempList(childRecs,expression,selectedColumnsMetadata);
		if (!newChildList.isEmpty())
		{
			childNode.addChildren(generateQueryDatamap(newChildList, queryHeaderData, cell, dataHandler,selectedColumnsMetadata));
		}
	}

	/**
     * Traverse through the table and populate the list(List<List<Object>>)
     * which contains the final result to be shown on the spreadsheet.
     * @param table table
	 * @param dataHandler dataHandler
	 * @param selectedColumnsMetadata
     * @return entityDataList
     */
    public List<List<OutputAttributeColumn>> getEntityDataList(final Table<TreeCell> table, QueryExportDataHandler dataHandler, SelectedColumnsMetadata selectedColumnsMetadata)
	{
    	Map<QueryHeaderData, Integer>entityVsMaxCnt = getEntityVsMxCnt(table);
    	dataHandler.entityVsMaxCount = entityVsMaxCnt;
    	final List<List<OutputAttributeColumn>> entityDataList = new ArrayList<List<OutputAttributeColumn>>(); // this is the method which is adding those output attributes in line to the List
    	Map<QueryHeaderData,List<TreeCell>> treeCellMap;
    	List<OutputAttributeColumn> rowDataList;
        for (int i = 0; i < table.numRows(); i++)
        {
        	treeCellMap = new HashMap<QueryHeaderData, List<TreeCell>>();
            for (int j = 0; j < table.numColumns(); j++)
            {
            	TreeCell cell = table.get(i,j);
            	if(cell != TreeCell.EMPTY_CELL )
            	{
            		List<TreeCell>treeCellList;
            		if(treeCellMap.get(cell.getQueryHeaderData()) == null)
            		{
            			treeCellList = new ArrayList<TreeCell>();
            			treeCellList.add(cell);
            			treeCellMap.put(cell.getQueryHeaderData(), treeCellList);
            		}
            		else
            		{
            			treeCellList = treeCellMap.get(cell.getQueryHeaderData());
            			treeCellList.add(cell);
            		}
            	}
            }
            rowDataList = populateRowData(treeCellMap, entityVsMaxCnt,selectedColumnsMetadata);// this method adds all denormalised output attribtues in the RowDataList without sorting according to the defined View.
            entityDataList.add(rowDataList);
        }
        return entityDataList;
    }

    /**
     * Populate entity v/s max count by traversing the entire table.
     * @param table table
     * @return entityVsMaxCnt
     */
    private Map<QueryHeaderData, Integer> getEntityVsMxCnt(Table<TreeCell> table)
    {
    	Map<QueryHeaderData, Integer>tempMap;
    	Map<QueryHeaderData, Integer>entityVsMaxCnt = new HashMap<QueryHeaderData, Integer>();
    	for (int i = 0; i < table.numRows(); i++)
        {
    		tempMap = new HashMap<QueryHeaderData, Integer>();
            for (int j = 0; j < table.numColumns(); j++)
            {
            	TreeCell cell = table.get(i,j);
            	// this check is needed cause if the cell is only in view then this check is needed other wise it shows one set column of extra for null records.
            	if(cell != TreeCell.EMPTY_CELL && !(cell.getRec().isEmpty() && !cell.getQueryHeaderData().getExpression().containsRule()))
            	{
	            	if(tempMap.get(cell.getQueryHeaderData()) == null)
	            	{
	            		tempMap.put(cell.getQueryHeaderData(), 1);
	            	}
	            	else
	            	{
	            		Integer cnt = tempMap.get(cell.getQueryHeaderData());
	            		tempMap.put(cell.getQueryHeaderData(), cnt+1);
	            	}
            	}
            }
            if(entityVsMaxCnt.isEmpty())
            {
            	entityVsMaxCnt = tempMap;
            }
            else
            {
            	for(QueryHeaderData queryData : tempMap.keySet())
            	{
                	// this check is needed cause if the cell is only in view, & first record does not contains any data for that entity then second record should update the count.
            		if(entityVsMaxCnt.get(queryData)==null || tempMap.get(queryData)>entityVsMaxCnt.get(queryData))
            		{
            			entityVsMaxCnt.put(queryData, tempMap.get(queryData));
            		}
            	}
            }
        }
		return entityVsMaxCnt;
	}

	/**
     * Populate each row of the table with data from the map.
     * @param treeCellMap treeCellMap
	 * @param selectedColumnsMetadata
	 * @param dataHandler
     * @param dataHandler dataHandler
     * @return rowDataList
     */
    private List<OutputAttributeColumn> populateRowData(Map<QueryHeaderData,List<TreeCell>> treeCellMap,
    		 Map<QueryHeaderData, Integer>entityVsMaxCnt, SelectedColumnsMetadata selectedColumnsMetadata)
    {
    	//first iterate on each query header & sort the the output attributes in each Record of the queryHeader, as well as find out the lowest index,
    	//of each header.
    	//Now iterate on each header according to sort order of its lowest index & then merge all the attributes in one list.
    	Map<QueryHeaderData,List<List<OutputAttributeColumn>>> queryHeaderVsRecordList = new HashMap<QueryHeaderData, List<List<OutputAttributeColumn>>>();
    	Map<Integer,QueryHeaderData> indexVsHeaderMap = new TreeMap<Integer,QueryHeaderData >();

    	for(QueryHeaderData queryHeaderData : treeCellMap.keySet())
    	{
    		List<TreeCell> cellList = treeCellMap.get(queryHeaderData);
    		int listSize = cellList.size();
    		Integer maxRecordCnt = entityVsMaxCnt.get(queryHeaderData);
    		if(maxRecordCnt==null)
    		{
    			maxRecordCnt = 1;
    		}
    		int index = 0;
    		for(TreeCell cell : cellList)
    		{
    			Map<OutputAssociationColumn, Object> record = cell.getRec();
    			if(!record.isEmpty())
    			{
    			List<OutputAttributeColumn> rowDataList = new ArrayList<OutputAttributeColumn>();
    	        for (OutputAssociationColumn key : record.keySet())
    	    	{

    	        	if(key.getAbstractAttr() instanceof AttributeInterface)
    	        	{
    	        		final OutputAttributeColumn opAttributeColumn =
    	        			(OutputAttributeColumn)record.get(key);
    	        		rowDataList.add(opAttributeColumn);
    	        		//set attribute header
    	        		setHeaderDisplayName(index, opAttributeColumn);
    	        	}
    	    	}
    	        updateQueryHeaderRecordMap(queryHeaderVsRecordList, indexVsHeaderMap,
						queryHeaderData, rowDataList);
    	        index++;
    			}
    		}


    		while(index<maxRecordCnt)
    		{
    			Map<OutputAssociationColumn, Object> record = cellList.get(0).getRec();
    			List<OutputAttributeColumn> rowDataList = new ArrayList<OutputAttributeColumn>();
    			for (OutputAssociationColumn key : record.keySet())
            	{
                	if(key.getAbstractAttr() instanceof AttributeInterface)
                	{
                		final OutputAttributeColumn prevOpAttrCol =
                			(OutputAttributeColumn)record.get(key);
                		OutputAttributeColumn opAttributeColumn = new OutputAttributeColumn
                		("", prevOpAttrCol.getColumnIndex(),prevOpAttrCol.getAttribute(),
                				prevOpAttrCol.getExpression(), null);
                		setHeaderDisplayName(index, opAttributeColumn);
                		rowDataList.add(opAttributeColumn);
                	}
            	}
    			updateQueryHeaderRecordMap(queryHeaderVsRecordList, indexVsHeaderMap,
						queryHeaderData, rowDataList);
    			index++;
    		}
    	}

    	// now collect all the output attributes in order as per the index in headerVsIndexMap
    	List<OutputAttributeColumn> finalRowDataList = new ArrayList<OutputAttributeColumn>();
    	for(Entry<Integer, QueryHeaderData> queryHeaderEntry : indexVsHeaderMap.entrySet() )
    	{
    		final List<List<OutputAttributeColumn>> recordList = queryHeaderVsRecordList.get(queryHeaderEntry.getValue());
    		for(List<OutputAttributeColumn> record : recordList)
    		{
    			finalRowDataList.addAll(record);
    		}

    	}
    	return finalRowDataList;
    }




	private void setHeaderDisplayName(int index, final OutputAttributeColumn opAttributeColumn)
	{
		StringBuffer headerDisplay = new StringBuffer();
		headerDisplay.append(Utility.getDisplayNameForColumn(opAttributeColumn.getAttribute()));
		if(index>0)
		{
			headerDisplay.append('_').append(index);
		}
		opAttributeColumn.setHeader(headerDisplay.toString());
	}

	private void updateQueryHeaderRecordMap(
			Map<QueryHeaderData, List<List<OutputAttributeColumn>>> queryHeaderVsRecordList,
			Map<Integer,QueryHeaderData> indexVsHeaderMap, QueryHeaderData queryHeaderData,
			List<OutputAttributeColumn> rowDataList)
	{
		Collections.sort(rowDataList, new AttributeOrderComparator());
		List<List<OutputAttributeColumn>> headerRecordList = queryHeaderVsRecordList.get(queryHeaderData);
		if(headerRecordList==null)
		{
			headerRecordList = new ArrayList<List<OutputAttributeColumn>>();
			queryHeaderVsRecordList.put(queryHeaderData, headerRecordList);
		}
		headerRecordList.add(rowDataList);

		if(!rowDataList.isEmpty())
		{
		  	indexVsHeaderMap.put(rowDataList.get(0).getColumnIndex(),queryHeaderData);
		}
	}
}
