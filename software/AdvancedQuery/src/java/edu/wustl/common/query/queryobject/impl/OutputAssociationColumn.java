package edu.wustl.common.query.queryobject.impl;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.wustl.common.querysuite.queryobject.IExpression;

/**
 * This class contains the details required to generate the map for de-normalization.
 * The key to this map is an object of this class with details of association/attribute,
 * source & target expressions.
 * @author pooja_tavase
 *
 */
public class OutputAssociationColumn
{
	private BaseAbstractAttributeInterface abstractAttr;
	private IExpression srcExpression;
	private IExpression tgtExpression;

	public OutputAssociationColumn
	(BaseAbstractAttributeInterface abstractAttr, IExpression srcExpression, IExpression tgtExpression)
	{
		this.abstractAttr = abstractAttr;
		this.srcExpression = srcExpression;
		this.tgtExpression = tgtExpression;
	}

	/**
	 * @return the association
	 */
	public BaseAbstractAttributeInterface getAbstractAttr()
	{
		return abstractAttr;
	}

	/**
	 * @param association the association to set
	 */
	public void setAbstractAttr(BaseAbstractAttributeInterface association)
	{
		this.abstractAttr = association;
	}

	/**
	 * @return the srcExpression
	 */
	public IExpression getSrcExpression()
	{
		return srcExpression;
	}

	/**
	 * @param srcExpression the srcExpression to set
	 */
	public void setSrcExpression(IExpression srcExpression)
	{
		this.srcExpression = srcExpression;
	}

	/**
	 * @return the tgtExpression
	 */
	public IExpression getTgtExpression()
	{
		return tgtExpression;
	}

	/**
	 * @param tgtExpression the tgtExpression to set
	 */
	public void setTgtExpression(IExpression tgtExpression)
	{
		this.tgtExpression = tgtExpression;
	}

	@Override
	public int hashCode()
	{
		return (int) (abstractAttr.getId() * 3L);
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof OutputAssociationColumn)
		{
			OutputAssociationColumn opAssocColumn = (OutputAssociationColumn) obj;
			if(tgtExpression == null || opAssocColumn.getTgtExpression()==null)
			{
				if(abstractAttr.equals(opAssocColumn.getAbstractAttr())
						&& srcExpression.equals(opAssocColumn.getSrcExpression()))
				{
					isEqual = true;
				}
			}
			else
			{
				if(abstractAttr.equals(opAssocColumn.getAbstractAttr())
						&& srcExpression.equals(opAssocColumn.getSrcExpression())
						&& tgtExpression.equals(opAssocColumn.getTgtExpression()))
				{
					isEqual = true;
				}
			}
		}
		return isEqual;
	}
}
