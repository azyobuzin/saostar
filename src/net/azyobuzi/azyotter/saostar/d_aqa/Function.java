package net.azyobuzi.azyotter.saostar.d_aqa;

import java.util.ArrayList;
import java.util.List;

import net.azyobuzi.azyotter.saostar.ContextAccess;
import net.azyobuzi.azyotter.saostar.R;

public abstract class Function implements Invokable {
	public Function(String funcName) {
		this.funcName = funcName;
	}

	private String funcName;

	public abstract boolean checkArgs(List<Invokable> args);

	public void setArguments(List<Invokable> args) {
		if (args == null) args = new ArrayList<Invokable>();

		if (checkArgs(args)) {
			this.args = args;
		} else {
			throw new IllegalArgumentException(
				ContextAccess.getText(R.string.arguments_have_problem).toString().replace("$funcName$", funcName)
			);
		}
	}

	protected List<Invokable> args;
}
