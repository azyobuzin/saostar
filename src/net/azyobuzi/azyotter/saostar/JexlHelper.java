package net.azyobuzi.azyotter.saostar;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;

public class JexlHelper {
	private static final JexlEngine engine = new JexlEngine();

	public static Expression createExpression(String expression) {
		synchronized (engine) {
			return engine.createExpression(expression);
		}
	}

	public static Object evaluate(String expression, JexlContext context) {
		return createExpression(expression).evaluate(context);
	}
}
