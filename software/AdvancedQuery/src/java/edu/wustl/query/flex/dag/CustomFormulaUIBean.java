
package edu.wustl.query.flex.dag;

import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;

/**
 *
 */
public class CustomFormulaUIBean
{

	/**
	 * ICustomFormula object.
	 */
	private ICustomFormula customFormula;

	/**
	 * CustomFormulaNode object.
	 */
	private CustomFormulaNode twoNode;

	/**
	 * SingleNodeCustomFormula object.
	 */
	private SingleNodeCustomFormulaNode singleNode;

	/**
	 * isCalculatedResult.
	 */
	private boolean isCalculatedResult = false;

	/**
	 * IOutputTerm object.
	 */
	private IOutputTerm outputTerm;

	/**
	 * @return Returns the isCalculatedResult.
	 */
	public boolean isCalculatedResult()
	{
		return isCalculatedResult;
	}

	/**
	 * @param isCalculatedResult The isCalculatedResult to set.
	 */
	public void setCalculatedResult(boolean isCalculatedResult)
	{
		this.isCalculatedResult = isCalculatedResult;
	}

	/**
	 * @return Returns the outputTerm.
	 */
	public IOutputTerm getOutputTerm()
	{
		return outputTerm;
	}

	/**
	 * @param outputTerm The outputTerm to set.
	 */
	public void setOutputTerm(IOutputTerm outputTerm)
	{
		this.outputTerm = outputTerm;
	}

	/**
	 * @param customFormula ICustomFormula object
	 * @param twoNode CustomFormulaNode object
	 * @param singleNode SingleNodeCustomFormulaNode object
	 * @param outputTerm IOutputTerm object
	 */
	public CustomFormulaUIBean(ICustomFormula customFormula, CustomFormulaNode twoNode,
			SingleNodeCustomFormulaNode singleNode, IOutputTerm outputTerm)
	{
		this.customFormula = customFormula;
		this.singleNode = singleNode;
		this.twoNode = twoNode;
		this.outputTerm = outputTerm;
	}

	/**
	 * @return Returns the customFormula.
	 */
	public ICustomFormula getCf()
	{
		return customFormula;
	}

	/**
	 * @param customFormula The customFormula to set.
	 */
	public void setCf(ICustomFormula customFormula)
	{
		this.customFormula = customFormula;
	}

	/**
	 * @return Returns the singleNode.
	 */
	public SingleNodeCustomFormulaNode getSingleNode()
	{
		return singleNode;
	}

	/**
	 * @param singleNode The singleNode to set.
	 */
	public void setSingleNode(SingleNodeCustomFormulaNode singleNode)
	{
		this.singleNode = singleNode;
	}

	/**
	 * @return Returns the twoNode.
	 */
	public CustomFormulaNode getTwoNode()
	{
		return twoNode;
	}

	/**
	 * @param twoNode The twoNode to set.
	 */
	public void setTwoNode(CustomFormulaNode twoNode)
	{
		this.twoNode = twoNode;
	}
}
