package net.azyobuzi.azyotter.saostar.d_aqa.properties;

import net.azyobuzi.azyotter.saostar.d_aqa.Property;
import net.azyobuzi.azyotter.saostar.d_aqa.PropertyFactory;
import net.azyobuzi.azyotter.saostar.timeline_data.TimelineItem;

public class RetweetedUserVerifiedProperty extends Property {

	@Override
	public int getResultType() {
		return TYPE_BOOLEAN;
	}

	@Override
	public Object invoke(TimelineItem target) {
		if (target.retweeted != null)
			return target.retweeted.from.isVerified;
		else
			return false;
	}

	public static class Factory implements PropertyFactory {

		@Override
		public String getPropertyName() {
			return "retweetedUser.verified";
		}

		@Override
		public int getType() {
			return TYPE_BOOLEAN;
		}

		@Override
		public Property createProperty() {
			return new RetweetedUserVerifiedProperty();
		}

	}

}
