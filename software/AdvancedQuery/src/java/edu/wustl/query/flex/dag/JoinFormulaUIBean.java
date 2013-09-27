/**
 *
 */
package edu.wustl.query.flex.dag;

import edu.wustl.common.querysuite.queryobject.ICustomFormula;

/**
 * @author pooja_deshpande
 *
 */
public class JoinFormulaUIBean
{

	private ICustomFormula iCustomFormula;

	private JoinFormulaNode joinFormulaNode;

	public JoinFormulaUIBean(ICustomFormula iCustomFormula, JoinFormulaNode joinFormulaNode)
	{
		this.iCustomFormula = iCustomFormula;
		this.joinFormulaNode = joinFormulaNode;
	}

	/**
	 * @return the iCustomFormula
	 */
	public ICustomFormula getICustomFormula()
	{
		return iCustomFormula;
	}
	/**
	 * @param customFormula the iCustomFormula to set
	 */
	public void setICustomFormula(ICustomFormula customFormula)
	{
		iCustomFormula = customFormula;
	}
	/**
	 * @return the joinFormulaNode
	 */
	public JoinFormulaNode getJoinFormulaNode()
	{
		return joinFormulaNode;
	}
	/**
	 * @param joinFormulaNode the joinFormulaNode to set
	 */
	public void setJoinFormulaNode(JoinFormulaNode joinFormulaNode)
	{
		this.joinFormulaNode = joinFormulaNode;
	}
}
