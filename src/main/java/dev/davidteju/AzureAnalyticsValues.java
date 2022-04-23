package dev.davidteju;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

public class AzureAnalyticsValues {
	static public int positive = 0;
	static public int negative = 0;
	static public int neutral = 0;
	
	static void printValues() {
		System.out.println(valuesToString());
	}
	
	static String valuesToString() {
		return String.format("Positive: %d%nNegative: %d%nNeutral: %d%n", positive, negative, neutral);
	}
	
	public static void printToFile() {
		try (PrintWriter writer = new PrintWriter("sentimentValues.txt")) {
			writer.printf("%d%n%d%n%d", positive, negative, neutral);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void modifyValues(JSONObject confidenceScores) {
		AzureAnalyticsValues.negative += Math.round((double) confidenceScores.get("negative"));
		AzureAnalyticsValues.positive += Math.round((double) confidenceScores.get("positive"));
		AzureAnalyticsValues.neutral += Math.round((double) confidenceScores.get("neutral"));
	}
}
