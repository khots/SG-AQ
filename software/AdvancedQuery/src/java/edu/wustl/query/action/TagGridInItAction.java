
package edu.wustl.query.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/*import edu.wustl.common.tagFolder.beans.Tag;
import edu.wustl.common.tagFolder.bizlogic.TagBizLogic;*/
import edu.wustl.common.tags.domain.Tag;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.velocity.VelocityManager;
import edu.wustl.query.bizlogic.QueryTagBizLogic;
import edu.wustl.query.util.global.AQConstants;

public class TagGridInItAction extends Action
{

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		QueryTagBizLogic queryBizLogic = new QueryTagBizLogic();
		SessionDataBean sessionBean = (SessionDataBean)request.getSession().getAttribute(Constants.SESSION_DATA);
		List<Tag> tagList = queryBizLogic.getTagList(sessionBean);
		String responseString = VelocityManager.getInstance().evaluate(tagList,
				"privilegeGridTemplate.vm");
		response.getWriter().write(responseString);
		response.setContentType("text/xml");
		return null;
	}

}