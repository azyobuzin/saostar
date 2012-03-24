package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.linq.Enumerable;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.system.Action2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;

public class Tabs {
	private static final String fileName = "tabs.xml";

	private static ArrayList<Tab> list = null;

	private static void loadTabs() {
		list = new ArrayList<Tab>();

		try {
			Element root = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(ContextAccess.openFileInput(fileName))
				.getDocumentElement();

			NodeList tabs = root.getElementsByTagName("tab");
			for (int i = 0; i < tabs.getLength(); i++) {
				Element elm = (Element)tabs.item(i);
				Tab re = new Tab();
				re.setName(elm.getElementsByTagName("name").item(0).getTextContent());
				re.setFilter(elm.getElementsByTagName("filter").item(0).getTextContent());
				list.add(re);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			//初期化
			Tab homeTab = new Tab();
			homeTab.setName("Home");
			homeTab.setFilter("item.isHomeTweet");
			add(homeTab);
		}
	}

	public static Enumerable<Tab> getAllTabs() {
		if (list == null) loadTabs();
		return Enumerable.from(list.toArray()).cast();
	}

	public static int getTabsCount() {
		if (list == null) loadTabs();
		return list.size();
	}

	public static Tab get(int index) {
		if (list == null) loadTabs();
		return list.get(index);
	}
	
	public static int indexOf(Tab tab) {
		if (list == null) loadTabs();
		return list.indexOf(tab);
	}

	public static void add(Tab tab) {
		list.add(tab);

		for (Action1<Tab> handler : addedHandler) {
			handler.invoke(tab);
		}

		save();
	}

	public static final ArrayList<Action1<Tab>> addedHandler = new ArrayList<Action1<Tab>>();

	public static void remove(Tab tab) {
		list.remove(tab);

		for (Action1<Tab> handler : removedHandler) {
			handler.invoke(tab);
		}

		save();
	}

	public static final ArrayList<Action1<Tab>> removedHandler = new ArrayList<Action1<Tab>>();

	public static void save() {
		try {
			final Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.newDocument();

				final Element root = doc.createElement("tabs");

				getAllTabs().forEach(new Action2<Tab, Integer>() {
					@Override
					public void invoke(Tab arg0, Integer arg1) {
						Element elm = doc.createElement("tab");

						Element child = doc.createElement("name");
						child.appendChild(doc.createTextNode(arg0.getName()));
						elm.appendChild(child);

						child = doc.createElement("filter");
						child.appendChild(doc.createTextNode(arg0.getFilter()));
						elm.appendChild(child);

						root.appendChild(elm);
					}
				});

				doc.appendChild(root);

				TransformerFactory.newInstance()
					.newTransformer()
					.transform(new DOMSource(doc), new StreamResult(ContextAccess.openFileOutput(fileName, Context.MODE_PRIVATE)));
		} catch (Exception ex) {
			ex.printStackTrace(); //エラー起きたらスイマセーンｗｗｗｗｗ
		}
	}
}
