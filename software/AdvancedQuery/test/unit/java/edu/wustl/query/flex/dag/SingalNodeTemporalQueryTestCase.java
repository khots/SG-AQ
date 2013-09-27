package edu.wustl.query.flex.dag;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IDateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import junit.framework.TestCase;

public class SingalNodeTemporalQueryTestCase extends TestCase
{
	public void testAllMethods()
	{
		SingalNodeTemporalQuery singalNodeTq = new SingalNodeTemporalQuery();

		ArithmeticOperator arithOp = null;
		String arithmeticOp = "+";
		for (ArithmeticOperator operator : ArithmeticOperator.values())
		{
			if (operator.mathString().equals(arithmeticOp))
			{
				arithOp = operator;
				break;
			}
		}
		singalNodeTq.setArithOp(arithOp);

		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IExpression partExp = GenericQueryGeneratorMock.createExpression(participantEntity);
        AttributeInterface attributeById = GenericQueryGeneratorMock.findAttribute(participantEntity,"lastName");
		singalNodeTq.setAttributeById(attributeById);

		singalNodeTq.setAttributeIExpression(null);
		singalNodeTq.setAttributeType("String");

		ICustomFormula customFormula = GenericQueryGeneratorMock.createCustomFormulaParticipantCPR();
		singalNodeTq.setCustomFormula(customFormula);

		singalNodeTq.setDateOffsetAttr(null);
		singalNodeTq.setEntityExpressionId(0);
		singalNodeTq.setEntityIExpression(partExp);
		singalNodeTq.setICon(QueryObjectFactory.createLogicalConnector(LogicalOperator.And));
		IDateOffsetLiteral offSet = QueryObjectFactory.createDateOffsetLiteral("30", TimeInterval.Minute);
		singalNodeTq.setLhsDateLiteral(offSet);
		singalNodeTq.setLhsDateOffSetLiteral(offSet);

		ITerm lhs = QueryObjectFactory.createTerm();
		ITerm rhsTerm = QueryObjectFactory.createTerm();
		singalNodeTq.setLhsTerm(lhs);
		singalNodeTq.setLhsTimeInterval(null);
		singalNodeTq.setQAttrInterval(null);
		singalNodeTq.setRelOp(RelationalOperator.Between);
		singalNodeTq.setRhsDateLiteral(offSet);
		singalNodeTq.setRhsDateOffSetLiteral(offSet);
		singalNodeTq.setRhsTerm(rhsTerm);
		singalNodeTq.setRhsTimeInterval(null);

		singalNodeTq.getArithOp();
		singalNodeTq.getAttributeById();
		singalNodeTq.getAttributeIExpression();
		singalNodeTq.getAttributeType();
		singalNodeTq.getCustomFormula();
		singalNodeTq.getDateOffsetAttr();
		singalNodeTq.getEntityExpressionId();
		singalNodeTq.getEntityIExpression();
		singalNodeTq.getICon();
		singalNodeTq.getLhsDateLiteral();
		singalNodeTq.getLhsDateOffSetLiteral();
		singalNodeTq.getLhsTerm();
		singalNodeTq.getLhsTimeInterval();
		singalNodeTq.getQAttrInterval();
		singalNodeTq.getRelOp();
		singalNodeTq.getRhsDateLiteral();
		singalNodeTq.getRhsDateOffSetLiteral();
		singalNodeTq.getRhsTerm();
		singalNodeTq.getRhsTimeInterval();
		singalNodeTq.getTimeInterval("Second");
	}
}
