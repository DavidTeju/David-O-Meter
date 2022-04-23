package dev.davidteju;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class AzureAnalyticsRequest {
	private final List<Tweet> tweets = new LinkedList<>();
	
	public void addTweet(Tweet toAdd) {
		if (toAdd.getId() == -1) toAdd.setId(tweets.size() + 1);
		tweets.add(toAdd);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		var toReturn = new JSONObject();
		
		toReturn.put("documents", this.toJSONArray());
		
		return toReturn;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray toJSONArray() {
		var array = new JSONArray();
		
		for (Tweet tweet : tweets)
			array.add(tweet.toJSON());
		
		return array;
	}
	
	
	public static class Tweet {
		@SuppressWarnings("FieldCanBeLocal")
		private final String language = "en";
		private int id = -1;
		private final String text;
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			if (this.id != -1)
				throw new RuntimeException();
			this.id = id;
		}
		
		public Tweet(String text) {
			this.text = text;
		}
		
		@SuppressWarnings("unchecked")
		public JSONObject toJSON() {
			JSONObject toReturn = new JSONObject();
			
			toReturn.put("language", language);
			toReturn.put("id", String.valueOf(id));
			toReturn.put("text", text);
			
			return toReturn;
		}
	}
}


