package net.azyobuzi.saostar.util;

import java.util.HashSet;

import android.os.Handler;

public class Notificator<TEventArgs extends EventArgs>
{
    private final HashSet<Action2<Object, TEventArgs>> handlers = new HashSet<Action2<Object, TEventArgs>>();

    public Handler scheduler;

    public void add(final Action2<Object, TEventArgs> handler)
    {
        handlers.add(handler);
    }

    public void remove(final Action2<Object, TEventArgs> handler)
    {
        handlers.remove(handler);
    }

    public void raise(final Object sender, final TEventArgs e)
    {
        if (scheduler == null)
        {
            raise(sender, e);
        }
        else
        {
            scheduler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    _raise(sender, e);
                }
            });
        }
    }

    private void _raise(final Object sender, final TEventArgs e)
    {
        for (final Action2<Object, TEventArgs> handler : handlers)
            handler.invoke(sender, e);
    }
}
