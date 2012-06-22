package net.azyobuzi.azyotter.saostar.d_aqa

abstract class FunctionFactory {
	def String getFunctionName()
	def int getResultType()
	def Function createFunction()
}
