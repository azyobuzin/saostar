package net.azyobuzi.saostar.model.configuration;

import net.azyobuzi.saostar.App;
import net.azyobuzi.saostar.util.Action2;
import net.azyobuzi.saostar.util.Enumerable;
import net.azyobuzi.saostar.util.EventArgs;
import net.azyobuzi.saostar.util.Func2;
import net.azyobuzi.saostar.util.ListChangedEventArgs;
import net.azyobuzi.saostar.util.Notificator;
import net.azyobuzi.saostar.util.ObservableList;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class Accounts
{
    private static ObservableList<Account> list;

    public static ObservableList<Account> getList()
    {
        list.setScheduler(App.instance.h); //App インスタンスが作成されてないといけない
        return list;
    }

    static
    {
        list = new ObservableList<Account>();

        Enumerable.from(PreferenceManager.getDefaultSharedPreferences(App.instance).getString("twitterAccounts", "").split(",")).where(new Func2<String, Integer, Boolean>()
        {
            @Override
            public Boolean invoke(final String s, final Integer i)
            {
                return !Strings.isNullOrEmpty(s);
            }
        }).forEach(new Action2<String, Integer>()
        {
            @Override
            public void invoke(final String s, final Integer i)
            {
                list.add(new Account(Long.valueOf(s)));
            }
        });

        list.listChangedEvent.add(new Action2<Object, ListChangedEventArgs<Account>>()
        {
            @Override
            public void invoke(final Object sender, final ListChangedEventArgs<Account> e)
            {
                // 変更されるたびに保存
                PreferenceManager.getDefaultSharedPreferences(App.instance).edit()
                        .putString("twitterAccounts", Joiner.on(",").join(Lists.transform(list, new Function<Account, String>()
                        {
                            @Override
                            public String apply(final Account a)
                            {
                                return String.valueOf(a.getId());
                            }
                        })));
            }
        });
    }

    public static final Notificator<EventArgs> selectedAccountChangedEvent = new Notificator<EventArgs>();

    public static Account fromId(final long id)
    {
        return Enumerable.from(list).where(new Func2<Account, Integer, Boolean>()
        {
            @Override
            public Boolean invoke(final Account a, final Integer i)
            {
                return a.getId() == id;
            }
        }).firstOrDefault(null);
    }

    public static Account getSelectedAccount()
    {
        final long selectedId = PreferenceManager
                .getDefaultSharedPreferences(App.instance).getLong("selectedAccount", -1);

        Account re = fromId(selectedId);
        if (re == null)
        {
            re = Enumerable.from(list).firstOrDefault(null);
        }

        return re;
    }

    public static void setSelectedAccount(final Account value)
    {
        final SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(App.instance).edit();
        ed.putLong("selectedAccount", value != null ? value.getId() : 0);
        ed.apply();

        selectedAccountChangedEvent.raise(null, EventArgs.empty);
    }
}
