package edu.wustl.query.htmlprovider;

import junit.framework.TestCase;

public class GenerateHtmlDetailsTestCase extends TestCase
{
	public void testAllMethods()
	{
		GenerateHTMLDetails htmlDetails = new GenerateHTMLDetails();
		htmlDetails.setSearchString("Participant");

		htmlDetails.setAttributeChecked(true);

		htmlDetails.setPermissibleValuesChecked(false);

		assertEquals("Incorrect Search String" , htmlDetails.isAttributeChecked(),
				true);

		assertEquals("Incorrect Search String" , htmlDetails.isPermissibleValuesChecked(),
		false);

		assertEquals("Incorrect Search String" , htmlDetails.getSearchString(),
		"Participant");

		String[] searchStrings = htmlDetails.getSearcStrings();
	}

}
