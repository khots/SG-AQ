/**
 * <p>Title: AdvanceSearchForm Class>
 * <p>Description:  This Class is used to encapsulate all the request parameters passed
 * from ParticipantSearch.jsp/CollectionProtocolRegistrationSearch.jsp/
 * SpecimenCollectionGroupSearch.jsp & SpecimenSearch.jsp pages. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @version 1.00
 * Created on Jul 15, 2005
 */

package edu.wustl.query.actionForm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * This Class is used to encapsulate all the request parameters passed from Search Pages.
 * @author aniruddha_phadnis
 */
public class QueryAdvanceSearchForm extends ActionForm
{
	/**
	 * @return Returns the columnNames.
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}
	/**
	 * @param columnNames The columnNames to set.
	 */
	public void setColumnNames(String[] columnNames)
	{
		this.columnNames = columnNames;
	}
	/**
	 * @return Returns the selectedColumnNames.
	 */
	public String[] getSelectedColumnNames()
	{
		return selectedColumnNames;
	}
	/**
	 * @param selectedColumnNames The selectedColumnNames to set.
	 */
	public void setSelectedColumnNames(String[] selectedColumnNames)
	{
		this.selectedColumnNames = selectedColumnNames;
	}
	/**
	 * @return Returns the tableName.
	 */
	public String getTableName()
	{
		return tableName;
	}
	/**
	 * @param tableName The tableName to set.
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	/**
	 * A map that handles all the values of Advanced Search pages.
	 */
    private Map values = new HashMap();

    /**
	 * A map that handles event parameters' data.
	 */
    private Map eventMap = new HashMap();

    /**
	 * Object name of the advancedConditionNode Object.
	 */
    private String objectName = "";

    /**
	 * Selected node from the query tree.
	 */
    private String selectedNode = "";

    /**
	 * A counter that holds the number of event parameter rows.
	 */
    private int eventCounter = 1;

    /**
     * Item node id.
     */
    private String itemNodeId = "";

    //Variables necessary for Configuration of Advance Search Results.

    /**
     * Table name.
     */
     private String tableName;

     /**
      * Selected column names.
      */
     private String []selectedColumnNames;

     /**
      * Column names.
      */
     private String []columnNames;

    /**
     * Returns the selected node from a query tree.
     * @return The selected node from a query tree.
     * @see #setSelectedNode(String)
     */
	public String getSelectedNode()
	{
		return selectedNode;
	}

	/**
     * Sets the selected node of a query tree.
     * @param selectedNode the selected node of a query tree.
     * @see #getSelectedNode()
     */
	public void setSelectedNode(String selectedNode)
	{
		this.selectedNode = selectedNode;
	}

    /**
     * No argument constructor for StorageTypeForm class.
     */
    public QueryAdvanceSearchForm()
    {
        reset();
    }

    /**
     * Associates the specified object with the specified key in the map.
     * @param key the key to which the object is mapped.
     * @param value the object which is mapped.
     */
    public void setValue(String key, Object value)
    {
    	values.put(key, value);
    }

    /**
     * Returns the object to which this map maps the specified key.
     * @param key the required key.
     * @return the object to which this map maps the specified key.
     */
    public Object getValue(String key)
    {
        return values.get(key);
    }

    //Bug 700: changed the method name for setting the map values
    //as it was same in both AdvanceSearchForm and SimpleQueryInterfaceForm
    /**
     * Associates the specified object with the specified key in the map.
     * @param key the key to which the object is mapped.
     * @param value the object which is mapped.
     */
    public void setValue1(String key, Object value)
    {
    	values.put(key, value);
    }

    /**
     * Returns the object to which this map maps the specified key.
     * @param key the required key.
     * @return the object to which this map maps the specified key.
     */
    public Object getValue1(String key)
    {
        return values.get(key);
    }

    /**
     * Returns all the values of the map.
     * @return the values of the map.
     */
	public Collection getAllValues()
	{
		return values.values();
	}

	/**
     * Sets the map.
     * @param values the map.
     * @see #getValues()
     */
	public void setValues(Map values)
	{
		this.values = values;
	}

	/**
     * Returns the map.
     * @return the map.
     * @see #setValues(Map)
     */
	public Map getValues()
	{
		return this.values;
	}

	/**
     * Associates the specified array object with the specified key in the map.
     * @param key the key to which the object is mapped.
     * @param value the object which is mapped.
     */
    public void setValueList(String key, Object [] value)
    {
    	values.put(key, value);
    }

    /**
     * Returns the object array to which the specified key is been mapped.
     * @param key the required key.
     * @return the object to which this map maps the specified key.
     */
    public Object[] getValueList(String key)
    {
    	 return (Object [])values.get(key);
    }

    /**
     * Resets the values of all the fields.
     * Is called by the overridden reset method defined in ActionForm.
     */
    protected void reset()
    {
    }

    /**
     * Overrides the validate method of ActionForm.
     * @return error error
     * @param mapping mapping
     * @param request request
     */
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
    {
        return new ActionErrors();
    }

    /**
     * Returns the object name.
     * @return the object name.
     * @see #setObjectName(String)
     */
	public String getObjectName()
	{
		return objectName;
	}

	/**
	 * Sets the object name.
	 * @param objectName The object name to be set.
	 * @see #getObjectName()
	 */
	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	/**
     * Associates the specified object with the specified key in the map.
     * @param key the key to which the object is mapped.
     * @param value the object which is mapped.
     */
    public void setEventMap(String key, Object value)
    {
    	eventMap.put(key, value);
    }

    /**
     * Returns the object to which this map maps the specified key.
     * @param key the required key.
     * @return the object to which this map maps the specified key.
     */
    public Object getEventMap(String key)
    {
        return eventMap.get(key);
    }

    /**
     * Returns the map of event parameters' values.
     * @return the map of event parameters' values.
     */
    public Map getEventValues()
    {
    	return this.eventMap;
    }

    /**
     * Returns the no. of rows of event parameters.
     * @return The no. of rows of event parameters.
     * @see #setEventCounter(int)
     */
	public int getEventCounter()
	{
		return eventCounter;
	}

	/**
     * Sets the no. of rows of event parameters.
     * @param eventCounter The no. of rows of event parameters.
     * @see #getEventCounter()
     */
	public void setEventCounter(int eventCounter)
	{
		this.eventCounter = eventCounter;
	}

	/**
	 * @return itemNodeId
	 */
	public String getItemNodeId()
	{
		return itemNodeId;
	}

	/**
	 * @param itemId itemId
	 */
	public void setItemNodeId(String itemId)
	{
		this.itemNodeId = itemId;
	}

    //Map added to maintain values to display the Calendar icon
	/**
	 * Map to hold values for rows to display calendar icon.
	 */
	protected Map showCalendarValues = new HashMap();

	/**
	 * @return Returns the showCalendarValues.
	 */
	public Map getShowCalendarValues()
	{
		return showCalendarValues;
	}
	/**
	 * @param showCalendarValues The showCalendarValues to set.
	 */
	public void setShowCalendarValues(Map showCalendarValues)
	{
		this.showCalendarValues = showCalendarValues;
	}

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is mapped.
	 */
	public void setShowCalendar(String key, Object value)
	{
		showCalendarValues.put(key, value);
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * @param key
	 *            the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getShowCalendar(String key)
	{
		return showCalendarValues.get(key);
	}
}