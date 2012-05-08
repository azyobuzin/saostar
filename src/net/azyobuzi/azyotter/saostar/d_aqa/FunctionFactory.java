package net.azyobuzi.azyotter.saostar.d_aqa;

public interface FunctionFactory {
	String getFunctionName();
	int getResultType();
	Function createFunction();
}
