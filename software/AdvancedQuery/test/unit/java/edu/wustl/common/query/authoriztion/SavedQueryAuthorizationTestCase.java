package edu.wustl.common.query.authoriztion;

import junit.framework.TestCase;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.query.beans.SharedQueryBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.security.exception.SMException;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.exceptions.CSObjectNotFoundException;
import gov.nih.nci.security.exceptions.CSTransactionException;

public class SavedQueryAuthorizationTestCase extends TestCase
{
	public void testShareQuery()
	{
		SavedQueryAuthorization authorization = new SavedQueryAuthorization();
		SharedQueryBean bean = new SharedQueryBean();
		bean.setShareTo("all");
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
		IParameterizedQuery pQuery = QueryObjectFactory.createParameterizedQuery(query);
		try
		{
			authorization.shareQuery(bean, (ParameterizedQuery)pQuery);
			assertFalse("It should throw exception....",true);
		}
		catch (SMException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
		catch (CSTransactionException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
		catch (CSObjectNotFoundException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
		catch (CSException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
	}

	public void testGetUserProtectionGroup()
	{
		String csmUserId = "1";
		SavedQueryAuthorization authorization = new SavedQueryAuthorization();
		authorization.getUserProtectionGroup(csmUserId);
	}

	public void testGetSharedUsers()
	{
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
		ParameterizedQuery pQuery = (ParameterizedQuery)QueryObjectFactory.createParameterizedQuery(query);
		SavedQueryAuthorization authorization = new SavedQueryAuthorization();
		try
		{
			authorization.getSharedUsers(pQuery);
			assertFalse("It should throw exception....",true);
		}
		catch (SMException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
		catch (CSObjectNotFoundException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
	}

	public void testRemovePrevSharing()
	{
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
		ParameterizedQuery pQuery = (ParameterizedQuery)QueryObjectFactory.createParameterizedQuery(query);
		SavedQueryAuthorization authorization = new SavedQueryAuthorization();
		try
		{
			authorization.removePrevSharing(pQuery, "1");
			assertFalse("It should throw exception....",true);
		}
		catch (SMException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
		catch (CSTransactionException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
		catch (CSObjectNotFoundException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
		catch (BizLogicException e)
		{
			assertTrue("Expected Exception!!!,",true);
		}
	}
}
