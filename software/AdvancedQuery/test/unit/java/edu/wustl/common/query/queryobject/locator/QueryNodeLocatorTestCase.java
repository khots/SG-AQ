package edu.wustl.common.query.queryobject.locator;

import java.util.Map;

import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import junit.framework.TestCase;

public class QueryNodeLocatorTestCase extends TestCase
{
	public void testGetPositionMap()
	{
		IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
		Map<Integer, Position> positionMap = new QueryNodeLocator(400, query).getPositionMap();
		assertNotNull(positionMap);
	}
}
