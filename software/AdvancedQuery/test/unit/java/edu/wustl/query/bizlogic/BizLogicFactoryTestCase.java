package edu.wustl.query.bizlogic;

import junit.framework.TestCase;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.query.util.global.AQConstants;

public class BizLogicFactoryTestCase extends TestCase
{
	public void testSetBizLogicFactory()
	{
		IBizLogic bizLogic = BizLogicFactory.getInstance().getBizLogic
		(AQConstants.QUERY_INTERFACE_BIZLOGIC_ID);

		bizLogic = BizLogicFactory.getInstance().getBizLogic
		(AQConstants.QUERY_INTERFACE_ID);

		bizLogic = BizLogicFactory.getInstance().getBizLogic
		(AQConstants.ADVANCE_QUERY_INTERFACE_ID);

		BizLogicFactory.getInstance().setBizLogicFactory(null);
	}
}
