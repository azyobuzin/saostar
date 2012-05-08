package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList;

import net.azyobuzi.azyotter.saostar.d_aqa.Invokable;
import net.azyobuzi.azyotter.saostar.d_aqa.Reader;
import net.azyobuzi.azyotter.saostar.system.Action1;

public class Tab {
	public Tab() {
		try {
			filterExpr = Reader.read(filter);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private String name;
	public final ArrayList<Action1<Tab>> nameChangedHandler = new ArrayList<Action1<Tab>>();

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;

		for (Action1<Tab> handler : nameChangedHandler) {
			handler.invoke(this);
		}
	}

	private String filter = "false";
	private Invokable filterExpr;
	public final ArrayList<Action1<Tab>> filterChangedHandler = new ArrayList<Action1<Tab>>();

	public String getFilter() {
		return filter;
	}

	public boolean setFilter(String value) {
		try {
			Invokable tmp = Reader.read(value);
			if (tmp.getResultType() != Invokable.TYPE_BOOLEAN) return false;
			filterExpr = tmp;
		} catch (Exception ex) {
			return false;
		}

		filter = value;

		for (Action1<Tab> handler : filterChangedHandler) {
			handler.invoke(this);
		}

		return true;
	}

	public Invokable getFilterExpression() {
		return filterExpr;
	}

	//TODO:通知系
}
