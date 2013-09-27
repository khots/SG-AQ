package edu.wustl.common.query.queryobject.impl;

import java.util.List;

public interface Table<T>
{
    // creation/modification
    void set(int row, int column, T cell);

    void addRow(List<? extends T> row);

    void addRow(int rowNum, List<? extends T> row);

    void addColumn(List<? extends T> column);

    void addColumn(int colNum, List<? extends T> column);

    void clear();

    // merging tables
    void appendColumns(Table<? extends T> table);

    void appendRows(Table<? extends T> table);

    void crossProduct(Table<? extends T> table);

    // table properties
    int numRows();

    int numColumns();

    boolean isEmpty();

    // data retrieval
    T get(int ctr1, int ctr2);

    List<T> row(int rowNum);

    List<T> column(int colNum);

    // for blank cells
    Object EMPTY_CELL = new Object()
    {
        public String toString()
        {
            return "";
        };
    };
}
