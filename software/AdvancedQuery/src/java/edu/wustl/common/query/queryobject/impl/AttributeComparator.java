package edu.wustl.common.query.queryobject.impl;

import java.util.Comparator;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;

/**
 * This class sorts the list in such a way that all attributes come before associations.
 * @author pooja_tavase
 *
 */
public class AttributeComparator implements Comparator<AbstractAttributeInterface>
{

	public int compare(AbstractAttributeInterface attribute1, AbstractAttributeInterface attribute2)
	{
		int returnValue;
		if(attribute1 instanceof AssociationInterface && attribute2 instanceof AttributeInterface)
		{
			returnValue = 1;
		}
		else if(attribute1 instanceof AttributeInterface && attribute2 instanceof AssociationInterface)
		{
			returnValue = -1;
		}
		else
		{
			returnValue = 0;
		}
		return returnValue;
	}
}
