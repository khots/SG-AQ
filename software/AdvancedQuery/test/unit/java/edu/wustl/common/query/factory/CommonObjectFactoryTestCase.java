package edu.wustl.common.query.factory;

import junit.framework.TestCase;

public class CommonObjectFactoryTestCase extends TestCase
{
	public void testGetInstance()
	{
		CommonObjectFactory clientFactory = CommonObjectFactory.getInstance();
		assertNotNull("CommonObjectFactory's Instance",clientFactory);
		Object className = clientFactory.getObjectofClass("edu.wust.clinportal.domain.Participant");
		assertNull("Class object",className);
	}

}
