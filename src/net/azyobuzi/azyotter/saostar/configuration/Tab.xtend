package net.azyobuzi.azyotter.saostar.configuration;

import java.io.EOFException;
import java.util.ArrayList;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.R;
import net.azyobuzi.azyotter.saostar.d_aqa.Invokable;
import net.azyobuzi.azyotter.saostar.d_aqa.Reader;
import net.azyobuzi.azyotter.saostar.system.Action1;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class Tab {
	public Tab() {
		try {
			filterExpr = Reader.read(filter);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private String name = "NewTab";
	public final ArrayList<Action1<Tab>> nameChangedHandler = new ArrayList<Action1<Tab>>();

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;

		for (Action1<Tab> handler : nameChangedHandler) {
			handler.invoke(this);
		}

		Tabs.save();
	}

	private String filter = "false";
	private Invokable filterExpr;
	public final ArrayList<Action1<Tab>> filterChangedHandler = new ArrayList<Action1<Tab>>();

	public String getFilter() {
		return filter;
	}

	public void setFilter(String value) throws EOFException, IllegalArgumentException {
		//正しく動くかテスト
		Invokable tmp = Reader.read(value);
		if (tmp.getResultType() != Invokable.TYPE_BOOLEAN)
			throw new IllegalArgumentException(ContextAccess.getString(R.string.return_type_is_not_boolean));
		tmp.invoke(TimelineItem.getDummyTweet());

		filterExpr = tmp;
		filter = value;

		for (Action1<Tab> handler : filterChangedHandler) {
			handler.invoke(this);
		}

		Tabs.save();
	}

	public Invokable getFilterExpression() {
		return filterExpr;
	}

	//TODO:通知系
}
