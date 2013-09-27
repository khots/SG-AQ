package edu.ustl.query.util.querysuite;

import java.util.List;

import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.query.util.querysuite.TemporalQueryUtility;
import junit.framework.TestCase;

public class TemporalQueryUtilityTestCase extends TestCase
{
	public void testGetRelationalOperators()
	{
		List<String> operators = TemporalQueryUtility.getRelationalOperators();
		assertNotNull(operators);
	}

	public void testGetTimeIntervals()
	{
		List<String> timeIntervalList = TemporalQueryUtility.getTimeIntervals();
		assertNotNull(timeIntervalList);
	}

	public void testGetTimeInterval()
	{
		TimeInterval timeInterval = null;
		timeInterval = TemporalQueryUtility.getTimeInterval("Years");
		assertEquals("Time interval",timeInterval.toString(),"Year");
	}

	public void testGetRelationalOperator()
	{
		RelationalOperator relOp = null;
		relOp = TemporalQueryUtility.getRelationalOperator("Not Equals");
		assertEquals("Relational Operator", relOp.toString(),"NotEquals");
	}
}
