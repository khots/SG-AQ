package edu.wustl.query.htmlprovider;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IParameter;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.TermType;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.common.querysuite.queryobject.impl.DateLiteral;
import edu.wustl.common.querysuite.queryobject.impl.DateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.querysuite.utils.QueryUtility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.TemporalQueryUtility;
/**
 * This class generated html for saved and temporal queries.
 */

public class SavedQueryHtmlProvider
{
	/**
	 * This method retrieve expressionid , entity and conditions from the query object.
	 * @param queryObject IQuery
	 * @param isShowAll boolean
	 * @param forPage page from where request is made (Saved query/Execute query)
	 * @param cFIndexMap map of custom formula
	 * @return generated html
	 */
	public String getHTMLForSavedQuery(IQuery queryObject, boolean isShowAll,
			String forPage,Map<Integer,ICustomFormula> cFIndexMap)
	{
		HtmlProvider generateHtml = new HtmlProvider(null);
		StringBuffer htmlString = new StringBuffer();
		List<IParameter<?>> parameterList = null;
		Map<ICustomFormula, String> cFColumNameMap =
			getCustomFormulaColumnNameMap(queryObject,forPage);
		if (queryObject instanceof ParameterizedQuery)
		{
			ParameterizedQuery pQuery = (ParameterizedQuery) queryObject;
			parameterList = pQuery.getParameters();
		}
		Map<Integer, Map<EntityInterface, List<ICondition>>> expressionMap =
			new HashMap<Integer, Map<EntityInterface, List<ICondition>>>();
		IConstraints constraints = queryObject.getConstraints();
		for(IExpression expression : constraints)
		{
			for (int i = 0; i < expression.numberOfOperands(); i++)
			{
				IExpressionOperand operand = expression.getOperand(i);
				EntityInterface entity =
						expression.getQueryEntity().getDynamicExtensionsEntity();
					if(operand instanceof IRule)
					{
						IRule ruleObject = (IRule) operand;
						List<ICondition> conditions =
							edu.wustl.common.util.Collections.list(ruleObject);
						Map<EntityInterface, List<ICondition>> conditionMap =
							new HashMap<EntityInterface, List<ICondition>>();
						conditionMap.put(entity, conditions);
						expressionMap.put(
							expression.getExpressionId(), conditionMap);
					}
			}
		}
		htmlString.append(createTableForSavedQuery(forPage));
		htmlString.append(generateHtml.
				getHtmlForSavedQuery(expressionMap, isShowAll, forPage,parameterList));
		htmlString.append(
				generateHTMLForTemporalSavedQuery(
						cFColumNameMap,forPage,cFIndexMap));
		htmlString.append("</table>");

		return htmlString.toString();
	}
	/**
	 * Gets map of custom formula and column name.
	 * @param queryObject IQuery
	 * @param forPage String
	 * @return map
	 */
	private Map<ICustomFormula, String> getCustomFormulaColumnNameMap(IQuery queryObject,String forPage)
	{
		List<IOutputTerm> outputTerms = queryObject.getOutputTerms();
		Set<ICustomFormula> customFormulas = null;
		if (forPage.equalsIgnoreCase(AQConstants.SAVE_QUERY_PAGE))
		{
			customFormulas = QueryUtility.getCustomFormulas(queryObject);
		}
		else if(forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE))
		{
			ParameterizedQuery pQuery = (ParameterizedQuery)queryObject;
			customFormulas = (Set<ICustomFormula>) QueryUtility.
			getAllParameterizedCustomFormulas(pQuery);
		}
		Map<ICustomFormula, String> cFColumNameMap =
			createCustomFormulaColumnNameMap(outputTerms, customFormulas);
		return cFColumNameMap;
	}

	/**
	 * Creates customformulaColumNameMap.
	 * @param outputTerms list of IOutputTerm
	 * @param customFormulas set of ICustomFormula
	 * @return Map
	 */
	private Map<ICustomFormula, String> createCustomFormulaColumnNameMap(List<IOutputTerm> outputTerms,
			Set<ICustomFormula> customFormulas)
	{
		Map<ICustomFormula, String> cFColumNameMap = new HashMap<ICustomFormula, String>();
		for (ICustomFormula customFormula : customFormulas)
		{
			for (IOutputTerm outputTerm : outputTerms)
			{
				if (customFormula.getLhs().equals(outputTerm.getTerm()))
				{
					cFColumNameMap.put(customFormula, outputTerm.getName());
				}
			}
		}
		return cFColumNameMap;
	}

	/**
	 * @param forPage String
	 * @return html
	 */
	private String createTableForSavedQuery(String forPage)
	{
		StringBuffer generatedHTML = new StringBuffer(
				"<table  cellpadding=\"3\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
		String html;
		if (forPage.equalsIgnoreCase(AQConstants.SAVE_QUERY_PAGE))
		{
			html="<tr ><td  valign='center' "
				+"class='formSubTitleWithoutBorder'>"
				+ ApplicationProperties
								.getValue("savequery.column.userDefined")
				+ "</td><td valign='center' class='formSubTitleWithoutBorder'>"
				+ ApplicationProperties
						.getValue("savequery.column.displayLabel")
				+ "</td><td valign='center' class='formSubTitleWithoutBorder'>"
				+ ApplicationProperties
						.getValue("savequery.column.attributeName")
				+ "</td><td valign='center' class='formSubTitleWithoutBorder'>"
				+ ApplicationProperties
								.getValue("savequery.column.condition")
				+ "</td><td colspan=\"4\"  valign='center' class='formSubTitleWithoutBorder'>"
				+ ApplicationProperties
						.getValue("savequery.column.value")
				+ "</td></tr>";
			generatedHTML
					.append(html);
		}
		else if(forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE))
		{
			html="<tr valign='top'>"
				 +"<td class=\"formSubTitleWithoutBorder\" valign='top'>"
				 + ApplicationProperties
							.getValue("savequery.column.displayLabel")
				 + "</td><td class=\"formSubTitleWithoutBorder\" valign='top' >"
				 + ApplicationProperties
				 		.getValue("savequery.column.condition")
				 + "</td><td class=\"formSubTitleWithoutBorder\" colspan=\"4\" valign='top' >"
				 + ApplicationProperties
				 	.getValue("savequery.column.value")
				 + "</td></tr>";
			generatedHTML
				.append(html);
		}
		return generatedHTML.toString();
	}

	/**
	 * Generate html for temporal query.
	 * @param cFColumNameMap Map
	 * @param forPage String
	 * @param cFIndexMap Map
	 * @return generated html
	 */
	private String generateHTMLForTemporalSavedQuery(
			Map<ICustomFormula, String> cFColumNameMap,
			String forPage,Map<Integer,ICustomFormula> cFIndexMap)
	{
        StringBuffer generateHTML = new StringBuffer(AQConstants.MAX_SIZE);
        int count = 0;
        String html;
        for (ICustomFormula customFormula : cFColumNameMap.keySet())
        {
        	String operator=customFormula.getOperator().getStringRepresentation();
			//String cssClass = "";
			String componentId = "comp"+count;
			generateHTML.append("\n<tr>");
        	if(forPage.equalsIgnoreCase(AQConstants.SAVE_QUERY_PAGE))
        	{
        		html =" " + GenerateHtml.generateCheckBox(componentId, false)
					+ "<td valign='top' align='left' class='standardTextQuery'>"
					+ "<label for='" + componentId + "_displayName' title='"
					+ cFColumNameMap.get(customFormula) + "'>"
					+ "<input type=\"textbox\"  class=\"formFieldSized20\"  name='"
					+ componentId + "_displayName'     id='" + componentId
					+ "_displayName' value='" + cFColumNameMap.get(customFormula)
					+ "' disabled='true'> " + "</label></td>";
				generateHTML.append(html);
				html = "<td valign=\"top\"></td>";
				generateHTML.append(html);
			}
        	else if(forPage.equalsIgnoreCase(AQConstants.EXECUTE_QUERY_PAGE))
        	{
        		html ="<td valign='top' align='left' "
        			+ "class='standardTextQuery' nowrap='nowrap' width=\"15%\">"
					+ cFColumNameMap.get(customFormula) + " ";
				generateHTML.append(html);
				generateHTML.append(AQConstants.END_TD);
			}
        	getHtmlForTerm(generateHTML, customFormula, operator, componentId);
        	cFIndexMap.put(count, customFormula);
        	count++;
		}
        html="<input type='hidden' id='totalCF' value='"+ cFColumNameMap.keySet().size() + "' />";
        generateHTML.append(html);
        generateHTML.append("<input type='hidden' id='strToFormTQ' value='' name='strToFormTQ'/>");
		return generateHTML.toString();
	}

	/**
	 * Get html for each term in temporal query.
	 * @param generateHTML StringBuffer
	 * @param customFormula ICustomFormula
	 * @param operator String
	 * @param componentId String
	 */
	private void getHtmlForTerm(StringBuffer generateHTML, ICustomFormula customFormula,
			String operator, String componentId)
	{
		TermType termType = customFormula.getLhs().getTermType();
		List<String> operatorList = TemporalQueryUtility.getRelationalOperators();

		generateHTML.append(GenerateHtml.generateHTMLForOperator(componentId,operator,operatorList,false));
		ITerm term = customFormula.getAllRhs().get(0);
        generateHTML.append(getHtmlDSInterval(componentId, term,termType));
		boolean isTimeStamp = getHtmlTimestamp(generateHTML, componentId, termType, term);
		String html = "<td valign=\"top\"></td>"
			         +"<input type='hidden' id='isTimeStamp_"
			         +componentId +"' value='"+ isTimeStamp + "' />"
			         +"\n</tr>";
		generateHTML.append(html);
	}

	/**
	 * Get html for time stamp attribute.
	 * @param generateHTML generated html
	 * @param componentId component id
	 * @param termType TermType
	 * @param term ITerm
	 * @return isTimeStamp
	 */
	private boolean getHtmlTimestamp(StringBuffer generateHTML, String componentId,
			 TermType termType, ITerm term)
	{
		boolean isTimeStamp;
		if(termType.equals(TermType.Timestamp))
		{
			DateLiteral operand = (DateLiteral)term.getOperand(0);
			String textBoxId = componentId + "_textBox";
			String calendarId = componentId + "_calendar";
			generateHTML.append("<td valign=\"top\">");
			String date = "";
			String datePattern = CommonServiceLocator.getInstance().getDatePattern();	
			if(operand.getDate()!=null)
			{
				SimpleDateFormat format =
				        new SimpleDateFormat(datePattern);
				date = format.format(operand.getDate());
			}
			String html ="<input style=\"width:150px; display:block;\" value='"
				+date+"' type=\"text\" name=\""
			    +textBoxId + "\" id=\"" +textBoxId  + "\">";
			generateHTML.append(html);
			generateHTML.append("</td>");
			String imgStr = "<img id=\"calendarImg\" "
					+"src=\"images/advQuery/calendar.gif\" width=\"24\" height=\"22\""
					+" border=\"0\"  onclick='scwShow("+ textBoxId + ",event,\""+datePattern+"\");'>";
			String innerStr = "\n<td width='45%' valign='top' id=\"" + calendarId + "\">"
						+ "\n" + imgStr ;
			generateHTML.append(innerStr);
			generateHTML.append("</td>");
			isTimeStamp = true;
		}
		else
		{
			isTimeStamp = false;
		}
		return isTimeStamp;
	}

	/**
	 * This method gets html for DSInterval.
	 * @param componentId String
	 * @param term ITerm
	 * @param termType TermType
	 * @return generated html
	 */
	private StringBuffer getHtmlDSInterval(String componentId, ITerm term,TermType termType)
	{
		StringBuffer generateHTML = new StringBuffer();
		if(termType.equals(TermType.DSInterval))
    	{
			List<String> operatorList;
			DateOffsetLiteral operand = (DateOffsetLiteral)term.getOperand(0);
			TimeInterval<?> timeInterval = operand.getTimeInterval();
			StringBuffer operator= new StringBuffer(timeInterval.toString());
			operator.append('s');
			operatorList = TemporalQueryUtility.getTimeIntervals();
			String html = "<td valign=\"top\">";
			generateHTML.append(html);
			html = "<input style=\"width:150px; display:block;\" "
				 + "type=\"text\" value = '"+operand.getOffset()+" 'name=\""
			     + componentId + "\" id=\""+componentId+"_textBox"+ "\">";
			generateHTML.append(html);
			generateHTML.append("</td>");
			generateHTML.append(GenerateHtml.
				generateHTMLForOperator(componentId,operator.toString(),operatorList,true));
    	}
		return generateHTML;
	}
}
