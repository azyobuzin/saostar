package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.StringUtil;
import net.azyobuzi.azyotter.saostar.linq.Enumerable;
import net.azyobuzi.azyotter.saostar.services.TimelineReceiveService;
import net.azyobuzi.azyotter.saostar.system.Action;
import net.azyobuzi.azyotter.saostar.system.Func2;

import android.content.SharedPreferences.Editor;
import android.os.Handler;

public class Accounts {
	private static ArrayList<Account> list = null;
	private static final Object lockObj = new Object();
	private static final Handler h = new Handler();

	private static void loadAccounts() { //synchronized内から呼ぶ
		list = new ArrayList<Account>();

		String[] accountsStr = ContextAccess.getDefaultSharedPreferences().getString("twitterAccounts", "").split(",");
		for (String id : accountsStr) {
			if (!StringUtil.isNullOrEmpty(id)) {
				Account re = new Account(Long.valueOf(id));
				list.add(re);
				TimelineReceiveService.addAccount(re);
			}
		}
	}

	public static Enumerable<Account> getAllAccounts() {
		synchronized (lockObj) {
			if (list == null) loadAccounts();

			return Enumerable.from(list.toArray()).cast();
		}
	}

	public static Account get(final long id) {
		return getAllAccounts().where(new Func2<Account, Integer, Boolean>() {
			@Override
			public Boolean invoke(Account arg0, Integer arg1) {
				return arg0.getId() == id;
			}
		}).firstOrDefault(null);
	}

	public static int getAccountsCount() {
		synchronized (lockObj) {
			if (list == null) loadAccounts();

			return list.size();
		}
	}

	public static void add(final Account newAccount) {
		synchronized (lockObj) {
			if (list == null) loadAccounts();
		}

		h.post(new Runnable() {
			@Override
			public void run() {
				synchronized (lockObj) {
					list.add(newAccount);

					for (Action handler : accountsChangedHandler) {
						handler.invoke();
					}

					save();

					TimelineReceiveService.addAccount(newAccount);
				}
			}
		});
	}

	public static void remove(final Account account) {
		synchronized (lockObj) {
			if (list == null) loadAccounts();
		}

		h.post(new Runnable() {
			@Override
			public void run() {
				synchronized (lockObj) {
					TimelineReceiveService.removeAccount(account);

					list.remove(account);

					for (Action handler : accountsChangedHandler) {
						handler.invoke();
					}

					save();
				}
			}
		});
	}

	public static void sort(final int from, final int to) {
		synchronized (lockObj) {
			if (list == null) loadAccounts();
		}

		h.post(new Runnable() {
			@Override
			public void run() {
				synchronized (lockObj) {
					Account account = list.remove(from);
					list.add(to, account);

					for (Action handler : accountsChangedHandler) {
						handler.invoke();
					}

					save();
				}
			}
		});
	}

	public static int indexOf(long id) {
		Account a = get(id);

		synchronized (lockObj) {
			return list.indexOf(a);
		}
	}

	public static final ArrayList<Action> accountsChangedHandler = new ArrayList<Action>();

	public static void save() {
		ContextAccess.getDefaultSharedPreferences().edit().putString("twitterAccounts", StringUtil.join(",",
			getAllAccounts().select(new Func2<Account, Integer, CharSequence>() {
				@Override
				public CharSequence invoke(Account arg0, Integer arg1) {
					return String.valueOf(arg0.getId());
				}
			})
		))
		.apply();
	}

	public static Account getSelectedAccount() {
		long selectedId = ContextAccess.getDefaultSharedPreferences().getLong("selectedAccount", -1);

		Account re = get(selectedId);
		if (re == null) {
			re = getAllAccounts().firstOrDefault(null);
		}

		return re;
	}

	public static void setSelectedAccount(Account value) {
		Editor ed = ContextAccess.getDefaultSharedPreferences().edit();
		ed.putLong("selectedAccount", value != null ? value.getId() : 0);
		ed.apply();

		for (Action handler : selectedAccountChangedHandler) {
			handler.invoke();
		}
	}

	public static final ArrayList<Action> selectedAccountChangedHandler = new ArrayList<Action>();
}
