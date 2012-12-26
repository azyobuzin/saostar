package net.azyobuzi.saostar.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public abstract class Enumerable<T>
{
    public static <T> Enumerable<T> empty()
    {
        return new Enumerable<T>()
        {
            @Override
            public Enumerator<T> getEnumerator()
            {
                return new Enumerator<T>()
                {
                    @Override
                    public boolean moveNext()
                    {
                        return false;
                    }

                    @Override
                    public T getCurrent()
                    {
                        return null;
                    }
                };
            }
        };
    }

    public static <T> Enumerable<T> from(final Func<Enumerator<T>> generator)
    {
        return new Enumerable<T>()
        {
            @Override
            public Enumerator<T> getEnumerator()
            {
                return generator.invoke();
            }
        };
    }

    public static <T> Enumerable<T> from(final Iterable<T> source)
    {
        if (source == null) return empty();

        return from(new Func<Enumerator<T>>()
        {
            @Override
            public Enumerator<T> invoke()
            {
                return new Enumerator<T>()
                {
                    private final Iterator<T> itr = source.iterator();
                    private T current;

                    @Override
                    public boolean moveNext()
                    {
                        if (itr.hasNext())
                        {
                            current = itr.next();
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }

                    @Override
                    public T getCurrent()
                    {
                        return current;
                    }
                };
            }
        });
    }

    public static <T> Enumerable<T> from(final T[] source)
    {
        if (source == null) return empty();

        return from(new Func<Enumerator<T>>()
        {
            @Override
            public Enumerator<T> invoke()
            {
                return new Enumerator<T>()
                {
                    private int index = -1;
                    private T current;

                    @Override
                    public boolean moveNext()
                    {
                        index++;

                        if (source.length > index)
                        {
                            current = source[index];
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }

                    @Override
                    public T getCurrent()
                    {
                        return current;
                    }
                };
            }
        });
    }

    public static <T> Enumerable<T> oneElement(final T value)
    {
        return from(new Func<Enumerator<T>>()
        {
            @Override
            public Enumerator<T> invoke()
            {
                return new Enumerator<T>()
                {
                    private boolean returned = false;

                    @Override
                    public boolean moveNext()
                    {
                        if (!returned)
                        {
                            returned = true;
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }

                    @Override
                    public T getCurrent()
                    {
                        return value;
                    }
                };
            }
        });
    }

    public abstract Enumerator<T> getEnumerator();

    public void forEach(final Action2<T, Integer> action)
    {
        final Enumerator<T> enumerator = getEnumerator();
        int index = 0;
        while (enumerator.moveNext())
        {
            action.invoke(enumerator.getCurrent(), index++);
        }
    }

    public ArrayList<T> toArrayList()
    {
        final ArrayList<T> re = new ArrayList<T>();
        forEach(new Action2<T, Integer>()
        {
            @Override
            public void invoke(final T arg0, final Integer arg1)
            {
                re.add(arg0);
            }
        });
        return re;
    }

    public <TResult> Enumerable<TResult> select(final Func2<T, Integer, TResult> selector)
    {
        return from(new Func<Enumerator<TResult>>()
        {
            @Override
            public Enumerator<TResult> invoke()
            {
                return new Enumerator<TResult>()
                {
                    private final Enumerator<T> source = Enumerable.this.getEnumerator();
                    private TResult current;
                    private int index = 0;

                    @Override
                    public boolean moveNext()
                    {
                        if (source.moveNext())
                        {
                            current = selector.invoke(source.getCurrent(), index++);
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }

                    @Override
                    public TResult getCurrent()
                    {
                        return current;
                    }
                };
            }
        });
    }

    public Enumerable<T> where(final Func2<T, Integer, Boolean> predicate)
    {
        return from(new Func<Enumerator<T>>()
        {
            @Override
            public Enumerator<T> invoke()
            {
                return new Enumerator<T>()
                {
                    private final Enumerator<T> source = getEnumerator();
                    private T current;
                    private int index = 0;

                    @Override
                    public boolean moveNext()
                    {
                        if (source.moveNext())
                        {
                            final boolean result = predicate.invoke(source.getCurrent(), index++);
                            if (result)
                            {
                                current = source.getCurrent();
                                return true;
                            }
                            else
                            {
                                return moveNext();
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }

                    @Override
                    public T getCurrent()
                    {
                        return current;
                    }
                };
            }
        });
    }

    public <TResult> Enumerable<TResult> cast()
    {
        return select(new Func2<T, Integer, TResult>()
        {
            @SuppressWarnings("unchecked")
            @Override
            public TResult invoke(final T arg0, final Integer arg1)
            {
                return (TResult)arg0;
            }
        });
    }

    public T firstOrDefault(final T defaultValue)
    {
        T result = defaultValue;

        final Enumerator<T> source = getEnumerator();
        if (source.moveNext()) result = source.getCurrent();

        return result;
    }

    public T lastOrDefault(final T defaultValue)
    {
        T result = defaultValue;

        final Enumerator<T> source = getEnumerator();
        while (source.moveNext())
        {
            result = source.getCurrent();
        }

        return result;
    }

    public Enumerable<T> concat(final Enumerable<? extends T> second)
    {
        return from(new Func<Enumerator<T>>()
        {
            @Override
            public Enumerator<T> invoke()
            {
                return new Enumerator<T>()
                {
                    private T current;

                    private final Enumerator<T> firstEnumerator = getEnumerator();
                    private final Enumerator<? extends T> secondEnumerator = second.getEnumerator();

                    @Override
                    public boolean moveNext()
                    {
                        if (firstEnumerator.moveNext())
                        {
                            current = firstEnumerator.getCurrent();
                            return true;
                        }
                        else if (secondEnumerator.moveNext())
                        {
                            current = secondEnumerator.getCurrent();
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public T getCurrent()
                    {
                        return current;
                    }
                };
            }
        });
    }

    public T elementAtOrDefault(final int index, final T defaultValue)
    {
        int i = 0;
        final Enumerator<T> source = getEnumerator();
        while (source.moveNext())
        {
            if (i++ == index) return source.getCurrent();
        }

        return defaultValue;
    }

    public Enumerable<T> union(final Enumerable<? extends T> second)
    {
        return from(new Func<Enumerator<T>>()
        {
            @Override
            public Enumerator<T> invoke()
            {
                return new Enumerator<T>()
                {
                    private T current;
                    private final HashSet<T> returned = new HashSet<T>();

                    private final Enumerator<T> firstEnumerator = getEnumerator();
                    private final Enumerator<? extends T> secondEnumerator = second.getEnumerator();

                    @Override
                    public boolean moveNext()
                    {
                        if (firstEnumerator.moveNext())
                        {
                            current = firstEnumerator.getCurrent();
                            return returned.add(current) ? true : moveNext();
                        }
                        else if (secondEnumerator.moveNext())
                        {
                            current = secondEnumerator.getCurrent();
                            return returned.add(current) ? true : moveNext();
                        }

                        return false;
                    }

                    @Override
                    public T getCurrent()
                    {
                        return current;
                    }
                };
            }
        });
    }

    public Enumerable<T> distinct()
    {
        return from(new Func<Enumerator<T>>()
        {
            @Override
            public Enumerator<T> invoke()
            {
                return new Enumerator<T>()
                {
                    private T current;
                    private final HashSet<T> returned = new HashSet<T>();

                    private final Enumerator<T> source = getEnumerator();

                    @Override
                    public boolean moveNext()
                    {
                        if (source.moveNext())
                        {
                            current = source.getCurrent();
                            return returned.add(current) ? true : moveNext();
                        }
                        else
                        {
                            return false;
                        }
                    }

                    @Override
                    public T getCurrent()
                    {
                        return current;
                    }
                };
            }
        });
    }

    public Enumerable<T> distinct(final Func2<T, T, Boolean> compareEquality)
    {
        return from(new Func<Enumerator<T>>()
        {
            @Override
            public Enumerator<T> invoke()
            {
                return new Enumerator<T>()
                {
                    private T current;
                    private final HashSet<T> returned = new HashSet<T>();

                    private final Enumerator<T> source = getEnumerator();

                    @Override
                    public boolean moveNext()
                    {
                        if (source.moveNext())
                        {
                            current = source.getCurrent();
                            final Enumerable<T> equal = from(returned).where(new Func2<T, Integer, Boolean>()
                            {
                                @Override
                                public Boolean invoke(final T arg0, final Integer arg1)
                                {
                                    return compareEquality.invoke(current, arg0);
                                }
                            });
                            if (equal.firstOrDefault(null) == null)
                            {
                                returned.add(current);
                                return true;
                            }
                            else
                            {
                                return moveNext();
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }

                    @Override
                    public T getCurrent()
                    {
                        return current;
                    }
                };
            }
        });
    }
}
