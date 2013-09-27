
package edu.wustl.query.flex.dag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.IConnector;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IDateOffsetAttribute;
import edu.wustl.common.querysuite.queryobject.IDateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionAttribute;
import edu.wustl.common.querysuite.queryobject.ILiteral;
import edu.wustl.common.querysuite.queryobject.INumericLiteral;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.query.util.global.AQConstants;

/**
 *
 */
public class TwoNodesTemporalQuery
{
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(TwoNodesTemporalQuery.class);

	/**
	 * Date offset attribute 1.
	 */
	private IDateOffsetAttribute dateOffsetAttr1 = null;
	/**
	 * Date offset attribute 2.
	 */
	private IDateOffsetAttribute dateOffsetAttr2 = null;
	/**
	 * First expression.
	 */
	private IExpressionAttribute iExpression1 = null;
	/**
	 * Second Expression.
	 */
	private IExpressionAttribute iExpression2 = null;
	/**
	 * Date offset literal.
	 */
	private IDateOffsetLiteral dateOffSetLiteral = null;
	/**
	 * Date literal.
	 */
	private ILiteral dateLiteral = null;
	/**
	 * LHS Term.
	 */
	private ITerm lhsTerm = null;
	/**
	 * RHS Term.
	 */
	private ITerm rhsTerm = null;
	/**
	 * IConnector object.
	 */
	private IConnector iCon = null;
	/**
	 * Custom Formula.
	 */
	private ICustomFormula customFormula = null;
	/**
	 * Source expression.
	 */
	private IExpression srcIExpression = null;
	/**
	 * Destination expression.
	 */
	private IExpression destIExpression = null;
	/**
	 * Arithmetic operator.
	 */
	private ArithmeticOperator arithOp = null;
	/**
	 * Relational operator.
	 */
	private RelationalOperator relOp = null;
	/**
	 * Source attribute identifier.
	 */
	private int srcExpressionId = 0;
	/**
	 * Destination attribute identifier.
	 */
	private int destExpressionId = 0;
	/**
	 * Source attribute.
	 */
	private AttributeInterface srcAttributeById = null;
	/**
	 * Destination attribute.
	 */
	private AttributeInterface destAttributeById = null;
	/**
	 * Data type of first data type.
	 */
	private String firstAttributeType = null;
	/**
	 * Data type of second attribute.
	 */
	private String secondAttributeType = null;

	/**
	 * time interval.
	 */
	private TimeInterval timeInterval = null;

	private TimeInterval qAttrInterval1 = null;

	private TimeInterval qAttrInterval2 = null;
	/**
	 * Numeric literal.
	 */
	private INumericLiteral intLiteral = null;

	public TimeInterval getQAttrInterval2()
	{
		return qAttrInterval2;
	}

	public void setQAttrInterval2(TimeInterval attrInterval2)
	{
		qAttrInterval2 = attrInterval2;
	}

	public TimeInterval getQAttrInterval1()
	{
		return qAttrInterval1;
	}

	public void setQAttrInterval1(TimeInterval attrInterval)
	{
		qAttrInterval1 = attrInterval;
	}

	/**
	 * @return Returns the firstAttributeType.
	 */
	public String getFirstAttributeType()
	{
		return firstAttributeType;
	}

	/**
	 * @param firstAttributeType The firstAttributeType to set.
	 */
	public void setFirstAttributeType(String firstAttributeType)
	{
		this.firstAttributeType = firstAttributeType;
	}

	/**
	 * @return Returns the secondAttributeType.
	 */
	public String getSecondAttributeType()
	{
		return secondAttributeType;
	}

	/**
	 * @param secondAttributeType The secondAttributeType to set.
	 */
	public void setSecondAttributeType(String secondAttributeType)
	{
		this.secondAttributeType = secondAttributeType;
	}

	/**
	 * @return Returns the destAttributeById.
	 */
	public AttributeInterface getDestAttributeById()
	{
		return destAttributeById;
	}

	/**
	 * @param destAttributeById The destAttributeById to set.
	 */
	public void setDestAttributeById(AttributeInterface destAttributeById)
	{
		this.destAttributeById = destAttributeById;
	}

	/**
	 * @return Returns the srcAttributeById.
	 */
	public AttributeInterface getSrcAttributeById()
	{
		return srcAttributeById;
	}

	/**
	 * @param srcAttributeById The srcAttributeById to set.
	 */
	public void setSrcAttributeById(AttributeInterface srcAttributeById)
	{
		this.srcAttributeById = srcAttributeById;
	}

	/**
	 * @return Returns the arithOp.
	 */
	public ArithmeticOperator getArithOp()
	{
		return arithOp;
	}

	/**
	 * @param arithOp The arithOp to set.
	 */
	public void setArithOp(ArithmeticOperator arithOp)
	{
		this.arithOp = arithOp;
	}

	/**
	 * @return Returns the customFormula.
	 */
	public ICustomFormula getCustomFormula()
	{
		return customFormula;
	}

	/**
	 * @param customFormula The customFormula to set.
	 */
	public void setCustomFormula(ICustomFormula customFormula)
	{
		this.customFormula = customFormula;
	}

	/**
	 * @return Returns the dateLiteral.
	 */
	public ILiteral getDateLiteral()
	{
		return dateLiteral;
	}

	/**
	 * @param dateLiteral The dateLiteral to set.
	 */
	public void setDateLiteral(ILiteral dateLiteral)
	{
		this.dateLiteral = dateLiteral;
	}

	/**
	 * @return Returns the dateOffsetAttr1.
	 */
	public IDateOffsetAttribute getDateOffsetAttr1()
	{
		return dateOffsetAttr1;
	}

	/**
	 * @param dateOffsetAttr1 The dateOffsetAttr1 to set.
	 */
	public void setDateOffsetAttr1(IDateOffsetAttribute dateOffsetAttr1)
	{
		this.dateOffsetAttr1 = dateOffsetAttr1;
	}

	/**
	 * @return Returns the dateOffsetAttr2.
	 */
	public IDateOffsetAttribute getDateOffsetAttr2()
	{
		return dateOffsetAttr2;
	}

	/**
	 * @param dateOffsetAttr2 The dateOffsetAttr2 to set.
	 */
	public void setDateOffsetAttr2(IDateOffsetAttribute dateOffsetAttr2)
	{
		this.dateOffsetAttr2 = dateOffsetAttr2;
	}

	/**
	 * @return Returns the dateOffSetLiteral.
	 */
	public IDateOffsetLiteral getDateOffSetLiteral()
	{
		return dateOffSetLiteral;
	}

	/**
	 * @param dateOffSetLiteral The dateOffSetLiteral to set.
	 */
	public void setDateOffSetLiteral(IDateOffsetLiteral dateOffSetLiteral)
	{
		this.dateOffSetLiteral = dateOffSetLiteral;
	}

	/**
	 * @return Returns the destExpressionId.
	 */
	public int getDestExpressionId()
	{
		return destExpressionId;
	}

	/**
	 * @param destExpressionId The destExpressionId to set.
	 */
	public void setDestExpressionId(int destExpressionId)
	{
		this.destExpressionId = destExpressionId;
	}

	/**
	 * @return Returns the iCon.
	 */
	public IConnector getICon()
	{
		return iCon;
	}

	/**
	 * @param con The iCon to set.
	 */
	public void setICon(IConnector con)
	{
		iCon = con;
	}

	/**
	 * @return Returns the iExpression1.
	 */
	public IExpressionAttribute getIExpression1()
	{
		return iExpression1;
	}

	/**
	 * @param expression1 The iExpression1 to set.
	 */
	public void setIExpression1(IExpressionAttribute expression1)
	{
		iExpression1 = expression1;
	}

	/**
	 * @return Returns the iExpression2.
	 */
	public IExpressionAttribute getIExpression2()
	{
		return iExpression2;
	}

	/**
	 * @param expression2 The iExpression2 to set.
	 */
	public void setIExpression2(IExpressionAttribute expression2)
	{
		iExpression2 = expression2;
	}

	/**
	 * @return Returns the lhsTerm.
	 */
	public ITerm getLhsTerm()
	{
		return lhsTerm;
	}

	/**
	 * @param lhsTerm The lhsTerm to set.
	 */
	public void setLhsTerm(ITerm lhsTerm)
	{
		this.lhsTerm = lhsTerm;
	}

	/**
	 * @return Returns the relOp.
	 */
	public RelationalOperator getRelOp()
	{
		return relOp;
	}

	/**
	 * @param relOp The relOp to set.
	 */
	public void setRelOp(RelationalOperator relOp)
	{
		this.relOp = relOp;
	}

	/**
	 * @return Returns the rhsTerm.
	 */
	public ITerm getRhsTerm()
	{
		return rhsTerm;
	}

	/**
	 * @param rhsTerm The rhsTerm to set.
	 */
	public void setRhsTerm(ITerm rhsTerm)
	{
		this.rhsTerm = rhsTerm;
	}

	/**
	 * @return Returns the srcExpressionId.
	 */
	public int getSrcExpressionId()
	{
		return srcExpressionId;
	}

	/**
	 * @param srcExpressionId The srcExpressionId to set.
	 */
	public void setSrcExpressionId(int srcExpressionId)
	{
		this.srcExpressionId = srcExpressionId;
	}

	/**
	 * @return Returns the srcIExpression.
	 */
	public IExpression getSrcIExpression()
	{
		return srcIExpression;
	}

	/**
	 * @param srcIExpression The srcIExpression to set.
	 */
	public void setSrcIExpression(IExpression srcIExpression)
	{
		this.srcIExpression = srcIExpression;
	}

	/**
	 * Create expressions.
	 * @param isJoinQuery isJoinQuery
	 */
	public void createExpressions(boolean isJoinQuery)
	{
		//If Both attributes have type
		if(isJoinQuery)
		{
			if (firstAttributeType.equals(secondAttributeType) || firstAttributeType.equals(String.class.getSimpleName()) || secondAttributeType.equals(String.class.getSimpleName()))
			{
				// add joining condition by casting the attribute which is of other data type to string using to_char().
				createExpressionsForSameType(isJoinQuery);
			}
		}
		else
		{
			//If The attribute type is of type Date
			//If Both attributes have type
			if ((firstAttributeType.equals(AQConstants.DATE_TYPE) && secondAttributeType.equals(AQConstants.DATE_TYPE)))
			{
				iExpression1 = QueryObjectFactory.createExpressionAttribute(srcIExpression,
						srcAttributeById, false);
				iExpression2 = QueryObjectFactory.createExpressionAttribute(destIExpression,
						destAttributeById, false);
			}
			else
			{
				if (firstAttributeType.equals(AQConstants.DATE_TYPE))
				{
					processForDateTypeAttribute();
				}
				else
				{
					setDateOffsetAttribute();
				}
			}
		}
	}

	/**
	 * Set date offset attribute.
	 */
	private void setDateOffsetAttribute()
	{
		iExpression2 = QueryObjectFactory.createExpressionAttribute(destIExpression,
				destAttributeById, false);
		if (qAttrInterval1 == null)
		{
			dateOffsetAttr1 = QueryObjectFactory.createDateOffsetAttribute
			(srcIExpression,srcAttributeById, TimeInterval.Day);
		}
		else
		{
			dateOffsetAttr1 = QueryObjectFactory.createDateOffsetAttribute
			(srcIExpression,srcAttributeById, qAttrInterval1);
		}
	}

	/**
	 * Process for date type attribute.
	 */
	private void processForDateTypeAttribute()
	{
		iExpression1 = QueryObjectFactory.createExpressionAttribute(srcIExpression,
				srcAttributeById, false);
		if (qAttrInterval2 == null)
		{
			dateOffsetAttr2 = QueryObjectFactory.createDateOffsetAttribute
			(destIExpression,destAttributeById, TimeInterval.Day);
		}
		else
		{
			dateOffsetAttr2 = QueryObjectFactory.createDateOffsetAttribute
			(destIExpression,destAttributeById, qAttrInterval2);
		}
	}

	/**
	 * @param isJoinQuery
	 * @param isDate
	 */
	private void createExpressionsForSameType(boolean isJoinQuery)
	{
		boolean isDate = false;
		if(isJoinQuery && AQConstants.DATE_TYPE.equals(firstAttributeType) && AQConstants.DATE_TYPE.equals(secondAttributeType))
		{
			isDate = true;
		}
		iExpression1 = QueryObjectFactory.createExpressionAttribute(srcIExpression,
				srcAttributeById, isDate);
		iExpression2 = QueryObjectFactory.createExpressionAttribute(destIExpression,
				destAttributeById, isDate);
	}

	/**
	 * This method will create only lhsTERM.
	 */
	public void createOnlyLHS()
	{
		lhsTerm = QueryObjectFactory.createTerm();
		if (iExpression1 != null && iExpression2 != null)
		{
			lhsTerm.addOperand(iExpression1);
			lhsTerm.addOperand(iCon, iExpression2);
		}
		else
		{
			addOperandToLhsTerm();
		}
	}

	/**
	 * Add operand to LHS term.
	 */
	private void addOperandToLhsTerm()
	{
		if (iExpression1 != null && dateOffsetAttr2 != null)
		{
			lhsTerm.addOperand(iExpression1);
			lhsTerm.addOperand(iCon, dateOffsetAttr2);
		}
		else
		{
			lhsTerm.addOperand(iExpression2);
			lhsTerm.addOperand(iCon, dateOffsetAttr1);
		}
	}

	/**
	 * This method creates either date Literal or dateOffset Literal depending on the time Interval values.
	 * @param timeIntervalValue timeIntervalValue
	 * @param timeValue timeValue
	 */
	public void createLiterals(String timeIntervalValue, String timeValue)
	{
		if ((!"null".equals(timeIntervalValue)) && (!"null".equals(timeValue)))
		{
			//Creating the dateOffSet Literal
			setTimeInterval(timeIntervalValue);
			dateOffSetLiteral = QueryObjectFactory.createDateOffsetLiteral(timeValue, timeInterval);
		}
		else
		{
			checkAttributeType(timeValue);
		}
	}

	/**
	 * Check attribute data type.
	 * @param timeValue timeValue
	 */
	private void checkAttributeType(String timeValue)
	{
		if (("Integer".equals(firstAttributeType)) && ("Integer".equals(secondAttributeType)))
		{
			intLiteral = QueryObjectFactory.createNumericLiteral(timeValue);
		}
		else if (!"null".equals(timeValue))
		{
			Date date = dateFormater(timeValue);
			dateLiteral = QueryObjectFactory.createDateLiteral(new java.sql.Date(date.getTime()));
		}
	}

	/**
	 * Format the date using the appropriate pattern.
	 * @param timeValue timeValue
	 * @return date
	 */
	private Date dateFormater(String timeValue)
	{
		//Date date = Utility.parseDate(timeValue, "MM/dd/yyyy HH:MM:SS");
		Date date = null;
		String pattern = "";
		try
		{
			if (("DateTime".equals(firstAttributeType)) && ("DateTime".equals(secondAttributeType)))
			{
				pattern = "MM/dd/yyyy HH:mm:ss";
			}
			else
			{
				pattern = "MM/dd/yyyy";
			}
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			date = formatter.parse(timeValue);
		}
		catch (ParseException e)
		{
			logger.error(e.getMessage(), e);
		}
		return date;
	}

	/**
	 * Sets the time interval.
	 * @param timeIntervalValue timeIntervalValue
	 */
	public void setTimeInterval(String timeIntervalValue)
	{
		for (TimeInterval time : TimeInterval.values())
		{
			if (timeIntervalValue.equals(time.name() + "s"))
			{
				timeInterval = time;
				break;
			}
		}
	}

	/**
	 * @param timeIntervalValue timeIntervalValue
	 */
	public void setQAttrTInterval1(String timeIntervalValue)
	{
		qAttrInterval1 = getTInterval(timeIntervalValue);
	}

	/**
	 * @param timeIntervalValue timeIntervalValue
	 */
	public void setQAttrTInterval2(String timeIntervalValue)
	{
		qAttrInterval2 = getTInterval(timeIntervalValue);
	}

	//This method is marked public as it is accessed from DAGPanel class also
	/**
	 * @param timeIntervalValue timeIntervalValue
	 * @return timeInterval
	 */
	public TimeInterval getTInterval(String timeIntervalValue)
	{
		TimeInterval timeInterval = null;
		for (TimeInterval time : TimeInterval.values())
		{
			if (timeIntervalValue.equals(time.name() + "s"))
			{
				timeInterval = time;
				break;
			}
		}
		return timeInterval;
	}

	/**
	 * @param timeIntervalValue timeIntervalValue
	 */
	public void createDateOffsetLiteral(String timeIntervalValue)
	{
		setTimeInterval(timeIntervalValue);
		dateOffSetLiteral = QueryObjectFactory.createDateOffsetLiteral(timeInterval);
	}

	/**
	 * Creates RHS.
	 */
	public void createOnlyRHS()
	{
		rhsTerm = QueryObjectFactory.createTerm();
		if (dateOffSetLiteral != null)
		{
			rhsTerm.addOperand(dateOffSetLiteral);
		}
		else if (intLiteral != null)
		{
			rhsTerm.addOperand(intLiteral);
		}

	}

	/**
	 * Create LHS and RHS.
	 */
	public void createLHSAndRHS()
	{
		lhsTerm = QueryObjectFactory.createTerm();
		rhsTerm = QueryObjectFactory.createTerm();
		if (iExpression1 != null && iExpression2 != null)
		{
			if(iCon ==null)
			{
				lhsTerm.addOperand(iExpression1);
				rhsTerm.addOperand(iExpression2);
			}
			else
			{
				addOperandsToTerms();
			}
		}
		else
		{
			addOperand();
		}
	}

	/**
	 * Add operands to LHS and RHS terms.
	 */
	private void addOperandsToTerms()
	{
		lhsTerm.addOperand(iExpression1);
		lhsTerm.addOperand(iCon, iExpression2);
		if (dateOffSetLiteral != null)
		{
			rhsTerm.addOperand(dateOffSetLiteral);
		}
		else if (intLiteral != null)
		{
			rhsTerm.addOperand(intLiteral);
		}
	}

	/**
	 * Add operand.
	 */
	private void addOperand()
	{
		if (iExpression1 != null && dateOffsetAttr2 != null)
		{
			lhsTerm.addOperand(iExpression1);
			lhsTerm.addOperand(iCon, dateOffsetAttr2);
			rhsTerm.addOperand(dateLiteral);
		}
		else
		{
			lhsTerm.addOperand(dateOffsetAttr1);
			lhsTerm.addOperand(iCon, iExpression2);
			rhsTerm.addOperand(dateLiteral);

		}
	}

	/**
	 * @return Returns the timeInterval.
	 */
	public TimeInterval getTimeInterval()
	{
		return timeInterval;
	}

	/**
	 * @param timeInterval The timeInterval to set.
	 */
	public void setTimeInterval(TimeInterval timeInterval)
	{
		this.timeInterval = timeInterval;
	}

	/**
	 * @return Returns the destIExpression.
	 */
	public IExpression getDestIExpression()
	{
		return destIExpression;
	}

	/**
	 * @param destIExpression The destIExpression to set.
	 */
	public void setDestIExpression(IExpression destIExpression)
	{
		this.destIExpression = destIExpression;
	}
}
