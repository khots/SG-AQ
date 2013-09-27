/*
 *
 */

package edu.wustl.query.util.global;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Variables //extends edu.wustl.common.util.global.Variables
{

	/**
	 * maximum Tree Node Limit For Child Node.
	 */
	public static int maxTreeNdLmtForChildNd;
	/**
	 * maximum Tree Node Limit.
	 */
	public static int maximumTreeNodeLimit;
	/**
	 * Alias and page of map.
	 */
	public static Map<String, String> aliasAndPageOfMap = new HashMap<String, String>();
	/**
	 * Query Generator class name.
	 */
	public static String queryGeneratorClassName = "";
	/**
	 * properties.
	 */
	public static Properties properties;
	/**
	 * application Cvs tag.
	 */
	public static String applicationCvsTag = "";
	/**
	 * Empty string.
	 */
	private static final String CONST_VARIABLES_VALUE = "";
	/**
	 * main protocol object.
	 */
	public static String mainProtocolObject = CONST_VARIABLES_VALUE;
	/**
	 * entity CP Sql map.
	 */
    public static Map<String, String> entityCPSqlMap = new HashMap<String, String>();


    /**
     *
     * @param dataColl dataColl
     * @return the string
     */
	public static String prepareColTypes(List dataColl)
	{
		return prepareColTypes(dataColl, false);
	}

	/**
	 * Prepare column types.
	 *
	 * @param dataColl the data collection
	 * @param createCheckBoxColumn the create check box column
	 *
	 * @return the string
	 */
	public static String prepareColTypes(List dataColl, boolean createCheckBoxColumn)
	{
		StringBuffer colType = new StringBuffer();
		if (dataColl != null && !dataColl.isEmpty())
		{
			List rowDataColl = (List) dataColl.get(0);

			Iterator itr = rowDataColl.iterator();
			colTypeForCheckbox(createCheckBoxColumn, colType);
			while (itr.hasNext())
			{
				populateColTypes(colType, itr);
			}
		}
		return setColumnType(colType);
	}

	/**
	 * @param colType colType
	 * @param itr iterator
	 */
	private static void populateColTypes(StringBuffer colType, Iterator itr)
	{
		Object obj = itr.next();
		if (obj instanceof Number)
		{
			colType.append("int,");
		}
		else if (obj instanceof Date)
		{
			colType.append("date,");
		}
		else
		{
			colType.append("str,");
		}
	}

	/**
	 * @param createCheckBoxColumn createCheckBoxColumn
	 * @param colType colType
	 */
	private static void colTypeForCheckbox(boolean createCheckBoxColumn,
			StringBuffer colType)
	{
		if (createCheckBoxColumn)
		{
			colType.append("ch,");
		}
	}

	/**
	 * @param colType type
	 * @param tColType type
	 * @return tColType
	 */
	private static String setColumnType(StringBuffer colType)
	{
		String tColType="";
		if (colType.length() > 0)
		{
			tColType = colType.toString();
			tColType = tColType.substring(0, tColType.length() - 1);
		}
		return tColType;
	}

	/**
	 * specify queryReadDeniedObjectList.
	 */
	public static List<String> queryReadDeniedObjectList = new ArrayList<String>();
	/**
	 * specify validatorClassname.
	 */
	public static String validatorClassname = CONST_VARIABLES_VALUE;

	/**
	 * Specify the main protocol object query.
	 */
	public static String mainProtocolQuery = CONST_VARIABLES_VALUE;
}