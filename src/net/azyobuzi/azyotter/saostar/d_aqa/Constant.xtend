package net.azyobuzi.azyotter.saostar.d_aqa

import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem

class Constant extends Invokable {
	new(int type, Object value) {
		this.type = type
		this._value = value
	}

	private int type
	override getResultType() {
		type
	}

	private Object _value
	def getValue() {
		_value
	}

	override invoke(TimelineItem target) {
		getValue()
	}
}