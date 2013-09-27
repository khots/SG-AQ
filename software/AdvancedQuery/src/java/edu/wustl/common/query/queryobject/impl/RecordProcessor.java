package edu.wustl.common.query.queryobject.impl;

import java.util.Map;

public class RecordProcessor
{
    public static class TreeCell
    {
        private QueryHeaderData queryHeaderData;

        private Map<OutputAssociationColumn, Object> rec;

        private TreeCell parent;

        public static final TreeCell EMPTY_CELL = new TreeCell();

        TreeCell(QueryHeaderData queryHeaderData, Map<OutputAssociationColumn, Object> rec)
        {
            this.queryHeaderData = queryHeaderData;
            this.rec = rec;
        }

        private TreeCell()
        {

        }

        public void setParentCell(TreeCell parent)
        {
            this.parent = parent;
        }

        public TreeCell getParentCell()
        {
            return parent;
        }

        public QueryHeaderData getQueryHeaderData()
        {
            return queryHeaderData;
        }

        public Map<OutputAssociationColumn, Object> getRec()
        {
            return rec;
        }

		@Override
        public String toString()
        {
            return queryHeaderData.getEntity().getName() + rec;
        }
    }
}
