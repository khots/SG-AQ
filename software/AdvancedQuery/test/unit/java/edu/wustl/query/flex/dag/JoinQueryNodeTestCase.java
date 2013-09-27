package edu.wustl.query.flex.dag;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class JoinQueryNodeTestCase extends TestCase
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
		List<JoinFormulaNode> joinFormulaNodeList = new ArrayList<JoinFormulaNode>();
		joinFormulaNodeList.add(joinFormulaNode);

		JoinQueryNode node = new JoinQueryNode();
		node.setFirstEntityName("Participant");
		node.setFirstNodeExpressionId(1);
		node.setJoinFormulaNodeList(joinFormulaNodeList);
		node.setName("joinQueryNode");
		node.setNodeView("view");
		node.setOperation("add");
		node.setSecondEntityName("ClinicalStudyRegistration");
		node.setSecondNodeExpressionId(2);

		node.getFirstEntityName();
		node.getFirstNodeExpressionId();
		node.getJoinFormulaNodeList();
		node.getName();
		node.getNodeView();
		node.getOperation();
		node.getSecondEntityName();
		node.getSecondNodeExpressionId();
	}
}
