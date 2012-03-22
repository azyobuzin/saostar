package net.azyobuzi.azyotter.saostar.timeline_data;

import java.util.ArrayList;

import twitter4j.EntitySupport;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

import net.azyobuzi.azyotter.saostar.linq.Enumerable;
import net.azyobuzi.azyotter.saostar.system.Func2;

import android.net.Uri;

public class TweetEntities {
	public TweetEntities(EntitySupport source) {
		userMentions = Enumerable.from(source.getUserMentionEntities()).select(new Func2<UserMentionEntity, Integer, String>() {
			@Override
			public String invoke(UserMentionEntity arg0, Integer arg1) {
				return arg0.getScreenName();
			}
		}).toArrayList();
		
		urls = Enumerable.from(source.getURLEntities()).select(new Func2<URLEntity, Integer, Uri>() {
			@Override
			public Uri invoke(URLEntity arg0, Integer arg1) {
				return Uri.parse(arg0.getExpandedURL().toString());
			}
		}).toArrayList();
		
		hashtags = Enumerable.from(source.getHashtagEntities()).select(new Func2<HashtagEntity, Integer, String>() {
			@Override
			public String invoke(HashtagEntity arg0, Integer arg1) {
				return arg0.getText();
			}
		}).toArrayList();
		
		media = Enumerable.from(source.getMediaEntities()).select(new Func2<MediaEntity, Integer, Uri>() {
			@Override
			public Uri invoke(MediaEntity arg0, Integer arg1) {
				return Uri.parse(arg0.getMediaURLHttps().toString());
			}
		}).toArrayList();
	}
	
	public ArrayList<String> userMentions;
	public ArrayList<Uri> urls;
	public ArrayList<String> hashtags;
	public ArrayList<Uri> media;
}
