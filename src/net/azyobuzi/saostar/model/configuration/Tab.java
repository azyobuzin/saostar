package net.azyobuzi.saostar.model.configuration;

import java.util.Random;

import net.azyobuzi.saostar.App;
import net.azyobuzi.saostar.util.Action2;
import net.azyobuzi.saostar.util.EventArgs;
import net.azyobuzi.saostar.util.ListChangedEventArgs;
import net.azyobuzi.saostar.util.Notificator;
import net.azyobuzi.saostar.util.ObservableList;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.base.Joiner;

public class Tab // TODO:タブ削除時に SharedPreference をクリア、イベントリスナー解除
{
    public static final int HOME = 0;

    public Tab(final int id)
    {
        this.id = id;
        sp = App.instance.getSharedPreferences("tab_" + getId(), Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        name = sp.getString("name", "NewTab");
        type = sp.getInt("type", HOME);
        useUserStreams = sp.getBoolean("useUserStreams", true);
        for (final String accountId : sp.getString("accounts", "").split(","))
            accounts.add(Long.valueOf(accountId));
        useAllAccounts = sp.getBoolean("useAllAccounts", true);
        nameChangedEvent.scheduler = typeChangedEvent.scheduler = useUserStreamsChangedEvent.scheduler = useAllAccountsChangedEvent.scheduler = App.instance.h;
        accounts.setScheduler(App.instance.h);
        accounts.listChangedEvent.add(accountsChangedHandler);
    }

    private final Action2<Object, ListChangedEventArgs<Long>> accountsChangedHandler = new Action2<Object, ListChangedEventArgs<Long>>()
    {
        @Override
        public void invoke(final Object arg0, final ListChangedEventArgs<Long> arg1)
        {
            sp.edit().putString("accounts", Joiner.on(",").join(accounts)).apply();
        }
    };

    private static final Random rnd = new Random();

    public static Tab newTab()
    {
        return new Tab(rnd.nextInt());
    }

    private final SharedPreferences sp;

    private final long id;

    public long getId()
    {
        return id;
    }

    private String name;
    public final Notificator<EventArgs> nameChangedEvent = new Notificator<EventArgs>();

    public String getName()
    {
        return name;
    }

    public void setName(final String value)
    {
        if (!name.equals(value))
        {
            name = value;
            sp.edit().putString("name", value).apply();
            nameChangedEvent.raise(this, EventArgs.empty);
        }
    }

    private int type;
    public final Notificator<EventArgs> typeChangedEvent = new Notificator<EventArgs>();

    public int getType()
    {
        return type;
    }

    public void setType(final int value)
    {
        if (type != value)
        {
            type = value;
            sp.edit().putInt("type", value).apply();
            typeChangedEvent.raise(this, EventArgs.empty);
        }
    }

    private boolean useUserStreams;
    public final Notificator<EventArgs> useUserStreamsChangedEvent = new Notificator<EventArgs>();

    public boolean getUseUserStreams()
    {
        return useUserStreams;
    }

    public void setUseUserStreams(final boolean value)
    {
        if (useUserStreams != value)
        {
            useUserStreams = value;
            sp.edit().putBoolean("useUserStreams", value);
            useUserStreamsChangedEvent.raise(this, EventArgs.empty);
        }
    }

    public final ObservableList<Long> accounts = new ObservableList<Long>();

    private boolean useAllAccounts;
    public final Notificator<EventArgs> useAllAccountsChangedEvent = new Notificator<EventArgs>();

    public boolean getUseAllAccounts()
    {
        return useAllAccounts;
    }

    public void setUseAllAccounts(final boolean value)
    {
        if (useAllAccounts != value)
        {
            useAllAccounts = value;
            sp.edit().putBoolean("useAllAccounts", value);
            useAllAccountsChangedEvent.raise(this, EventArgs.empty);
        }
    }
}
