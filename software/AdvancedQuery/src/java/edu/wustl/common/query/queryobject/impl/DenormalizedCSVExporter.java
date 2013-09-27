package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.query.util.global.Utility;

/**
 * This class is responsible for forming the data list in denormalized form to be exported to a CSV file.
 * @author pooja_tavase
 *
 */
public class DenormalizedCSVExporter
{
	/**
	 * Header list to be displayed on the result view.
	 */
	private List<String> headerList = new ArrayList<String>();

	/**
	 * @return the headerList
	 */
	public List<String> getHeaderList()
	{
		return headerList;
	}

	/**
	 * @param headerList the headerList to set
	 */
	public void setHeaderList(List<String> headerList)
	{
		this.headerList = headerList;
	}

	/**
	 * Get the header list to be displayed on the UI in the results view.
	 * @param dataHandler dataHandler
	 * @param dataList dataList
	 */
	private void getHeaderList(QueryExportDataHandler dataHandler, List<OutputAttributeColumn> dataList)
	{
		List<Object> headerList = new ArrayList<Object>();
		int maxRecordCount = -1;
		int cntr = 0;
		for(OutputAttributeColumn opAttributeColumn : dataList)
		{
			IExpression expression = opAttributeColumn.getExpression();
			QueryHeaderData queryHeaderData = new QueryHeaderData(opAttributeColumn.getAttribute().getEntity(), expression);
			if(maxRecordCount == -1 || cntr>=maxRecordCount)
			{
				maxRecordCount = dataHandler.getMaxRecordCountForQueryHeader(queryHeaderData);
				cntr=0;
			}
				StringBuffer headerDisplay = new StringBuffer();
				headerDisplay.append(Utility.getDisplayNameForColumn(opAttributeColumn.getAttribute()));
				if(cntr>0)
				{
					headerDisplay.append('_').append(cntr);
				}
				if(opAttributeColumn.getHeader() == null)
				{
					opAttributeColumn.setHeader(headerDisplay.toString());
				}
				headerList.add(opAttributeColumn);
				/*else
				{
					StringBuffer originalHeader = new StringBuffer(opAttributeColumn.getHeader());
					originalHeader.append('|').append(headerDisplay.toString());
					opAttributeColumn.setHeader(originalHeader.toString());
				}*/
				cntr++;
		}
		List<String> finalHeaderList = new ArrayList<String>();
		//Collections.sort(headerList, new AttributeOrderComparator());
		for(Object opAttrCol : headerList)
		{
			String header = ((OutputAttributeColumn)opAttrCol).getHeader();
			StringTokenizer token = new StringTokenizer(header, "|");
			while(token.hasMoreTokens())
			{
				finalHeaderList.add(token.nextToken());
			}
		}
		this.headerList = finalHeaderList;
	}

	/**
	 * Get the data list to be displayed on the UI in the results view.
	 * @param resultList resultList
	 * @param dataHandler dataHandler
	 * @return newDataList
	 */
	public List<String> getFinalDataList(List<OutputAttributeColumn> resultList, QueryExportDataHandler dataHandler)
	{
		List<OutputAttributeColumn> dataList;
		List<String> newDataList;
		dataList = new ArrayList<OutputAttributeColumn>();
		newDataList = new ArrayList<String>();
		populateDataList(resultList, dataList);
		//Collections.sort(dataList, new AttributeOrderComparator()); // here all the group of entity attributes gets scattered & all columns for same attributes get close to each other
		// as the columnIndex will be same for all the new columns for the same attribute.
		if(headerList.isEmpty())
		{
			getHeaderList(dataHandler, dataList);
		}
		for(OutputAttributeColumn opAttrCol : dataList)
		{
			String value = opAttrCol.getValue();
			if(value == null)
			{
				newDataList.add(null);
			}
			else
			{
				if(value.length()==0)
				{
					newDataList.add(" ");
				}
				else
				{
					StringTokenizer token = new StringTokenizer(value, "|");
					while(token.hasMoreTokens())
					{
						newDataList.add(token.nextToken());
					}
				}
			}
		}
		return newDataList;
	}

	/**
	 * Convert the elements of the result list from object to String and add them in the dataList.
	 * @param resultList resultList
	 * @param dataList dataList
	 */
	private void populateDataList(List<OutputAttributeColumn> resultList, List<OutputAttributeColumn> dataList)
	{
		OutputAttributeColumn data;
		for(Object object : resultList)
		{
			data = null;
			if(object != null)
			{
				data = (OutputAttributeColumn) object;
			}
			dataList.add(data);
		}
	}
}
