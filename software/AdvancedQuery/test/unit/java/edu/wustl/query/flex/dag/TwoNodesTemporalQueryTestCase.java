package edu.wustl.query.flex.dag;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IDateOffsetLiteral;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionAttribute;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import junit.framework.TestCase;

public class TwoNodesTemporalQueryTestCase extends TestCase
{
	public void testAllMethods()
	{
		TwoNodesTemporalQuery tqBean = new TwoNodesTemporalQuery();
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        IExpression partExp = GenericQueryGeneratorMock.createExpression(participantEntity);
        int destExpressionId = partExp.getExpressionId();
        AttributeInterface attributeById = GenericQueryGeneratorMock.findAttribute(participantEntity,"birthDate");
        AttributeInterface deathDate = GenericQueryGeneratorMock.findAttribute(participantEntity,"deathDate");
        ICustomFormula customFormula = GenericQueryGeneratorMock.createCustomFormulaParticipantCPR();
        IDateOffsetLiteral offSet = QueryObjectFactory.createDateOffsetLiteral("30", TimeInterval.Minute);
        ITerm lhsTerm = QueryObjectFactory.createTerm();
		ITerm rhsTerm = QueryObjectFactory.createTerm();
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
		IExpressionAttribute birthDate = QueryObjectFactory.createExpressionAttribute(
				partExp, attributeById,false);
		IExpressionAttribute deathDateExp = QueryObjectFactory.createExpressionAttribute(
				partExp, deathDate,false);

		tqBean.setArithOp(arithOp);
		tqBean.setCustomFormula(customFormula);
		tqBean.setDateLiteral(offSet);
		tqBean.setDateOffsetAttr1(null);
		tqBean.setDateOffsetAttr2(null);
		tqBean.setDateOffSetLiteral(offSet);
		tqBean.setDestAttributeById(attributeById);
		tqBean.setDestExpressionId(destExpressionId);
		tqBean.setDestIExpression(partExp);
		tqBean.setFirstAttributeType("Date");
		tqBean.setICon(QueryObjectFactory.createLogicalConnector(LogicalOperator.And));
		tqBean.setIExpression1(birthDate);
		tqBean.setIExpression2(deathDateExp);
		tqBean.setLhsTerm(lhsTerm);
		tqBean.setQAttrInterval1(null);
		tqBean.setQAttrInterval2(null);
		tqBean.setRelOp(RelationalOperator.Equals);
		tqBean.setRhsTerm(rhsTerm);
		tqBean.setSecondAttributeType("Date");
		tqBean.setSrcAttributeById(deathDate);
		tqBean.setSrcExpressionId(destExpressionId);
		tqBean.setSrcIExpression(partExp);
		tqBean.setTimeInterval("Minute");

		tqBean.getArithOp();
		tqBean.getCustomFormula();
		tqBean.getDateLiteral();
		tqBean.getDateOffsetAttr1();
		tqBean.getDateOffsetAttr2();
		tqBean.getDateOffSetLiteral();
		tqBean.getDestAttributeById();
		tqBean.getDestExpressionId();
		tqBean.getFirstAttributeType();
		tqBean.getDestIExpression();
		tqBean.getICon();
		tqBean.getIExpression1();
		tqBean.getIExpression2();
		tqBean.getLhsTerm();
		tqBean.getQAttrInterval1();
		tqBean.getQAttrInterval2();
		tqBean.getRelOp();
		tqBean.getRhsTerm();
		tqBean.getSecondAttributeType();
		tqBean.getSrcAttributeById();
		tqBean.getSrcExpressionId();
		tqBean.getSrcIExpression();
		tqBean.getTimeInterval();
		tqBean.getTInterval("Second");
	}
}
