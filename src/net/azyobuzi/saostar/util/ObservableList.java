package net.azyobuzi.saostar.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import android.os.Handler;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class ObservableList<E> extends AbstractList<E>
{
    private final ArrayList<E> list = Lists.newArrayList();
    private final Object lock = new Object();

    public final Notificator<ListChangedEventArgs<E>> listChangedEvent = new Notificator<ListChangedEventArgs<E>>();

    @Override
    public void add(final int location, final E object)
    {
        synchronized (lock)
        {
            list.add(location, object);
        }

        listChangedEvent.raise(this, new ListChangedEventArgs<E>(
                ListChangedEventArgs.ADD,
                Optional.<E> absent(),
                Optional.fromNullable(object)));
    }

    @Override
    public void clear()
    {
        synchronized (lock)
        {
            list.clear();
        }

        listChangedEvent.raise(this, new ListChangedEventArgs<E>(
                ListChangedEventArgs.RESET,
                Optional.<E> absent(),
                Optional.<E> absent()));
    }

    @Override
    public E get(final int location)
    {
        synchronized (lock)
        {
            return list.get(location);
        }
    }

    @Override
    public int indexOf(final Object object)
    {
        synchronized (lock)
        {
            return list.indexOf(object);
        }
    }

    @Override
    public Iterator<E> iterator()
    {
        synchronized (lock)
        {
            return Lists.newArrayList(list).iterator();
        }
    }

    @Override
    public int lastIndexOf(final Object object)
    {
        synchronized (lock)
        {
            return list.lastIndexOf(object);
        }
    }

    @Override
    public ListIterator<E> listIterator(final int location)
    {
        synchronized (lock)
        {
            return Lists.newArrayList(list).listIterator(location);
        }
    }

    public void move(final int from, final int to)
    {
        E item;

        synchronized (lock)
        {
            item = list.remove(from);
            list.add(to, item);
        }

        listChangedEvent.raise(this, new ListChangedEventArgs<E>(
                ListChangedEventArgs.MOVE,
                Optional.fromNullable(item),
                Optional.fromNullable(item)));
    }

    @Override
    public E remove(final int location)
    {
        E resItem;

        synchronized (lock)
        {
            resItem = list.remove(location);
        }

        listChangedEvent.raise(this, new ListChangedEventArgs<E>(
                ListChangedEventArgs.REMOVE,
                Optional.fromNullable(resItem),
                Optional.<E> absent()));

        return resItem;
    }

    @Override
    public E set(final int location, final E object)
    {
        E resItem;

        synchronized (lock)
        {
            resItem = list.set(location, object);
        }

        listChangedEvent.raise(this, new ListChangedEventArgs<E>(
                ListChangedEventArgs.REPLACE,
                Optional.fromNullable(resItem),
                Optional.fromNullable(object)));

        return resItem;
    }

    @Override
    public int size()
    {
        synchronized (lock)
        {
            return list.size();
        }
    }

    public void setScheduler(final Handler scheduler)
    {
        listChangedEvent.scheduler = scheduler;
    }
}
