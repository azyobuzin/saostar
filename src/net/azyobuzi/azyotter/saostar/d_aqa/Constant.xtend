package net.azyobuzi.azyotter.saostar.d_aqa;

import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class Constant implements Invokable {
	public Constant(int type, Object value) {
		this.type = type;
		this.value = value;
	}

	private int type;
	@Override
	public int getResultType() {
		return type;
	}

	private Object value;
	public Object getValue() {
		return value;
	}

	@Override
	public Object invoke(TimelineItem target) {
		return getValue();
	}
}