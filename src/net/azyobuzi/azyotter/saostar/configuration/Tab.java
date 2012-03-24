package net.azyobuzi.azyotter.saostar.configuration;

import java.util.ArrayList;

import org.apache.commons.jexl2.Expression;

import net.azyobuzi.azyotter.saostar.JexlHelper;
import net.azyobuzi.azyotter.saostar.filter.FilterUtil;
import net.azyobuzi.azyotter.saostar.system.Action1;

public class Tab {
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
	private Expression filterExpr = JexlHelper.createExpression("false");
	private FilterUtil fu = new FilterUtil();
	public final ArrayList<Action1<Tab>> filterChangedHandler = new ArrayList<Action1<Tab>>();

	public String getFilter() {
		return filter;
	}

	public boolean setFilter(String value) {
		try {
			filterExpr = JexlHelper.createExpression(value);
		} catch (Exception ex) {
			return false;
		}

		filter = value;
		fu = new FilterUtil();

		for (Action1<Tab> handler : filterChangedHandler) {
			handler.invoke(this);
		}

		return true;
	}

	public Expression getFilterExpression() {
		return filterExpr;
	}

	public FilterUtil getFilterUtil() {
		return fu;
	}

	//TODO:通知系
}
