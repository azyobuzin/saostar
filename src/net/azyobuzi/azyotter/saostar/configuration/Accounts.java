package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.linq.Enumerable;
import net.azyobuzi.azyotter.saostar.system.Action;
import net.azyobuzi.azyotter.saostar.system.Action2;
import net.azyobuzi.azyotter.saostar.system.Func2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

public class Accounts {
	private static final String fileName = "accounts.xml";

	private static ArrayList<Account> list = null;
	private static final Object lockObj = new Object();
	private static final Handler h = new Handler();

	private static void loadAccounts() { //synchronized内から呼ぶ
		list = new ArrayList<Account>();

		try {
			Element root = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(ContextAccess.openFileInput(fileName))
				.getDocumentElement();

			NodeList accounts = root.getElementsByTagName("account");
			for (int i = 0; i < accounts.getLength(); i++) {
				Element elm = (Element)accounts.item(i);
				Account re = new Account();
				re.id = Long.valueOf(elm.getElementsByTagName("id").item(0).getTextContent());
				re.screenName = elm.getElementsByTagName("screen_name").item(0).getTextContent();
				re.oauthToken = elm.getElementsByTagName("oauth_token").item(0).getTextContent();
				re.oauthTokenSecret = elm.getElementsByTagName("oauth_token_secret").item(0).getTextContent();
				re.setUseUserStream(Boolean.valueOf(elm.getElementsByTagName("use_user_stream").item(0).getTextContent()));
				list.add(re);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
				return arg0.id == id;
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
					list.remove(account);

					for (Action handler : accountsChangedHandler) {
						handler.invoke();
					}

					save();
				}
			}
		});
	}

	public static final ArrayList<Action> accountsChangedHandler = new ArrayList<Action>();

	public static void save() {
		try {
			final Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.newDocument();

			final Element root = doc.createElement("accounts");

			getAllAccounts().forEach(new Action2<Account, Integer>() {
				@Override
				public void invoke(Account arg0, Integer arg1) {
					Element elm = doc.createElement("account");

					Element child = doc.createElement("id");
					child.appendChild(doc.createTextNode(String.valueOf(arg0.id)));
					elm.appendChild(child);

					child = doc.createElement("screen_name");
					child.appendChild(doc.createTextNode(arg0.screenName));
					elm.appendChild(child);

					child = doc.createElement("oauth_token");
					child.appendChild(doc.createTextNode(arg0.oauthToken));
					elm.appendChild(child);

					child = doc.createElement("oauth_token_secret");
					child.appendChild(doc.createTextNode(arg0.oauthTokenSecret));
					elm.appendChild(child);

					child = doc.createElement("use_user_stream");
					child.appendChild(doc.createTextNode(String.valueOf(arg0.getUseUserStream())));
					elm.appendChild(child);

					root.appendChild(elm);
				}
			});

			doc.appendChild(root);

			TransformerFactory.newInstance()
				.newTransformer()
				.transform(new DOMSource(doc), new StreamResult(ContextAccess.openFileOutput(fileName, Context.MODE_PRIVATE)));
		} catch (Exception ex) {
			ex.printStackTrace(); //エラー？なにそれ？
		}
	}

	public static Account getSelectedAccount() {
		long selectedId = ContextAccess.getDefaultSharedPreferences().getLong("selectedAccount", 0);

		Account re = get(selectedId);
		if (re == null) {
			re = getAllAccounts().firstOrDefault(null);
		}

		return re;
	}

	public static void setSelectedAccount(Account value) {
		Editor ed = ContextAccess.getDefaultSharedPreferences().edit();
		ed.putLong("selectedAccount", value != null ? value.id : 0);
		ed.apply();

		for (Action handler : selectedAccountChangedHandler) {
			handler.invoke();
		}
	}

	public static final ArrayList<Action> selectedAccountChangedHandler = new ArrayList<Action>();
}
