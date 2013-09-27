/**
 *
 */
package edu.wustl.query.bizlogic;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOUtility;
import edu.wustl.query.beans.DashBoardBean;
import edu.wustl.query.generator.GenericQueryGeneratorMock;

/**
 * @author pooja_tavase
 *
 */
public class DashboardBizLogicTestCase extends TestCase
{
	/**
	 * Test getPGsForQuery method from DashboardBizLogic class.
	 */
	public void testGetPGsForQuery()
	{
		try
		{
			new DashboardBizLogic().
			getPGsforQuery("21");
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * To test getQueryOwner method from DashboardBizLogic class.
	 */
	public void testGetQueryOwner()
	{
		try
		{
			new DashboardBizLogic().getQueryOwner("21");
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * To test setQueriesToDashboard method from DashboardBizLogic class.
	 */
//	public void testSetQueriesToDashboard()
//	{
//		SessionDataBean sessionDataBean = new SessionDataBean();
//		sessionDataBean.setCsmUserId("1");
//		SaveQueryForm saveQueryForm = new SaveQueryForm();
//		try
//		{
//			new DashboardBizLogic().setQueriesToDashboard(sessionDataBean, saveQueryForm);
//		}
//		catch (BizLogicException e)
//		{
//			e.printStackTrace();
//		}
//	}

	public void testGetDashboardDetails()
	{
		try
		{
			Collection<IParameterizedQuery> queries = DAOUtility.getInstance().executeHQL(DAOUtility.GET_PARAM_QUERIES_DETAILS);
			new DashboardBizLogic().getDashBoardDetails(queries, "1");

			IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
			IParameterizedQuery pQuery = QueryObjectFactory.createParameterizedQuery(query);
			DashBoardBean bean = new DashBoardBean();
			bean.setCountOfRootRecords("2");
			bean.setExecutedOn("N/A");
			bean.setOwnerName("admin@admin.com");
			bean.setQuery(pQuery);
			bean.setRootEntityName("Participant");

			bean.getCountOfRootRecords();
			bean.getExecutedOn();
			bean.getOwnerName();
			bean.getQuery();
			bean.getRootEntityName();
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
	}

	/*public void testNegativeGetDashboardDetails()
	{
		try
		{
			Collection<IParameterizedQuery> queries = DAOUtility.getInstance().executeHQL(DAOUtility.GET_PARAM_QUERIES_DETAILS);
			new DashboardBizLogic().getDashBoardDetails(queries, null);
			assertFalse("It should throw Null pointer exception....",true);
		}
		catch(NullPointerException e)
		{
			assertTrue("Expected NullPointerExcpetionException!!!, Null pointer",true);
		}
		catch (BizLogicException e)
		{
			e.printStackTrace();
		}
		catch (DAOException e)
		{
			e.printStackTrace();
		}
	}*/
}
