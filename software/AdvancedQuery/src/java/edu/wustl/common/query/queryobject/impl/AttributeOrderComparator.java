package edu.wustl.common.query.queryobject.impl;

import java.util.Comparator;

public class AttributeOrderComparator implements Comparator<OutputAttributeColumn>
{
	public int compare(OutputAttributeColumn object1, OutputAttributeColumn object2)
	{
		int result;
		if(object1.getColumnIndex() <=object2.getColumnIndex())
		{
			result = -1;
		}
		else
		{
			result = 1;
		}
		return result;
	}
}
