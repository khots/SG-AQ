package edu.wustl.query.bizlogic;

import java.io.IOException;
import java.sql.SQLException;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.exception.DAOException;
import junit.framework.TestCase;

public class DefaultQueryBizLogicTestCase extends TestCase
{
	public void testAllMethods() throws BizLogicException, DAOException, ClassNotFoundException, SQLException, IOException
	{
		DefaultQueryBizLogic bizLogic = new DefaultQueryBizLogic();
		SessionDataBean dataBean = new SessionDataBean();
		dataBean.setCsmUserId("1");
		dataBean.setFirstName("abc");
		dataBean.setAdmin(true);

		boolean isAuthorized = bizLogic.isAuthorized(null, null, dataBean);
		assertEquals("The user is authorized",true, isAuthorized);
		bizLogic.createProtectionElement(null);
		bizLogic.execute(dataBean, null, 0);
		bizLogic.executeSQL("SELECT * FROM CATISSUE_PARTICIPANT");
		bizLogic.getAliasName("firstName", "abc");
		bizLogic.getAllTableNames("CATISSUE_PARTICIPANT", 0);
		bizLogic.getAttributeType("FIRST_NAME", "firstName");
		bizLogic.getColumnNames("firstName");
		bizLogic.getColumnNames("firstName", false);
		bizLogic.getDisplayName("firstName");
		bizLogic.getDisplayNamebyTableName("CATISSUE_PARTICIPANT");
		bizLogic.getMainObjectsOfQuery();
		bizLogic.getNextTableNames(null);
		bizLogic.getPivilegeTypeMap();
		bizLogic.getQueryObjectNameTableNameMap();
		bizLogic.getRelatedTableAliases("firstName");
		bizLogic.getRelationData();
		bizLogic.getSpecimenTypeCount("tissue",null);
		bizLogic.getTableIdFromAliasName("participant");
		bizLogic.getTotalSummaryDetails();
		bizLogic.initializeQueryData();
		bizLogic.insertQuery("SELECT * FROM CATISSUE_PARTICIPANT",dataBean);
		bizLogic.insertQueryForMySQL("SELECT * FROM CATISSUE_PARTICIPANT",dataBean,null);
		bizLogic.insertQueryForOracle("SELECT * FROM CATISSUE_PARTICIPANT",dataBean,null);
		bizLogic.setColumnNames("firstName");
		bizLogic.setTablesInPath(Long.valueOf(1),Long.valueOf(2));
	}
}
