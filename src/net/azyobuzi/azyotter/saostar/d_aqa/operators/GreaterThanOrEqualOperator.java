package net.azyobuzi.azyotter.saostar.d_aqa.operators;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.azyobuzi.azyotter.saostar.d_aqa.Invokable;
import net.azyobuzi.azyotter.saostar.d_aqa.Operator;
import net.azyobuzi.azyotter.saostar.d_aqa.OperatorFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class GreaterThanOrEqualOperator extends Operator {

	public GreaterThanOrEqualOperator(Invokable left, Invokable right) {
		super(left, right);
	}

	@Override
	public Object invoke(TimelineItem target) {
		Object leftResult = left.invoke(target);
		Object rightResult = right.invoke(target);

		if (left.getResultType() == TYPE_NUMBER && right.getResultType() == TYPE_NUMBER) {
			return (Long)leftResult >= (Long)rightResult;
		} else {
			return ((Date)leftResult).compareTo((Date)rightResult) >= 0;
		}
	}

	public static class Factory implements OperatorFactory {

		@Override
		public String getOperatorIdentifier() {
			return ">=";
		}

		private static final HashMap<Integer, Integer> parameterTypes = new HashMap<Integer, Integer>() {
			private static final long serialVersionUID = 1L;

			{
				put(TYPE_NUMBER, TYPE_NUMBER);
				put(TYPE_DATETIME, TYPE_DATETIME);
			}
		};

		@Override
		public Map<Integer, Integer> getParameterTypes() {
			return parameterTypes;
		}

		@Override
		public Operator createOperator(Invokable left, Invokable right) {
			return new GreaterThanOrEqualOperator(left, right);
		}

	}

}
