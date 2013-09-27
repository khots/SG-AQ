
package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.tags.bizlogic.ITagBizlogic;
import edu.wustl.common.tags.dao.TagDAO;
import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.tags.domain.TagItem;
import edu.wustl.common.tags.util.TagUtil;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.util.global.AQConstants;
import edu.wustl.query.util.global.UserCache;
import gov.nih.nci.security.authorization.domainobjects.User;

public class QueryTagBizLogic implements ITagBizlogic
{	
	
	private static final Logger LOGGER = Logger.getCommonLogger(QueryTagBizLogic.class);
	/**
	 * Insert New Tag to the database.
	 * @param entityName from hbm file.
	 * @param label for new Tag.
	 * @param userId.
	 * @return tag identifier.
	 * @throws DAOException,BizLogicException.
	 */
	public long createNewTag(String label, Long userId) throws DAOException,
			BizLogicException
	{
		TagDAO tagDao = null;
		try
		{
			Tag tag = new Tag();
			tag.setLabel(label);
			tag.setUserId(userId);
			tagDao = new TagDAO(AQConstants.ENTITY_QUERYTAG);
			tagDao.insertTag(tag);
			tagDao.commit();
			return tag.getIdentifier();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			if(tagDao != null)
			{	
				tagDao.closeSession();
			}
		}	
	}
 
	/**
	 * Assign the Query to existing folder.
	 * @param entityName from hbm file.
	 * @param tagId.
	 * @param objId.
	 * @throws DAOException,BizLogicException.
	 */
	
	public void assignTag(Long tagId, Long objId) throws DAOException,
			BizLogicException
	{
		TagDAO<ParameterizedQuery> tagDao = null;
		try
		{
			tagDao = new TagDAO(AQConstants.ENTITY_QUERYTAGITEM);
			boolean alreadyAssigned = tagDao.isTagItemAlreadyAssigned(tagId, objId);
			if (! alreadyAssigned)
			{
				Tag tag = new Tag();
				tag.setIdentifier(tagId);
				TagItem<ParameterizedQuery> tagItem = new TagItem<ParameterizedQuery>();
	  
				tagItem.setTag(tag);
				tagItem.setObjId(objId);
				
				tagDao.insertTagItem(tagItem);
			}
			tagDao.commit();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			if(tagDao != null)
			{	
				tagDao.closeSession();
			}
		}	
	}

	/**
	 * Get list of Tags from the database.
	 * @param entityName from hbm file.
	 * @param obj Object to be inserted in database
	 * @throws DAOException,BizLogicException.
	 */

	public List<Tag> getTagList(SessionDataBean sessionBean) throws DAOException, BizLogicException
	{
		TagDAO tagDao = null;
		List<Tag> tagList = null;
		try
		{
			tagDao = new TagDAO(AQConstants.ENTITY_QUERYTAG);
			tagList = tagDao.getTags(sessionBean); 
			return tagList;
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			if(tagDao != null)
			{	
				tagDao.closeSession();
			}
		}	
	}

 	/**
	 * Get Tag object.
	 * @param entityName from hbm file.
	 * @param  tagId.
	 * @return Tag Object.
	 * @throws DAOException,BizLogicException.
	 */

	public Tag getTagById(Long tagId) throws DAOException, BizLogicException
	{
		TagDAO tagDao = null;
		try
		{
			tagDao = new TagDAO(AQConstants.ENTITY_QUERYTAG);
			Tag tag = tagDao.getTagById(tagId);
			return tag;
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			if(tagDao != null)
			{	
				tagDao.closeSession();
			}
		}	
	}

	/**
	 * Get the Set of TagItems.
	 * @param entityName from hbm file.
	 * @param tagId.
	 * @return Set<TagItem>.
	 */
	public Set<TagItem> getTagItemByTagId(Long tagId) throws BizLogicException
	{
		TagDAO tagDao = null;
		Set<TagItem> tagItem = new HashSet<TagItem>();
		try
		{
			tagDao = new TagDAO(AQConstants.ENTITY_QUERYTAG);
			tagItem = tagDao.getTagItemBytagId(tagId);
		}
		catch (BizLogicException e)
		{
			LOGGER.error("Error occured while getting queries", e);
		}
		finally
		{
			if(tagDao != null)
			{	
				tagDao.closeSession();
			}
		}	
		return tagItem;
	}

	 
	/**
	 * Delete the Tag from database.
	 * @param entityName from hbm file.
	 * @param tagId to retrieve TagItem Object and delete it from database.
	 * @throws DAOException,BizLogicException.
	 */

	public void deleteTag(SessionDataBean sessionBean, Long tagId) throws DAOException, BizLogicException
	{
		TagDAO tagDao = null;
		try
		{
			tagDao = new TagDAO(AQConstants.ENTITY_QUERYTAG); 
			Tag tag = tagDao.getTagById(tagId);
			if(tag.getUserId() == sessionBean.getUserId()){
				tagDao.deleteTag(tag);
			} else {
				tag.getSharedUserIds().remove(Long.parseLong(sessionBean.getCsmUserId()));
				tagDao.updateTag(tag);
			}
			tagDao.commit();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			if(tagDao != null)
			{	
				tagDao.closeSession();
			}
		}	
	}
	/**
	 * Delete the Tag Item from database.
	 * @param entityName from hbm file.
	 * @param objId to retrieve TagItem Object and delete it from database.
	 * @throws DAOException,BizLogicException.
	 */
	public void deleteTagItem(Long tagItemId, Long userId) throws DAOException,
			BizLogicException
	{
		TagDAO tagDAO = null;
		try
		{
			tagDAO = new TagDAO(AQConstants.ENTITY_QUERYTAGITEM);
			TagItem tagItem = tagDAO.getTagItemById(tagItemId);
			Tag tag = tagItem.getTag(); 
			if(tag.getUserId() == userId){
				tagDAO.deleteTagItem(tagItem);
			} else {
				LOGGER.error("User does not have authority to delete this item");
			}
			tagDAO.commit();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		}
		finally
		{
			if(tagDAO != null)
			{	
				tagDAO.closeSession();
			}
		}	
	}
	
	/**
	 * Get queries from database.
	 * @param Set<TagItem> tagItemList.
	 * @throws DAOException.
	 */
	public List<List<String>> getQueries(long tagId) throws DAOException, BizLogicException
	{
		CommonQueryBizLogic commonQueryBizLogic = new CommonQueryBizLogic();
		List<List<String>> result = commonQueryBizLogic.getQueries(tagId);
		return result;
	}

	/**
	 * Get the Tag Item from database for Tree Grid.
	 * @param entityName from hbm file.
	 * @param  tagId.
	 * @return Json Object.
	 * @throws DAOException,BizLogicException.
	 */
	public JSONObject getJSONObj(Long tagId) throws DAOException,
			BizLogicException
	{

		JSONObject arrayObj = new JSONObject();
		try
		{
			JSONArray treeData = new JSONArray();
			int childCount = 0;
			List<List<String>> result = getQueries(tagId); 
			for (List<String> row : result)
			{
 				JSONObject obj = new JSONObject();
				obj.put(AQConstants.IDENTIFIER, Long.parseLong(row.get(0)));
				obj.put(AQConstants.NAME, row.get(2));
				obj.put("ownerId",row.get(4));
				childCount++;
				treeData.put(obj);
			}

			arrayObj.put(AQConstants.TREE_DATA, treeData);
			arrayObj.put(AQConstants.CHILDCOUNT, childCount);
		}
		catch (Exception e)
		{
		}
		return arrayObj;

	}
	/**
	 * share Tags to users.
	 * @param Set<Long> selectedUsers.
	 * @param Set<Long> tagIdSet.
	 * @throws DAOException.
	 */
	public void shareTags(SessionDataBean sessionBean, Set<Long> tagIdSet, Set<Long> csmUserIds)
			throws DAOException, BizLogicException 
	{
		List<Tag> tags = new ArrayList<Tag>();
		
		for(Long tagId : tagIdSet) 
		{
			TagDAO tagDao = null;
			try
			{
				tagDao = new TagDAO(AQConstants.ENTITY_QUERYTAG);
				Tag tag = tagDao.getTagById(tagId);
				if(tag.getUserId() == sessionBean.getUserId())
				{
					Long csmUserId = Long.parseLong(sessionBean.getCsmUserId());
					if(csmUserIds.contains(csmUserId)){
						csmUserIds.remove(csmUserId);
					}
					tag.getSharedUserIds().addAll(csmUserIds); 
					tagDao.updateTag(tag);
				}
				tagDao.commit();
				tags.add(tag);
			}
			catch (DAOException e)
			{
				throw new BizLogicException(e);
			} 
			finally
			{
				if(tagDao != null)
				{	
					tagDao.closeSession();
				}
			}	
		} 
		try 
		{
			if (! tags.isEmpty()){
				Set<User> users = getUsersFromCsmIds(csmUserIds);
				User user = UserCache.getUser(sessionBean.getCsmUserId());
				TagUtil.sendSharedTagEmailNotification(user, tags, 
						users, AQConstants.SHARE_QUERY_FOLDER_EMAIL_TEMPL);
			}
		} catch (Exception e) {
			LOGGER.error("Error while sending email for query folder",e);
		} 
	}
	
	public Set<User> getUsersFromCsmIds(Set<Long> csmUserIds) 
			throws BizLogicException
	{
		Set<User> users = new HashSet<User>();		
		for(Long userId : csmUserIds){
			User user = UserCache.getUser(userId.toString());
			users.add(user);	
		}
		return users;
	}
 
	/**
	 * Get queries from from TagId.
	 * @param Long tagId;
	 * @throws DAOException.
	 */
	public List<Long> getQueryIds(long tagId) throws BizLogicException
	{
		TagDAO tagDao = null;
		List<Long> queryIds = new ArrayList<Long>();
		try
		{
			tagDao = new TagDAO(AQConstants.ENTITY_QUERYTAG);
			Set<TagItem> tagItemSet = tagDao.getTagItemBytagId(tagId);
			queryIds = new ArrayList<Long>();
		
			for (TagItem tagItem : tagItemSet) {
				queryIds.add(tagItem.getObjId());
			}
		}
		catch (BizLogicException e)
		{
			LOGGER.error("Error occured while getting queries", e);
		}
		finally
		{
			if(tagDao != null)
			{	
				tagDao.closeSession();
			}
		}	
		return queryIds;
	}

	/**
	 * delete TagItems from object Ids.
	 * @param List<Long> objIds.
	 * @param Long tagId.
	 * @param Long userId.
	 * @throws DAOException.
	 */
	public void deleteTagItemsFromObjIds(List<Long> specimenIds, Long tagId, Long userId)
			throws DAOException, BizLogicException 
	{
		TagDAO tagDAO = null;
		try
		{
			Tag tag = getTagById(tagId);
			tagDAO = new TagDAO(AQConstants.ENTITY_QUERYTAGITEM);	
			if(tag.getUserId() == userId){
				tagDAO.deleteTagItemsFromObjIds(specimenIds, tagId);
			} else {
				throw new Exception("User does not have authority to delete this item(s)");
			}
			tagDAO.commit();
		}
		catch (DAOException e)
		{
			throw new BizLogicException(e);
		} catch (Exception e) {
			LOGGER.error("User does not have authority to delete this item(s)",e);
		}
		finally
		{
			if(tagDAO != null)
			{	
				tagDAO.closeSession();
			}
		}	
	}
}
