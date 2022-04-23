package dev.davidteju;

import dev.davidteju.AzureAnalyticsRequest.Tweet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AzureSentimentAnalysis {
	
	public static String sendRequest(final String azureApiKey, final String jsonRequest) throws IOException, InterruptedException {
		final String AZURE_ENDPOINT = "https://david-tweet-sentiment.cognitiveservices.azure.com";
		final String AZURE_SENTIMENT_PATH = "/text/analytics/v3.1/sentiment";
		final String API_KEY_HEADER_NAME = "Ocp-Apim-Subscription-Key";
		HttpClient client = HttpClient.newHttpClient();
		
		var request = HttpRequest.newBuilder()
				.header("Content-Type", "application/json")
				.header(API_KEY_HEADER_NAME, azureApiKey)
				.uri(URI.create(AZURE_ENDPOINT + AZURE_SENTIMENT_PATH))
				.POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
				.build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		assert response.statusCode() == 200;
		
		return response.body();
	}
	
	public static JSONArray runSentimentAnalysis(JSONArray tweets) throws IOException, InterruptedException, ParseException {
		final String azureApiKey = System.getenv("AZURE_API_KEY");
		
		String jsonRequest = createRequestJSON(tweets);
		
		String responseString = sendRequest(azureApiKey, jsonRequest);
		
		JSONObject parser = (JSONObject) new JSONParser().parse(responseString);
		
		return (JSONArray) parser.get("documents");
	}
	
	public static String createRequestJSON(JSONArray tweets) {
		var request = new AzureAnalyticsRequest();
		for (Object tweet : tweets) {
			String content = (String) ((JSONObject) tweet).get("text");
			request.addTweet(new Tweet(content));
		}
		return request.toJSON().toString();
	}
}
