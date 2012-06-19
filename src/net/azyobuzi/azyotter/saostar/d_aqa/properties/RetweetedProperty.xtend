package net.azyobuzi.azyotter.saostar.d_aqa.properties;

import net.azyobuzi.azyotter.saostar.d_aqa.Property;
import net.azyobuzi.azyotter.saostar.d_aqa.PropertyFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class RetweetedProperty extends Property {

	@Override
	public int getResultType() {
		return TYPE_BOOLEAN;
	}

	@Override
	public Object invoke(TimelineItem target) {
		return target.retweeted != null;
	}

	public static class Factory implements PropertyFactory {

		@Override
		public String getPropertyName() {
			return "retweeted";
		}

		@Override
		public int getType() {
			return TYPE_BOOLEAN;
		}

		@Override
		public Property createProperty() {
			return new RetweetedProperty();
		}

	}

}
