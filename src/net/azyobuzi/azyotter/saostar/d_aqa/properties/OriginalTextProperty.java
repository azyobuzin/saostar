package net.azyobuzi.azyotter.saostar.d_aqa.properties;

import net.azyobuzi.azyotter.saostar.d_aqa.Property;
import net.azyobuzi.azyotter.saostar.d_aqa.PropertyFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class OriginalTextProperty extends Property {

	@Override
	public int getResultType() {
		return TYPE_STRING;
	}

	@Override
	public Object invoke(TimelineItem target) {
		if (target.retweeted != null)
			return target.retweeted.originalText;
		else
			return target.originalText;
	}

	public static class Factory implements PropertyFactory {

		@Override
		public String getPropertyName() {
			return "originalText";
		}

		@Override
		public int getType() {
			return TYPE_STRING;
		}

		@Override
		public Property createProperty() {
			return new OriginalTextProperty();
		}

	}

}
