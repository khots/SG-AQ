package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

public class TableImpl<T> implements Table<T>
{
    @SuppressWarnings("serial")
    private class Row extends ArrayList<T>
    {
        Row()
        {
            super();
            pad();
        }

        Row(List<? extends T> row)
        {
            super(row);
            pad();
        }

        void pad()
        {
        	int noOfCols = numCols - size();
            for (int i = 0; i < noOfCols; i++)
            {
                add((T) EMPTY_CELL);
            }
        }
    }

    public TableImpl(TableImpl<? extends T> table)
    {
        EMPTY_CELL = table.EMPTY_CELL;
        numCols = table.numCols;
        appendRows(table);
    }

    public TableImpl(T emptyCell)
    {
        EMPTY_CELL = emptyCell;
    }

    private List<Row> rows = new ArrayList<Row>();

    private int numCols = 0;

    private final T EMPTY_CELL;

    public void addColumn(List<? extends T> column)
    {
        addColumn(numCols, column);
    }

    private void checkNumRows(int reqdSz)
    {
    	int numNewRows = reqdSz - rows.size();
        for (int i = 0; i < numNewRows; i++)
        {
            rows.add(new Row());
        }
    }

    public void addColumn(int ctr, List<? extends T> column)
    {
        int colSz = column.size();
        checkNumRows(colSz);
        for (int j = 0; j < colSz; j++) {
            rows.get(j).add(ctr, column.get(j));
        }
        numCols++;
    }

    public void addRow(List<? extends T> row)
    {
        addRow(rows.size(), row);
    }

    private void checkNumCols(int reqdSz)
    {
        if (reqdSz > numCols)
        {
            numCols = reqdSz;
            for (Row row : rows)
            {
                row.pad();
            }
        }
    }

    public void addRow(int counter, List<? extends T> row)
    {
        checkNumCols(row.size());
        rows.add(counter, new Row(row));
    }

    public void set(int ctr1, int ctr2, T cell)
    {
        rows.get(ctr1).set(ctr2, cell);
    }

    public void clear()
    {
        numCols = 0;
        rows.clear();
    }

    public void appendColumns(Table<? extends T> table)
    {
        for (int cnt = 0, nCols = table.numColumns(); cnt < nCols; cnt++)
        {
            addColumn(table.column(cnt));
        }
    }

    public void appendRows(Table<? extends T> table)
    {
        for (int i = 0, nRows = table.numRows(); i < nRows; i++)
        {
            addRow(table.row(i));
        }
    }

    public void crossProduct(Table<? extends T> table)
    {
        if (table.isEmpty())
        {
            return;
        }
        if(isEmpty())
        {
        	appendRows(table);
            return;
        }
        TableImpl<T> table1 = new TableImpl<T>(this);
        clear();

        for (int i = 0, rows1 = table1.numRows(); i < rows1; i++)
        {
            for (int j = 0, rows2 = table.numRows(); j < rows2; j++)
            {
                Row row = new Row(table1.row(i));
                row.addAll(table.row(j));
                rows.add(row);
            }
        }
        numCols = table1.numColumns() + table.numColumns();
    }

    public T get(int ctr1, int ctr2)
    {
        return rows.get(ctr1).get(ctr2);
    }

    public List<T> column(int cnt)
    {
        List<T> res = new ArrayList<T>(rows.size());
        for (Row row : rows)
        {
            res.add(row.get(cnt));
        }
        return res;
    }

    public int numColumns()
    {
        return numCols;
    }

    public int numRows()
    {
        return rows.size();
    }

    public boolean isEmpty()
    {
        return numCols == 0;
    }

    public List<T> row(int rowNum)
    {
        return new ArrayList<T>(rows.get(rowNum));
    }
}
