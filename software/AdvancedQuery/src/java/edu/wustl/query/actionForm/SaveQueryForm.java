/**
 *
 */

package edu.wustl.query.actionForm;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.util.global.Validator;
import edu.wustl.query.beans.DashboardBean;

/**
 * @author chetan_patil
 * @created Sep 12, 2007, 10:28:02 PM
 */
public class SaveQueryForm extends AbstractActionForm
{

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Collection of parameterized queries.
	 */
	private Collection<IParameterizedQuery> parameterizedQueryCollection;

	/**
	 * The title of the saved query.
	 */
	private String title;


	public boolean isShowTree()
	{
		return showTree;
	}



	public void setShowTree(boolean showTree)
	{
		this.showTree = showTree;
	}

	/**
	 * The description of the saved query.
	 */
	private String description;

	private boolean showTree=true;

	/**
	 * The query id.
	 */
	private Long queryId;

	/**
	 * The query string.
	 */
	private String queryString;

	/**
	 * The dashBoardDetailsMap.
	 */
	private Map<Long,DashboardBean> dashBoardDetailsMap;

	/**
	 * To specify whom is the query shared to.
	 */
	private String shareTo="none";

	/**
	 * The selected roles.
	 */
	private String selectedRoles;

	/**
	 * Collection of all queries.
	 */
	private Collection<IParameterizedQuery> allQueries;

	/**
	 * Collection of my queries.
	 */
	private Collection<IParameterizedQuery> myQueries;

	/**
	 * Collection of shared queries.
	 */
	private Collection<IParameterizedQuery> sharedQueries;

	/**
	 * The protocolCoordinatorIds.
	 */
	protected long[] protocolCoordinatorIds;

	/**
	 *
	 * @return protocolCoordinatorIds The protocolCoordinatorIds
	 */
	private boolean  editQuery;

	/**
	 * @return the editQuery
	 */
	public boolean isEditQuery() {
		return editQuery;
	}


	/**
	 * @param editQuery the editQuery to set
	 */
	public void setEditQuery(boolean editQuery)
	{
		this.editQuery = editQuery;
	}


	public long[] getProtocolCoordinatorIds()
	{
		return protocolCoordinatorIds;
	}

	/**
	 *
	 * @param protocolCoordinatorIds Protocol coordinator's id
	 */
	public void setProtocolCoordinatorIds(long[] protocolCoordinatorIds)
	{
		this.protocolCoordinatorIds = protocolCoordinatorIds;
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
	 *
	 * @return shareTo Returns whom the query is shared to.
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
	 *
	 * @return queryString The query string.
	 */
	public String getQueryString()
	{
		return queryString;
	}

	/**
	 *
	 * @param queryString the queryString to set.
	 */
	public void setQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	@Override
	public int getFormId()
	{
		return 0;
	}

	@Override
	protected void reset()
	{

	}
	/**
	 * sets all values
	 */
	public void setAllValues(AbstractDomainObject arg0)
	{

	}

	/**
	 * @return the parameterizedQueryCollection
	 */
	public Collection<IParameterizedQuery> getParameterizedQueryCollection()
	{
		return parameterizedQueryCollection;
	}

	/**
	 * @param parameterizedQueryCollection the parameterizedQueryCollection to set
	 */
	public void setParameterizedQueryCollection(
			Collection<IParameterizedQuery> parameterizedQueryCollection)
	{
		this.parameterizedQueryCollection = parameterizedQueryCollection;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @param mapping ActionMapping mapping
	 * @param request HttpServletRequest request
	 * @return ActionErrors
	 * */
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		Validator validator = new Validator();
		if (title == null || validator.isEmpty(title))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required", "Title"));
		}
		return errors;
	}

	/**
	 * @return the queryId
	 */
	public Long getQueryId()
	{
		return queryId;
	}

	/**
	 * @param queryId the queryId to set
	 */
	public void setQueryId(Long queryId)
	{
		this.queryId = queryId;
	}

    @Override
    public void setAddNewObjectIdentifier(String arg0, Long arg1)
    {
        // TODO Auto-generated method stub
    }

    /**
     *
     * @param queryExecutedOnMap dashBoardDetailsMap to set.
     */
	public void setDashBoardDetailsMap(Map<Long, DashboardBean> queryExecutedOnMap)
	{
		this.dashBoardDetailsMap = queryExecutedOnMap;
	}

	/**
	 *
	 * @return dashBoardDetailsMap the dashBoardDetailsMap.
	 */
	public Map<Long,DashboardBean> getDashBoardDetailsMap()
	{
		return dashBoardDetailsMap;
	}

	/**
	 * @return the allQueries
	 */
	public Collection<IParameterizedQuery> getAllQueries()
	{
		return allQueries;
	}

	/**
	 * @param allQueries the allQueries to set
	 */
	public void setAllQueries(Collection<IParameterizedQuery> allQueries)
	{
		this.allQueries = allQueries;
	}

	/**
	 * @return the myQueries
	 */
	public Collection<IParameterizedQuery> getMyQueries()
	{
		return myQueries;
	}

	/**
	 * @param myQueries the myQueries to set
	 */
	public void setMyQueries(Collection<IParameterizedQuery> myQueries)
	{
		this.myQueries = myQueries;
	}

	/**
	 * @return the sharedQueries
	 */
	public Collection<IParameterizedQuery> getSharedQueries()
	{
		return sharedQueries;
	}

	/**
	 * @param sharedQueries the sharedQueries to set
	 */
	public void setSharedQueries(Collection<IParameterizedQuery> sharedQueries)
	{
		this.sharedQueries = sharedQueries;
	}
}
