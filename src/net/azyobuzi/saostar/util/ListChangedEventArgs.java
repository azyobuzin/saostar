package net.azyobuzi.saostar.util;

import com.google.common.base.Optional;

public class ListChangedEventArgs<E> extends EventArgs
{
    public static final int ADD = 0;
    public static final int REMOVE = 1;
    public static final int REPLACE = 2;
    public static final int MOVE = 3;
    public static final int RESET = 4;

    public ListChangedEventArgs(final int action, final Optional<E> oldItem, final Optional<E> newItem)
    {
        this.action = action;
        this.oldItem = oldItem;
        this.newItem = newItem;
    }

    private final int action;

    public int getAction()
    {
        return action;
    }

    private final Optional<E> oldItem;

    public Optional<E> getOldItem()
    {
        return oldItem;
    }

    private final Optional<E> newItem;

    public Optional<E> getNewItem()
    {
        return newItem;
    }
}
