package edu.wustl.query.beans;

import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import gov.nih.nci.security.authorization.domainobjects.User;

public class SharedQueryBean
{

	private String shareTo;

	private String selectedRoles;

	private long[] protocolCoordinatorIds;

	private User owner;

	private String csmUserId;

	private ParameterizedQuery query;
	/**
	 * @return the csmUserId
	 */
	public String getCsmUserId()
	{
		return csmUserId;
	}

	/**
	 * @param csmUserId the csmUserId to set
	 */
	public void setCsmUserId(String csmUserId)
	{
		this.csmUserId = csmUserId;
	}

	/**
	 * @return the query
	 */
	public ParameterizedQuery getQuery()
	{
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(ParameterizedQuery query)
	{
		this.query = query;
	}

	/**
	 * @return the selectedRoles
	 */
	public String getSelectedRoles()
	{
		return selectedRoles;
	}

	/**
	 * @param roles the selectedRoles to set
	 */
	public void setSelectedRoles(String roles)
	{
		this.selectedRoles = roles;
	}

	/**
	 * @return the selectedUsers
	 */
	public long[] getProtocolCoordinatorIds()
	{
		return protocolCoordinatorIds;
	}

	/**
	 * @param users the selectedUsers to set
	 */
	public void setProtocolCoordinatorIds(long[] users)
	{
		this.protocolCoordinatorIds = users;
	}

	/**
	 * @return the shareTo
	 */
	public String getShareTo()
	{
		return shareTo;
	}

	/**
	 * @param shareTo the shareTo to set
	 */
	public void setShareTo(String shareTo)
	{
		this.shareTo = shareTo;
	}

	/**
	 * @return the owner
	 */
	public User getOwner()
	{
		return owner;
	}
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(User owner)
	{
		this.owner = owner;
	}
}
