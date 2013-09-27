package edu.wustl.query.htmlprovider;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.generator.GenericQueryGeneratorMock;

public class SavedQueryHtmlProviderTestCase extends TestCase
{
	public void testGetHTMLForSavedQuery()
	{
		IQuery query = GenericQueryGeneratorMock.createTemporalQueryParticipantCPR();
		Map<Integer, ICustomFormula> customFormulaIndexMap = new HashMap<Integer,
		ICustomFormula>();
		ICustomFormula customFormula = GenericQueryGeneratorMock.createCustomFormulaParticipantCPR();
		customFormulaIndexMap.put(1, customFormula);
		String html = new SavedQueryHtmlProvider().getHTMLForSavedQuery(query, false, "Save Query Page", customFormulaIndexMap);
	}
}
