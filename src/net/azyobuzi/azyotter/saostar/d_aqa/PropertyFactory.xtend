package net.azyobuzi.azyotter.saostar.d_aqa

abstract class PropertyFactory {
	def String getPropertyName()
	def int getType()
	def Property createProperty()
}
