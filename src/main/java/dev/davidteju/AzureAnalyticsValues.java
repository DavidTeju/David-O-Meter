package dev.davidteju;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

public class AzureAnalyticsValues {
	static public long positive = 0;
	static public long negative = 0;
	static public long neutral = 0;
	
	static void printValues() {
		System.out.println(valuesToString());
	}
	
	static String valuesToString() {
		return String.format("Positive: %d%nNegative: %d%nNeutral: %d%n", positive, neutral, negative);
	}
	
	@SuppressWarnings("unchecked")
	public static void printToFile() {
		try (PrintWriter writer = new PrintWriter("sentimentValues.json")) {
			JSONObject output = new JSONObject();
			output.put("time", System.currentTimeMillis());
			output.put("positive", positive);
			output.put("neutral", neutral);
			output.put("negative", negative);
			
			writer.print(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void modifyValues(JSONObject confidenceScores) {
		AzureAnalyticsValues.positive += Math.round((double) confidenceScores.get("positive"));
		AzureAnalyticsValues.neutral += Math.round((double) confidenceScores.get("neutral"));
		AzureAnalyticsValues.negative += Math.round((double) confidenceScores.get("negative"));
	}
}
