
package edu.wustl.query.testQuerySuite;

/**
 *
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.ustl.query.util.querysuite.CsmUtilityTestCase;
import edu.ustl.query.util.querysuite.QueryCSMUtilTestCase;
import edu.ustl.query.util.querysuite.QueryDetailsTestCase;
import edu.ustl.query.util.querysuite.QueryModuleSqlUtilTestCase;
import edu.ustl.query.util.querysuite.QueryModuleUtilTestCase;
import edu.ustl.query.util.querysuite.TemporalColumnMetadataTestCase;
import edu.ustl.query.util.querysuite.TemporalQueryUtilityTestCase;
import edu.wustl.common.query.authoriztion.SavedQueryAuthorizationTestCase;
import edu.wustl.common.query.factory.AbstractQueryGeneratorFactoryTestCase;
import edu.wustl.common.query.factory.CommonObjectFactoryTestCase;
import edu.wustl.common.query.queryobject.impl.AssociationDataHandlerTestCase;
import edu.wustl.common.query.queryobject.impl.AttributeComparatorTestCase;
import edu.wustl.common.query.queryobject.impl.DenormalizedCSVExporterTestCase;
import edu.wustl.common.query.queryobject.impl.ListComparatorTestCase;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNodeTestCase;
import edu.wustl.common.query.queryobject.impl.QueryExportDataHandlerTestCase;
import edu.wustl.common.query.queryobject.impl.metadata.QueryTreeNodeDataTestCase;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadataTestCase;
import edu.wustl.common.query.queryobject.locator.PositionTestCase;
import edu.wustl.common.query.queryobject.locator.QueryNodeLocatorTestCase;
import edu.wustl.common.query.queryobject.util.QueryObjectProcessorTestCase;
import edu.wustl.query.bizlogic.BizLogicFactoryTestCase;
import edu.wustl.query.bizlogic.CommonQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.CreateQueryObjectTestCase;
import edu.wustl.query.bizlogic.DashboardBizLogicTestCase;
import edu.wustl.query.bizlogic.DefaultQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.DefineGridViewBizLogicTestCase;
import edu.wustl.query.bizlogic.ExportQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.QueryCsmBizLogicTestCase;
import edu.wustl.query.bizlogic.QueryOutputSpreadsheetBizLogicTestCase;
import edu.wustl.query.bizlogic.QueryOutputTreeBizLogicTestCase;
import edu.wustl.query.bizlogic.SaveQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.ShareQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.SpreadsheetDenormalizationBizLogicTestCase;
import edu.wustl.query.bizlogic.ValidateQueryBizLogicTestCase;
import edu.wustl.query.executor.AbstractQueryExecutorTestCase;
import edu.wustl.query.flex.dag.CustomFormulaNodeTestCase;
import edu.wustl.query.flex.dag.CustomFormulaUIBeanTestCase;
import edu.wustl.query.flex.dag.DAGPath;
import edu.wustl.query.flex.dag.DAGPathTestCase;
import edu.wustl.query.flex.dag.JoinFormulaNodeTestCase;
import edu.wustl.query.flex.dag.JoinFormulaUIBeanTestCase;
import edu.wustl.query.flex.dag.JoinQueryNodeTestCase;
import edu.wustl.query.flex.dag.SingalNodeTemporalQueryTestCase;
import edu.wustl.query.flex.dag.TwoNodesTemporalQueryTestCase;
import edu.wustl.query.generator.SqlGeneratorGenericTestCase;
import edu.wustl.query.generator.SqlKeyWordsTestCase;
import edu.wustl.query.htmlprovider.GenerateHtmlDetailsTestCase;
import edu.wustl.query.htmlprovider.GenerateHtmlTestCase;
import edu.wustl.query.htmlprovider.HtmlProviderTestCase;
import edu.wustl.query.htmlprovider.ParseXMLFileTestCase;
import edu.wustl.query.htmlprovider.SavedQueryHtmlProviderTestCase;
import edu.wustl.query.security.QueryCsmCacheManagerTestCase;
import edu.wustl.query.util.global.UserCacheTestCase;
import edu.wustl.query.util.global.UtilityTestCase;

/**
 * @author prafull_kadam
 * Test Suite for testing all Query Interface related classes.
 */
public class TestAll
{
	public static void main(String[] args)
	{
		junit.swingui.TestRunner.run(TestAll.class);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test suite for Query Interface Classes");
		suite.addTestSuite(CreateQueryObjectTestCase.class);

		suite.addTestSuite(SqlGeneratorGenericTestCase.class);
		//suite.addTestSuite(MySqlQueryGenerator.class);
		suite.addTestSuite(HtmlProviderTestCase.class);
		suite.addTestSuite(DashboardBizLogicTestCase.class);
		suite.addTestSuite(GenerateHtmlDetailsTestCase.class);
		suite.addTestSuite(GenerateHtmlTestCase.class);
		suite.addTestSuite(ParseXMLFileTestCase.class);
		//suite.addTestSuite(SavedQueryHtmlProviderTestCase.class);
		suite.addTestSuite(QueryCsmBizLogicTestCase.class);
		suite.addTestSuite(SelectedColumnsMetadataTestCase.class);
		suite.addTestSuite(QueryModuleSqlUtilTestCase.class);
		suite.addTestSuite(ExportQueryBizLogicTestCase.class);
		suite.addTestSuite(SaveQueryBizLogicTestCase.class);
		suite.addTestSuite(CommonQueryBizLogicTestCase.class);
		suite.addTestSuite(TemporalQueryUtilityTestCase.class);
		suite.addTestSuite(QueryObjectProcessorTestCase.class);
		suite.addTestSuite(QueryNodeLocatorTestCase.class);
		suite.addTestSuite(AbstractQueryGeneratorFactoryTestCase.class);
		suite.addTestSuite(CommonObjectFactoryTestCase.class);
		suite.addTestSuite(DefaultQueryBizLogicTestCase.class);
		suite.addTestSuite(ShareQueryBizLogicTestCase.class);
		suite.addTestSuite(DefineGridViewBizLogicTestCase.class);
		suite.addTestSuite(QueryCsmCacheManagerTestCase.class);
		suite.addTestSuite(UtilityTestCase.class);
		suite.addTestSuite(QueryDetailsTestCase.class);
		suite.addTestSuite(QueryModuleUtilTestCase.class);
		suite.addTestSuite(QueryOutputTreeBizLogicTestCase.class);
		suite.addTestSuite(QueryCSMUtilTestCase.class);
		suite.addTestSuite(QueryOutputSpreadsheetBizLogicTestCase.class);
		suite.addTestSuite(BizLogicFactoryTestCase.class);
		suite.addTestSuite(QueryTreeNodeDataTestCase.class);
		suite.addTestSuite(AbstractQueryExecutorTestCase.class);
		suite.addTestSuite(CustomFormulaUIBeanTestCase.class);
		suite.addTestSuite(SpreadsheetDenormalizationBizLogicTestCase.class);
		suite.addTestSuite(ListComparatorTestCase.class);
		suite.addTestSuite(QueryExportDataHandlerTestCase.class);
		suite.addTestSuite(SavedQueryAuthorizationTestCase.class);
		suite.addTestSuite(CsmUtilityTestCase.class);
		suite.addTestSuite(UserCacheTestCase.class);
		suite.addTestSuite(AttributeComparatorTestCase.class);
		suite.addTestSuite(ValidateQueryBizLogicTestCase.class);
		suite.addTestSuite(SqlKeyWordsTestCase.class);
		suite.addTestSuite(TemporalColumnMetadataTestCase.class);
		suite.addTestSuite(PositionTestCase.class);
		suite.addTestSuite(OutputTreeDataNodeTestCase.class);
		suite.addTestSuite(SingalNodeTemporalQueryTestCase.class);
		suite.addTestSuite(TwoNodesTemporalQueryTestCase.class);
		suite.addTestSuite(JoinFormulaUIBeanTestCase.class);
		suite.addTestSuite(JoinFormulaNodeTestCase.class);
		suite.addTestSuite(JoinQueryNodeTestCase.class);
		suite.addTestSuite(DAGPathTestCase.class);
		suite.addTestSuite(CustomFormulaNodeTestCase.class);
		suite.addTestSuite(DenormalizedCSVExporterTestCase.class);
		suite.addTestSuite(AssociationDataHandlerTestCase.class);
		return suite;
	}
}
