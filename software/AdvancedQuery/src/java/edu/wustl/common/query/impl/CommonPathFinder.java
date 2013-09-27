
package edu.wustl.common.query.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.cab2b.common.cache.AbstractEntityCache;
import edu.wustl.cab2b.server.path.PathFinder;
import edu.wustl.common.querysuite.metadata.associations.IInterModelAssociation;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.metadata.path.ICuratedPath;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.util.logger.Logger;

// TODO: Auto-generated Javadoc
/**
 * This class is used to find all the possible paths between two entities.
 *
 * @author deepti_shelar
 * @author lalit_chand
 */
public class CommonPathFinder implements IPathFinder
{
	/** instance of PathFinder. */
	private static PathFinder pathFinder = null;

	/**
	 * Instantiates a new common path finder.
	 */
	public CommonPathFinder()
	{
		if (AbstractEntityCache.isCacheReady)
		{
			getPathFinder();
		}
	}

	/**
	 * Get the PathFinder instance.
	 */
	private void getPathFinder()
	{
		Connection connection = null;
		try
		{
			InitialContext initialContext = new InitialContext();
			Context env = (Context) initialContext.lookup("java:comp/env");
		    String dsName = (String) env.lookup("DataSource");
		    Logger.out.info("Data source name found: " + dsName);

		    DataSource dataSource = (DataSource)initialContext.lookup(dsName);
		    Logger.out.info("Data source found: " + dataSource);

			connection = dataSource.getConnection();
			Logger.out.info("Connection established: " + connection);

			pathFinder = PathFinder.getInstance(connection);
		}
		catch (NamingException e)
		{
			Logger.out.error("CommonPathFinder:", e);
			//TODO need to see how to handle exception
		}
		catch (SQLException e)
		{
			Logger.out.error("CommonPathFinder:", e);
			//TODO need to see how to handle exception
		}
		finally
		{
			try
			{
				if (connection != null)
				{
					connection.close();
				}
			}
			catch (SQLException e)
			{
				Logger.out.error("CommonPathFinder:", e);
			}
		}
	}

	/**
	 * This method gets all the possible paths between two entities.
	 * @param srcEntity Source Entity.
	 * @param destEntity Destination Entity.
	 *
	 * @return List of all possible paths between source and destination entity.
	 */
	public List<IPath> getAllPossiblePaths(EntityInterface srcEntity, EntityInterface destEntity)
	{
		//PathFinder pathFinder= getPathFinderInstance();
		return pathFinder.getAllPossiblePaths(srcEntity, destEntity);
	}

	/**
	 * This method gets all the paths for query.
	 * @param srcEntity Source Entity.
	 * @param destEntity Destination Entity.
	 * @return All the paths
	 */
	public List<IPath> getAllPathsForQuery(EntityInterface srcEntity, EntityInterface destEntity)
	{
		//PathFinder pathFinder= getPathFinderInstance();
		return pathFinder.getAllPathsForQuery(srcEntity, destEntity);
	}

	/**
	 * Gets the path for associations.
	 * @param intraModelAssociationList List of intra-model association
	 * @return IPath object
	 */
	public IPath getPathForAssociations(List<IIntraModelAssociation> intraModelAssociationList)
	{
		//PathFinder pathFinder = getPathFinderInstance();
		return pathFinder.getPathForAssociations(intraModelAssociationList);
	}

	/**
	 * Auto connect.
	 * @param entitySet Set of entities.
	 * @return Set of curated paths.
	 */
	public Set<ICuratedPath> autoConnect(Set<EntityInterface> entitySet)
	{
		return pathFinder.autoConnect(entitySet);
	}

	/**
	 * Gets the curated paths.
	 * @param srcEntity Source Entity.
	 * @param destEntity Destination Entity.
	 * @return Curated paths between srcEntity and destEntity.
	 */
	public Set<ICuratedPath> getCuratedPaths(EntityInterface srcEntity, EntityInterface destEntity)
	{
		return pathFinder.getCuratedPaths(srcEntity, destEntity);
	}

	/**
	 * Gets the inter model associations.
	 * @param arg0 arg0
	 * @return List of inter model associations.
	 */
	public List<IInterModelAssociation> getInterModelAssociations(Long arg0)
	{
		return pathFinder.getInterModelAssociations(arg0);
	}
}
