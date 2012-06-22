package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList

import net.azyobuzi.azyotter.saostar.ContextAccess
import net.azyobuzi.azyotter.saostar.StringUtil
import net.azyobuzi.azyotter.saostar.linq.Enumerable
import net.azyobuzi.azyotter.saostar.services.TimelineReceiveService
import net.azyobuzi.azyotter.saostar.system.Action
import net.azyobuzi.azyotter.saostar.system.Func2

import android.content.SharedPreferences.Editor
import android.os.Handler

class Accounts {
	private static ArrayList<Account> list = null
	private static val lockObj = new Object()
	private static val h = new Handler()

	def private static loadAccounts() { //synchronized内から呼ぶ
		list = new ArrayList<Account>()

		val accountsStr = ContextAccess.getDefaultSharedPreferences().getString("twitterAccounts", "").split(",")
		for (String id : accountsStr) {
			if (!StringUtil.isNullOrEmpty(id)) {
				val re = new Account(Long.valueOf(id))
				list.add(re)
				TimelineReceiveService.addAccount(re)
			}
		}
	}

	def static getAllAccounts() {
		synchronized (lockObj) {
			if (list == null) loadAccounts()

			Enumerable.from(list.toArray()).cast()
		}
	}

	def static get(long id) {
		getAllAccounts().where(new Func2<Account, Integer, Boolean>() {
			override invoke(Account arg0, Integer arg1) {
				return arg0.getId() == id
			}
		}).firstOrDefault(null)
	}

	def static getAccountsCount() {
		synchronized (lockObj) {
			if (list == null) loadAccounts()

			list.size()
		}
	}

	def static add(final Account newAccount) {
		synchronized (lockObj) {
			if (list == null) loadAccounts()
		}

		h.post(new Runnable() {
			override run() {
				synchronized (lockObj) {
					list.add(newAccount)

					for (Action handler : accountsChangedHandler) {
						handler.invoke()
					}

					save()

					TimelineReceiveService.addAccount(newAccount)
				}
			}
		})
	}

	def static remove(Account account) {
		synchronized (lockObj) {
			if (list == null) loadAccounts()
		}

		h.post(new Runnable() {
			override run() {
				synchronized (lockObj) {
					TimelineReceiveService.removeAccount(account)

					list.remove(account)

					for (Action handler : accountsChangedHandler) {
						handler.invoke()
					}

					save()
				}
			}
		})
	}

	def static move(int from, int to) {
		synchronized (lockObj) {
			if (list == null) loadAccounts()
		}

		h.post(new Runnable() {
			override run() {
				synchronized (lockObj) {
					Account account = list.remove(from)
					list.add(to, account)

					for (Action handler : accountsChangedHandler) {
						handler.invoke()
					}

					save()
				}
			}
		})
	}

	def static indexOf(long id) {
		Account a = get(id);

		synchronized (lockObj) {
			list.indexOf(a)
		}
	}

	static val accountsChangedHandler = new ArrayList<Action>()

	def static save() {
		ContextAccess.getDefaultSharedPreferences().edit().putString("twitterAccounts", StringUtil.join(",",
			getAllAccounts().select(new Func2<Account, Integer, CharSequence>() {
				override invoke(Account arg0, Integer arg1) {
					return String.valueOf(arg0.getId())
				}
			})
		))
		.apply()
	}

	def static getSelectedAccount() {
		long selectedId = ContextAccess.getDefaultSharedPreferences().getLong("selectedAccount", -1)

		Account re = get(selectedId)
		if (re == null) {
			re = getAllAccounts().firstOrDefault(null)
		}

		re
	}

	def static setSelectedAccount(Account value) {
		Editor ed = ContextAccess.getDefaultSharedPreferences().edit()
		ed.putLong("selectedAccount", value != null ? value.getId() : 0)
		ed.apply()

		for (Action handler : selectedAccountChangedHandler) {
			handler.invoke()
		}
	}

	static val selectedAccountChangedHandler = new ArrayList<Action>()
}
