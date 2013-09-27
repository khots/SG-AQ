package edu.wustl.query.util.global;

import edu.wustl.common.exception.BizLogicException;
import junit.framework.TestCase;

public class UserCacheTestCase extends TestCase
{
	public void testInit()
	{
		try
		{
			UserCache.init();
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}
	}

	public void testGetUser()
	{
		String userId = "1";
		try
		{
			UserCache.getUser(userId );
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}
	}
}
