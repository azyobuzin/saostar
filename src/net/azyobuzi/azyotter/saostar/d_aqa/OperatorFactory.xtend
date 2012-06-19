package net.azyobuzi.azyotter.saostar.d_aqa;

import java.util.Map;

public interface OperatorFactory {
	String getOperatorIdentifier();
	Map<Integer, Integer> getParameterTypes();
	Operator createOperator(Invokable left, Invokable right);
}
