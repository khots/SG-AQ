
package edu.wustl.query.htmlprovider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.cab2b.common.util.AttributeInterfaceComparator;
import edu.wustl.cab2b.common.util.PermissibleValueComparator;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IParameter;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.query.util.global.AQConstants;

/**
 * This class generates UI for 'Add Limits' and 'Edit Limits' section.
 * @author deepti_shelar
 */
public class HtmlProvider
{
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(HtmlProvider.class);
	/**
	 * Object which holds data operators for attributes.
	 */
	private static ParseXMLFile parseFile = null;
	/**
	 *
	 */
	private int expressionId = -1;

	/**
	 * list of attributes.
	 */
	private String attributesList = "";

	/**
	 *
	 */
	private final GenerateHTMLDetails generateHTMLDetails;

	/**
	 * attribute details for each attribute.
	 */
	private AttributeDetails attributeDetails;

	/**
	 *	For page set to SAVE_QUERY/ADD_EDIT/EXECUTE_QUERY depending upon
	 *  which page the request has come from.
	 */
	private String forPage = "";
	/**
	 * @return the expressionId
	 */
	public int getExpressionId()
	{
		return expressionId;
	}

//	private String formName = "categorySearchForm";
	/**
	 * @param generateHTMLDetails the generateHTMLDetails to be set
	 */
	public HtmlProvider(GenerateHTMLDetails generateHTMLDetails)
	{
		this.generateHTMLDetails = new GenerateHTMLDetails();
		setGenerateHTMLDetails(generateHTMLDetails);
		if (parseFile == null)
		{
			try
			{
			    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
                        AQConstants.DYNAMIC_UI_XML);
                parseFile = ParseXMLFile.getInstance(inputStream);
			}
			catch (CheckedException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}
	/**
	 * @param generateHTMLDetails the GenerateHTMLDetails to be set
	 */
	private void setGenerateHTMLDetails(GenerateHTMLDetails generateHTMLDetails)
	{
		if(generateHTMLDetails==null)
		{
			this.generateHTMLDetails.setSearchString("");
			this.generateHTMLDetails.setAttributeChecked(false);
			this.generateHTMLDetails.setPermissibleValuesChecked(false);
		}
		else
		{
			this.generateHTMLDetails.setSearchString(generateHTMLDetails.getSearchString());
			this.generateHTMLDetails.setAttributeChecked(generateHTMLDetails.isAttributeChecked());
			this.generateHTMLDetails.
					setPermissibleValuesChecked
					(generateHTMLDetails.isPermissibleValuesChecked());
		}
	}

	/**
	 *
	 * @param expressionId the expressionId to set
	 */
	public void setExpressionId(int expressionId)
	{
		this.expressionId = expressionId;
	}
	/**
	 *
	 * @return
	 *//*
	public StringBuffer generateSaveQueryPreHTML()
	{
		StringBuffer generatedPreHTML = new StringBuffer(100);
		generatedPreHTML
				.append("<table border=\"0\" width=\"100%\"+
				 cellspacing=\"0\" cellpadding=\"0\">");
		return generatedPreHTML;
	}*/
	/**
	 * Generates component name for an attribute.
	 * @param attribute for which component name has to be generated
	 * @return component name.
	 */
	private String generateComponentName(AttributeInterface attribute)
	{
		StringBuffer componentId = new StringBuffer();
		String attributeName = "";
		if (getExpressionId() > -1)
		{
			componentId = componentId.append(getExpressionId() + "_");
		}
		else
		{
			attributeName = attribute.getName();
		}
		componentId = componentId.append(attributeName).append(
				attribute.getId().toString());

		return componentId.toString();
	}



	/**
	 * This method generates the HTML for Add Limits and Edit Limits section.
	 * This internally calls methods to generate other UI components like text, Calendar, Combobox etc.
	 * @param entity entity to be presented on UI.
	 * @param conditions List of conditions , These are required in case of edit limits,
	 * 		For adding limits this parameter is null
	 * @return String HTML generated for Add Limits section.
	 */
	public String generateHTML(EntityInterface entity, List<ICondition> conditions)
	{
		boolean attributeChecked = generateHTMLDetails.isAttributeChecked();
		boolean permissibleValuesChecked = generateHTMLDetails.isPermissibleValuesChecked();
		Collection<AttributeInterface> attributeCollection = entity.getEntityAttributesForQuery();
		String nameOfTheEntity = entity.getName();
		String entityId = entity.getId().toString();
		String eName = nameOfTheEntity.substring(nameOfTheEntity.lastIndexOf('.')+1);
		boolean isEditLimits = isEditLimits(conditions);
		String attributesStr = getAttributesString(attributeCollection);
		StringBuffer generatedPreHTML = GenerateHtml.getHtmlHeader
					(eName,entityId,attributesStr,isEditLimits);
		StringBuffer generatedHTML = getHtmlAttributes(conditions, attributeChecked, permissibleValuesChecked,
				attributeCollection);
		return generatedPreHTML.toString() + "####" + generatedHTML.toString();
	}

	/**
	 * Generates HTML for all the attributes of the entity.
	 * @param conditions list of conditions
	 * @param attributeChecked boolean
	 * @param permissibleValuesChecked boolean
	 * @param attributeCollection collection of attributes
	 * @return StringBuffer
	 */
	private StringBuffer getHtmlAttributes(List<ICondition> conditions, boolean attributeChecked,
			boolean permissibleValuesChecked, Collection<AttributeInterface> attributeCollection)
	{
		StringBuffer generatedHTML = new StringBuffer(AQConstants.MAX_SIZE);
		generatedHTML
				.append("<table valign='top' border=\"0\" width=\"100%\" " +
						"height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" " +
						"class='rowBGWhiteColor'>");
		GenerateHtml.getTags(generatedHTML);
		if (!attributeCollection.isEmpty())
		{
			boolean isBold;
			StringBuffer attrLabel;
			List<AttributeInterface> attributes =
				new ArrayList<AttributeInterface>(attributeCollection);
			Collections.sort(attributes, new AttributeInterfaceComparator());
			for (int i = 0; i < attributes.size(); i++)
			{
				AttributeInterface attribute = attributes.get(i);
				getAttributeDetails(attribute,conditions,null);
				attrLabel = new StringBuffer
					(Utility.getDisplayLabel(attributeDetails.getAttrName()));
				isBold = checkAttributeBold(attributeChecked, permissibleValuesChecked,
						attribute);
				if(isBold)
				{
					attrLabel = GenerateHtml.getBoldLabel(attrLabel.toString());
				}
				String componentId = generateComponentName(attribute);
				attributesList = attributesList + ";" + componentId;
				GenerateHtml.getAlternateCss(generatedHTML, false, componentId);
				generatedHTML.append(attrLabel).append(' ');
				GenerateHtml.getDateFormat(generatedHTML, isBold, attribute,attributeDetails.isParameterizedCondition());
				generatedHTML.append(":&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
				generateHTMLForConditions(generatedHTML,attribute);
				generatedHTML.append("\n</tr>");
			}
		}
		GenerateHtml.getTags(generatedHTML);
		generatedHTML.append("</table>");
		return generatedHTML;
	}
	/**
	 * Gets attribute details for each attribute.
	 * @param attribute details to be set of this attribute
	 * @param conditions list of conditions
	 * @param parameterList list of parameters
	 */
	private void getAttributeDetails(AttributeInterface attribute,
			List<ICondition> conditions,List<IParameter<?>> parameterList)
	{
		attributeDetails = new AttributeDetails();
		attributeDetails.setAttrName(attribute.getName());
		attributeDetails.setOperatorsList(HtmlUtility.getConditionsList(attribute,parseFile));
		if(!attributeDetails.getOperatorsList().isEmpty())
		{
			attributeDetails.setBetween(
					GenerateHtml.checkBetweenOperator(
							attributeDetails.getOperatorsList().get(0)));
		}
		attributeDetails.setConditions(conditions);
		attributeDetails.setDataType(attribute.getDataType());
		ICondition condition  = null;
		attributeDetails.setAttributeNameConditionMap(HtmlUtility.getMapOfConditions(conditions));
		if(attributeDetails.getAttributeNameConditionMap()!=null)
		{
			condition = attributeDetails.getAttributeNameConditionMap().
							get(attributeDetails.getAttrName());
		}
		attributeDetails.setEditValues(null);
		attributeDetails.setSelectedOperator(null);
		if(condition != null)
		{
			attributeDetails.setEditValues(condition.getValues());
			attributeDetails.setSelectedOperator
				(condition.getRelationalOperator().getStringRepresentation());
		}
		getParameterizedCondition(parameterList, forPage);
	}

	/**
	 * get details for parameterized query.
	 * @param parameterList list of parameters
	 * @param forPage String
	 */
	private void getParameterizedCondition(List<IParameter<?>> parameterList, String forPage)
	{
		IParameter<?> parameter=null;
		attributeDetails.setParameterList(parameterList);
		boolean isPresentInMap = isAttributePresentInMap();
		setIsParameterized(parameterList, isPresentInMap);
		if (forPage!=null && forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE) && isPresentInMap)
		{
			parameter = HtmlUtility.isParameterized(attributeDetails.getAttributeNameConditionMap().
					get(attributeDetails.getAttrName()),parameterList);
			attributeDetails.setParameter(parameter);
			attributeDetails.setParameterizedCondition(
					attributeDetails.getAttributeNameConditionMap().
					containsKey(attributeDetails.getAttrName())&& parameter!=null);
		}
	}

	/**
	 * @return isPresentInMap
	 */
	private boolean isAttributePresentInMap()
	{
		boolean isPresentInMap =
				attributeDetails.getAttributeNameConditionMap()!=null &&
				attributeDetails.getAttributeNameConditionMap().
								get(attributeDetails.getAttrName())!=null;
		return isPresentInMap;
	}

	/**
	 * @param parameterList parameterList
	 * @param isPresentInMap isPresentInMap
	 */
	private void setIsParameterized(List<IParameter<?>> parameterList,
			boolean isPresentInMap)
	{
		IParameter<?> parameter;
		if(isPresentInMap)
		{
			parameter = HtmlUtility.isParameterized(attributeDetails.getAttributeNameConditionMap().
					get(attributeDetails.getAttrName()),parameterList);
			attributeDetails.setParameter(parameter);
			attributeDetails.setParameterizedCondition(
					attributeDetails.getAttributeNameConditionMap().
					containsKey(attributeDetails.getAttrName())&& parameter!=null);
		}
	}

	/**
	 * check if attribute label is bold.
	 * @param attributeChecked boolean
	 * @param permissibleValuesChecked boolean
	 * @param attribute AttributeInterface
	 * @return boolean
	 */
	private boolean checkAttributeBold(boolean attributeChecked, boolean permissibleValuesChecked,
			AttributeInterface attribute)
	{
		boolean isBold = false;
		if(attributeChecked)
		{
			isBold = isAttributeBold();
		}
		if(!isBold && permissibleValuesChecked)
		{
			isBold = isPerValueAttributeBold(HtmlUtility.getPermissibleValuesList(attribute));
		}
		return isBold;
	}

    /**
     *
     * @param permissibleValuesList list of permissible values
     * @return boolean
     */
	private boolean isPerValueAttributeBold(
			List<PermissibleValueInterface> permissibleValuesList)
	{
		boolean isBold = false;
		if (permissibleValuesList != null && !permissibleValuesList.isEmpty())
		{
			for (int i = 0; i < permissibleValuesList.size(); i++)
			{
				if (isAttributeBold())
				{
					isBold = true;
					break;
				}
			}
		}
		return isBold;
	}

	/**
	 * @return boolean
	 */
	private boolean isAttributeBold()
	{
		return false;
	}

	/**
	 * This function generates the HTML for enumerated values.
	 *
	 * @param componentId
	 *            id of component
	 * @param permissibleValues
	 *            list of permissible values
	 * @param editLimitPermissibleValues
	 *            values list in case of edit limits
	 * @return String HTML for enumerated value drop down
	 */
	private String generateHTMLForEnumeratedValues(String  componentId,
			List<PermissibleValueInterface> permissibleValues, List<String> editLimitPermissibleValues)
	{
		StringBuffer html = new StringBuffer(AQConstants.MAX_SIZE);
		//String attributeName = attribute.getName();
		//String componentId = generateComponentName(attribute);
		if (permissibleValues != null && !permissibleValues.isEmpty())
		{
            html.append("\n<td width='70%' valign='centre' colspan='4' >");

            // Bug #3700. Non restricting the list width & increasing the
            // height
            String temp = "\n<select style=\"display:block;\" MULTIPLE styleId='country' "
        		+ "size ='5' name=\"" + componentId
                + "_enumeratedvaluescombobox\" id=\""+componentId
                + "_enumeratedvaluescombobox\">";
            html.append(temp);
			List<PermissibleValueInterface> values =
				new ArrayList<PermissibleValueInterface>(permissibleValues);
			Collections.sort(values, new PermissibleValueComparator());
			for (int i = 0; i < values.size(); i++)
			{
				PermissibleValueInterface perValue = values.get(i);
				getHtmlEnumValues(editLimitPermissibleValues, html, perValue);
			}
			html.append("\n</select>\n</td>");
		}
		return html.toString();

	}
	/**
	 *	Get HTML for enumerated values.
	 * @param editLimitPermissibleValues values list in case of edit limits
	 * @param html generated HTML
	 * @param perValue permissible value
	 */
	private void getHtmlEnumValues(List<String> editLimitPermissibleValues, StringBuffer html,
			PermissibleValueInterface perValue)
	{
		String value = perValue.getValueAsObject().toString();
		if (editLimitPermissibleValues != null
				&& editLimitPermissibleValues.contains(DynamicExtensionsUtility.getUnEscapedStringValue(value))
				|| isAttributeBold())
		{
			html.append("\n<option class=\"PermissibleValuesQuery\" title=\"" + value
					+ "\" value=\"" + value + "\" SELECTED>" + value + "</option>");
		}
		else
		{
			html.append("\n<option class=\"PermissibleValuesQuery\" title=\"" + value
					+ "\" value=\"" + value + "\">" + value + "</option>");
		}
	}

	/**
	 * Gets string from collection of attributes.
	 * @param attributeCollection collection of attributes
	 * @return String
	 */
	private String getAttributesString(Collection<AttributeInterface> attributeCollection)
	{
		StringBuffer attributesList = new StringBuffer();
		if (!attributeCollection.isEmpty())
		{
			List<AttributeInterface> attributes =
				new ArrayList<AttributeInterface>(attributeCollection);
			Collections.sort(attributes, new AttributeInterfaceComparator());
			for (int i = 0; i < attributes.size(); i++)
			{
				AttributeInterface attribute = attributes.get(i);
				//String attrName = attribute.getName();
				String componentId = generateComponentName(attribute);
				attributesList.append(';').append(componentId);
			}
		}
		return attributesList.toString();
	}

	/**
	 * Method to generate HTML for condition NULL.
	 * @param generatedHTML generated HTML
	 * @param attribute AttributeInterface
	 * @param attributeDetails details of attribute
	 */
	private void generateHTMLForConditionNull(StringBuffer generatedHTML,
			AttributeInterface attribute,AttributeDetails attributeDetails)
	{
		List<PermissibleValueInterface> permissibleValues =HtmlUtility.getPermissibleValuesList(attribute);
		String componentId = generateComponentName(attribute);
		boolean isDate = false;
		AttributeTypeInformationInterface attrTypeInfo = attribute
		.getAttributeTypeInformation();
		if (attrTypeInfo instanceof DateAttributeTypeInformation)
		{
			isDate = true;
		}
		generatedHTML.append(AQConstants.NEWLINE).append(
				GenerateHtml.generateHTMLForOperators(componentId,isDate,attributeDetails));
		if (!permissibleValues.isEmpty() && permissibleValues.size() < AQConstants.MAX_PV_SIZE)
		{
			generatedHTML.append(AQConstants.NEWLINE).append(
					generateHTMLForEnumeratedValues(componentId, permissibleValues,
							attributeDetails.getEditValues()));
		}
		else
		{
			if (attribute.getDataType().equalsIgnoreCase(AQConstants.DATATYPE_BOOLEAN))
			{
				generatedHTML
						.append(AQConstants.NEWLINE).append(
								GenerateHtml.generateHTMLForRadioButton(
								componentId, attributeDetails.getEditValues()));
			}
			else
			{
				generatedHTML.append(AQConstants.NEWLINE).append(
						GenerateHtml.generateHTMLForTextBox(
								componentId,attributeDetails));
			}
		}
	}

	/**
	 * Method for generating HTML depending on condition.
	 * @param generatedHTML generated HTML
	 * @param attribute AttributeInterface
	 */
	private void generateHTMLForConditions(StringBuffer generatedHTML,
			AttributeInterface attribute)
	{
		List<ICondition> conditions = attributeDetails.getConditions();
		if (conditions != null)
		{
			getHtmlConditionNotNull(generatedHTML,attribute, forPage);
		}
		if (conditions == null || (attributeDetails.getAttributeNameConditionMap()!=null
				&& !attributeDetails.getAttributeNameConditionMap().
				containsKey(attributeDetails.getAttrName())))
		{
			generateHTMLForConditionNull(generatedHTML, attribute,attributeDetails);
		}
	}

	/**
	 *
	 * @param generatedHTML generated HTML
	 * @param attribute AttributeInterface
	 * @param forPage String
	 */
	private void getHtmlConditionNotNull(StringBuffer generatedHTML,
			AttributeInterface attribute, String forPage)
	{
		if (attributeDetails.getAttributeNameConditionMap()!=null &&
				attributeDetails.getAttributeNameConditionMap().
				containsKey(attributeDetails.getAttrName()))
		{
			IParameter<?> parameter = attributeDetails.getParameter();
			if (forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE)
					&& parameter==null)
			{
				return;
			}

			generateHTMLForConditionNull(generatedHTML,attribute,attributeDetails);
		}
	}
	/**
	 * Method generates HTML for each entity of saved query.
	 * @param expressionID expression id
	 * @param entity entity for which HTML to be generated
	 * @param conditions list of conditions
	 * @param isShowAll boolean
	 * @param entityList list of entities
	 * @param parameterList list of parameters
	 * @return generated HTML
	 */
	private StringBuffer getSaveQueryPageHtml(int expressionID, EntityInterface entity,
			List<ICondition> conditions, boolean isShowAll, Map<EntityInterface,
			List<Integer>> entityList,List<IParameter<?>> parameterList)
	{
		setExpressionId(expressionID);
		StringBuffer generatedHTML = new StringBuffer();
		StringBuffer generatedPreHTML = new StringBuffer();
		Collection<AttributeInterface> attributeCollection = entity.getEntityAttributesForQuery();
		Collection<AttributeInterface> collection = new ArrayList<AttributeInterface>();
		// String attributesList = "";
		//boolean isParameterizedCondition = false;
		boolean isBGColor = false;

		if (!attributeCollection.isEmpty())
		{
			// get the list of dag ids for the corresponding entity
			String dagNodeId = getDagNodeId(expressionID, entity, entityList);
			List<AttributeInterface> attributes =
				new ArrayList<AttributeInterface>(attributeCollection);
			Collections.sort(attributes, new AttributeInterfaceComparator());
			GenerateHtml.getHtmlAddEditPage(forPage, generatedHTML);
			for(AttributeInterface attribute : attributes)
			{
				getAttributeDetails(attribute, conditions, parameterList);
				String attrName = attributeDetails.getAttrName();
				Map<String, ICondition> attributeNameConditionMap =
					attributeDetails.getAttributeNameConditionMap();
				if (checkAtrributeCondition(isShowAll, attrName, attributeNameConditionMap))
				{
					continue;
				}
				collection.add(attribute);
				String componentId = generateComponentName(attribute);
				isBGColor = getAlternateHtmlForSavedQuery(
						generatedHTML, isBGColor,componentId);
				generatedHTML.append(getHtmlAttributeSavedQuery(
						entity, dagNodeId, attribute));

			}
			boolean isEditLimits = isEditLimits(conditions);
			generatedPreHTML.append(getHtml(entity, generatedHTML,collection,isEditLimits));
		}
		if (forPage.equalsIgnoreCase(AQConstants.ADD_EDIT_PAGE))
		{
			generatedPreHTML.append("####");
			generatedPreHTML.append(generatedHTML);
			generatedHTML = generatedPreHTML;
		}
		return generatedHTML;
	}
	/**
	 * Edit Limit case if Conditions on attribute is not null.
	 * @param conditions list of conditions
	 * @return isEditLimits
	 */
	public static boolean isEditLimits(List<ICondition> conditions)
	{
		boolean isEditLimits=false;
		if (conditions != null)
		{
			isEditLimits = true;
		}
		return isEditLimits;
	}
	/**
	 * This method checks if an attribute has conditions.
	 * @param isShowAll boolean
	 * @param attrName name of attribute
	 * @param attributeNameConditionMap map containing attribute and its conditions.
	 * @return boolean
	 */
	private boolean checkAtrributeCondition(boolean isShowAll, String attrName,
			Map<String, ICondition> attributeNameConditionMap)
	{
		return attributeNameConditionMap != null
				&& !attributeNameConditionMap.containsKey(attrName) && !isShowAll;
	}

	/**
	 *
	 * @param entity for which HTML is to be generated
	 * @param generatedHTML generated HTML
	 * @param collection collection of attributes
	 * @param isEditLimits boolean
	 * @return StringBuffer
	 */
	private StringBuffer getHtml(EntityInterface entity, StringBuffer generatedHTML,
			Collection<AttributeInterface> collection, boolean isEditLimits)
	{
		StringBuffer generatedPreHTML = new StringBuffer();
		if (forPage.equalsIgnoreCase(AQConstants.SAVE_QUERY_PAGE)
				|| forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE))
		{
			generatedHTML.append(" <input type='hidden'  id='" + expressionId + ":"
					+ Utility.parseClassName(entity.getName()) + "_attributeList'" + "value="
					+ getAttributesString(collection) + " />  ");
		}
		else if (forPage.equalsIgnoreCase(AQConstants.ADD_EDIT_PAGE))
		{
			GenerateHtml.getTags(generatedHTML);
			generatedHTML.append("</table>");
			generatedPreHTML = GenerateHtml.generatePreHtml(
				getAttributesString(entity.getEntityAttributesForQuery()), entity.getName(),isEditLimits);
		}
		return generatedPreHTML;
	}

	/**
	 * Method returns DagNode Id.
	 * @param expressionID expressionID
	 * @param entity EntityInterface
	 * @param entityList list of entities
	 * @return String
	 */
	private String getDagNodeId(int expressionID, EntityInterface entity,
			Map<EntityInterface, List<Integer>> entityList)
	{
		List<Integer> entityDagId = entityList.get(entity);
		String dagNodeId = "";	// Converting the dagId to string
		if (entityDagId.size() > 1)
		{
			// DAGNodeId / expressionID to be shown only in case
			//if there are more than one node of the same class
			dagNodeId = expressionID + AQConstants.QUERY_DOT;
		}
		return dagNodeId;
	}

	/**
	 * This method gets HTML for each attribute in saved query.
	 * @param entity EntityInterface
	 * @param dagNodeId String
	 * @param attribute AttributeInterface
	 * @return generated HTML
	 */
	private StringBuffer getHtmlAttributeSavedQuery(EntityInterface entity, String dagNodeId,
			AttributeInterface attribute)
	{
		StringBuffer generatedHTML = new StringBuffer(AQConstants.MAX_SIZE);
		String name = Utility.parseClassName(entity.getName());
		String componentId = generateComponentName(attribute);
		generatedHTML.append(getHtmlForPage(dagNodeId,componentId,name));
		GenerateHtml.getDateFormat(generatedHTML, false, attribute,
				attributeDetails.isParameterizedCondition());
		if ((forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE)
		 	&& attributeDetails.isParameterizedCondition())
		 	|| !forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE))
		{
			generatedHTML.append("&nbsp;&nbsp;&nbsp;&nbsp;</b></td>\n");
		}
		generateHTMLForConditions(generatedHTML, attribute);
		generatedHTML.append("\n</tr>");
		return generatedHTML;
	}

	/**
	 * This method generates the alternate CSS for each attribute in saved query.
	 * @param generatedHTML generated HTML
	 * @param isBGColor boolean
	 * @param componentId component identifier
	 * @return boolean
	 */

	private boolean getAlternateHtmlForSavedQuery(StringBuffer generatedHTML,boolean isBGColor,
			 String componentId)
	{
		boolean bgColor = isBGColor;
		String styleSheetClass = "bgImageForColumns";
		if ((forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE)
				&& attributeDetails.isParameterizedCondition())
				|| !forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE))
		{
			attributesList = attributesList + ";" + componentId;
		}

		bgColor ^= true; 	//BGColor = !BGColor
		String html = "\n<tr  class='"+styleSheetClass +"'" +
		"  id=\"componentId\" "+" >\n";
		generatedHTML.append(html);
		return bgColor;
	}

	/**
	 * Modifies HTML based on ForPage i.e SAVE_QUERY, EXECUTE_QUERY_PAGE.
	 * @param dagNodeId DAG node id
	 * @param componentId id of component
	 * @param name of Entity
	 * @return StringBuffer
	 */
	private StringBuffer getHtmlForPage(String dagNodeId,String componentId,String name)
	{
		StringBuffer generatedHTML = new StringBuffer(AQConstants.MAX_SIZE);
		String attrLabel = Utility.getDisplayLabel(attributeDetails.getAttrName());
		String html;
		boolean isChecked=false;
		String isDisabled = "disabled='true'";
		if(attributeDetails.isParameterizedCondition())
		{
			isChecked=true;
			isDisabled="";
		}
		if (forPage.equalsIgnoreCase(AQConstants.SAVE_QUERY_PAGE))
		{
			html = " " + GenerateHtml.generateCheckBox(componentId, isChecked)
			+ "<td valign='top' align='left' class='standardTextQuery'>"
			+"<label for='" + componentId
			+ "_displayName' title='" + dagNodeId + name + "." + attrLabel + "'>"
			+ "<input type=\"textbox\"  class=\"formFieldSized20\"  name='"
			+ componentId + "_displayName'     id='" + componentId
			+ "_displayName' value='" + dagNodeId + name + "." + attrLabel
			+ "' "+isDisabled+"> " + "</label></td>";
			generatedHTML.append(html);
		}
		getHtmlForSaveQueryPage(generatedHTML, attrLabel);
		getHtmlForExecuteQueryPage(generatedHTML);
		return generatedHTML;
	}

	/**
	 * @param generatedHTML generatedHTML
	 * @param attrLabel label
	 */
	private void getHtmlForSaveQueryPage(StringBuffer generatedHTML,
			String attrLabel)
	{
		String html;
		if (!forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE))
		{
			html="<td valign='top' align='left' "
				+ "class='standardTextQuery' nowrap='nowrap' width=\"15%\">"
				+ attrLabel + " ";
			generatedHTML
					.append(html);
		}
	}

	/**
	 * @param generatedHTML generatedHTML
	 */
	private void getHtmlForExecuteQueryPage(StringBuffer generatedHTML)
	{
		String html;
		if (forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE)
				&& attributeDetails.isParameterizedCondition())
		{
			html="<td valign='top' align='left' class='standardTextQuery' "
				+ "nowrap='nowrap' width=\"15%\">"
				+ attributeDetails.getParameter().getName() + " ";
			generatedHTML
					.append(html);
		}
	}
	/**
	 * This method generates the HTML for Save Query section. This internally
	 * calls methods to generate other UI components like text, Calendar,
	 * Combobox etc. This method is same as the generateHTML except that this
	 * will generate HTML for selected conditions and will display only those
	 * conditions with their values set by user.
	 * @param expressionMap map which holds the list of all DAG id's / expression id's for a particular entity
	 * @param isShowAll boolean
	 * @param forPage String
	 * @param parameterList list of parameters
	 * @return String HTML generated for Save Query section.
	 */
	public String getHtmlForSavedQuery(
			Map<Integer, Map<EntityInterface, List<ICondition>>>  expressionMap, boolean isShowAll,
			String forPage,List<IParameter<?>> parameterList)
	{
		this.forPage = forPage;
		StringBuffer generatedHTML = new StringBuffer(AQConstants.MAX_SIZE);
		attributesList = "";
		StringBuffer expressionEntityString = new StringBuffer();
		if (expressionMap.isEmpty())
		{
			generatedHTML.append("No record found.");
		}
		else
		{
			//get the map which holds the list of all DAG id's / expression id's for a particular entity
			expressionEntityString = getMapsForEntity(
				expressionMap, isShowAll, parameterList, generatedHTML);
		}
		if (!expressionMap.isEmpty() && (forPage.equalsIgnoreCase(AQConstants.SAVE_QUERY_PAGE)
				|| forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE)))
		{
			String html = "<input type='hidden' id='totalentities' value='"
				+ expressionEntityString + "' />";
			generatedHTML.append(html);
			html = "<input type='hidden' id='attributesList' value='"
				+ attributesList + "' />";
			generatedHTML.append(html);
			generatedHTML
			.append("<input type='hidden' id='conditionList' name='conditionList' value='' />");
		}
		return generatedHTML.toString();
	}

	/**
	 * Create a map which holds the list of all Expression(DAGNode) id's for a particular entity.
	 * @param expressionMap map of expression id's for an entity
	 * @param isShowAll boolean
	 * @param parameterList list of parameters
	 * @param generatedHTML generated HTML
	 * @return expressionEntityString
	 */
	private StringBuffer getMapsForEntity(
			Map<Integer, Map<EntityInterface, List<ICondition>>> expressionMap, boolean isShowAll,
			List<IParameter<?>> parameterList, StringBuffer generatedHTML)
	{
		StringBuffer expressionEntityString = new StringBuffer();
		Map<EntityInterface, List<ICondition>> entityConditionMap;
		Map<EntityInterface, List<Integer>> entityExpressionIdListMap =
					GenerateHtml.getEntityExpressionIdListMap(expressionMap);
		Iterator<Integer> iterator = expressionMap.keySet().iterator();
		while (iterator.hasNext())
		{
			Integer expressionId = iterator.next();
			entityConditionMap = expressionMap.get(expressionId);
			if (entityConditionMap.isEmpty())
			{
				continue;
			}
			Iterator<EntityInterface> it2 = entityConditionMap.keySet().iterator();
			while (it2.hasNext())
			{
				EntityInterface entity = it2.next();
				List<ICondition> conditions = entityConditionMap.get(entity);
				generatedHTML.append(getSaveQueryPageHtml(expressionId.intValue(), entity,
				conditions, isShowAll, entityExpressionIdListMap,parameterList));
				expressionEntityString.append(expressionId.intValue()).append(':')
						.append(Utility.parseClassName(
							entity.getName())).append(AQConstants.ENTITY_SEPARATOR);
			}
		}
		return expressionEntityString;
	}

	/**
	 * @return the attributesList
	 */
	public String getAttributesList()
	{
		return attributesList;
	}

	/**
	 * @param attributesList the attributesList to set
	 */
	public void setAttributesList(String attributesList)
	{
		this.attributesList = attributesList;
	}

	/**
	 * @return the attributeDetails
	 */
	public AttributeDetails getAttributeDetails()
	{
		return attributeDetails;
	}

	/**
	 * @param attributeDetails the attributeDetails to set
	 */
	public void setAttributeDetails(AttributeDetails attributeDetails)
	{
		this.attributeDetails = attributeDetails;
	}

	/**
	 * @return the forPage
	 */
	public String getForPage()
	{
		return forPage;
	}

	/**
	 * @param forPage the forPage to set
	 */
	public void setForPage(String forPage)
	{
		this.forPage = forPage;
	}
}
