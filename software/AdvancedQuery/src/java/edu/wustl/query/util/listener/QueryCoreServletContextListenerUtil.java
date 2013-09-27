package edu.wustl.query.util.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.query.executor.AbstractQueryExecutor;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.UserCache;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.global.Variables;

// TODO: Auto-generated Javadoc
/**
 * The Class QueryCoreServletContextListenerUtil.
 */
public class QueryCoreServletContextListenerUtil
{

	/** The logger. */
	private static org.apache.log4j.Logger logger = LoggerConfig
	.getConfiguredLogger(QueryCoreServletContextListenerUtil.class);
	//String DATASOURCE_JNDI_NAME = "java:/clinportal";

   /**
	 * Context initialized.
	 * @param sce the sce
	 * @param jndiName the jndi name
	 */
	public static void contextInitialized(ServletContextEvent sce, String jndiName)
	    {
	        try
	        {
	            logger.info("entered contextInitialized method of query...jndiname : "+jndiName);
	            //ErrorKey.init("~");
	            ServletContext servletContext = sce.getServletContext();
	            //Variables.applicationHome = sce.getServletContext().getRealPath("");
	            String propDirPath =sce.getServletContext().getRealPath("WEB-INF");
	            LoggerConfig.configureLogger(propDirPath);
	            ErrorKey.init("~");
	            //Logger.configDefaultLogger(servletContext);
	            ApplicationProperties
	                    .initBundle(servletContext.getInitParameter("resourcebundleclass"));
	            CommonServiceLocator.getInstance().setAppHome(sce.getServletContext().getRealPath(""));
	            setGlobalVariable();
	            Utility.setReadDeniedAndEntitySqlMap();
	            UserCache.init();

	    		final AbstractQueryExecutor queryExecutor = Utility.getQueryExecutor();
	    		queryExecutor.deleteQueryTempViews(AQConstants.TEMP_OUPUT_TREE_TABLE_NAME);
	    		queryExecutor.deleteQueryTempViews(AQConstants.TEMP_INNER_VIEW);

	        }
	        catch (Exception e)
	        {
	            logger.error("Application failed to initialize");
	            throw new RuntimeException(e.getLocalizedMessage(), e);

	        }
	        logger.info("exit from contextInitialized method of query...jndiname : "+jndiName);
	    }

	/**
	 * Sets the global variable.
	 * @throws Exception the exception
	 */
	private static void setGlobalVariable() throws Exception
	{
		String path = System.getProperty("app.propertiesFile");
		XMLPropertyHandler.init(path);
//	/	File propetiesDirPath = new File(path);
//		Variables.propertiesDirPath = propetiesDirPath.getParent();
//
//		Variables.applicationName = ApplicationProperties.getValue("app.name");
//		Variables.applicationVersion = ApplicationProperties.getValue("app.version");
		int maximumTreeNodeLimit = Integer.parseInt(XMLPropertyHandler
				.getValue(AQConstants.MAXIMUM_TREE_NODE_LIMIT));
		Variables.maximumTreeNodeLimit = maximumTreeNodeLimit;
		Variables.maxTreeNdLmtForChildNd = Integer.parseInt(XMLPropertyHandler
				.getValue(AQConstants.MAX_TREE_NODE_LIMIT_CHILD));
		readProperties();
	}

	/**
	 * Read properties.
	 */
	private static void readProperties()
	{
	    String appHome = CommonServiceLocator.getInstance().getAppHome();
		File file = new File(appHome + System.getProperty("file.separator")
				+ "WEB-INF" + System.getProperty("file.separator") + "classes"
				+ System.getProperty("file.separator") + "query.properties");
		if (file.exists())
		{
			Properties queryProperties = new Properties();
			try
			{
				queryProperties.load(new FileInputStream(file));

				Variables.queryGeneratorClassName = queryProperties
						.getProperty("query.queryGeneratorClassName");

				Variables.properties = queryProperties;
			}
			catch (FileNotFoundException e)
			{
				logger.error(e.getMessage());
			}
			catch (IOException e)
			{
				logger.error(e.getMessage());
			}
		}

	}

	/**
	 * Context destroyed.
	 * @param sce the sce
	 */
	public static void contextDestroyed(ServletContextEvent sce)
	{/*
		//  shutting down the cacheManager
		try
		{
			//CatissueCoreCacheManager catissueCoreCacheManager = CatissueCoreCacheManager.getInstance();
			//catissueCoreCacheManager.shutdown();
		}
		catch (CacheException e)
		{
			//logger.debug("Exception occurred while shutting instance of CatissueCoreCacheManager");
			//e.printStackTrace();
		}
	*/}
}
