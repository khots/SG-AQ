package edu.wustl.query.flex.dag;

import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import junit.framework.TestCase;

public class JoinFormulaUIBeanTestCase extends TestCase
{
	public void testAllMethods()
	{
		ICustomFormula formula = QueryObjectFactory.createCustomFormula();

		JoinFormulaUIBean bean = new JoinFormulaUIBean(formula, new JoinFormulaNode());

		bean.setICustomFormula(formula);
		bean.setJoinFormulaNode(new JoinFormulaNode());
		bean.getICustomFormula();
		bean.getJoinFormulaNode();
	}
}
