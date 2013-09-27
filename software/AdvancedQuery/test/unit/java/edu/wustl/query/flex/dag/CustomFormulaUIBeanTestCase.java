package edu.wustl.query.flex.dag;

import junit.framework.TestCase;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.generator.GenericQueryGeneratorMock;

public class CustomFormulaUIBeanTestCase extends TestCase
{
	public void testAllMethods()
	{
		ICustomFormula customFormula = GenericQueryGeneratorMock.createCustomFormulaParticipantCPR();
		IQuery query = GenericQueryGeneratorMock.createTemporalQueryParticipantCSR();
		SingleNodeCustomFormulaNode singleCNode = new SingleNodeCustomFormulaNode();
		CustomFormulaNode node = new CustomFormulaNode();
		CustomFormulaUIBean bean = new CustomFormulaUIBean(customFormula, node, singleCNode, query.getOutputTerms().get(0));

		bean.setCalculatedResult(false);
		bean.setCf(customFormula);
		bean.setOutputTerm(query.getOutputTerms().get(0));
		bean.setSingleNode(singleCNode);
		bean.setTwoNode(node);

		bean.getCf();
		bean.getOutputTerm();
		bean.getSingleNode();
		bean.getTwoNode();
	}
}
