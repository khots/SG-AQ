package edu.wustl.common.query.queryobject.impl;


public class ColumnAppenderProcNode<T> extends AbstractTableProcessorNode<T>
{

    @Override
    public Table<T> getTable(T emptyCell)
    {
        final Table<T> res = new TableImpl<T>(emptyCell);

        for (AbstractTableProcessorNode<T> child : children)
        {
            res.appendColumns(child.getTable(emptyCell));
        }

        return res;
    }

}
