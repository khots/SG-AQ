/**
 *
 */

package edu.wustl.query.util.querysuite;

import java.util.ArrayList;
import java.util.List;

import edu.wustl.common.querysuite.queryobject.DSInterval;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.TimeInterval;
import edu.wustl.common.querysuite.queryobject.YMInterval;
import edu.wustl.query.util.global.AQConstants;

/**
 * @author supriya_dankh
 *
 */
public class TemporalQueryUtility
{

	/**
	 * Get relational operators.
	 * @return relationalOperatorsList
	 */
	public static List<String> getRelationalOperators()
	{
		/**
		 * Getting relational operators excluding those deals with Strings
		 */
		List<String> relationalOperatorsList = new ArrayList<String>();
		for (RelationalOperator operator : RelationalOperator.values())
		{
			String opStr = operator.getStringRepresentation();
			if ((!opStr.equals(AQConstants.CONTAINS))
					&& (!opStr.equals(AQConstants.STRATS_WITH))
					&& (!opStr.equals(AQConstants.ENDS_WITH))
					&& (!opStr.equals(AQConstants.IN_OPERATOR))
					&& (!opStr.equals(AQConstants.BETWEEN))
					&& (!opStr.equals(AQConstants.NOT_IN))
					&& (!opStr.equalsIgnoreCase(AQConstants.IS_NULL))
					&& (!opStr.equalsIgnoreCase(AQConstants.IS_NOT_NULL))
					&& (!opStr.equalsIgnoreCase(AQConstants.NOT_BETWEEN)))

			{
				relationalOperatorsList.add(opStr);
			}
		}
		return relationalOperatorsList;
	}

	/**
	 * Get list of time intervals.
	 * @return timeIntervalList
	 */
	public static List<String> getTimeIntervals()
	{
		List<String> timeIntervalList = new ArrayList<String>();
		/**
		 * Getting all days time Intervals
		 */
		for (DSInterval timeInterval : DSInterval.values())
		{
			timeIntervalList.add(timeInterval.name() + "s");
		}

		for (YMInterval timeInterval1 : YMInterval.values())
		{
			timeIntervalList.add(timeInterval1.name() + "s");
		}
		return timeIntervalList;
	}

	/**
	 * Get time interval.
	 * @param timeIntervalValue timeIntervalValue
	 * @return timeInterval
	 */
	public static TimeInterval getTimeInterval(String timeIntervalValue)
	{
		TimeInterval timeInterval = null;
		for (TimeInterval time : TimeInterval.values())
		{
			if (timeIntervalValue.equals(time.name() + "s"))
			{
				timeInterval = time;
				break;
			}
		}
		return timeInterval;
	}

	/**
	 * Get relational operator.
	 * @param relationalOp relational operator
	 * @return relOp
	 */
	public static RelationalOperator getRelationalOperator(String relationalOp)
	{
		RelationalOperator relOp = null;
		for (RelationalOperator operator : RelationalOperator.values())
		{
			if (operator.getStringRepresentation().equals(relationalOp))
			{
				relOp = operator;
				break;
			}
		}
		return relOp;
	}
}
