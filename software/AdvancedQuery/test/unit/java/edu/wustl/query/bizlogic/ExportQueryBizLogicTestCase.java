package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import junit.framework.TestCase;

public class ExportQueryBizLogicTestCase extends TestCase
{
	/**
	 * Test exportDetails method.
	 * @throws MultipleRootsException
	 */
	public void testExportDetails() throws MultipleRootsException
	{
		ExportQueryBizLogic bizLogic = new ExportQueryBizLogic();
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
		IParameterizedQuery parameterizedQuery = QueryObjectFactory.createParameterizedQuery(query);
		List<List<String>> exportList = new ArrayList<List<String>>();
		bizLogic.exportDetails(parameterizedQuery, exportList);
		query = GenericQueryGeneratorMock.createInheritanceQueryWithAssociation1();
		parameterizedQuery = QueryObjectFactory.createParameterizedQuery(query);
		parameterizedQuery.setName("Query 1");
		bizLogic.exportDetails(parameterizedQuery, exportList);
	}
}
