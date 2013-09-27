package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.query.queryobject.impl.AssociationDataHandler;
import edu.wustl.common.query.queryobject.impl.DenormalizedCSVExporter;
import edu.wustl.common.query.queryobject.impl.OutputAssociationColumn;
import edu.wustl.common.query.queryobject.impl.OutputAttributeColumn;
import edu.wustl.common.query.queryobject.impl.QueryExportDataHandler;
import edu.wustl.common.query.queryobject.impl.RecordProcessor.TreeCell;
import edu.wustl.common.query.queryobject.impl.Table;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.query.util.querysuite.QueryDetails;

public class SpreadsheetDenormalizationBizLogic
{
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(SpreadsheetDenormalizationBizLogic.class);

	/**
	 * Scans the entire IQuery starting from root node in order to populate the map
	 * required as an input to getDataOutProcessor.
	 * @param queryDetailsObj query details object
	 * @param dataList the list that contains the results of the query
	 * @param selectedColumnsMetadata selectedColumnsMetadata
	 * @param querySessionData querySessionData
	 */
	public Map<String,Object> scanIQuery(QueryDetails queryDetailsObj, List<List<String>> dataList,
			SelectedColumnsMetadata selectedColumnsMetadata, QuerySessionData querySessionData)
	{
		Map<String,Object> exportDetailsMap = new HashMap<String,Object>();
		try
		{
			IQuery query = queryDetailsObj.getQuery();
			IConstraints constraints = query.getConstraints();
			JoinGraph joinGraph = (JoinGraph)constraints.getJoinGraph();
			IExpression rootExp = joinGraph.getRoot();

			QueryExportDataHandler dataHandler = new QueryExportDataHandler(rootExp,constraints);
			//de normalization starts
			RowProcessor rowProcessor = new RowProcessor();
			List<Map<OutputAssociationColumn,Object>> denormalizationList =
				rowProcessor.populateData(dataList, querySessionData.getSql(), selectedColumnsMetadata,
						queryDetailsObj);
			dataHandler.setExpVsAssoc(rowProcessor.getExpVsAssoc());
			dataHandler.setTgtExpVsAssoc(rowProcessor.getTgtExpVsAssoc());
			DenormalizedCSVExporter csvExporter = new DenormalizedCSVExporter();

			/**
			 * For new implementation...only containments should be de-normalized
			 * and associations should be shown in normal form.
			 */
			AssociationDataHandler assocDataHandler = new AssociationDataHandler();
			Table<TreeCell> res = assocDataHandler.updateRowDataList
			(denormalizationList, rootExp, dataHandler,selectedColumnsMetadata);
			List<List<OutputAttributeColumn>> entityDataList = assocDataHandler.getEntityDataList(res, dataHandler,selectedColumnsMetadata);
			List<List<String>> finalDataList = new ArrayList<List<String>>();
			List<String> newDataList;
			for(List<OutputAttributeColumn> list: entityDataList)
			{
				newDataList = csvExporter.getFinalDataList(list,dataHandler);
				finalDataList.add(newDataList);
			}
			List<String> finalHeaderList = csvExporter.getHeaderList();
			exportDetailsMap.put("headerList", finalHeaderList);			
			exportDetailsMap.put("dataList", finalDataList);
		}
		catch (MultipleRootsException e)
		{
			logger.error(e.getMessage(), e);
		}
		return exportDetailsMap;
	}
}
