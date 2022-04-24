package dev.davidteju;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		final int MAX_PER_15_MIN = 300;
		final long MILLIS_IN_15_MIN = 900000;
		final long WAIT_TIME = MILLIS_IN_15_MIN / MAX_PER_15_MIN;
		
		File toPrintTo = new File("sentimentValues.json");
		if (toPrintTo.isFile()) {
			
			try {
				String input = FileUtils.readFileToString(toPrintTo);
				var parsedInput = (JSONObject) new JSONParser().parse(input);
				AzureAnalyticsValues.positive = (int) parsedInput.get("positive");
				AzureAnalyticsValues.neutral = (int) parsedInput.get("neutral");
				AzureAnalyticsValues.negative = (int) parsedInput.get("negative");
			} catch (FileNotFoundException | ParseException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else AzureAnalyticsValues.printToFile();
		
		
		//noinspection InfiniteLoopStatement
		while (true) {
			for (int i = 0; i < MAX_PER_15_MIN; i++) {
				runBatch();
				System.out.println(i + 1);
				//noinspection BusyWait
				sleep(WAIT_TIME);
			}
			AzureAnalyticsValues.printValues();
			AzureAnalyticsValues.printToFile();
		}
	}
	
	public static void runBatch() {
		try {
			JSONArray parsedResponses = TwitterRecentSearch.runTwitterRequest();
			JSONArray resultArray = AzureSentimentAnalysis.runSentimentAnalysis(parsedResponses);
			
			if (resultArray == null) {
				AzureAnalyticsValues.printToFile();
				System.out.println("Azure unresponsive. Will continue in 60 seconds");
				sleep(1000 * 60);
				return;
			}
			
			for (Object resultObject : resultArray) {
				JSONObject result = (JSONObject) resultObject;
				AzureAnalyticsValues.modifyValues((JSONObject) result.get("confidenceScores"));
			}
		} catch (ParseException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
