package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTableProcessorNode<T>
{
    protected List<AbstractTableProcessorNode<T>> children = new ArrayList<AbstractTableProcessorNode<T>>();

    public void addChild(AbstractTableProcessorNode<T> child)
    {
        children.add(child);
    }

    public void addChildren(List<? extends AbstractTableProcessorNode<T>> newChildren)
    {
        children.addAll(newChildren);
    }

    public abstract Table<T> getTable(T emptyCell);
}
