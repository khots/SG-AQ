package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ListComparatorTestCase extends TestCase
{
	public void testCompare()
	{
		int mainColumnIndex = 2;
		ListComparator comparator = new ListComparator(mainColumnIndex);

		List<String> list1 = new ArrayList<String>();
		list1.add("abc");
		list1.add("mrn1");
		list1.add("2");

		List<String> list2 = new ArrayList<String>();
		list2.add("pqr");
		list2.add("mrn2");
		list2.add("1");

		comparator.compare(list1, list2);
		comparator.compare(list2, list1);
		comparator.compare(list1, list1);
	}
}
