
package edu.wustl.query.actionForm;

import java.util.List;

import org.apache.struts.action.ActionForm;

import edu.wustl.common.beans.NameValueBean;

/**
 * FormBean representing the QueryModule's properties.
 * @author Mandar Shidhore
 * @author deepti_shelar
 * @version 1.0
 * @created 06-Nov-2006 10.40.04 AM
 */

public class CategorySearchForm extends ActionForm
{

	private boolean normalizedQuery = false;
	/**
	 * serial version id.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * String to store the text field value.
	 */
	private String textField = null;
	/**
	 * String to store the classChecked checkbox's  value.
	 */
	private String classChecked = null;
	/**
	 * String to store the attributeChecked checkbox's value.
	 */
	private String attributeChecked = null;
	/**
	 * String to store the permissibleValuesChecked checkbox's value.
	 */
	private String pVsChecked = null;
	/**
	 * String to store the the IncludeDescription checkbox's value.
	 */
	private String includeDescChkd = null;
	/**
	 * String to store the radio button selected value.
	 */
	private String selected = null;
	/**
	 * String to store the radio button selected value.
	 */
	private String options = null;
	/**
	 * String to store the entityName value.
	 */
	private String entityName = null;
	/**
	 * String to store the string used To Create Query Object.
	 */
	private String strToCreateQuery = null;
	/**
	 * String to store the errors value.
	 */
	private List errors = null;
	/**
	 * String to store the searchButton value.
	 */
	private String searchButton = null;
	/**
	 * String to store the nextOperation value.
	 */
	private String nextOperation = null;
	/**
	 * String to store the text field value.
	 */
	private String nodeId = null;
	/**
	 * String to store the text field value.
	 */
	private String currentPage = null;
	/**
	 * boolean attribute.
	 */
	private String booleanAttribute = null;

	/**
	 * Array of selected column names.
	 */
	private String[] selectedColNames;

	/**
	 * Column names.
	 */
	private String[] columnNames;

	/**
	 * selected column name value bean list.
	 */
	private List<NameValueBean> selectColNVBLst;

	/**
	 * String to store currentSelectedObject.
	 */
	private String curSelectedObj = null;
	/**
	* String to store currentSelectedObject.
	*/
	private String curSelectedNode = null;
	/**
	 * boolean variable to store hide tree's value.
	 */
	private boolean showTree=true;

	/**
	 * @return hideTree
	 */
	public boolean isShowTree()
	{
		return showTree;
	}

	/**
	 * @param hideTree hideTree
	 */
	public void setShowTree(boolean hideTree)
	{
		showTree = hideTree;
	}

	/**
	 * @return the currentPage
	 */
	public String getCurrentPage()
	{
		return currentPage;
	}

	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(String currentPage)
	{
		this.currentPage = currentPage;
	}

	/**
	 * @return the nextOperation
	 */
	public String getNextOperation()
	{
		return nextOperation;
	}

	/**
	 * @param nextOperation the nextOperation to set
	 */
	public void setNextOperation(String nextOperation)
	{
		this.nextOperation = nextOperation;
	}

	/**
	 * @return the searchButton
	 */
	public String getSearchButton()
	{
		return searchButton;
	}

	/**
	 * @param searchButton the searchButton to set
	 */
	public void setSearchButton(String searchButton)
	{
		this.searchButton = searchButton;
	}

	/**
	 * @return the errors
	 */
	public List getErrors()
	{
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List errors)
	{
		this.errors = errors;
	}

	/**
	 * @return the stringToCreateQueryObject
	 */
	public String getStringToCreateQueryObject()
	{
		return strToCreateQuery;
	}

	/**
	 * @param strToCreateQuery the stringToCreateQueryObject to set
	 */
	public void setStringToCreateQueryObject(String strToCreateQuery)
	{
		this.strToCreateQuery = strToCreateQuery;
	}

	/**
	 * @return the selected
	 */
	public String getSelected()
	{
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(String selected)
	{
		this.selected = selected;
	}

	/**
	 * @return the textField
	 */
	public String getTextField()
	{
		return textField;
	}

	/**
	 * @param textField the textField to set
	 */
	public void setTextField(String textField)
	{
		this.textField = textField;
	}

	/**
	 * @return the attributeChecked
	 */
	public String getAttributeChecked()
	{
		return attributeChecked;
	}

	/**
	 * @param attributeChecked the attributeChecked to set
	 */
	public void setAttributeChecked(String attributeChecked)
	{
		this.attributeChecked = attributeChecked;
	}

	/**
	 * @return the classChecked
	 */
	public String getClassChecked()
	{
		return classChecked;
	}

	/**
	 * @param classChecked the classChecked to set
	 */
	public void setClassChecked(String classChecked)
	{
		this.classChecked = classChecked;
	}

	/**
	 * @return the permissibleValuesChecked
	 */
	public String getPermissibleValuesChecked()
	{
		return pVsChecked;
	}

	/**
	 * @param pVsChecked the permissibleValuesChecked to set
	 */
	public void setPermissibleValuesChecked(String pVsChecked)
	{
		this.pVsChecked = pVsChecked;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName()
	{
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

	/**
	 * @return the nodeId
	 */
	public String getNodeId()
	{
		return nodeId;
	}

	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}

	/**
	 * @return the booleanAttribute
	 */
	public String getBooleanAttribute()
	{
		return booleanAttribute;
	}

	/**
	 * @param booleanAttribute the booleanAttribute to set
	 */
	public void setBooleanAttribute(String booleanAttribute)
	{
		this.booleanAttribute = booleanAttribute;
	}

	/**
	 * @return includeDescriptionChecked
	 */
	public String getIncludeDescriptionChecked()
	{
		return includeDescChkd;
	}

	/**
	 * @param includeDescChkd includeDescriptionChecked
	 */
	public void setIncludeDescriptionChecked(String includeDescChkd)
	{
		this.includeDescChkd = includeDescChkd;
	}

	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}

	/**
	 * @param columnNames the columnNames to set
	 */
	public void setColumnNames(String[] columnNames)
	{
		this.columnNames = columnNames;
	}

	/**
	 * @return the selectedColumnNames
	 */
	public String[] getSelectedColumnNames()
	{
		return selectedColNames;
	}

	/**
	 * @param selectedColNames the selectedColumnNames to set
	 */
	public void setSelectedColumnNames(String[] selectedColNames)
	{
		this.selectedColNames = selectedColNames;
	}

	/**
	 * @return the currentSelectedObject
	 */
	public String getCurrentSelectedObject()
	{
		return curSelectedObj;
	}

	/**
	 * @param curSelectedObj the currentSelectedObject to set
	 */
	public void setCurrentSelectedObject(String curSelectedObj)
	{
		this.curSelectedObj = curSelectedObj;
	}

	/**
	 * @return the currentSelectedNodeInTree
	 */
	public String getCurrentSelectedNodeInTree()
	{
		return curSelectedNode;
	}

	/**
	 * @param curSelectedNode the currentSelectedNodeInTree to set
	 */
	public void setCurrentSelectedNodeInTree(String curSelectedNode)
	{
		this.curSelectedNode = curSelectedNode;
	}

	/**
	 * @return the selectedColumnNameValueBeanList
	 */
	public List<NameValueBean> getSelColNVBeanList()
	{
		return selectColNVBLst;
	}

	/**
	 * @param selectColNVBLst the selectedColumnNameValueBeanList to set
	 */
	public void setSelColNVBeanList(
			List<NameValueBean> selectColNVBLst)
	{
		this.selectColNVBLst = selectColNVBLst;
	}

	/**
	 * @return the options
	 */
	public String getOptions()
	{
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(String options)
	{
		this.options = options;
	}

	public void setNormalizedQuery(boolean normalizedQuery)
	{
		this.normalizedQuery = normalizedQuery;
	}

	public boolean getNormalizedQuery()
	{
		return normalizedQuery;
	}
}
