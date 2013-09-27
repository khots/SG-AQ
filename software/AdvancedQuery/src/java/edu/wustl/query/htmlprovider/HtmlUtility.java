package edu.wustl.query.htmlprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.PermissibleValue;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IParameter;
import edu.wustl.query.util.global.AQConstants;
/**
 *This class has utility methods required to generate HTML for Add Limits in Query.
 */

public class HtmlUtility
{
	/**
	 * Method gets enumerated condition list.
	 * @param dataType dataType of attribute
	 * @param parseFile XML file which contains operator list
	 * @return operator list
	 */
	private static List<String> getEnumConditionList(String dataType,ParseXMLFile parseFile)
	{
		List<String> operatorsList;
		if(isNumber(dataType.toLowerCase()))
		{
			operatorsList = parseFile.getEnumConditionList(AQConstants.DATATYPE_NUMBER);
		}
		else
		{
			operatorsList = parseFile.getEnumConditionList(dataType.toLowerCase());
		}
		return operatorsList;
	}
	/**
	 * Check if data type is numeric.
	 * @param dataType type of attribute
	 * @return boolean
	 */
	private static boolean isNumber(String dataType)
	{
		boolean isNumber = false;
		for(int i=0;i<AQConstants.NUMBER.length;i++)
		{
			if(dataType.equalsIgnoreCase(AQConstants.NUMBER[i]))
			{
				isNumber = true;
				break;
			}
		}
		return isNumber;
	}
	/**
	 * Method gets non-enumerated condition list.
	 * @param dataType data type of attribute
	 * @param parseFile XML file which contains operator list
	 * @return operator list
	 */
	private static List<String> getNonEnumConditionList(String dataType,ParseXMLFile parseFile)
	{
		List<String> operatorsList;
		if (isNumber(dataType.toLowerCase()))
		{
			operatorsList = parseFile.getNonEnumConditionList(AQConstants.DATATYPE_NUMBER);
		}
		else
		{
			operatorsList = parseFile.getNonEnumConditionList(dataType.toLowerCase());
		}
		return operatorsList;
	}

	/**
	 * Returns list of possible non-enumerated/enumerated operators for attribute.
	 * @param attributeInterface AttributeInterface
	 * @param parseFile XML file which contains operator list
	 * @return List listOf operators.
	 */
	public static List<String> getConditionsList(AttributeInterface attributeInterface,ParseXMLFile parseFile)
	{
		List<String> operatorsList;
		List<String> strObj = null;
		if (attributeInterface != null)
		{
			String dataType = attributeInterface.getDataType();
			UserDefinedDE userDefineDE = (UserDefinedDE) attributeInterface
					.getAttributeTypeInformation().getDataElement();
			if (userDefineDE != null &&
				userDefineDE.getPermissibleValueCollection().size() < AQConstants.MAX_PV_SIZE)
			{
				operatorsList = HtmlUtility.getEnumConditionList(dataType,parseFile);
			}
			else
			{
				operatorsList = HtmlUtility.getNonEnumConditionList(dataType,parseFile);
			}
			strObj = new ArrayList<String>(operatorsList);
			Collections.sort(strObj);
		}
		return strObj;
	}

	/**
	 * Returns the map of name of the attribute and condition object as its value.
	 * @param conditions
	 *            list of conditions user had applied in case of edit limits
	 * @return Map name of the attribute and condition object
	 */
	public static Map<String, ICondition> getMapOfConditions(List<ICondition> conditions)
	{
		Map<String, ICondition> attributeNameConditionMap = null;
		if (conditions != null)
		{
			attributeNameConditionMap = new HashMap<String, ICondition>();
			for (int i = 0; i < conditions.size(); i++)
			{
				attributeNameConditionMap.put(
					conditions.get(i).getAttribute().getName(), conditions.get(i));
			}
		}
		return attributeNameConditionMap;
	}
	/**
	 * returns PermissibleValuesList' list for attribute.
	 * @param attribute
	 *            AttributeInterface
	 * @return List of permissible values for the passed attribute
	 */
	public static List<PermissibleValueInterface> getPermissibleValuesList(AttributeInterface attribute)
	{
		UserDefinedDE userDefineDE = (UserDefinedDE) attribute.getAttributeTypeInformation()
				.getDataElement();
		List<PermissibleValueInterface> permissibleValues = new ArrayList<PermissibleValueInterface>();
		if (userDefineDE != null && userDefineDE.getPermissibleValueCollection() != null)
		{
			Iterator<PermissibleValueInterface> permissibleValueInterface = userDefineDE
					.getPermissibleValueCollection().iterator();
			while (permissibleValueInterface.hasNext())
			{
				PermissibleValue permValue = (PermissibleValue) permissibleValueInterface.next();
				permissibleValues.add(permValue);
			}
		}
		return permissibleValues;
	}

	/**
	 * @param condition ICondition
	 * @param parameterList list of parameters
	 * @return IParameter
	 */
	public static IParameter<?> isParameterized(ICondition condition, List<IParameter<?>> parameterList)
	{
		IParameter tempParameter = null;
		if(parameterList !=null)
		{
		 for (IParameter<?> parameter : parameterList)
		 {
	            if (parameter.getParameterizedObject() instanceof ICondition)
	            {
	            	ICondition paramCondition = (ICondition) parameter.getParameterizedObject();
	                if(paramCondition.getId()==condition.getId())
	                {
	                	tempParameter = parameter;
	                	break;
	                }
	             }
	        }
		}
		return tempParameter;
	}
}
