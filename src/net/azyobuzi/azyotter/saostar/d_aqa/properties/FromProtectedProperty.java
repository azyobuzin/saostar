package net.azyobuzi.azyotter.saostar.d_aqa.properties;

import net.azyobuzi.azyotter.saostar.d_aqa.Property;
import net.azyobuzi.azyotter.saostar.d_aqa.PropertyFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class FromProtectedProperty extends Property {

	@Override
	public int getResultType() {
		return TYPE_BOOLEAN;
	}

	@Override
	public Object invoke(TimelineItem target) {
		return target.from.isProtected;
	}

	public static class Factory implements PropertyFactory {

		@Override
		public String getPropertyName() {
			return "from.protected";
		}

		@Override
		public int getType() {
			return TYPE_BOOLEAN;
		}

		@Override
		public Property createProperty() {
			return new FromProtectedProperty();
		}

	}

}
