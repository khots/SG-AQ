
package edu.wustl.query.querysuite;

import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.IArithmeticOperand;
import edu.wustl.common.querysuite.queryobject.IConnector;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IDateOffset;
import edu.wustl.common.querysuite.queryobject.IDateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TermType;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.query.generator.SqlGenerator;

public class QuerySqlGenerator extends SqlGenerator
{
	@Override
	protected String getCustomFormulaString(ICustomFormula formula)
	{
		ICustomFormula modifiedFormula = modifyForRounding(formula);
		return super.getCustomFormulaString(modifiedFormula);
	}

	private static class CaTissueFormula
	{

		private final IArithmeticOperand lhsOpnd1;

		private ArithmeticOperator lhsOper;

		private final IArithmeticOperand lhsOpnd2;

		private RelationalOperator relOper;

		private final IArithmeticOperand rhs;

		private final QueryType queryType;

		private enum QueryType
		{
			rhsDate, rhsOffset;

			private static QueryType fromTermType(TermType termType)
			{
				return TermType.isInterval(termType) ? rhsOffset : rhsDate;
			}
		}

		CaTissueFormula(ICustomFormula formula)
		{
			if (!formula.isValid())
			{
				throw new IllegalArgumentException("invalid custom formula.");
			}
			rhs = formula.getAllRhs().get(0).getOperand(0);
			lhsOpnd1 = formula.getLhs().getOperand(0);
			lhsOpnd2 = formula.getLhs().getOperand(1);
			lhsOper = formula.getLhs().getConnector(0, 1).getOperator();
			relOper = formula.getOperator();

			queryType = QueryType.fromTermType(rhs.getTermType());
		}

		private NormalizedFormula normalizedForm()
		{
			NormalizedFormula res = new NormalizedFormula();
			res.relOper = relOper;
			switch (queryType)
			{
				case rhsDate :
					res.rhsDate = rhs;
					res.lhsOper = lhsOper;
					// formula is valid; assume +/- are correct.
					if (lhsOpnd1.getTermType() == TermType.Date)
					{
						res.lhsDate = lhsOpnd1;
						res.lhsOffset = lhsOpnd2;
					}
					else
					{
						res.lhsDate = lhsOpnd2;
						res.lhsOffset = lhsOpnd1;
					}
					break;
				case rhsOffset :
					res.rhsDate = lhsOpnd2;
					res.lhsDate = lhsOpnd1;
					res.lhsOffset = rhs;
					res.lhsOper = ArithmeticOperator.Minus;
					break;
				default :
					throw new RuntimeException("can't occur.");
			}
			return res;
		}
	}

	private static class NormalizedFormula
	{
		// date (+/-) Interval = date
		private IArithmeticOperand lhsDate;

		private ArithmeticOperator lhsOper;

		private IArithmeticOperand lhsOffset;

		private IArithmeticOperand rhsDate;

		private RelationalOperator relOper;

		private TimeInterval<?> timeInterval()
		{
			if (lhsOffset.getTermType() == TermType.Numeric)
			{
				return TimeInterval.Day;
			}
			return ((IDateOffset) lhsOffset).getTimeInterval();
		}

		/**
		 * @return res
		 */
		private ICustomFormula roundedGenericForm()
		{
			ICustomFormula res = QueryObjectFactory.createCustomFormula();
			res.getLhs().addOperand(lhsDate);
			res.getLhs().addOperand(conn(lhsOper), lhsOffset);

			if (relOper == RelationalOperator.Equals || relOper == RelationalOperator.NotEquals)
			{
				return populateResultForComparisonOp(res);
			}
			res.setOperator(relOper);
			ITerm rhs = getRHS();
			res.addRhs(rhs);
			return res;
		}

		/**
		 * @param res result
		 * @return res
		 */
		private ICustomFormula populateResultForComparisonOp(ICustomFormula res)
		{
			res.setOperator(relOper == RelationalOperator.Equals
					? RelationalOperator.Between
					: RelationalOperator.NotBetween);
			res.addRhs(rhsLower());
			res.addRhs(rhsUpper());
			return res;
		}

		/**
		 * @return rhs
		 */
		private ITerm getRHS()
		{
			ITerm rhs;
			if (relOper == RelationalOperator.LessThanOrEquals)
			{
				rhs = rhsUpper();
			}
			else if (relOper == RelationalOperator.GreaterThanOrEquals)
			{
				rhs = rhsLower();
			}
			else
			{
				rhs = rhsTerm();
			}
			return rhs;
		}

		private ITerm rhsUpper()
		{
			ITerm term = rhsTerm();
			term.addOperand(conn(ArithmeticOperator.Plus), roundingOffset());
			return term;
		}

		private ITerm rhsLower()
		{
			ITerm term = rhsTerm();
			term.addOperand(conn(ArithmeticOperator.Minus), roundingOffset());
			return term;
		}

		private ITerm rhsTerm()
		{
			ITerm term = QueryObjectFactory.createTerm();
			term.addOperand(rhsDate);
			// term.addOperand(conn(ArithmeticOperator.Unknown),
			// roundingOffset());
			return term;
		}

		private IArithmeticOperand roundingOffset()
		{
			IArithmeticOperand operand;
			TimeInterval<?> timeInterval = timeInterval();
			if (timeInterval.equals(TimeInterval.Second))
			{
				operand = offset(0, TimeInterval.Second);
			}
			else if (timeInterval.equals(TimeInterval.Minute))
			{
				operand = offset(30, TimeInterval.Second);
			}
			else if (timeInterval.equals(TimeInterval.Hour))
			{
				operand = offset(30, TimeInterval.Minute);
			}
			else if (timeInterval.equals(TimeInterval.Day))
			{
				operand = offset(12, TimeInterval.Hour);
			}
			else if (timeInterval.equals(TimeInterval.Week))
			{
				// 3.5 * 24
				operand = offset(84, TimeInterval.Hour);
			}
			else if (timeInterval.equals(TimeInterval.Month))
			{
				operand = offset(15, TimeInterval.Day);
			}
			else if (timeInterval.equals(TimeInterval.Quarter))
			{
				operand = offset(45, TimeInterval.Day);
			}
			else if (timeInterval.equals(TimeInterval.Year))
			{
				operand = offset(6, TimeInterval.Month);
			}
			else
			{
				throw new RuntimeException("won't occur.");
			}
			return operand;
		}

		private static IDateOffsetLiteral offset(int offset, TimeInterval<?> timeInterval)
		{
			return QueryObjectFactory.createDateOffsetLiteral(String.valueOf(offset), timeInterval);
		}

		private static IConnector<ArithmeticOperator> conn(ArithmeticOperator oper)
		{
			return QueryObjectFactory.createArithmeticConnector(oper, 0);
		}
	}

	private ICustomFormula modifyForRounding(ICustomFormula formula)
	{
		if (formula.getAllRhs().isEmpty())
		{
			return formula;
		}
		CaTissueFormula caTissueFormula = new CaTissueFormula(formula);
		if (!isTemporal(caTissueFormula))
		{
			return formula;
		}
		NormalizedFormula normalizedFormula = caTissueFormula.normalizedForm();
		return normalizedFormula.roundedGenericForm();
	}

	private boolean isTemporal(CaTissueFormula caTissueFormula)
	{
		TermType rhsType = caTissueFormula.rhs.getTermType();
		return TermType.isInterval(rhsType) || rhsType == TermType.Timestamp
				|| rhsType == TermType.Date;
	}
}
