/**
 *
 */

package edu.wustl.query.security;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maintains cache related to Read and Identified Data Privileges for a particular user on CP id's to
 * which objects in Query are related.
 * @author supriya_dankh
 *
 */
public class QueryCsmCache
{

	/**
	 * A map that stores collection protocol id v/s a boolean value that a user is authorized
	 * to read data related to that CP.
	 */
	private final Map<Long, Boolean> readPrivilegeMap;

	/**
	 * A map that stores collection protocol id v/s a boolean value that a user
	 * is authorized to see Identified data related to that CP.
	 */
	private final Map<Long, Boolean> identifiedDataAccsessMap;

	/**
	 * Constructor.
	 */
	public QueryCsmCache()
	{
		readPrivilegeMap = new HashMap<Long, Boolean>();
		identifiedDataAccsessMap = new HashMap<Long, Boolean>();
	}

	/**
	 * Returns true or false depending on user privileges.
	 * @param identifier identifier
	 * @return <CODE>true</CODE> it's read denied,
	 * <CODE>false</CODE> otherwise
	 */
	public Boolean isReadDenied(Long identifier)
	{
		return readPrivilegeMap.get(identifier);
	}

	/**
	 * Returns true or false depending on user privileges.
	 * @param identifier identifier
	 * @return <CODE>true</CODE> identified access on the data,
	 * <CODE>false</CODE> otherwise
	 */
	public Boolean isIdentifedDataAccess(Long identifier)
	{
		return this.identifiedDataAccsessMap.get(identifier);
	}

	/**
	 * Adds record in ReadPrivilegeMap.
	 * @param identifier identifier
	 * @param isAuthorized isAuthorized
	 */
	public void addNewObjectInReadPrivilegeMap(Long identifier, Boolean isAuthorized)
	{
		readPrivilegeMap.put(identifier, isAuthorized);
	}

	/**
	 * Remove record from ReadPrivilegeMap
	 * @param identifier identifier
	 */
	public void removeObjectFromReadPrivilegeMap(Long identifier)
	{
		readPrivilegeMap.remove(identifier);
	}

	/**
	 * Adds record in IdentifiedDataAccsessMap.
	 * @param identifier identifier
	 * @param isAuthorized isAuthorized
	 */
	public void addNewObjectInIdentifiedDataAccsessMap(Long identifier, Boolean isAuthorized)
	{
		identifiedDataAccsessMap.put(identifier, isAuthorized);
	}

	/**
	 * Remove record from IdentifiedDataAccsessMap
	 * @param identifier identifier
	 */
	public void removeObjectFromIdentifiedDataAccsessMap(Long identifier)
	{
		identifiedDataAccsessMap.remove(identifier);
	}
}
