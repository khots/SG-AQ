package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

public class RecordProcessorNode<T> extends AbstractTableProcessorNode<T>
{

    private T cellContent;

    public RecordProcessorNode(T cellContent)
    {
        this.cellContent = cellContent;
    }

    public Table<T> getTable(T emptyCell)
    {
    	Table<T> res = new TableImpl<T>(emptyCell);

        for (AbstractTableProcessorNode<T> child : children)
        {
            res.crossProduct(child.getTable(emptyCell));
        }

        List<T> newCol = new ArrayList<T>();

        // add one row if empty
        if (res.numRows() == 0)
        {
            res.addRow(new ArrayList<T>());
        }

        for (int i = 0; i < res.numRows(); i++)
        {
            newCol.add(cellContent);
        }

        res.addColumn(0, newCol);
        return res;
    }
}
