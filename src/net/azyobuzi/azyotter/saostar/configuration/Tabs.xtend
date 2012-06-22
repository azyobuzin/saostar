package net.azyobuzi.azyotter.saostar.configuration

import java.util.ArrayList

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import net.azyobuzi.azyotter.saostar.ContextAccess
import net.azyobuzi.azyotter.saostar.linq.Enumerable
import net.azyobuzi.azyotter.saostar.system.Action1
import net.azyobuzi.azyotter.saostar.system.Action2
import net.azyobuzi.azyotter.saostar.system.Action3

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

import android.content.Context

class Tabs {
	private static val fileName = "tabs.xml"

	private static ArrayList<Tab> list = null

	def private static loadTabs() {
		list = new ArrayList<Tab>()

		try {
			val root = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(ContextAccess.openFileInput(fileName))
				.getDocumentElement()

			val tabs = root.getElementsByTagName("tab")
			for (int i = 0; i < tabs.getLength(); i++) {
				val elm = (Element)tabs.item(i)
				val re = new Tab()
				re.setName(elm.getElementsByTagName("name").item(0).getTextContent())
				re.setFilter(elm.getElementsByTagName("filter").item(0).getTextContent())
				list.add(re)
			}
		} catch (Exception ex) {
			ex.printStackTrace()

			//初期化
			val homeTab = new Tab()
			homeTab.setName("Home")
			try {
				homeTab.setFilter("prop:isHomeTweet")
			} catch (Exception e) {
				e.printStackTrace()
			}
			add(homeTab)
		}
	}

	def static getAllTabs() {
		if (list == null) loadTabs()
		Enumerable.from(list.toArray()).cast()
	}

	def static getTabsCount() {
		if (list == null) loadTabs()
		list.size()
	}

	def static get(int index) {
		if (list == null) loadTabs()
		list.get(index)
	}

	def static indexOf(Tab tab) {
		if (list == null) loadTabs()
		list.indexOf(tab)
	}

	def static add(Tab tab) {
		if (list == null) loadTabs()
		list.add(tab)

		for (handler : addedHandler) {
			handler.invoke(tab)
		}

		save()
	}

	static val addedHandler = new ArrayList<Action1<Tab>>();

	def static remove(Tab tab) {
		if (list == null) loadTabs()
		list.remove(tab)

		for (handler : removedHandler) {
			handler.invoke(tab)
		}

		save()
	}

	static val removedHandler = new ArrayList<Action1<Tab>>();

	def static move(int from, int to) {
		if (list == null) loadTabs()
		val tab = list.remove(from)
		list.add(to, tab)

		for (handler : movedHandler) {
			handler.invoke(tab, from, to)
		}

		save()
	}

	static val movedHandler = new ArrayList<Action3<Tab, Integer, Integer>>();

	def static save() {
		try {
			val doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.newDocument()

			val root = doc.createElement("tabs")

			getAllTabs().forEach(new Action2<Tab, Integer>() {
				override invoke(Tab arg0, Integer arg1) {
					val elm = doc.createElement("tab")

					val child = doc.createElement("name")
					child.appendChild(doc.createTextNode(arg0.getName()))
					elm.appendChild(child)

					child = doc.createElement("filter")
					child.appendChild(doc.createTextNode(arg0.getFilter()))
					elm.appendChild(child)

					root.appendChild(elm)
				}
			})

			doc.appendChild(root)

			TransformerFactory.newInstance()
				.newTransformer()
				.transform(new DOMSource(doc), new StreamResult(ContextAccess.openFileOutput(fileName, Context.MODE_PRIVATE)))
		} catch (Exception ex) {
			ex.printStackTrace() //エラー起きたらスイマセーンｗｗｗｗｗ
		}
	}
}
