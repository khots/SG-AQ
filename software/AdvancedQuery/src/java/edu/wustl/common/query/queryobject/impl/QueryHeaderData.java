package edu.wustl.common.query.queryobject.impl;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.queryobject.IExpression;

/**
 * @author pooja_tavase
 *
 */
public class QueryHeaderData
{
	private EntityInterface entity;
	private IExpression expression;

	public QueryHeaderData(EntityInterface entity,IExpression expression)
	{
		this.entity = entity;
		this.expression = expression;
	}

	/**
	 * @return the entity
	 */
	public EntityInterface getEntity()
	{
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(EntityInterface entity)
	{
		this.entity = entity;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(IExpression expression)
	{
		this.expression = expression;
	}

	/**
	 * @return the expression
	 */
	public IExpression getExpression()
	{
		return expression;
	}

	@Override
	public int hashCode()
	{
		return (int) (entity.getId() * 3L);
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof QueryHeaderData)
		{
			QueryHeaderData entityObject = (QueryHeaderData) obj;
			if (entity.equals(entityObject.getEntity()) &&
					expression.equals(entityObject.getExpression()))
			{
				isEqual = true;
			}
		}
		return isEqual;
	}
}
