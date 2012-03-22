package net.azyobuzi.azyotter.saostar;

import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;

public class Twitter4JFactories {
	public static final TwitterFactory twitterFactory = new TwitterFactory();
	public static final AsyncTwitterFactory asyncTwitterFactory = new AsyncTwitterFactory();
	public static final TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory();
}
