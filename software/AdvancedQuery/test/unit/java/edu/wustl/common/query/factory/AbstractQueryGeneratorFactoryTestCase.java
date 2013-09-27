package edu.wustl.common.query.factory;

import edu.wustl.query.generator.ISqlGenerator;
import edu.wustl.query.util.global.Variables;
import junit.framework.TestCase;

public class AbstractQueryGeneratorFactoryTestCase extends TestCase
{
	public void testGetDefaultQueryGenerator()
	{
		Variables.queryGeneratorClassName = "edu.wustl.query.generator.SqlGenerator";
		ISqlGenerator queryGenerator = AbstractQueryGeneratorFactory.getDefaultQueryGenerator();
		assertNotNull(queryGenerator);
	}
}
