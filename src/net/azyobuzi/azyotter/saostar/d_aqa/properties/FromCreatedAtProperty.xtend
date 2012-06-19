package net.azyobuzi.azyotter.saostar.d_aqa.properties;

import net.azyobuzi.azyotter.saostar.d_aqa.Property;
import net.azyobuzi.azyotter.saostar.d_aqa.PropertyFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class FromCreatedAtProperty extends Property {

	@Override
	public int getResultType() {
		return TYPE_DATETIME;
	}

	@Override
	public Object invoke(TimelineItem target) {
		return target.from.createdAt;
	}

	public static class Factory implements PropertyFactory {

		@Override
		public String getPropertyName() {
			return "from.createdAt";
		}

		@Override
		public int getType() {
			return TYPE_DATETIME;
		}

		@Override
		public Property createProperty() {
			return new FromCreatedAtProperty();
		}

	}

}
