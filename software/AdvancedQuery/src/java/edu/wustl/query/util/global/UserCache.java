package edu.wustl.query.util.global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.query.bizlogic.SaveQueryBizLogic;
import edu.wustl.security.exception.SMException;
import edu.wustl.security.manager.ISecurityManager;
import edu.wustl.security.manager.SecurityManagerFactory;
import gov.nih.nci.security.authorization.domainobjects.User;


/**
 * The Class UserNameCache.
 * @author nitesh_marwaha
 *
 */
public final class UserCache
{

	/**
     * Private default constructor to avoid instantiation.
     */
    private UserCache()
    {
        super();
    }

    /**
     * Logger object for logging any information to the log files.
     */
    private static org.apache.log4j.Logger logger = LoggerConfig
            .getConfiguredLogger(UserCache.class);



	/** Map containing all the user names. */
    private static final Map<String, User> USER_MAP = new HashMap<String, User>();

    /**
     * This method inits the cache by calling methods to put the users names
     *
     */
    public static void init() throws BizLogicException
    {
        setUsersToMap();
    }

	private static void setUsersToMap() throws BizLogicException
	{
		//Retrive all the user and set them into map as key=id and value=first name + last name
		try
		{
			ISecurityManager securityManager = SecurityManagerFactory.getSecurityManager();
			List<User> userList=securityManager.getUsers();
			if (!userList.isEmpty())
            {
                for (final User user : userList)
                {
                	USER_MAP.put(user.getUserId().toString(), user);
                }
            }
		}
		catch (SMException e)
		{
			logger.error(e.getMessage(), e);
			throw new BizLogicException(null,e,"SMException : error while getting users from security manager");
		}
	}

	public static User getUser(String userId) throws BizLogicException
	{
		User user=USER_MAP.get(userId);
			if(user == null)
			{
				//Retrieve the user name and set to map
				user = getUserFromDB(userId);
			}
		return user;
	}

	/**
	 * @param userId
	 * @return
	 * @throws BizLogicException
	 */
	private static User getUserFromDB(String userId) throws BizLogicException
	{
		SaveQueryBizLogic bizLogic = new SaveQueryBizLogic();
		User user=bizLogic.getUserById(userId);
		USER_MAP.put(userId, user);
		return user;
	}
}
