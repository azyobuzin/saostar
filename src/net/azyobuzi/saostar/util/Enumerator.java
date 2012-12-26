package net.azyobuzi.saostar.util;

public interface Enumerator<T>
{
    boolean moveNext();

    T getCurrent();
}
