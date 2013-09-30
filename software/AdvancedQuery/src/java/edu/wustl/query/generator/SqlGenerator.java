
package edu.wustl.query.generator;

import static edu.wustl.query.generator.SqlKeyWords.WHERE;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.common.dynamicextensions.domain.BooleanAttributeTypeInformation;
import edu.common.dynamicextensions.domain.UserDefinedDE;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.BooleanTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DateTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.DoubleTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.FileTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.FloatTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.IntegerTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.LongTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.common.dynamicextensions.domaininterface.StringTypeInformationInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.exceptions.SqlException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.IArithmeticOperand;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConnector;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IDateLiteral;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionAttribute;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.common.querysuite.queryobject.impl.Connector;
import edu.wustl.common.querysuite.queryobject.impl.DateLiteral;
import edu.wustl.common.querysuite.queryobject.impl.DateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;
import edu.wustl.common.querysuite.utils.CustomFormulaProcessor;
import edu.wustl.common.querysuite.utils.DatabaseSQLSettings;
import edu.wustl.common.querysuite.utils.DatabaseType;
import edu.wustl.common.querysuite.utils.TermProcessor;
import edu.wustl.common.querysuite.utils.TermProcessor.IAttributeAliasProvider;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.DAOFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.metadata.util.DyExtnObjectCloner;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * To generate SQL from the given Query Object.
 *
 * @author prafull_kadam
 */
public class SqlGenerator implements ISqlGenerator
{
	
	/**
	 * Select string.
	 */
	private static final String SELECT = "select ";

	/**
	 * Select Distinct.
	 */
	private static final String SELECT_DISTINCT = "select distinct ";

	/**
	 * strTodateFunction.
	 */
	private static String strTodateFunction = "";

	/**
	 * The date pattern.
	 */
	private static String datePattern = "";

	/**
	 * reference to the join graph object present in the query object.
	 */
	private JoinGraph joinGraph;

	/**
	 * reference to the constraints object present in the query object.
	 */
	private IConstraints constraints;

	/**
	 * Column name.
	 */
	public static final String COLUMN_NAME = "Column";

	/**
	 * Comma.
	 */
	private static final String SELECT_COMMA = ", ";
	/**
	 * This set will contain the expression id's of the empty expression. An
	 * expression is empty expression when it does not contain any Rule & its
	 * sub-expressions (also their subexpressions & so on) also does not contain
	 * any Rule
	 */
	private Set<IExpression> emptyExpressions;// Set of Empty Expressions.

	// Variables required for output tree.
	/**
	 * List of Roots of the output tree node.
	 */
	private List<OutputTreeDataNode> rootOpTreeNodLst;

	/**
	 * Object of FromBuilder.
	 */
	private FromBuilder fromBuilder;

	/**
	 * This count represents number of output trees formed.
	 */
	private int treeNo;

	/**
	 * Attribute to column name mapping.
	 */
	private final Map<AttributeInterface, String> attrColNameMap = new HashMap<AttributeInterface, String>();

	/**
	 * To specify if the query contains column of CLOB type.
	 */
	private boolean hasCLOBTypeCol = false;

	/**
	 * column value bean for SQL injection.
	 */
	private LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();

	/**
	 * Required to populate column value bean.
	 */
	private LinkedList<Object> data = new LinkedList<Object>();

	private static final String LOWER = "lower(";

	/**
	 * Generates SQL for the given Query Object.
	 *
	 * @param query
	 *            The Reference to Query Object.
	 * @return the String representing SQL for the given Query object.
	 * @throws MultipleRootsException
	 *             When there are multiple roots present in a graph.
	 * @throws SqlException
	 *             When there is error in the passed IQuery object.
	 * @throws RuntimeException
	 *             RuntimeException
	 * @throws DAOException
	 *             DAOException
	 * @see edu.wustl.common.querysuite.queryengine.ISqlGenerator#generateSQL
	 *      (edu.wustl.common.querysuite.queryobject.IQuery)
	 */
	public String generateSQL(IQuery query) throws MultipleRootsException, SqlException,
			RuntimeException, DAOException
	{
		columnValueBean = new LinkedList<ColumnValueBean>();
		data = new LinkedList<Object>();
		Logger.out.debug("Started SqlGenerator.generateSQL().....");
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO dao = daofactory.getJDBCDAO();
		strTodateFunction = dao.getStrTodateFunction();
		datePattern = dao.getDatePattern();
		String sql = buildQuery(query);
		Logger.out.debug("Finished SqlGenerator.generateSQL()...SQL:" + sql);
		columnValueBean = populateColumnValueBean(data);
		return sql;
	}

	/**
	 * To initialize map the variables. & build the SQL for the Given Query
	 * Object.
	 *
	 * @param query
	 *            the IQuery reference.
	 * @return The Root Expression of the IQuery.
	 * @throws MultipleRootsException
	 *             When there exists multiple roots in join graph.
	 * @throws SqlException
	 *             When there is error in the passed IQuery object.
	 * @throws RuntimeException
	 *             RuntimeException
	 */
	private String buildQuery(IQuery query) throws MultipleRootsException, SqlException,
			RuntimeException
	{
		IQuery queryClone = cloneQuery(query);
		constraints = queryClone.getConstraints();

		joinGraph = (JoinGraph) constraints.getJoinGraph();
		IExpression rootExpression = constraints.getRootExpression();

		// Identifying empty Expressions.
		emptyExpressions = new HashSet<IExpression>();
		isEmptyExpression(rootExpression.getExpressionId());

		// Generating output tree.
		createTree();

		// Creating SQL.
		fromBuilder = new FromBuilder(joinGraph);
		// String fromPart = fromBuilder.getFromPartSQL(rootExpression, null,
		// new HashSet<IExpression>());
		String fromPart = fromBuilder.getFromClause();
		String wherePart = getCompleteWherePart(rootExpression);
		wherePart += fromBuilder.getWhereClause();
		String selectPart = getSelectPart();
		selectPart += getSelectForOutputTerms(queryClone.getOutputTerms());
		selectPart = removeLastComma(selectPart);

		String sql = selectPart + " " + fromPart + " " + wherePart;
		log(sql);
		return sql;
	}

	/**
	 * Returns the clone of the passed query.
	 *
	 * @param query
	 *            query
	 * @return clone of the q	uery
	 */
	private IQuery cloneQuery(IQuery query)
	{
		return new DyExtnObjectCloner().clone(query);
	}

	/**
	 * @param sql
	 *            the query
	 */
	private void log(String sql)
	{
		try
		{
			new SQLLogger().log(sql);
		}
		catch (IOException e)
		{
			Logger.out.error("Error while logging sql.\n" + e);
		}
	}

	/**
	 * Returns complete where part which includes PAND conditions.
	 *
	 * @param rootExpression
	 *            The root expression
	 * @return wherePart The where part
	 * @throws SqlException
	 *             SqlException
	 * @throws RuntimeException
	 *             RuntimeException
	 */
	private String getCompleteWherePart(IExpression rootExpression) throws SqlException,
			RuntimeException
	{
		String wherePart = getWherePartSQL(rootExpression);
		StringBuffer where = new StringBuffer();
		where.append(WHERE).append(wherePart);
		return where.toString();
	}

	/**
	 * To get the select part of the SQL.
	 *
	 * @return The SQL for the select part of the query.
	 */
	private String getSelectPart()
	{
		StringBuffer selectAttributes = new StringBuffer(80);
		selectIndex = 0;
		StringBuffer selectAttribute = new StringBuffer(80);
		for (OutputTreeDataNode rootOutputTreeNode : rootOpTreeNodLst)
		{
			selectAttributes.append(getSelectAttributes(rootOutputTreeNode));
		}
		// Deepti : added quick fix for bug 6950. Add distinct only when columns
		// do not include CLOB type.
		if (hasCLOBTypeCol)
		{
			selectAttribute.append(SELECT).append(selectAttributes);
		}
		else
		{
			selectAttribute.append(SELECT_DISTINCT).append(selectAttributes);
		}
		return selectAttribute.toString();
	}

	/**
	 * Removes the last comma from the given string.
	 *
	 * @param selectString
	 *            select clause
	 * @return select clause with last comma removed
	 */
	private String removeLastComma(String selectString)
	{
		String select = selectString;
		if (select.endsWith(SELECT_COMMA))
		{
			select = select.substring(0, select.length() - SELECT_COMMA.length());
		}
		return select;
	}

	/**
	 * It will return the select part attributes for this node along with its
	 * child nodes.
	 *
	 * @param treeNode
	 *            the output tree node.
	 * @return The select part attributes for this node along with its child
	 *         nodes.
	 */
	private String getSelectAttributes(OutputTreeDataNode treeNode)
	{
		StringBuffer selectPart = new StringBuffer("");
		IExpression expression = constraints.getExpression(treeNode.getExpressionId());

		IOutputEntity outputEntity = treeNode.getOutputEntity();
		List<AttributeInterface> attributes = outputEntity.getSelectedAttributes();
		EntityInterface deEntity = outputEntity.getDynamicExtensionsEntity();

		for (AttributeInterface attribute : attributes)
		{
			selectPart.append(fromBuilder.aliasOf(attribute, expression));
			String columnAliasName = COLUMN_NAME + selectIndex;
			selectPart.append(" " + columnAliasName + SELECT_COMMA);
			// code to get display name. & pass it to the Constructor along with
			// treeNode.
			String className = edu.wustl.query.util.global.Utility.parseClassName(deEntity
					.getName());
			String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute
					.getName());
			String displayNmForCol = className + " : " + attributeLabel;
			treeNode.addAttribute(new QueryOutputTreeAttributeMetadata(attribute, columnAliasName,
					treeNode, displayNmForCol));
			attrColNameMap.put(attribute, columnAliasName);
			selectIndex++;
			if ("file".equalsIgnoreCase(attribute.getDataType()))
			{
				hasCLOBTypeCol = true;
			}
		}
		List<OutputTreeDataNode> children = treeNode.getChildren();
		for (OutputTreeDataNode childTreeNode : children)
		{
			selectPart.append(getSelectAttributes(childTreeNode));
		}
		return selectPart.toString();
	}

	/**
	 * Select index.
	 */
	private int selectIndex;

	/**
	 * To compile the SQL & get the SQL representation of the Expression.
	 *
	 * @param expression
	 *            the Expression whose SQL to be generated.
	 * @return The SQL representation of the Expression.
	 * @throws SqlException
	 *             When there is error in the passed IQuery object.
	 * @throws RuntimeException
	 *             RuntimeException
	 */
	private String getWherePartSQL(IExpression expression) throws SqlException, RuntimeException
	{
		StringBuffer buffer = new StringBuffer("");
		int currentNestingCounter = 0;
		// holds current nesting number count i.e. no of opening Braces that
		// needs to be closed.
		int noOfRules = expression.numberOfOperands(); 
		for (int i = 0; i < noOfRules; i++)
		{
			IExpressionOperand operand = expression.getOperand(i);
			String operandSQL = getOperandSQL(operand);
			operandSQL = completeRule(noOfRules, operandSQL);
			if (i != noOfRules - 1)
			{
				Connector connector = (Connector) expression.getConnector(i, i + 1);
				int nestingNumber = connector.getNestingNumber();

				int nextIndex = i + 1;
				IExpressionOperand nextOperand = expression.getOperand(nextIndex);
				if (nextOperand instanceof IExpression && emptyExpressions.contains(nextOperand))
				{
					nextIndex = checkExpressionOperands(expression, noOfRules, nextIndex);
					if (nextIndex == noOfRules)
					// Expression over add closing parenthesis.
					{
						buffer.append(operandSQL);
						buffer.append(getParenthesis(currentNestingCounter, ")"));
						currentNestingCounter = 0;
					}
					else
					{
						Connector newConnector = (Connector) expression.getConnector(nextIndex - 1,
								nextIndex);
						int newNestingNumber = newConnector.getNestingNumber();
						currentNestingCounter = attachOperandSQL(buffer, currentNestingCounter,
								operandSQL, newNestingNumber);
						if (buffer.length() > 0)
						{
							buffer.append(' ').append(newConnector.getOperator());
						}
					}
					i = nextIndex - 1;
				}
				else
				{
					currentNestingCounter = attachOperandSQL(buffer, currentNestingCounter,
							operandSQL, nestingNumber);
					if (buffer.length() > 0)
					{
						buffer.append(' ').append(connector.getOperator());
					}
				}
			}
			else
			{
				buffer.append(operandSQL);
				// Finishing SQL by adding closing parenthesis if any.
				buffer.append(getParenthesis(currentNestingCounter, ")"));
				currentNestingCounter = 0;
			}
		}
		return buffer.toString();
	}

	/**
	 * putting RuleSQL in Braces so that it will not get mixed with other Rules.
	 *
	 * @param noOfRules
	 *            number of rules
	 * @param operandSQL
	 *            operand SQL
	 * @return operandSQL
	 */
	private String completeRule(int noOfRules, String operandSQL)
	{
		String tempSQL = operandSQL;
		if (!"".equals(operandSQL) && noOfRules != 1)
		{
			tempSQL = " (" + operandSQL + ") ";
			/*
			 * putting RuleSQL in Braces so that it will not get mixed with
			 * other Rules.
			 */
		}
		return tempSQL;
	}

	/**
	 * @param operand
	 *            operand
	 * @return operandSQL
	 * @throws SqlException
	 *             SqlException
	 */
	private String getOperandSQL(IExpressionOperand operand) throws SqlException
	{
		String operandSQL = "";
		boolean emptyExppression;
		if (operand instanceof IRule)
		{
			// Processing Rule.
			operandSQL = getSQL((IRule) operand);
		}
		else if (operand instanceof IExpression)
		{
			// Processing sub Expression.
			emptyExppression = emptyExpressions.contains(operand);
			if (!emptyExppression)
			{
				operandSQL = getWherePartSQL((IExpression) operand);
			}
		}
		else
		{
			ICustomFormula formula = (ICustomFormula) operand;
			if(isYearInterval(formula)){
			   operandSQL = getCFForYear(formula); //custom formula for time interval: Year
			}
			else{
			   operandSQL = getCustomFormulaString(formula);
			}
		}
		return operandSQL;
	}

	private String getCFForYear(ICustomFormula formula) {
		String lhsString = modifyForTimeIntervalYear(formula.getLhs());
		ITerm term = formula.getAllRhs().get(0);
		DateOffsetLiteral operand = (DateOffsetLiteral)term.getOperand(0);
		
		StringBuilder sql = new StringBuilder("");
		sql.append(lhsString).append(" ");  
		sql.append(RelationalOperator.getSQL(formula.getOperator())).append(" ");
		sql.append(operand.getOffset());	
		return sql.toString();
	}

	private boolean isYearInterval(ICustomFormula formula) {
		ITerm term = formula.getAllRhs().get(0);
		if(term.getOperand(0) instanceof DateOffsetLiteral){
			DateOffsetLiteral operand = (DateOffsetLiteral)term.getOperand(0);
			return (operand.getTimeInterval() == TimeInterval.Year);		
		}
		return false;
	}

	/**
	 * @param expression
	 *            expression
	 * @param noOfRules
	 *            number of rules
	 * @param nextIndex
	 *            next index
	 * @return nextIndex
	 */
	private int checkExpressionOperands(IExpression expression, int noOfRules, int nextIndex)
	{
		int index = nextIndex;
		IExpressionOperand nextOperand;
		for (; index < noOfRules; index++)
		{
			nextOperand = expression.getOperand(index);
			if (!(nextOperand instanceof IExpression && emptyExpressions.contains(nextOperand)))
			{
				break;
			}
		}
		return index;
	}

	/**
	 * To append the operand SQL to the SQL buffer, with required number of
	 * parenthesis.
	 *
	 * @param buffer
	 *            The reference to the String buffer containing SQL for SQL of
	 *            operands of an expression.
	 * @param nestingCounter
	 *            The current nesting count.
	 * @param operandSQL
	 *            The SQL of the operand to be appended to buffer
	 * @param nestingNumber
	 *            The nesting number for the current operand's operator.
	 * @return The updated current nesting count.
	 */
	private int attachOperandSQL(StringBuffer buffer, int nestingCounter, String operandSQL,
			int nestingNumber)
	{
		int currentNestingCounter = nestingCounter;
		if (currentNestingCounter < nestingNumber)
		{
			buffer.append(getParenthesis(nestingNumber - currentNestingCounter, " ("));
			currentNestingCounter = nestingNumber;
			buffer.append(operandSQL);
		}
		else if (currentNestingCounter > nestingNumber)
		{
			buffer.append(operandSQL);
			buffer.append(getParenthesis(currentNestingCounter - nestingNumber, ")"));
			currentNestingCounter = nestingNumber;
		}
		else
		{
			buffer.append(operandSQL);
		}
		return currentNestingCounter;
	}

	/**
	 * To get n number of parenthesis.
	 *
	 * @param number
	 *            The positive integer value
	 * @param parenthesis
	 *            either Opening parenthesis or closing parenthesis.
	 * @return The n number of parenthesis.
	 */
	private String getParenthesis(int number, String parenthesis)
	{
		StringBuffer string = new StringBuffer();
		for (int i = 0; i < number; i++)
		{
			string.append(parenthesis);
		}
		return string.toString();
	}

	/**
	 * To get the SQL representation of the Rule.
	 *
	 * @param rule
	 *            The reference to Rule.
	 * @return The SQL representation of the Rule.
	 * @throws SqlException
	 *             When there is error in the passed IQuery object.
	 */
	private String getSQL(IRule rule) throws SqlException
	{
		StringBuffer buffer = new StringBuffer("");

		int noOfConditions = rule.size();
		checkForEmptyConditions(noOfConditions);
		for (int i = 0; i < noOfConditions; i++)
		// Processing all conditions in Rule and combine them with AND operator.
		{
			String condition = getSQL(rule.getCondition(i), rule.getContainingExpression());

			if (i == noOfConditions - 1) // Intermediate Condition.
			{
				// Last Condition, this will not followed by And logical
				// operator.
				buffer.append(condition);
			}
			else
			{
				buffer.append(condition + " " + LogicalOperator.And + " ");
			}
		}
		return buffer.toString();
	}

	/**
	 * @param noOfConditions
	 *            number of conditions.
	 * @throws SqlException
	 *             SqlException
	 */
	private void checkForEmptyConditions(int noOfConditions) throws SqlException
	{
		if (noOfConditions == 0)
		{
			throw new SqlException("No conditions defined in the Rule!!!");
		}
	}

	/**
	 * To get the SQL Representation of the Condition.
	 *
	 * @param condition
	 *            The reference to condition.
	 * @param expression
	 *            The reference to Expression to which this condition belongs.
	 * @return The SQL Representation of the Condition.
	 * @throws SqlException
	 *             When there is error in the passed IQuery object.
	 */
	public String getSQL(ICondition condition, IExpression expression) throws SqlException
	{
		String sql;
		AttributeInterface attribute = condition.getAttribute();
		setDatePatterns();
		String attributeName = fromBuilder.aliasOf(attribute, expression);

		RelationalOperator operator = condition.getRelationalOperator();

		if (operator.equals(RelationalOperator.Between))
		{
			// Processing Between Operator, it will be treated as (OP>=val1 and
			// OP<=val2)
			sql = processBetweenOperator(condition, attributeName);
		}
		else if (operator.equals(RelationalOperator.In)
				|| operator.equals(RelationalOperator.NotIn))
		{
			sql = processInOperator(condition, attributeName);
		}
		else if (operator.equals(RelationalOperator.IsNotNull)
				|| operator.equals(RelationalOperator.IsNull))
		{
			sql = processNullCheckOperators(condition, attributeName);
		}
		else if (operator.equals(RelationalOperator.Contains)
				|| operator.equals(RelationalOperator.StartsWith)
				|| operator.equals(RelationalOperator.EndsWith))
		{
			sql = processLikeOperators(condition, attributeName);
		}
		else
		{
			// Processing rest operators like =, !=, <, > , <=, >= etc.
			sql = processComparisonOperator(condition, attributeName);
		}

		return sql;
	}

	/**
	 * Set the date patterns as per the database.
	 */
	private void setDatePatterns()
	{
		if (fromBuilder == null)
		{
			fromBuilder = new FromBuilder(joinGraph);
			if (getDatabaseSQLSettings().getDatabaseType().equals(DatabaseType.Oracle))
			{
				strTodateFunction = "TO_DATE";
				datePattern = "mm-dd-yyyy";
			}
			else if (getDatabaseSQLSettings().getDatabaseType().equals(DatabaseType.MySQL))
			{
				strTodateFunction = "STR_TO_DATE";
				datePattern = "%m-%d-%Y";
			}
		}
	}

	/**
	 * Processing operators like =, !=, <, > , <=, >= etc.
	 *
	 * @param condition
	 *            the condition.
	 * @param tempAttributeName
	 *            the Name of the attribute to be returned in SQL.
	 * @return SQL representation for given condition.
	 * @throws SqlException
	 *             when: 1. value list contains more/less than 1 value. 2. other
	 *             than = ,!= operator present for String data type.
	 */
	private String processComparisonOperator(ICondition condition, String tempAttributeName)
			throws SqlException
	{
		String attributeName = tempAttributeName;
		AttributeTypeInformationInterface dataType = condition.getAttribute()
				.getAttributeTypeInformation();
		RelationalOperator operator = condition.getRelationalOperator();
		List<String> values = condition.getValues();
		if (values.size() != 1)
		{
			throw new SqlException("Incorrect number of values found for Operator '" + operator
					+ "' for condition:" + condition);
		}
		String value = values.get(0);
		checkComparisonOperatorForString(condition, dataType, operator);

		checkComparisonOperatorForBoolean(condition, dataType, operator);
		String conditionStr;
		attributeName = modifyStringForDateForComparisonOperator(attributeName, dataType);
		value = modifyValueforDataType(value, dataType, condition);
		conditionStr = attributeName + RelationalOperator.getSQL(operator) + value;

		if (getDatabaseSQLSettings().getDatabaseType().equals(DatabaseType.Oracle)
				&& dataType instanceof StringTypeInformationInterface)
		{
			conditionStr = LOWER + attributeName + ") " + RelationalOperator.getSQL(operator)
					+ " lower(" + value + ")";
		}
		return conditionStr;
	}

	/**
	 * @param attributeName
	 *            attribute Name
	 * @param dataType
	 *            data Type
	 * @return attributeName
	 */
	private String modifyStringForDateForComparisonOperator(String attributeName,
			AttributeTypeInformationInterface dataType)
	{
		String tempAttribute = attributeName;
		if (dataType instanceof DateTypeInformationInterface)
		{
			if (getDatabaseSQLSettings().getDatabaseType().equals(DatabaseType.MySQL))
			{
				tempAttribute = strTodateFunction + "(DATE_FORMAT(DATE(" + attributeName + "),'"
						+ datePattern + "'),'" + datePattern + "')";
			}
			else if (getDatabaseSQLSettings().getDatabaseType().equals(DatabaseType.Oracle))
			{
				tempAttribute = strTodateFunction + "(TO_CHAR(TRUNC(" + attributeName + "),'"
						+ datePattern + "'),'" + datePattern + "')";
			}
		}
		return tempAttribute;
	}

	/**
	 * @param condition
	 *            condition
	 * @param dataType
	 *            data Type
	 * @param operator
	 *            operator
	 * @throws SqlException
	 *             SqlException
	 */
	private void checkComparisonOperatorForBoolean(ICondition condition,
			AttributeTypeInformationInterface dataType, RelationalOperator operator)
			throws SqlException
	{
		if (dataType instanceof BooleanAttributeTypeInformation
				&& !(operator.equals(RelationalOperator.Equals) || operator
						.equals(RelationalOperator.NotEquals)))
		{
			throw new SqlException("Incorrect operator found"
					+ " for Boolean datatype for condition:" + condition);
		}
	}

	/**
	 * @param condition
	 *            condition
	 * @param dataType
	 *            data Type
	 * @param operator
	 *            operator
	 * @throws SqlException
	 *             SqlException
	 */
	private void checkComparisonOperatorForString(ICondition condition,
			AttributeTypeInformationInterface dataType, RelationalOperator operator)
			throws SqlException
	{
		if (dataType instanceof StringTypeInformationInterface
				&& !(operator.equals(RelationalOperator.Equals) || operator
						.equals(RelationalOperator.NotEquals)))
		{
			throw new SqlException("Incorrect operator found "
					+ "for String datatype for condition:" + condition);
		}
	}

	/**
	 * To process String operators. for e.g. starts with, contains etc.
	 *
	 * @param condition
	 *            the condition.
	 * @param attributeName
	 *            the Name of the attribute to returned in SQL.
	 * @return SQL representation for given condition.
	 * @throws SqlException
	 *             when 1. The data type of attribute is not String. 2. The
	 *             value list empty or more than 1 value.
	 */
	private String processLikeOperators(ICondition condition, String attributeName)
			throws SqlException
	{
		RelationalOperator operator = condition.getRelationalOperator();

		checkInvalidDataTypeForLike(condition, operator);

		List<String> values = condition.getValues();
		if (values.size() != 1)
		{
			throw new SqlException("Incorrect number of values found for Operator '" + operator
					+ "' for condition:" + condition);
		}
		String value = values.get(0);

		value = replaceSpecialChars(value, operator);
		String appName = CommonServiceLocator.getInstance().getAppName();
		DAOFactory daoFactory = (DAOFactory) DAOConfigFactory.getInstance().getDAOFactory(appName);
		String str;
		if ("Oracle".equalsIgnoreCase(daoFactory.getDataBaseType()))
		{
			str = LOWER + attributeName + ") like lower(" + value + ")";
		}
		else
		{
			str = attributeName + " like " + value;
		}
		return str;
	}

	/**
	 * @param condition
	 *            condition
	 * @param operator
	 *            operator
	 * @throws SqlException
	 *             SqlException
	 */
	private void checkInvalidDataTypeForLike(ICondition condition, RelationalOperator operator)
			throws SqlException
	{
		if (!(condition.getAttribute().getAttributeTypeInformation() instanceof StringTypeInformationInterface || condition
				.getAttribute().getAttributeTypeInformation() instanceof FileTypeInformationInterface))
		{
			throw new SqlException("Incorrect data type found for" + " Operator '" + operator
					+ "' for condition:" + condition);
		}
	}

	/**
	 * To process 'Is Null' & 'Is Not Null' operator.
	 *
	 * @param condition
	 *            the condition.
	 * @param attributeName
	 *            the Name of the attribute to returned in SQL.
	 * @return SQL representation for given condition.
	 * @throws SqlException
	 *             when the value list is not empty.
	 */
	private String processNullCheckOperators(ICondition condition, String attributeName)
			throws SqlException
	{
		String operatorStr = RelationalOperator.getSQL(condition.getRelationalOperator());
		if (condition.getValues().size() > 0)
		{
			throw new SqlException("No value expected in value part for '" + operatorStr
					+ "' operator !!!");
		}
		return attributeName + " " + operatorStr;
	}

	/**
	 * To process 'In' & 'Not In' operator.
	 *
	 * @param condition
	 *            the condition.
	 * @param attributeName
	 *            the Name of the attribute to returned in SQL.
	 * @return SQL representation for given condition.
	 * @throws SqlException
	 *             when the value list is empty or problem in parsing any of the
	 *             value.
	 */
	private String processInOperator(ICondition condition, String attributeName)
			throws SqlException
	{
		StringBuffer buffer = new StringBuffer("");
		buffer.append(attributeName + " "
				+ RelationalOperator.getSQL(condition.getRelationalOperator()) + " (");
		List<String> valueList = condition.getValues();
		AttributeTypeInformationInterface dataType = condition.getAttribute()
				.getAttributeTypeInformation();
		StringBuffer valueStr = new StringBuffer();

		checkForInvalidConditions(condition, valueList, dataType);
		getQueryStringForInOperator(buffer, valueList, dataType, valueStr);
		if (getDatabaseSQLSettings().getDatabaseType().equals(DatabaseType.Oracle)
				&& dataType instanceof StringTypeInformationInterface)
		{
			buffer = new StringBuffer(LOWER + attributeName + ") "
					+ RelationalOperator.getSQL(condition.getRelationalOperator()) + " ("
					+ valueStr);
		}
		return buffer.toString();
	}

	/**
	 * @param buffer
	 *            buffer
	 * @param valueList
	 *            value List
	 * @param dataType
	 *            data Type
	 * @param valueStr
	 *            String
	 * @throws SqlException
	 *             SqlException
	 */
	private void getQueryStringForInOperator(StringBuffer buffer, List<String> valueList,
			AttributeTypeInformationInterface dataType, StringBuffer valueStr) throws SqlException
	{
		for (int i = 0; i < valueList.size(); i++)
		{
			String value = modifyValueforDataType(valueList.get(i), dataType, null);
			if (i == valueList.size() - 1)
			{
				valueStr.append(LOWER).append(value).append("))");
				buffer.append(value).append(')');
			}
			else
			{
				valueStr.append(LOWER).append(value).append("),");
				buffer.append(value).append(',');
			}
		}
	}

	/**
	 * Check if the list of values is empty or the data type is boolean which is
	 * invalid.
	 *
	 * @param condition
	 *            condition
	 * @param valueList
	 *            String
	 * @param dataType
	 *            data type
	 * @throws SqlException
	 *             SqlException
	 */
	private void checkForInvalidConditions(ICondition condition, List<String> valueList,
			AttributeTypeInformationInterface dataType) throws SqlException
	{
		if (valueList.isEmpty() || valueList.get(0) == null)
		{
			throw new SqlException(
					"at least one value required for 'In' operand list for condition:" + condition);
		}
		if (dataType instanceof BooleanAttributeTypeInformation)
		{
			throw new SqlException("Incorrect operator found for Boolean datatype for condition:"
					+ condition);
		}
	}

	/**
	 * To get the SQL for the given condition with Between operator. It will be
	 * treated as (operator>=val1 and operator<=val2)
	 *
	 * @param condition
	 *            The condition.
	 * @param attributeName
	 *            the Name of the attribute to returned in SQL.
	 * @return SQL representation for given condition.
	 * @throws SqlException
	 *             when: 1. value list does not have 2 values 2. Data type is
	 *             not date 3. problem in parsing date.
	 */
	private String processBetweenOperator(ICondition condition, String attributeName)
			throws SqlException
	{
		List<String> values = condition.getValues();
		if (values.size() != AQConstants.TWO)
		{
			throw new SqlException("Incorrect number of operand for Between operator"
					+ " in condition:" + condition);
		}

		AttributeTypeInformationInterface dataType = checkForValidDataType(condition);

		String firstValue = modifyValueforDataType(values.get(0), dataType, condition);
		String secondValue = modifyValueforDataType(values.get(1), dataType, condition);
		StringBuffer buffer = new StringBuffer();
		buffer.append('(').append(attributeName);
		buffer.append(RelationalOperator.getSQL(RelationalOperator.GreaterThanOrEquals)).append(
				firstValue);
		buffer.append(' ').append(LogicalOperator.And).append(' ').append(attributeName).append(
				RelationalOperator.getSQL(RelationalOperator.LessThanOrEquals)).append(secondValue)
				.append(')');

		return buffer.toString();
	}

	/**
	 * Check for valid data type for between operator.
	 *
	 * @param condition
	 *            condition
	 * @return dataType
	 * @throws SqlException
	 *             SqlException
	 */
	private AttributeTypeInformationInterface checkForValidDataType(ICondition condition)
			throws SqlException
	{
		AttributeTypeInformationInterface dataType = condition.getAttribute()
				.getAttributeTypeInformation();
		if (!(dataType instanceof DateTypeInformationInterface
				|| dataType instanceof IntegerTypeInformationInterface
				|| dataType instanceof LongTypeInformationInterface
				|| dataType instanceof DoubleTypeInformationInterface || dataType instanceof FloatTypeInformationInterface))
		{
			throw new SqlException("Incorrect Data type of operand for"
					+ " Between operator in condition:" + condition);
		}
		return dataType;
	}

	/**
	 * To Modify value as per the Data type. 1. In case of String data type,
	 * replace occurrence of single quote by single quote twice. 2. Enclose the
	 * Given values by single Quotes for String Data type.
	 *
	 * @param value
	 *            the Modified value.
	 * @param dataType
	 *            The DataType of the passed value.
	 * @return The String representing encoded value for the given value & data
	 *         type.
	 * @throws SqlException
	 *             when there is problem with the values, for e.g. unable to
	 *             parse date/integer/double etc.
	 */
	private String modifyValueforDataType(String value, AttributeTypeInformationInterface dataType,
			ICondition condition) throws SqlException
	{
		String tempValue = value;
        Collection<PermissibleValueInterface> pvColl = null;
        if(dataType.getDataElement()!=null)
        {
            pvColl = ((UserDefinedDE)dataType.getDataElement()).getPermissibleValueCollection();
        }
        if(pvColl!=null&&!pvColl.isEmpty())
        {
            //encode value as in case of special character DE saves encoded value in DB.
            tempValue = DynamicExtensionsUtility.getEscapedStringValue(value);
        }
		if (dataType instanceof StringTypeInformationInterface)// for data type
		// String it will be enclosed in single quote.
		{
			data.add(tempValue);
			tempValue = tempValue.replaceAll("'", "''");
			tempValue = "?";
		}
		else if (dataType instanceof DateTypeInformationInterface) // for
		// data type date it will be enclosed in single quote.
		{
			tempValue = modifyValueForDate(tempValue, condition);
		}
		else if (dataType instanceof BooleanTypeInformationInterface) // defining
		// value for boolean data type.
		{
			tempValue = modifyValueForBoolean(tempValue, condition);
		}
		else if (dataType instanceof IntegerTypeInformationInterface)
		{
			if (!isNumeric(tempValue))
			{
				throw new SqlException("Non numeric value found in value part!!!");
			}
		}
		else if (dataType instanceof DoubleTypeInformationInterface
				&& !isDouble(tempValue))
		{
			throw new SqlException("Non numeric value found in value part!!!");
		}
		else
		{
			data.add(tempValue);
			tempValue = "?";
		}
		return tempValue;
	}

	private boolean isNumeric(String numString)
	{
		boolean isNumeric = true;
		try
		{
			Long.parseLong(numString);
		}
		catch (NumberFormatException exp)
		{ 
			isNumeric = false;
		}
		return isNumeric;
	}
	
	private boolean isDouble(String dblString)
	{
		boolean isDouble = true;
		try
		{
			Double.parseDouble(dblString);
		}
		catch (NumberFormatException exp)
		{
			isDouble = false;
		}
		return isDouble;
	}
	/**
	 * To Modify value for Boolean data type. For Boolean dataType it will
	 * change value to 1 if its TRUE, else 0.
	 *
	 * @param value
	 *            the Modified value.
	 * @return The String representing encoded value for the given value & data
	 *         type.
	 * @throws SqlException
	 *             when there is problem with the values, for e.g. unable to
	 *             parse date/integer/double etc.
	 */
	private String modifyValueForBoolean(String value, ICondition condition) throws SqlException
	{
		if (value == null
				|| !(value.equalsIgnoreCase(edu.wustl.query.util.global.AQConstants.TRUE) || value
						.equalsIgnoreCase(edu.wustl.query.util.global.AQConstants.FALSE)))
		{
			String entityName = Utility.parseClassName(condition.getAttribute().getEntity()
					.getName());
			throw new SqlException("Please add a condition to attribute '"
					+ Utility.getDisplayLabel(condition.getAttribute().getName()) + "' of "
					+ entityName + ".");
		}
		String tempValue = setBooleanValue(value);
		data.add(tempValue);
		tempValue = "?";
		return tempValue;
	}

	/**
	 * @param value
	 *            value
	 * @return booleanValue
	 */
	private String setBooleanValue(String value)
	{
		String booleanValue;
		if (value.equalsIgnoreCase(edu.wustl.query.util.global.AQConstants.TRUE))
		{
			booleanValue = "1";
		}
		else
		{
			booleanValue = "0";
		}
		return booleanValue;
	}

	/**
	 * To Modify value for Date data type. Enclose the given values by single
	 * Quotes for Date Data type.
	 *
	 * @param tempValue
	 *            the Modified value.
	 * @return The String representing encoded value for the given value & data
	 *         type.
	 * @throws SqlException
	 *             when there is problem with the values, for e.g. unable to
	 *             parse date/integer/double etc.
	 */
	private String modifyValueForDate(String tempValue, ICondition condition) throws SqlException
	{
		StringBuffer value;
		try
		{
			Date date = Utility.parseDate(tempValue);
			if (date == null)
			{
				String entityName = Utility.parseClassName(condition.getAttribute().getEntity()
						.getName());
				throw new SqlException("Please add a condition to attribute '"
						+ Utility.getDisplayLabel(condition.getAttribute().getName()) + "' of "
						+ entityName + ".");
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			value = new StringBuffer();
			value.append((calendar.get(Calendar.MONTH) + 1)).append('-').append(
					calendar.get(Calendar.DAY_OF_MONTH)).append('-').append(
					calendar.get(Calendar.YEAR));
			setDatePattern();
			String appName = CommonServiceLocator.getInstance().getAppName();
			data.add(value.toString());
			if ("MSSQLSERVER".equals(DAOConfigFactory.getInstance().getDAOFactory(appName)
					.getDataBaseType()))
			{
				value = new StringBuffer("CONVERT(datetime, ?, 110)");
			}
			else
			{
				String strToDateFunction = setStrToDateFunction();
				value = new StringBuffer(strToDateFunction + "(?,'" + datePattern + "')");
			}
		}
		catch (ParseException parseExp)
		{
			Logger.out.error(parseExp.getMessage(), parseExp);
			throw new SqlException(parseExp.getMessage(), parseExp);
		}
		return value.toString();
	}

	/**
	 * Set date pattern.
	 */
	private void setDatePattern()
	{
		if (datePattern == null || datePattern.trim().equals(""))
		{
			datePattern = "%m-%d-%Y"; // using MySQL function if the value is
										// not defined.
		}
	}

	/**
	 * @return strToDateFunction
	 */
	private String setStrToDateFunction()
	{
		String strToDateFunction = strTodateFunction;
		if (strToDateFunction == null || strToDateFunction.trim().equals(""))
		{
			strToDateFunction = "STR_TO_DATE"; // using MySQL function if the
												// Value is not defined.
		}
		return strToDateFunction;
	}

	/**
	 * This method will be used by Query Mock to set the join Graph externally.
	 *
	 * @param joinGraph
	 *            the reference to joinGraph.
	 */
	public void setJoinGraph(JoinGraph joinGraph)
	{
		this.joinGraph = joinGraph;
	}

	/**
	 * To check if the Expression is empty or not. It will add such empty
	 * expressions in the emptyExpressions set. An expression is said to be
	 * empty when: - it contains no rule as operand. - and all of its
	 * children(i.e subExpressions & their subExpressions & so on) contains no
	 * rule
	 *
	 * @param expressionId
	 *            the reference to the expression id.
	 * @return true if the expression is empty.
	 */
	private boolean isEmptyExpression(int expressionId)
	{
		Expression expression = (Expression) constraints.getExpression(expressionId);
		List<IExpression> operandList = joinGraph.getChildrenList(expression);

		boolean isEmpty = checkIsEmpty(operandList);
		isEmpty = isEmpty && !expression.containsRule();// check if there are
		// rule present as subexpression. SRINATH
		isEmpty = isEmpty && !expression.containsCustomFormula();
		if (isEmpty)
		{
			emptyExpressions.add(expression); // Expression is empty.
		}
		return isEmpty;
	}

	/**
	 * @param operandList
	 *            operand list
	 * @return isEmpty
	 */
	private boolean checkIsEmpty(List<IExpression> operandList)
	{
		boolean isEmpty = true;
		if (!operandList.isEmpty()) // Check whether any of its children
									// contains rule.
		{
			for (IExpression subExpression : operandList)
			{
				if (!isEmptyExpression(subExpression.getExpressionId()))
				{
					isEmpty = false;
				}
			}
		}
		return isEmpty;
	}

	/**
	 * To create output tree for the given expression graph.
	 *
	 * @throws MultipleRootsException
	 *             When there exist multiple roots in join graph.
	 */
	private void createTree() throws MultipleRootsException
	{
		IExpression rootExpression = joinGraph.getRoot();
		rootOpTreeNodLst = new ArrayList<OutputTreeDataNode>();
		// inViewRootOpTreeNodeList = new ArrayList<OutputTreeDataNode>();
		OutputTreeDataNode rootOutputTreeNode = null;
		treeNo = 0;
		if (rootExpression.isVisible())
		{
			// TODO PATHETIC CODE; fix later if this works...
			IOutputEntity rootOutputEntity = getOutputEntity(rootExpression);
			rootOutputTreeNode = new OutputTreeDataNode(rootOutputEntity, rootExpression
					.getExpressionId(), -1, rootExpression.isInView());
			rootOpTreeNodLst.add(rootOutputTreeNode);
		}
		completeTree(rootExpression, rootOutputTreeNode);

		if (rootExpression.isInView())
		{
			rootOpTreeNodLst.get(0).setTreeNo(0);
			assignTreeNumberToChildren(rootOpTreeNodLst.get(0), 0);

		}
		else
		{
			assignTreeNumber(rootOpTreeNodLst.get(0));
		}
	}

	private void assignTreeNumber(OutputTreeDataNode rootNode)
	{
		List<OutputTreeDataNode> childList = QueryModuleUtil.getInViewChildren(rootNode);
		int treeNo = 0;
		for (OutputTreeDataNode node : childList)
		{
			node.setTreeNo(treeNo);
			assignTreeNumberToChildren(node, treeNo);
			treeNo++;
		}

	}

	private void assignTreeNumberToChildren(OutputTreeDataNode rootNode, int treeNo)
	{
		for (OutputTreeDataNode node : rootNode.getChildren())
		{
			// TODO CHECK THIS
			if (node.isInView())
			{
				node.setTreeNo(treeNo);
			}
			assignTreeNumberToChildren(node, treeNo);
		}
	}

	/**
	 * To create the output tree from the constraints.
	 *
	 * @param expression
	 *            The reference to Expression
	 * @param parentOutputTreeNode
	 *            The reference to parent output tree node. null if there is no
	 *            parent.
	 */
	private void completeTree(IExpression expression, OutputTreeDataNode parentOutputTreeNode)
	{
		List<IExpression> children = joinGraph.getChildrenList(expression);
		for (IExpression childExp : children)
		{
			OutputTreeDataNode childNode = parentOutputTreeNode;
			/**
			 * Check whether child node is in view or not. if it is in view then
			 * create output tree node for it. else look for their children node
			 * & create the output tree hierarchy if required.
			 */
			if (childExp.isVisible())
			{
				childNode = getChildNode(parentOutputTreeNode, childExp);
			}
			completeTree(childExp, childNode);
		}
	}

	/**
	 * @param parentOutputTreeNode
	 *            parent output tree node
	 * @param childExp
	 *            child expression
	 * @return childNode
	 */
	private OutputTreeDataNode getChildNode(OutputTreeDataNode parentOutputTreeNode,
			IExpression childExp)
	{
		OutputTreeDataNode childNode;
		IOutputEntity childOutputEntity = getOutputEntity(childExp);

		if (parentOutputTreeNode == null)
		{
			// New root node for output tree found, so create root
			// node & add it in the rootOutputTreeNodeList.
			childNode = new OutputTreeDataNode(childOutputEntity, childExp.getExpressionId(),
					treeNo++, childExp.isInView());
			rootOpTreeNodLst.add(childNode);
		}
		else
		{
			childNode = parentOutputTreeNode.addChild(childOutputEntity,
					childExp.getExpressionId(), childExp.isInView());
		}
		return childNode;
	}

	/**
	 * To get the Output Entity for the given Expression.
	 *
	 * @param expression
	 *            The reference to the Expression.
	 * @return The output entity for the Expression.
	 */
	private IOutputEntity getOutputEntity(IExpression expression)
	{
		EntityInterface entity = expression.getQueryEntity().getDynamicExtensionsEntity();
		IOutputEntity outputEntity = QueryObjectFactory.createOutputEntity(entity);
		outputEntity.getSelectedAttributes().addAll(entity.getEntityAttributesForQuery());
		return outputEntity;
	}

	/**
	 * @return the rootOutputTreeNodeList
	 */
	public List<OutputTreeDataNode> getRootOutputTreeNodeList()
	{
		return rootOpTreeNodLst;
	}

	// //////// CUSTOM FORMULA

	/**
	 * Returns the CustomFormulaProcessor.
	 *
	 * @return CustomFormulaProcessor object
	 */
	private CustomFormulaProcessor getCustomFormulaProcessor()
	{
		return new CustomFormulaProcessor(getAliasProvider(), getDatabaseSQLSettings());
	}

	/**
	 * Get the database settings.
	 *
	 * @return Object of DatabaseSQLSettings
	 */
	private DatabaseSQLSettings getDatabaseSQLSettings()
	{
		String appName = CommonServiceLocator.getInstance().getAppName();
		String databaseName = DAOConfigFactory.getInstance().getDAOFactory(appName)
				.getDataBaseType();
		DatabaseType databaseType;
		if (databaseName.equalsIgnoreCase(AQConstants.MYSQL_DATABASE))
		{
			databaseType = DatabaseType.MySQL;
		}
		else if (databaseName.equals(AQConstants.ORACLE_DATABASE))
		{
			databaseType = DatabaseType.Oracle;
		}
		else if ("MSSQLSERVER".equals(DAOConfigFactory.getInstance().getDAOFactory(appName)
				.getDataBaseType()))
		{
			databaseType = DatabaseType.MsSqlServer;
		}
		else
		{
			throw new UnsupportedOperationException("Custom formulas on " + databaseName
					+ " are not supported.");
		}
		return new DatabaseSQLSettings(databaseType);
	}

	/**
	 * Returns the alias provider.
	 *
	 * @return alias provider
	 */
	private IAttributeAliasProvider getAliasProvider()
	{
		return new IAttributeAliasProvider()
		{

			public String getAliasFor(IExpressionAttribute exprAttr)
			{
				return fromBuilder.aliasOf(exprAttr.getAttribute(), exprAttr.getExpression());
			}
		};
	}

	/**
	 * Get custom formula in the form of string.
	 *
	 * @param formula
	 *            The custom formula
	 * @return string representation of formula
	 */
	protected String getCustomFormulaString(ICustomFormula formula)
	{
		return getCustomFormulaProcessor().asString(formula);
	}

	// output terms

	/**
	 * Get the term processor.
	 *
	 * @return the Object of TermProcessor
	 */
	private TermProcessor getTermProcessor()
	{
		return new TermProcessor(getAliasProvider(), getDatabaseSQLSettings());
	}

	/**
	 * Returns the term string.
	 *
	 * @param term
	 *            term
	 * @return The term string
	 */
	private String getTermString(ITerm term)
	{
		return getTermProcessor().convertTerm(term).getString();
	}
	
	/**
	 * @param terms
	 *            List of output terms.
	 * @return select clause for output terms
	 */
	private String getSelectForOutputTerms(List<IOutputTerm> terms)
	{
		outputTermsColumns = new HashMap<String, IOutputTerm>();
		StringBuffer selectClause = new StringBuffer();
		for (IOutputTerm term : terms)
		{
			String termString = null;
			if(term.getTimeInterval() == TimeInterval.Year){
				termString = modifyForTimeIntervalYear(term.getTerm());
			} else {
				termString = " (" + getTermString(term.getTerm()) + ")";
				termString = modifyForTimeInterval(termString, term.getTimeInterval());
			}
			String columnName = COLUMN_NAME + selectIndex++;
			selectClause.append(termString).append(' ').append(columnName).append(SELECT_COMMA);
			outputTermsColumns.put(columnName, term);
		}
		return removeLastComma(selectClause.toString());
	}
	
	private String modifyForTimeIntervalYear(ITerm term)
	{
		IConnector<ArithmeticOperator> operator = term.getConnector(0, 1);
		String operatorStr = "";
		if(operator.getOperator().name().equalsIgnoreCase("MINUS")){
			operatorStr = " - ";
		} else {
			operatorStr = " + ";
		}
		StringBuilder termString = new StringBuilder(" (");
			
		for(int i = 0; i < term.numberOfOperands(); i++) {
			IArithmeticOperand opr = term.getOperand(i);
			if(opr instanceof IDateLiteral){
				Calendar date = Calendar.getInstance();
				date.setTime( ((DateLiteral)opr).getDate());
				int year = date.get(Calendar.YEAR); 				
				termString.append(year);		
			} else if (opr instanceof IExpressionAttribute){
				IExpressionAttribute exprAttr = (IExpressionAttribute) opr;
				String alias = fromBuilder.aliasOf(exprAttr.getAttribute(), exprAttr.getExpression());
				termString.append("Extract(Year from ").append(alias).append(") ");
			}
			
			if(i == 0){
				termString.append(operatorStr);				
			}
		}
		
		return termString.append(")").toString();	
	}
	/**
	 * @param tempString
	 *            temporary term String
	 * @param timeInterval
	 *            timeInterval
	 * @return termString
	 */
	private String modifyForTimeInterval(String tempString, TimeInterval<?> timeInterval)
	{
		StringBuffer termString = new StringBuffer(tempString);
		if (timeInterval != null)
		{
			termString.append('/').append(timeInterval.numSeconds());
			String term = termString.toString();
			termString = new StringBuffer();
			String appName = CommonServiceLocator.getInstance().getAppName();
			if ("MSSQLSERVER".equals(DAOConfigFactory.getInstance().getDAOFactory(appName)
					.getDataBaseType()))
			{
				termString.append("cast(ROUND(").append(term).append(",0)as bigint)");
			}
			else
			{
				termString.append("ROUND(").append(term).append(')');
			}
		}
		return termString.toString();
	}

	/**
	 * outputTermsColumns Used to store the temporal query details.
	 */
	private Map<String, IOutputTerm> outputTermsColumns;

	/**
	 * Returns the output term columns.
	 *
	 * @return the outputTermsColumns
	 */
	public Map<String, IOutputTerm> getOutputTermsColumns()
	{
		return outputTermsColumns;
	}

	/**
	 * Returns the AttributeColumnNameMap.
	 *
	 * @return the attributeColumnNameMap
	 */
	public Map<AttributeInterface, String> getAttributeColumnNameMap()
	{
		return attrColNameMap;
	}

	/**
	 * This method replaces special characters with the escape sequences from
	 * the entered value.
	 *
	 * @param tempValue
	 *            entered by user.
	 * @param operator
	 *            Relational operator
	 * @return value special characters replaced by escape sequences.
	 */
	private String replaceSpecialChars(String tempValue, RelationalOperator operator)
	{
		String value = tempValue;
		String appName = CommonServiceLocator.getInstance().getAppName();
		DAOFactory daoFactory = (DAOFactory) DAOConfigFactory.getInstance().getDAOFactory(appName);
		if (AQConstants.ORACLE_DATABASE.equals(daoFactory.getDataBaseType()))
		{
			value = replaceSpecialCharactersForOracle(value, operator);
		}
		else if (AQConstants.MYSQL_DATABASE.equals(daoFactory.getDataBaseType()))
		{
			value = replaceSpecialCharactersForMySql(value, operator);
		}
		return value;
	}

	/**
	 * @param value
	 *            value
	 * @param operator
	 *            operator
	 * @return value
	 */
	private String replaceSpecialCharactersForMySql(String value, RelationalOperator operator)
	{
		String tempValue = value;
		tempValue = getOperatorSpecificValue(tempValue, operator);
		return tempValue;
	}

	/**
	 * @param value
	 *            value
	 * @param operator
	 *            operator
	 * @return value
	 */
	private String replaceSpecialCharactersForOracle(String value, RelationalOperator operator)
	{
		String tempValue = value;
		String finalValue = getOperatorSpecificValue(tempValue, operator);
		StringBuffer finalVal = new StringBuffer(finalValue);
		tempValue = finalVal.toString();
		return tempValue;
	}

	/**
	 * This method changes the value as per RelationalOperator's value.
	 *
	 * @param value
	 *            entered by user.
	 * @param operator
	 *            RelationalOperator.
	 * @return value as per RelationalOperators
	 */
	private String getOperatorSpecificValue(String value, RelationalOperator operator)
	{
		StringBuffer tempValue = new StringBuffer();
		if (operator.equals(RelationalOperator.Contains))
		{
			tempValue.append('%').append(value).append('%');
		}
		else if (operator.equals(RelationalOperator.StartsWith))
		{
			tempValue.append(value).append('%');
		}
		else if (operator.equals(RelationalOperator.EndsWith))
		{
			tempValue.append('%').append(value).append("");
		}
		data.add(tempValue.toString());
		tempValue = new StringBuffer("?");
		return tempValue.toString();
	}

	/**
	 * Populates the linked list of ColumnValueBean.
	 *
	 * @param data
	 *            data
	 * @return columnValueBean
	 */
	private static LinkedList<ColumnValueBean> populateColumnValueBean(LinkedList<Object> data)
	{
		LinkedList<ColumnValueBean> columnValueBean = new LinkedList<ColumnValueBean>();
		ColumnValueBean bean;
		for (Object object : data)
		{
			if (object != null)
			{
				bean = new ColumnValueBean(object.toString(), object);
				columnValueBean.add(bean);
			}
		}
		return columnValueBean;
	}

	/**
	 * Get the column value bean.
	 *
	 * @return columnValueBean
	 */
	public LinkedList<ColumnValueBean> getColumnValueBean()
	{
		return columnValueBean;
	}
}