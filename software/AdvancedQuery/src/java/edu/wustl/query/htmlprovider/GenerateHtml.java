package edu.wustl.query.htmlprovider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.query.util.global.AQConstants;

/**
 * This class generates HTML for text box, radio button and
 * other components for the Add Limits section of Query Page.
 * @author rukhsana_sameer
 */
public class GenerateHtml
{
	/**
	 * End tag for HTML tag TD.
	 */
	public static final String END_TD_TAG = "\n</td>";

	/**
	 * CSS with white background.
	 */
	public static final String CSS_BGWHITE = "rowBGWhiteColor";
	/**
	 * CSS with grey background.
	 */
	public static final String CSS_BGGREY = "rowBGGreyColor1";
	/**
	 * CSS for text box.
	 */
	public static final String CSS_TEXT = "standardTextQuery";
	/**
	 * CSS for permissible values.
	 */
	public static final String CSS_PV= "PermissibleValuesQuery";

	/**
	 * This method generates HTML for RadioButton.
	 * @param componentId
	 * 		String
	 * @param values
	 * 		List values
	 * @return String
	 */
	public static String generateHTMLForRadioButton(String  componentId, List<String> values)
	{
		String cssClass=CSS_TEXT;
		StringBuffer html = new StringBuffer(AQConstants.MAX_SIZE);
		html.append("\n<td class='" + cssClass + "' >");
		if (values == null)
		{
			html.append(getHtmlRadioButton(componentId,cssClass,true,""));
			html.append(getHtmlRadioButton(componentId,cssClass,false,""));
		}
		else
		{
			if(values.get(0) == null)
			{
				html.append(getHtmlRadioButton(componentId,cssClass,true,""));
				html.append(getHtmlRadioButton(componentId,cssClass,false,""));
			}
			else
			{
				getHtmlForValuesNotNull(componentId, values, cssClass, html);
			}
		}
		html.append(END_TD_TAG);
		html.append("\n<td class='" + cssClass + "'>&nbsp;");
		html.append(END_TD_TAG);
		html.append("\n<td class='" + cssClass + "'>&nbsp;");
		html.append(END_TD_TAG);
		html.append("\n<td class='" + cssClass + "'>&nbsp;");
		html.append(END_TD_TAG);
		return html.toString();
	}
	/**
	 *
	 * @param componentId id of component
	 * @param cssClass name of CSS class
	 * @param isRadioButtonTrue boolean
	 * @param checked String
	 * @return String
	 */
	private static String getHtmlRadioButton(String componentId, String cssClass,
			 boolean isRadioButtonTrue,String checked)
	{
		StringBuffer html= new StringBuffer(AQConstants.MAX_SIZE);
		String componentName = componentId + "_booleanAttribute";
		String buttonId;
		String name;
		String value;
		if(isRadioButtonTrue)
		{
			buttonId = componentId + AQConstants.UNDERSCORE + AQConstants.TRUE;
			name = "True";
			value=AQConstants.TRUE;
		}
		else
		{
			buttonId = componentId + AQConstants.UNDERSCORE + AQConstants.FALSE;
			name="False";
			value=AQConstants.FALSE;
		}
		html.append("\n<input type='radio' id = '" + componentId
				+ "_"+value+"' value='"+value+"' onclick=\"resetOptionButton('" + buttonId
				+ "',this)\" name='" + componentName + "' '"+checked + "'/><font class='" + cssClass
				+ "'>"+name+"</font>");
		return html.toString();
	}

	/**
	 * Generates HTML for radio button depending upon value of radio button.
	 * @param componentId generated component id
	 * @param values list of values
	 * @param cssClass CSS class name
	 * @param html generated HTML
	 */
	private static void getHtmlForValuesNotNull(String componentId, List<String> values, String cssClass,
			StringBuffer html)
	{
		if (values.get(0).equalsIgnoreCase(AQConstants.TRUE))
		{
			html.append(getHtmlRadioButton(componentId,cssClass,true,"checked"));
			html.append(getHtmlRadioButton(componentId,cssClass,false,""));
		}
		else if(values.get(0).equalsIgnoreCase(AQConstants.FALSE))
		{
			html.append(getHtmlRadioButton(componentId,cssClass,true,""));
			html.append(getHtmlRadioButton(componentId,cssClass,false,"checked"));
		}
		else
		{
			html.append(getHtmlRadioButton(componentId,cssClass,true,""));
			html.append(getHtmlRadioButton(componentId,cssClass,false,""));
		}
	}
	/**
	 * Generates HTML for textBox to hold the input for operator selected.
	 *
	 * @param componentId
	 *            String
	 * @param attrDetails
	 *            AttributeDetails
	 * @return String HTMLForTextBox
	 */
	public static String generateHTMLForTextBox(String componentId,AttributeDetails attrDetails)
	{
		String cssClass = CSS_TEXT;
		String textBoxId = componentId + "_textBox";
		String textBoxId1 = componentId + "_textBox1";
		StringBuffer html = new StringBuffer(AQConstants.MAX_SIZE);
		html.append("<td width='15%' valign='top' class=\"standardTextQuery\">\n");
		getHtmlValueAndOperator(
				attrDetails.getEditValues(),attrDetails.getSelectedOperator(), textBoxId, html);
		html.append(END_TD_TAG);
		getHtmlForDate(componentId, attrDetails, cssClass,html);
		getHtmlForBetweenOperator(attrDetails, textBoxId1, html);
		if(attrDetails.getDataType().equalsIgnoreCase(AQConstants.DATE) )
		{
			if(attrDetails.getSelectedOperator() == null || attrDetails.getSelectedOperator().equalsIgnoreCase(RelationalOperator.Between.toString()) || attrDetails.isBetween())
			{
				getHtmlForCalendar(componentId, attrDetails, cssClass, html);
			}
		}
		else
		{
			html.append("\n<td valign='top' />");
		}
		return html.toString();
	}

	/**
	 * @param componentId component identifier
	 * @param attrDetails attribute details
	 * @param cssClass class
	 * @param html string buffer
	 * @param newLine new Line
	 */
	private static void getHtmlForCalendar(String componentId,
			AttributeDetails attrDetails, String cssClass, StringBuffer html)
	{
		String newLine ="\n";
		if(attrDetails.getSelectedOperator() == null && attrDetails.isBetween())
		{
			html.append(newLine).append(generateHTMLForCalendar(
					componentId, false,true,cssClass));
		}
		else
		{
			html.append(newLine).append(generateHTMLForCalendar(
				componentId, false,checkBetweenOperator(attrDetails.getSelectedOperator()),cssClass));
		}
	}

	/**
	 * @param componentId component identifier
	 * @param attrDetails attribute details
	 * @param cssClass class
	 * @param html string buffer
	 * @return newLine
	 */
	private static void getHtmlForDate(String componentId,
			AttributeDetails attrDetails, String cssClass, StringBuffer html)
	{
		if (attrDetails.getDataType().equalsIgnoreCase(AQConstants.DATE))
		{
			html.append('\n').append(generateHTMLForCalendar(componentId, true, false,cssClass));
		}
		else
		{
			html.append("\n<td valign='top' width='1%'>&nbsp;</td>");
		}
	}

	/**
	 * @param attrDetails attribute details
	 * @param textBoxId1 text box
	 * @param html string buffer
	 */
	private static void getHtmlForBetweenOperator(AttributeDetails attrDetails,
			String textBoxId1, StringBuffer html)
	{
		html.append("<td width='15%'  valign='top' class=\"standardTextQuery\">\n");
		if (isBetween(attrDetails))
		{
			getHtmlTextBoxForBetweenOperator(attrDetails.getEditValues(), textBoxId1, html);
		}
		else
		{
			html.append("<input type=\"text\" name=\"" + textBoxId1 + "\" id=\"" + textBoxId1
					+ "\" style=\"display:none\">");
		}
		html.append(END_TD_TAG);
	}
	/**
	 *
	 * @param attrDetails AttributeDetails
	 * @return boolean
	 */
	private static boolean isBetween(AttributeDetails attrDetails)
	{
		return (attrDetails.getSelectedOperator()==null && attrDetails.isBetween())
				|| checkBetweenOperator(attrDetails.getSelectedOperator());
	}

	/**
	 * Generate HTML for text box based upon operator and values.
	 * @param values list of values
	 * @param operator selected operator
	 * @param textBoxId id of text box
	 * @param html generated HTML
	 */
	private static void getHtmlValueAndOperator(List<String> values, String operator, String textBoxId,
			StringBuffer html)
	{
		if (values == null || values.isEmpty())
		{
			getHtmlValueNull(operator, textBoxId, html);
		}
		else
		{
			getHtmlValueNotNull(values, operator, textBoxId, html);
		}
	}
	/**
	 *
	 * @param operator selected operator
	 * @param textBoxId id of text box
	 * @param html generated HTML
	 */
	private static void getHtmlValueNull(String operator, String textBoxId, StringBuffer html)
	{
		if(operator == null)
		{
			html.append("<input style=\"width:150px; display:block;\" type=\"text\" name=\""
					+ textBoxId + "\" id=\"" + textBoxId + "\">");
		}
		else
		{
			if(operator.equalsIgnoreCase(AQConstants.IS_NOT_NULL) ||
					operator.equalsIgnoreCase(AQConstants.IS_NULL))
			{
				html.append("<input style=\"width:150px; display:block;\" "
						+ "type=\"text\" disabled='true' name=\""
						+ textBoxId + "\" id=\"" + textBoxId + "\">");
			}

		}
	}
	/**
	 * @param values list of values
	 * @param operator selected operator
	 * @param textBoxId id of text box component
	 * @param html generated HTML
	 */
	private static void getHtmlValueNotNull(List<String> values, String operator, String textBoxId,
			StringBuffer html)
	{
		String valueStr;
		if (operator.equalsIgnoreCase(AQConstants.IN_STRING) || operator.equalsIgnoreCase(AQConstants.NOT_IN))
		{
			valueStr = values.toString();
			valueStr = valueStr.replace("[", "");
			valueStr = valueStr.replace("]", "");
			if(values.get(0) == null)
			{
				valueStr = "";
			}
			html.append("<input style=\"width:150px; display:block;\" type=\"text\" name=\""
					+ textBoxId + "\" id=\"" + textBoxId + "\" value=\"" + valueStr + "\">");
		}
		else
		{
			if(values.get(0) == null)
			{
				String temp = "<input style=\"width:150px; display:block;\" type=\"text\" name=\""
						+ textBoxId + "\" id=\"" + textBoxId + "\" value=\"" + "\">";
				html.append(temp);
			}
			else
			{
				html.append("<input style=\"width:150px; display:block;\" type=\"text\" name=\""
					+ textBoxId + "\" id=\"" + textBoxId + "\" value=\"" + values.get(0)
					+ "\">");
			}
		}
	}

	/**
	 * Method provides HTML for text box when operator is IsBetween.
	 * @param values list of values
	 * @param textBoxId1 id of text box component
	 * @param html generated HTML
	 */
	private static void getHtmlTextBoxForBetweenOperator(List<String> values, String textBoxId1,
			StringBuffer html)
	{
		if (values == null || values.isEmpty())
		{
			html.append("<input type=\"text\" name=\"" + textBoxId1 + "\" id=\"" + textBoxId1
					+ "\" style=\"display:block\">");
		}
		else
		{
			if(values.get(1) == null)
			{
				String temp = "<input type=\"text\" name=\"" + textBoxId1 + "\" id=\"" + textBoxId1
				+ "\" value=\"" + "\" style=\"display:block\">";
				html.append(temp);
			}
			else
			{
			html.append("<input type=\"text\" name=\"" + textBoxId1 + "\" id=\"" + textBoxId1
					+ "\" value=\"" + values.get(1) + "\" style=\"display:block\">");
			}
		}
	}
	/**
	 * Generators HTML for Calendar.Depending upon the value of operator the
	 * calendar is displayed(hidden/visible).
	 * @param componentId String
	 * @param isFirst
	 *            boolean
	 * @param isBetween
	 *            boolean
	 * @param cssClass
	 * 	       String
	 * @return String HTMLForCalendar
	 */
	private static String generateHTMLForCalendar(String  componentId, boolean isFirst,
			boolean isBetween, String cssClass)
	{
		String datePattern = CommonServiceLocator.getInstance().getDatePattern();
		StringBuffer innerStr = new StringBuffer(90); 
		if (isFirst)
		{
			String textBoxId = componentId + "_textBox";
			String calendarId = componentId + "_calendar";
			String imgStr = "\n<img id=\"calendarImg\" " +
					"src=\"images/advQuery/calendar.gif\" width=\"24\" height=\"22\"" +
					" border=\"0\" onclick='scwShow(document.getElementById(\""+ textBoxId + "\"),event,\""+datePattern+"\");'>";
			innerStr.append("\n<td width='3%' class='"+ cssClass
					    + "' valign='top' id=\"" + calendarId + "\">"
						+ "\n" + imgStr);
		}
		else
		{
			String textBoxId1 = componentId + "_textBox1";
			String calendarId1 = componentId + "_calendar1";
			String imgStr = "\n<img id=\"calendarImg\" " +
					"src=\"images/advQuery/calendar.gif\"" +
					" width=\"24\" height=\"22\" border='0'" +
					" onclick='scwShow(document.getElementById(\""+ textBoxId1 + "\"),event,\""+datePattern+"\");'>";
			String style;
			if (isBetween)
			{
				style = "display:block";
			}
			else
			{
				style = "display:none";
			}
			innerStr.append("\n<td width='3%' class='"+ cssClass
						+ "' valign='top' id=\"" + calendarId1 + "\" style=\"" + style
						+ "\">"
						+ "\n" + imgStr) ;
		}
		innerStr.append(END_TD_TAG);
		return innerStr.toString();
	}

	/**
	 * Method provides HTML for Add Limits Header.
	 * @param entityName String
	 * @param entityId String
	 * @param attributeCollection String
	 * @param isEditLimits boolean
	 * @return StringBuffer
	 */
	public static StringBuffer getHtmlHeader(String entityName,String entityId,
			String attributeCollection, boolean isEditLimits)
	{
		StringBuffer generatedPreHTML = new StringBuffer(AQConstants.MAX_SIZE);
		String header = AQConstants.DEFINE_SEARCH_RULES;
		String html = "<table border=\"0\" width=\"100%\" height=\"30%\" " +
					  "cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#EAEAEA\" >" +
					  "\n<tr height=\"2%\" bgcolor=\"#EAEAEA\" > " +
					  "<td class='standardLabelQuery' valign='top' height=\"2%\" " +
					  "colspan=\"8\" bgcolor=\"#EAEAEA\" ><font face=\"Arial\" size=\"2\" " +
					  "color=\"#000000\"><b>";
		generatedPreHTML.append(html);
		generatedPreHTML.append(header + " '" + entityName + "'</b></font>");
		generatedPreHTML.append(END_TD_TAG);
		generatedPreHTML.append("####");
		generatedPreHTML.append(generateHTMLForButton(entityId,attributeCollection, isEditLimits));
		generatedPreHTML.append("\n</tr></table>");
		return generatedPreHTML;
	}
	/**
	 * Generates HTML for button.
	 * @param entityName
	 *            entityName
	 * @param attributesStr
	 *            attributesStr
	 * @param isEditLimits boolean
	 * @return String HTMLForButton
	 */
	private static String generateHTMLForButton(String entityName, String attributesStr,
			boolean isEditLimits)
	{
		String buttonName = "addLimit";
		StringBuffer html = new StringBuffer(AQConstants.MAX_SIZE);
		String temp = "\n<td bgcolor=\"#EAEAEA\" colspan=\"2\" " +
				"height=\"2%\" valign=\"top\" align=\"right\" >";
		String buttonId = "TopAddLimitButton";
		html.append(temp);
		String buttonCaption;
		if (isEditLimits)
		{
			buttonCaption = "Edit Limit";
		}
		else
		{
			buttonCaption = "Add Limit";
		}
		html.append("\n<input id=\"" + buttonId + "\" type=\"button\" name=\"" + buttonName
				+ "\" onClick=\"produceQuery('" + buttonId
				+ "', 'addToLimitSet.do', 'categorySearchForm', '" + entityName + "','"
				+ attributesStr + "')\" value=\"" + buttonCaption + "\"></input>");
		html.append(END_TD_TAG);
		return html.toString();
	}
	/**
	 * @param attributeCollection String
	 * @param nameOfTheEntity String
	 * @param isEditLimits boolean
	 * @return StringBuffer
	 */
	public static StringBuffer generatePreHtml(String attributeCollection,
			String nameOfTheEntity, boolean isEditLimits)
	{
		String header = AQConstants.DEFINE_SEARCH_RULES;
		String entityName = Utility.parseClassName(nameOfTheEntity);
		StringBuffer generatedPreHTML = new StringBuffer(AQConstants.MAX_SIZE);
		String html = "<table border=\"0\" width=\"100%\" height=\"30%\" " +
					  "cellspacing=\"0\" cellpadding=\"0\">" +
					  "\n<tr height=\"2%\"> " +
					  "<td valign='top' height=\"2%\" colspan=\"8\" " +
					  "bgcolor=\"#EAEAEA\" ><font face=\"Arial\" size=\"2\" " +
					  "color=\"#000000\"><b>";
		generatedPreHTML.append(html);
		generatedPreHTML.append(header + " '" + entityName + "'</b></font>");
		generatedPreHTML.append(END_TD_TAG);
		generatedPreHTML.append(generateHTMLForButton(nameOfTheEntity,attributeCollection,isEditLimits));
		generatedPreHTML.append("\n</tr></table>");
		return generatedPreHTML;
	}
	/**
	 * This method generates the combobox's HTML to show the operators valid for
	 * the attribute passed to it.
	 * @param componentId
	 *            String
	 * @param isDate
	 *            boolean
	 * @param attributeDetails
	 *            AttributeDetails
	 * @return String HTMLForOperators
	 */
	public static String generateHTMLForOperators(String componentId,boolean isDate,
			AttributeDetails attributeDetails)
	{
		StringBuffer html = new StringBuffer();
		List<String> operatorsList = attributeDetails.getOperatorsList();
		if (operatorsList != null && !operatorsList.isEmpty())
		{
			html.append("\n<td width='15%' class=" + CSS_PV + " valign='top' >");
			if (isDate)
			{
				html
						.append("\n<select   class=" + CSS_PV
								+ " id=\""+componentId+"_combobox\" "
								+"style=\"width:150px; display:block;\" name=\""
								+ componentId + "_combobox\" "
								+ "onChange=\"operatorChanged('"
								+ componentId + "','true')\">");
			}
			else
			{
				html.append("\n<select  class=" + CSS_PV
						+ " id=\""+componentId+"_combobox\" "
						+ " style=\"width:150px; display:block;\" name=\"" + componentId
						+ "_combobox\" onChange=\"operatorChanged('" + componentId
						+ "','false')\">");
			}
			getHtmlForSelectedOperator(attributeDetails, CSS_PV, html, operatorsList);
			html.append("\n</select>");
			html.append(END_TD_TAG);
		}
		return html.toString();
	}

	/**
	 * Method generates HTML for selected operator.
	 * @param attributeDetails AttributeDetails
	 * @param cssClass String
	 * @param html StringBuffer
	 * @param operatorsList List
	 */
	private static void getHtmlForSelectedOperator(AttributeDetails attributeDetails,
			String cssClass, StringBuffer html, List<String> operatorsList)
	{
		Iterator<String> iter = operatorsList.iterator();
		while (iter.hasNext())
		{
			String operator = iter.next();
			if (operator.equalsIgnoreCase(attributeDetails.getSelectedOperator()))
			{
				html.append("\n<option  class=" + cssClass + " value=\"" + operator
						+ "\" SELECTED>" + operator + "</option>");
			}
			else
			{
				html.append("\n<option  class=" + cssClass + " value=\"" + operator + "\">"
						+ operator + "</option>");
			}
		}
	}
	/**
	 * @param generatedHTML StringBuffer
	 */
	public static void getTags(StringBuffer generatedHTML)
	{
		generatedHTML.append("\n<tr>\n<td valign=\"top\">");
		generatedHTML.append(END_TD_TAG);
		generatedHTML.append("\n</tr>");
	}

	/**
	 * Create a map which holds the list of all Expression(DAGNode) ids for a particular entity.
	 * @param expressionMap Map
	 * @return map consisting of the entity and their corresponding expression ids
	 */
	public static Map<EntityInterface, List<Integer>> getEntityExpressionIdListMap(
			Map<Integer, Map<EntityInterface, List<ICondition>>> expressionMap)
	{
			Map<EntityInterface, List<Integer>> entityExpressionIdMap =
				new HashMap<EntityInterface,List<Integer>>();
			Iterator<Integer> outerMapIterator = expressionMap.keySet().iterator();
			while (outerMapIterator.hasNext())
			{
				Integer expressionId = outerMapIterator.next();
				Map<EntityInterface, List<ICondition>> entityMap = expressionMap.get(expressionId);
				if (!entityMap.isEmpty())
				{
					Iterator<EntityInterface> innerMapIterator = entityMap.keySet().iterator();
					while (innerMapIterator.hasNext())
					{
						List<Integer> dagIdList;
						EntityInterface entity = innerMapIterator.next();
						if (!entityExpressionIdMap.containsKey(entity))
						{
							//if the entity is not present in the map
							//create new list and add it to map
							dagIdList = new ArrayList<Integer>();
							dagIdList.add(expressionId);
							entityExpressionIdMap.put(entity, dagIdList);
							continue;
						}
						//if the entity is present in the map
						//add the DAG id to the existing list
						dagIdList = entityExpressionIdMap.get(entity);
						dagIdList.add(expressionId);
						entityExpressionIdMap.put(entity, dagIdList);
					}
				}
			}
		return entityExpressionIdMap;
	}
	/**
	 *
	 * @param componentId String
	 * @param isSelected boolean
	 * @return String
	 */
	public static String generateCheckBox(String componentId, boolean isSelected)
	{
		String select = "";
		if(isSelected)
		{
			select="CHECKED";
		}
		String tag = "<td class=\"standardTextQuery\"  width=\"5\" valign=\"top\">"
				+ "<input type=\"checkbox\"   id='"
				+ componentId
				+ "_checkbox' "
				+ select
				+ "  onClick=\"enableDisplayField(this.form,'" + componentId + "')\"></td>";
		return tag;
	}
	/**
	 *
	 * @param componentId String
	 * @param oper String
	 * @param operatorList List
	 * @param isSecondTime boolean
	 * @return String
	 */
	public static String generateHTMLForOperator(String componentId,String oper,
			List<String>operatorList,boolean isSecondTime)
	{
		StringBuffer generateHTML = new StringBuffer();
		String comboboxId;
		if(isSecondTime)
		{
			comboboxId = "_combobox1";
		}
		else
		{
			comboboxId = "_combobox";
		}
		String comboboxName = componentId+comboboxId;
		if (operatorList != null && !operatorList.isEmpty())
		{
			String html = "\n<td width='15%'  valign='top' >"
						+"\n<select "
						+ " style=\"width:150px; display:block;\" name=\"" + comboboxName
						+ "\" id ='"+comboboxName+"' onChange=\"operatorChanged('"
						+ componentId + "','true')\">";
			generateHTML.append(html);
			Iterator<String> iter = operatorList.iterator();
			String operator;
			while (iter.hasNext())
			{
				operator = iter.next();
				getHtmlSelectAttribute(oper, generateHTML, operator);
			}
			generateHTML.append("\n</select>");
		}
		return generateHTML.toString();
	}
	/**
	 * @param oper
	 * 		String
	 * @param generateHTML
	 * 		StringBuffer
	 * @param operator String
	 */
	private static void getHtmlSelectAttribute(String oper, StringBuffer generateHTML,
			String operator)
	{
		if (operator.equalsIgnoreCase(oper))
		{
			generateHTML.append("\n<option   value=\"" + operator
					+ "\" SELECTED>" + operator + "</option>");
		}
		else
		{
			generateHTML.append("\n<option   value=\"" + operator + "\">"
					+ operator + "</option>");
		}
	}
	/**
	 * @param forPage
	 * 		String
	 * @param generatedHTML
	 * 	StringBuffer
	 */
	public static void getHtmlAddEditPage(String forPage, StringBuffer generatedHTML)
	{
		if (forPage.equalsIgnoreCase(AQConstants.ADD_EDIT_PAGE))
		{
			generatedHTML
					.append("<table border=\"0\" width=\"100%\" " +
							"height=\"100%\" cellspacing=\"0\" cellpadding=\"0\">");
			GenerateHtml.getTags(generatedHTML);
		}
	}
	/**
	 * Method generates HTML for bold attribute label.
	 * @param attrLabel
	 * 		String
	 * @return StringBuffer
	 */
	public static StringBuffer getBoldLabel(String attrLabel)
	{
		StringBuffer label = new StringBuffer("<b>");
		label.append(attrLabel).append("</b>");
		return label;
	}

	/**
	 * This method provides the alternate CSS for alternate attributes of an entity.
	 * @param generatedHTML
	 * 			StringBuffer
	 * @param isBGColor
	 * 		boolean
	 * @param componentId
	 * 		String
	 * @return boolean
	 */
	public static boolean getAlternateCss(StringBuffer generatedHTML, boolean isBGColor,
			String componentId)
	{
		String styleSheetClass;
		boolean bgColor = isBGColor;
		if (bgColor)
		{
			styleSheetClass = CSS_BGGREY;
		}
		else
		{
			styleSheetClass = CSS_BGWHITE;
		}
		bgColor ^= true;  //bgColor = !bgColor;
		String html = "\n<tr class='"
			+ styleSheetClass
			+ "' id=\""
			+ componentId
			+ "\" height=\"6%\" >\n"
			+ "<td valign='top' align='right' "
			+ "class='standardLabelQuery' nowrap='nowrap' width=\"15%\">";
		generatedHTML.append(html);
		return bgColor;
	}
	/**
	 * Checks if operator is between operator.
	 * @param operator
	 * 			String
	 * @return boolean
	 */
	public static boolean checkBetweenOperator(String operator)
	{
		boolean isBetween = false;
		if (operator!=null && operator.equalsIgnoreCase(
						RelationalOperator.Between.toString()))
		{
			isBetween = true;
		}
		return isBetween;
	}
	/**
	 * Generates HTML for date format.
	 * @param generatedHTML
	 * 			StringBuffer
	 * @param isBold
	 * 			boolean
	 * @param attribute
	 * 		AttributeInterface
	 */
	public static void getDateFormat(StringBuffer generatedHTML, boolean isBold,
			AttributeInterface attribute, boolean isParameterized)
	{
		if (attribute.getDataType().equalsIgnoreCase(AQConstants.DATE) && isParameterized)
		{
			StringBuffer dateFormat = new StringBuffer(CommonServiceLocator.getInstance().getDatePattern());
			if(isBold)
			{
				dateFormat.append("<b>").append(dateFormat).append("</b>");
			}
			generatedHTML.append("\n(" + dateFormat + ")");
		}
	}
}
