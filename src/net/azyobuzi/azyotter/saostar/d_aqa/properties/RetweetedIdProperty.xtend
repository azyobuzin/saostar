package net.azyobuzi.azyotter.saostar.d_aqa.properties;

import net.azyobuzi.azyotter.saostar.d_aqa.Property;
import net.azyobuzi.azyotter.saostar.d_aqa.PropertyFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class RetweetedIdProperty extends Property {

	@Override
	public int getResultType() {
		return TYPE_NUMBER;
	}

	@Override
	public Object invoke(TimelineItem target) {
		if (target.retweeted != null)
			return target.retweeted.id.id;
		else
			return -1L;
	}

	public static class Factory implements PropertyFactory {

		@Override
		public String getPropertyName() {
			return "retweeted.id";
		}

		@Override
		public int getType() {
			return TYPE_NUMBER;
		}

		@Override
		public Property createProperty() {
			return new RetweetedIdProperty();
		}

	}

}
