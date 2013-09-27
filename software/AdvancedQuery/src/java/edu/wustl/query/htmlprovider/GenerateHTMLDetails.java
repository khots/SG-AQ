
package edu.wustl.query.htmlprovider;

import java.util.StringTokenizer;

/**
 * @author vijay_pande
 * Class to hold details related to Generate HTML logic
 */
public class GenerateHTMLDetails
{
	/**
	 * String to be searched.
	 */
	private String searchString;

	/**
	 *	String searched in attributes, if true.
	 */
	private boolean attributeChecked;

	/**
	 * String searched in permissible values, if true.
	 */
	private boolean permissibleValuesChecked;
	/**
	 *
	 */
	private String[] searchStrings;

	/**
	 * Method to get String value of searchString.
	 * @return searchString value of searchString
	 */
	public String getSearchString()
	{
		return searchString;
	}

	/**
	 * Method set value of searchString.
	 * @param searchString the searchString to set
	 */
	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
		this.searchStrings = prepareSearchString(searchString);
	}

	/**
	 * Method to get value of attributeChecked.
	 * @return attributeChecked
	 */
	public boolean isAttributeChecked()
	{
		return attributeChecked;
	}

	/**
	 * Method to set value of attributeChecked.
	 * @param attributeChecked the attributeChecked to set
	 */
	public void setAttributeChecked(boolean attributeChecked)
	{
		this.attributeChecked = attributeChecked;
	}

	/**
	 * Method to get value of permissibleValuesChecked.
	 * @return permissibleValuesChecked
	 */
	public boolean isPermissibleValuesChecked()
	{
		return permissibleValuesChecked;
	}

	/**
	 * Method to set value of permissibleValuesChecked.
	 * @param permissibleValuesChecked the permissibleValuesChecked to set
	 */
	public void setPermissibleValuesChecked(boolean permissibleValuesChecked)
	{
		this.permissibleValuesChecked = permissibleValuesChecked;
	}

	/**
	 * Method to get String[]  of searcStrings.
	 * @return searcStrings
	 */
	public String[] getSearcStrings()
	{
		return searchStrings;
	}

	/**
	 * Prepares a String to be sent to AdvancedSearch logic.
	 * @param searchString String
	 * @return String[] array of strings , taken from user.
	 */
	private String[] prepareSearchString(String searchString)
	{
		int counter = 0;
		StringTokenizer tokenizer = new StringTokenizer(searchString);
		String[] searchStrings = new String[tokenizer.countTokens()];
		while (tokenizer.hasMoreTokens())
		{
			searchStrings[counter] = tokenizer.nextToken().toLowerCase();
			counter++;
		}
		return searchStrings;
	}
}
