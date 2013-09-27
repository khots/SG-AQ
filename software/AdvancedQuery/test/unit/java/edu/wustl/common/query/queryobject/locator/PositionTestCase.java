package edu.wustl.common.query.queryobject.locator;

import junit.framework.TestCase;

public class PositionTestCase extends TestCase
{
	public void test()
	{
		Position position = new Position(400, 300);
		position.getX();
		position.getY();
	}
}
