package net.azyobuzi.azyotter.saostar.d_aqa.operators;

import java.util.HashMap;
import java.util.Map;

import net.azyobuzi.azyotter.saostar.d_aqa.Invokable;
import net.azyobuzi.azyotter.saostar.d_aqa.Operator;
import net.azyobuzi.azyotter.saostar.d_aqa.OperatorFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class InequalityOperator extends Operator {

	public InequalityOperator(Invokable left, Invokable right) {
		super(left, right);
	}

	@Override
	public Object invoke(TimelineItem target) {
		Object leftResult = left.invoke(target);
		Object rightResult = right.invoke(target);

		if (left.getResultType() == TYPE_STRING && right.getResultType() == TYPE_STRING) {
			if (leftResult == null)
				return rightResult != null;

			return !((String)leftResult).equalsIgnoreCase((String)rightResult);
		}

		return !leftResult.equals(rightResult);
	}

	public static class Factory implements OperatorFactory {

		@Override
		public String getOperatorIdentifier() {
			return "!";
		}

		private static final HashMap<Integer, Integer> parameterTypes = new HashMap<Integer, Integer>() {
			private static final long serialVersionUID = 1L;

			{
				put(TYPE_STRING, TYPE_STRING);
				put(TYPE_NUMBER, TYPE_NUMBER);
				put(TYPE_BOOLEAN, TYPE_BOOLEAN);
				put(TYPE_DATETIME, TYPE_DATETIME);
			}
		};

		@Override
		public Map<Integer, Integer> getParameterTypes() {
			return parameterTypes;
		}

		@Override
		public Operator createOperator(Invokable left, Invokable right) {
			return new InequalityOperator(left, right);
		}

	}

}
