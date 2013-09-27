package edu.wustl.query.beans;

import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;

public class DashboardBean
{
	private IParameterizedQuery query;
	private String executedOn;
	private String rootEntityName;
	private String countOfRootRecords;
	private String ownerName;

	/**
	 * @return the ownerName
	 */
	public String getOwnerName()
	{
		return ownerName;
	}
	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}
	/**
	 * @return the query
	 */
	public IParameterizedQuery getQuery()
	{
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(IParameterizedQuery query)
	{
		this.query = query;
	}
	/**
	 * @return the executedOn
	 */
	public String getExecutedOn()
	{
		return executedOn;
	}
	/**
	 * @param executedOn the executedOn to set
	 */
	public void setExecutedOn(String executedOn)
	{
		this.executedOn = executedOn;
	}
	/**
	 * @return the rootEntityName
	 */
	public String getRootEntityName()
	{
		return rootEntityName;
	}
	/**
	 * @param rootEntityName the rootEntityName to set
	 */
	public void setRootEntityName(String rootEntityName)
	{
		this.rootEntityName = rootEntityName;
	}
	/**
	 * @return the countOfRootRecords
	 */
	public String getCountOfRootRecords()
	{
		return countOfRootRecords;
	}
	/**
	 * @param countOfRootRecords the countOfRootRecords to set
	 */
	public void setCountOfRootRecords(String countOfRootRecords)
	{
		this.countOfRootRecords = countOfRootRecords;
	}

}
