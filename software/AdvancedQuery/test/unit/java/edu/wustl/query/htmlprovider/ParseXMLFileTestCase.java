package edu.wustl.query.htmlprovider;

import java.io.InputStream;

import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.query.util.global.AQConstants;
import junit.framework.TestCase;

public class ParseXMLFileTestCase extends TestCase
{
	public void testAllMethods()
	{
		ParseXMLFile parseFile = null;
		try
		{
		    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(
                    AQConstants.DYNAMIC_UI_XML);
            parseFile = ParseXMLFile.getInstance(inputStream);
		}
		catch (CheckedException e)
		{
			e.printStackTrace();
		}
		assertEquals("Non enum class" , parseFile.getNonEnumClassName("string"),
				"edu.wustl.cab2b.client.ui.main.StringTypePanel");
		assertEquals("Enum class" , parseFile.getEnumClassName("number"),"edu.wustl.cab2b.client.ui.main.EnumTypePanel");
	}
}
