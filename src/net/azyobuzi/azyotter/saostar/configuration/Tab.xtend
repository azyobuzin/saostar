package net.azyobuzi.azyotter.saostar.configuration

import java.io.EOFException
import java.util.ArrayList

import net.azyobuzi.azyotter.saostar.ContextAccess
import net.azyobuzi.azyotter.saostar.R
import net.azyobuzi.azyotter.saostar.d_aqa.Invokable
import net.azyobuzi.azyotter.saostar.d_aqa.Reader
import net.azyobuzi.azyotter.saostar.system.Action1
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem

class Tab {
	new() {
		try {
			filterExpr = Reader.read(filter)
		} catch (Exception ex)
		{
			ex.printStackTrace()
		}
	}

	private String _name = "NewTab"
	val nameChangedHandler = new ArrayList<Action1<Tab>>()

	def getName() {
		_name
	}

	def setName(String value) {
		_name = value

		for (handler : nameChangedHandler) {
			handler.invoke(this)
		}

		Tabs.save()
	}

	private String _filter = "false"
	private Invokable filterExpr
	val filterChangedHandler = new ArrayList<Action1<Tab>>()

	def getFilter() {
		return _filter;
	}

	def setFilter(String value) {
		//正しく動くかテスト
		val tmp = Reader.read(value)
		if (tmp.getResultType() != Invokable.TYPE_BOOLEAN)
			throw new IllegalArgumentException(ContextAccess.getString(R.string.return_type_is_not_boolean))
		tmp.invoke(TimelineItem.getDummyTweet())

		filterExpr = tmp
		_filter = value

		for (handler : filterChangedHandler) {
			handler.invoke(this)
		}

		Tabs.save()
	}

	def getFilterExpression() {
		filterExpr
	}

	//TODO:通知系
}
