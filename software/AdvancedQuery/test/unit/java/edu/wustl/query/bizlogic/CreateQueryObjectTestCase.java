package edu.wustl.query.bizlogic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.common.cache.AbstractEntityCache;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.query.generator.GenericQueryGeneratorMock;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.querysuite.EntityCacheFactory;


public class CreateQueryObjectTestCase extends TestCase
{
	static
	{
		CommonServiceLocator.getInstance();
	}
	public CreateQueryObjectTestCase()
	{
		super();
	}

	/**
	 * Test rule details map.
	 */
	public void testRuleDetailsMap()
	{
		try
		{
			CreateQueryObjectBizLogic queryBizlogic = new CreateQueryObjectBizLogic();
			EntityCache cache = EntityCacheFactory.getInstance();
			while(AbstractEntityCache.isCacheReady == false)
			{
				continue;
			}
			EntityInterface entity = GenericQueryGeneratorMock.createEntity("Participant");
			Long attributeId = null;
			Long birthDateId = null;
			entity = GenericQueryGeneratorMock.getEntity(cache, entity);
			Collection<AttributeInterface> attrCollection = entity.getAllAttributes();

			for(AttributeInterface attribute : attrCollection)
			{
				if(attribute.getName().equals("id"))
				{
					attributeId = attribute.getId();
				}
				else if(attribute.getName().equals("birthDate"))
				{
					birthDateId = attribute.getId();
				}
			}
			String strToCreateQueryObject = "@#condition#@id"+attributeId+"!*=*!Between!*=*!1!*=*!10;";
			queryBizlogic.getRuleDetailsMap(strToCreateQueryObject, attrCollection);
			strToCreateQueryObject = "@#condition#@id"+attributeId+"!*=*!Between!*=*!1!*=*!missingTwoValues;";
			queryBizlogic.getRuleDetailsMap(strToCreateQueryObject, attrCollection);
			strToCreateQueryObject = "@#condition#@birthDate"+birthDateId+"!*=*!Equals!*=*!2-01-2010;";
			queryBizlogic.getRuleDetailsMap(strToCreateQueryObject, attrCollection);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			fail();
		}
	}

	/**
	 * Test setInputDataToQuery method.
	 */
	public void testSetInputDataToQuery()
	{
		try
		{
			CreateQueryObjectBizLogic queryBizlogic = new CreateQueryObjectBizLogic();
			IQuery query = GenericQueryGeneratorMock.creatParticipantQuery();
			String queryInputString = "";
			String errorMessage = queryBizlogic.setInputDataToQuery(queryInputString, query.getConstraints(), null, query);
			assertEquals("",errorMessage);
		}
		catch (Exception e)
		{
			fail("Unexpected Exception while setting input data to query!!!");
		}
	}

	/**
	 * Test setInputDataToTQ method.
	 */
	public void testSetInputDataToTQ()
	{
		try
		{
			ICustomFormula customFormula = GenericQueryGeneratorMock.createCustomFormulaParticipantCPR();
			IQuery query = GenericQueryGeneratorMock.createTemporalQueryParticipantCPR();
			Map<Integer, ICustomFormula> cFIndexMap = new HashMap<Integer, ICustomFormula>();
			cFIndexMap.put(1, customFormula);
			CreateQueryObjectBizLogic queryBizlogic = new CreateQueryObjectBizLogic();
			String errorMessage = queryBizlogic.setInputDataToTQ(query, AQConstants.SAVE_QUERY_PAGE, "", cFIndexMap);
			assertEquals("",errorMessage);
		}
		catch(Exception e)
		{
			fail("Unexpected Exception while setting input data to TQ!!!");
		}
	}
}
