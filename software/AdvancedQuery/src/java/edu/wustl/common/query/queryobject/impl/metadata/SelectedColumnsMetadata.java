
package edu.wustl.common.query.queryobject.impl.metadata;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;

/**
 *
 * @author deepti_shelar
 *
 */
public class SelectedColumnsMetadata
{

	/**
	 * Selected object by user at the time of defining columns.
	 */
	private OutputTreeDataNode currentSelectedObject;
	/**
	 * List of attributes user has selected.
	 */
	private List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList;
	/**
	 * List of name value bean object for selected columns.
	 */
	private List<NameValueBean> selColNVBeanList;

	/**
	 * Returns true/false Whether view is defined.
	 */
	private boolean ifDefinedView;
	/**
	 * List of output attribute objects.
	 */
	private List<IOutputAttribute> selectedOutputAttributeList;

	private int noOfExpr;

	private int actualTotalRecords;
	/**
	 * Check if it's define view.
	 * @return ifDefinedView
	 */
	public boolean isDefinedView()
	{
		return ifDefinedView;
	}

	/**
	 * Set defined view.
	 * @param isDefinedView isDefinedView
	 */
	public void setDefinedView(boolean isDefinedView)
	{
		this.ifDefinedView = isDefinedView;
	}

	/**
	 * Returns the attribute list.
	 * @return attributeList
	 */
	public List<AttributeInterface> getAttributeList()
	{
		List<AttributeInterface> attributeList = new ArrayList<AttributeInterface>();
		if (selectedAttributeMetaDataList != null)
		{
			for (QueryOutputTreeAttributeMetadata metadata : selectedAttributeMetaDataList)
			{
				attributeList.add(metadata.getAttribute());
			}
		}
		return attributeList;
	}

	//	public void setAttributeList(List<AttributeInterface> attributeList)
	//	{
	//		this.attributeList = attributeList;
	//	}
	/**
	 * Get current selected object.
	 * @return currentSelectedObject
	 */
	public OutputTreeDataNode getCurrentSelectedObject()
	{
		return currentSelectedObject;
	}

	/**
	 * @param currentSelectedObject the currentSelectedObject to set
	 */
	public void setCurrentSelectedObject(OutputTreeDataNode currentSelectedObject)
	{
		this.currentSelectedObject = currentSelectedObject;
	}

	/**
	 * @return the selColNVBeanList
	 */
	public List<NameValueBean> getSelColNVBeanList()
	{
		return selColNVBeanList;
	}

	/**
	 * @param selColNVBeanList the selColNVBeanList to set
	 */
	public void setSelColNVBeanList(
			List<NameValueBean> selColNVBeanList)
	{
		this.selColNVBeanList = selColNVBeanList;
	}

	/**
	 * @return the selectedAttributeMetaDataList
	 */
	public List<QueryOutputTreeAttributeMetadata> getSelectedAttributeMetaDataList()
	{
		return selectedAttributeMetaDataList;
	}

	/**
	 * @param selectedAttributeMetaDataList the selectedAttributeMetaDataList to set
	 */
	public void setSelectedAttributeMetaDataList(
			List<QueryOutputTreeAttributeMetadata> selectedAttributeMetaDataList)
	{
		this.selectedAttributeMetaDataList = selectedAttributeMetaDataList;
	}

	/**
	 * @return the selectedOutputAttributeList
	 */
	public List<IOutputAttribute> getSelectedOutputAttributeList()
	{
		return selectedOutputAttributeList;
	}

	/**
	 * @param selectedOutputAttributeList the selectedOutputAttributeList to set
	 */
	public void setSelectedOutputAttributeList(List<IOutputAttribute> selectedOutputAttributeList)
	{
		this.selectedOutputAttributeList = selectedOutputAttributeList;
	}

	/**
	 * @param noOfExpr the noOfExpr to set.
	 */
	public void setNoOfExpr(int noOfExpr)
	{
		this.noOfExpr = noOfExpr;
	}

	/**
	 * @return the noOfExpr.
	 */
	public int getNoOfExpr()
	{
		return noOfExpr;
	}

	/**
	 * @param actualTotalRecords the actualTotalRecords to set
	 */
	public void setActualTotalRecords(int actualTotalRecords)
	{
		this.actualTotalRecords = actualTotalRecords;
	}

	/**
	 * @return the actualTotalRecords
	 */
	public int getActualTotalRecords()
	{
		return actualTotalRecords;
	}
}
