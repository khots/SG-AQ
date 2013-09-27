package edu.wustl.common.query.queryobject.impl;

public class RowAppenderProcNode<T> extends AbstractTableProcessorNode<T> {

    @Override
    public Table<T> getTable(T emptyCell)
    {
        Table<T> res = new TableImpl<T>(emptyCell);

        for (AbstractTableProcessorNode<T> child : children)
        {
            res.appendRows(child.getTable(emptyCell));
        }

        return res;
    }

}
