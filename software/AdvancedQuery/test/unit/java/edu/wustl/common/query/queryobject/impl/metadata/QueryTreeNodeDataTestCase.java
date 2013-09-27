package edu.wustl.common.query.queryobject.impl.metadata;

import junit.framework.TestCase;
import edu.wustl.common.query.queryobject.impl.QueryTreeNodeData;

public class QueryTreeNodeDataTestCase extends TestCase
{
	public void testAllMethods()
	{
		QueryTreeNodeData treeNodeData = new QueryTreeNodeData();
		String displayName = "Participant";
		String identifier = "2";
		String objectName = "edu.wustl.clinportal.domain.ClinicalStudyRegistration";
		String parentIdentifier = "1";
		String parentObjectName = "edu.wustl.clinportal.domain.Participant";
		String toolTipText = "Clinical Study Registration";
		String combinedParentIdentifier = "1";
		String combinedParentObjectName = "ABC";
		String rootName = "Participant";

		treeNodeData.setCombinedParentIdentifier(combinedParentIdentifier);
		treeNodeData.setCombinedParentObjectName(combinedParentObjectName);
		treeNodeData.setDisplayName(displayName);
		treeNodeData.setIdentifier(identifier);
		treeNodeData.setObjectName(objectName);
		treeNodeData.setParentIdentifier(parentIdentifier);
		treeNodeData.setParentObjectName(parentObjectName);
		treeNodeData.setToolTipText(toolTipText);
		treeNodeData.initialiseRoot(rootName);

		treeNodeData.getCombinedParentIdentifier();
		treeNodeData.getCombinedParentObjectName();
		treeNodeData.getDisplayName();
		treeNodeData.getIdentifier();
		treeNodeData.getObjectName();
		treeNodeData.getParentIdentifier();
		treeNodeData.getParentObjectName();
		treeNodeData.getParentTreeNode();
		treeNodeData.getToolTipText();

		treeNodeData.hasEqualParents(treeNodeData);
		treeNodeData.isChildOf(treeNodeData);
	}
}
