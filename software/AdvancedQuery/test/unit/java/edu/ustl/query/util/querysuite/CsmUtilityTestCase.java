package edu.ustl.query.util.querysuite;

import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.util.querysuite.CsmUtility;
import edu.wustl.security.exception.SMException;
import junit.framework.TestCase;

public class CsmUtilityTestCase extends TestCase
{
	public void testGetQueryPEs()
	{
		String queryId = "1";
		try
		{
			CsmUtility.getQueryPEs(queryId);
		}
		catch (SMException e)
		{
			e.printStackTrace();
		}
	}

	public void testGetUserProtectionGroup()
	{
		String csmUserId = "1";
		CsmUtility.getUserProtectionGroup(csmUserId);
	}

//	public void testGetParameterizedQueryCollection()
//	{
//		try
//		{
//			CsmUtility.getParameterizedQueryCollection();
//		}
//		catch (DAOException e)
//		{
//			e.printStackTrace();
//		}
//	}

	public void testGetSharedQueryIdList()
	{
		String userName = "admin@admin.com";
		try
		{
			CsmUtility.getSharedQueryIdList(userName);
		}
		catch (SMException e)
		{
			e.printStackTrace();
		}
	}
}
