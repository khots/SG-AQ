package edu.wustl.query.bizlogic;

import edu.wustl.common.exception.BizLogicException;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.List;

import junit.framework.TestCase;

public class ShareQueryBizLogicTestCase extends TestCase
{
	public void testGetUsers()
	{
		ShareQueryBizLogic bizLogic = new ShareQueryBizLogic();
		List<User> users = bizLogic.getUsers();
	}

	public void testGetCSMUsers()
	{
		ShareQueryBizLogic bizLogic = new ShareQueryBizLogic();
		try
		{
			List users = bizLogic.getCSMUsers();
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}
	}
}
