package net.azyobuzi.azyotter.saostar.d_aqa.operators

import java.util.HashMap
import java.util.Map

import net.azyobuzi.azyotter.saostar.d_aqa.Invokable
import net.azyobuzi.azyotter.saostar.d_aqa.Operator
import net.azyobuzi.azyotter.saostar.d_aqa.OperatorFactory
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem

class EqualityOperator extends Operator {

	new(Invokable left, Invokable right) {
		super(left, right);
	}

	override invoke(TimelineItem target) {
		val leftResult = left.invoke(target)
		val rightResult = right.invoke(target)

		if (left.getResultType() == TYPE_STRING && right.getResultType() == TYPE_STRING) {
			if (leftResult == null)
				return rightResult == null

			(leftResult as String).equalsIgnoreCase(rightResult as String)
		}

		leftResult.equals(rightResult)
	}

	static class Factory extends OperatorFactory {

		override getOperatorIdentifier() {
			"="
		}

		private static val parameterTypes = new HashMap<Integer, Integer>() {
			private static val long serialVersionUID = 1L

			{
				put(TYPE_STRING, TYPE_STRING)
				put(TYPE_NUMBER, TYPE_NUMBER)
				put(TYPE_BOOLEAN, TYPE_BOOLEAN)
				put(TYPE_DATETIME, TYPE_DATETIME)
			}
		}

		override getParameterTypes() {
			parameterTypes
		}

		override createOperator(Invokable left, Invokable right) {
			new EqualityOperator(left, right)
		}

	}

}
