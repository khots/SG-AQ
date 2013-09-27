
package edu.wustl.query.util.querysuite;

public class QueryModuleException extends Exception
{

	private final String message;
	private QueryModuleError key;

	public QueryModuleException(String message)
	{
		super();
		this.message = message;
	}

	public QueryModuleException(String message, QueryModuleError key)
	{
		super(message);
		this.key = key;
		this.message = message;
	}

	/**Parameterized constructor.
	 * @param message error message
	 * @param exception Exception object
	 * @param errorKey QueryModuleError
	 */
	public QueryModuleException(String message,  Exception exception,  QueryModuleError errorKey)
	{
		super(message, exception);
		key = errorKey;
		this.message = message;
	}


	/**
	 * @return the key
	 */
	public QueryModuleError getKey()
	{
		return key;
	}

	public String getMessage()
	{
		return message;
	}
}
