package edu.wustl.query.flex.dag;

import junit.framework.TestCase;

public class JoinFormulaNodeTestCase extends TestCase
{
	public void testAllMethods()
	{
		JoinFormulaNode joinFormulaNode = new JoinFormulaNode();
		String firstAttributeDataType = "String";
		joinFormulaNode.setFirstAttributeDataType(firstAttributeDataType);
		joinFormulaNode.setFirstAttributeId("1");
		joinFormulaNode.setFirstAttributeName("lname");
		joinFormulaNode.setName("joinFormula");
		joinFormulaNode.setSecondAttributeDataType("String");
		joinFormulaNode.setSecondAttributeId("2");
		joinFormulaNode.setSecondAttributeName("firstName");

		joinFormulaNode.getFirstAttributeDataType();
		joinFormulaNode.getFirstAttributeId();
		joinFormulaNode.getFirstAttributeName();
		joinFormulaNode.getName();
		joinFormulaNode.getSecondAttributeDataType();
		joinFormulaNode.getSecondAttributeId();
		joinFormulaNode.getSecondAttributeName();
	}
}
