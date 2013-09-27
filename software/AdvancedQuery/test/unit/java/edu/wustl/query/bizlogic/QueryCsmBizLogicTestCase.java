package edu.wustl.query.bizlogic;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.querysuite.EntityCacheFactory;

public class QueryCsmBizLogicTestCase extends TestCase
{
	public void testGetMainEntityList()
	{
		EntityCache cache = EntityCacheFactory.getInstance();
        EntityInterface participantEntity = GenericQueryGeneratorMock.createEntity("Participant");
        participantEntity = GenericQueryGeneratorMock.getEntity(cache, participantEntity);
        EntityInterface csrEntity = GenericQueryGeneratorMock.createEntity("CollectionProtocolRegistration");
        csrEntity = GenericQueryGeneratorMock.getEntity(cache, csrEntity);
        QueryCsmBizLogic.getMainEntityList(participantEntity, csrEntity);
	}
}
