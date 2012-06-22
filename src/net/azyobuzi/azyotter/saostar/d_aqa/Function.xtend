package net.azyobuzi.azyotter.saostar.d_aqa

import java.util.ArrayList
import java.util.List

import net.azyobuzi.azyotter.saostar.ContextAccess
import net.azyobuzi.azyotter.saostar.R

abstract class Function extends Invokable {
	new(String funcName) {
		this.funcName = funcName
	}

	private String funcName

	def boolean checkArgs(List<Invokable> args)

	def setArguments(List<Invokable> args) {
		val List<Invokable> _args = args != null ? args : new ArrayList<Invokable>()

		if (checkArgs(args)) {
			this.args = args
		} else {
			throw new IllegalArgumentException(
				ContextAccess.getText(R.string.arguments_have_problem).toString().replace("$funcName$", funcName)
			)
		}
	}

	protected List<Invokable> args
}
