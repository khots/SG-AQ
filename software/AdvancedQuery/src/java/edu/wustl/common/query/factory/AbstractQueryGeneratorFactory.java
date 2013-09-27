
package edu.wustl.common.query.factory;

import edu.wustl.common.util.Utility;
import edu.wustl.query.generator.ISqlGenerator;
import edu.wustl.query.util.global.Variables;

/**
 * Factory to return the SqlGenerator's instance.
 * @author deepti_shelar
 */
public abstract class AbstractQueryGeneratorFactory
{
	/**
	 * Method to create instance of class SqlGenerator.
	 * @return The reference of SqlGenerator.
	 */
	public static ISqlGenerator getDefaultQueryGenerator()
	{
		return (ISqlGenerator) Utility.getObject(Variables.queryGeneratorClassName);
	}

	/**
	 * Method to create instance of class SqlGenerator.
	 * @param className class name.
	 * @return The reference of SqlGenerator.
	 */
	public abstract ISqlGenerator configureQueryGenerator(String className);
}
