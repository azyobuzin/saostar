package net.azyobuzi.azyotter.saostar

import twitter4j.AsyncTwitterFactory
import twitter4j.TwitterFactory
import twitter4j.TwitterStreamFactory

class Twitter4JFactories {
	static val twitterFactory = new TwitterFactory()
	static val asyncTwitterFactory = new AsyncTwitterFactory()
	static val twitterStreamFactory = new TwitterStreamFactory()
}
