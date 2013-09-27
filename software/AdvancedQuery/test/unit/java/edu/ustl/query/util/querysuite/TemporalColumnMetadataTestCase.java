package edu.ustl.query.util.querysuite;

import edu.wustl.common.querysuite.queryobject.TermType;
import edu.wustl.query.util.querysuite.TemporalColumnMetadata;
import junit.framework.TestCase;

public class TemporalColumnMetadataTestCase extends TestCase
{
	public void testAllMethods()
	{
		TemporalColumnMetadata tqMetadata = new TemporalColumnMetadata();
		tqMetadata.setBirthDate(true);
		tqMetadata.setColumnIndex(0);
		tqMetadata.setPHIDate(null);
		tqMetadata.setTermType(TermType.DSInterval);
		tqMetadata.setTimeInterval(null);

		tqMetadata.getColumnIndex();
		tqMetadata.getPHIDate();
		tqMetadata.getTermType();
		tqMetadata.getTimeInterval();
	}
}
