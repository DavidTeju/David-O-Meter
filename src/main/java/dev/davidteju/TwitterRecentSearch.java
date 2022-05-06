package dev.davidteju;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class TwitterRecentSearch {
	public static JSONArray runTwitterRequest() throws IOException, InterruptedException, ParseException {
		String searchQuery = "\"David\" -@david lang:en";
		String bearerToken = System.getenv("TWITTER_API_BEARER_TOKEN");
		
		String response = search(searchQuery, bearerToken);
		
		return toJSONArray(response);
	}
	
	static JSONArray toJSONArray(String response) throws ParseException, IOException {
		JSONObject obj = (JSONObject) new JSONParser().parse(response);
		JSONArray array = (JSONArray) obj.get("data");
		
		
		//If there's a failed run
		if (array == null || obj.get("error") != null) {
			logTweets(obj.toString(), true);
			return null;
		}
		
		logTweets(array.toString(), false);
		
		return (JSONArray) obj.get("data");
	}
	
	private static void logTweets(String toLog, boolean isError) throws IOException {
		File f1 = new File("logs/tweetsAnalysed.log");
		if (!f1.exists()) //noinspection ResultOfMethodCallIgnored
			f1.createNewFile();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(f1.getName(), true));
		var currentTime = new java.util.Date(System.currentTimeMillis());
		
		if (!isError)
			bw.write("\n" + currentTime + "\nINFO  " + toLog);
		else
			bw.write("\n" + currentTime + "\nERROR  " + toLog);
		
		bw.close();
	}
	
	static String search(String searchQuery, String bearerToken) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		final String TWITTER_ENDPOINT = "https://api.twitter.com/2/tweets/search/recent";
		final String QUERY = "?query=" +
				URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString())
						.replace("+", "%20");
		
		var request = HttpRequest.newBuilder()
				.header("Authorization", String.format("Bearer %s", bearerToken))
				.header("Content-Type", "application/json")
				.uri(URI.create(TWITTER_ENDPOINT + QUERY))
				.build();
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		
		assert response.statusCode() == 200;
		
		return response.body();
	}
}
