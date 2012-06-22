package net.azyobuzi.azyotter.saostar.d_aqa

import java.util.Map

abstract class OperatorFactory {
	def String getOperatorIdentifier()
	def Map<Integer, Integer> getParameterTypes()
	def Operator createOperator(Invokable left, Invokable right)
}
