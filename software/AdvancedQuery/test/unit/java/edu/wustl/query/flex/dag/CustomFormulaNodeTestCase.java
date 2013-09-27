package edu.wustl.query.flex.dag;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.IConnector;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.ITerm;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;

public class CustomFormulaNodeTestCase extends TestCase
{
	public void testAllMethods()
	{
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);

        EntityInterface csrEntity = GenericQueryGeneratorMock.createEntity("ClinicalStudyRegistration");
        csrEntity = GenericQueryGeneratorMock.getEntity(cache, csrEntity);

        ICustomFormula c = GenericQueryGeneratorMock.createCustomFormulaParticipantCPR();
        ITerm lhs = c.getLhs();
		IConnector<ArithmeticOperator> connector = lhs.getConnector(0, 1);
		RelationalOperator relOperator = c.getOperator();
		CustomFormulaNode node = new CustomFormulaNode();
		node.setCcInterval("");
		node.setCustomColumnName("age");
		node.setFirstNodeExpId(1);
		node.setFirstNodeName(participantEntity.getName());
		node.setFirstSelectedAttrId("847");
		node.setFirstSelectedAttrName("birthDate");
		node.setFirstSelectedAttrType("date");
		node.setName("TQNode");
		node.setNodeView("result");
		node.setOperation("add");
		node.setQAttrInterval1("");
		node.setQAttrInterval2("");
		node.setSecondNodeExpId(2);
		node.setSecondNodeName(csrEntity.getName());
		node.setSecondSelectedAttrId("323");
		node.setSecondSelectedAttrName("registrationDate");
		node.setSecondSelectedAttrType("date");
		node.setSelectedArithmeticOp(connector.getOperator().mathString());
		node.setSelectedLogicalOp(relOperator.getStringRepresentation());
		node.setTimeInterval("");
		node.setTimeValue("");
		node.setX(3);
		node.setY(4);

		node.getCcInterval();
		node.getCustomColumnName();
		node.getFirstNodeExpId();
		node.getFirstNodeName();
		node.getFirstSelectedAttrId();
		node.getFirstSelectedAttrName();
		node.getFirstSelectedAttrType();
		node.getName();
		node.getNodeView();
		node.getOperation();
		node.getQAttrInterval1();
		node.getQAttrInterval2();
		node.getSecondNodeExpId();
		node.getSecondNodeName();
		node.getSelectedArithmeticOp();
		node.getSecondSelectedAttrType();
		node.getSelectedLogicalOp();
		node.getTimeInterval();
		node.getTimeValue();
		node.getX();
		node.getY();
	}
}
