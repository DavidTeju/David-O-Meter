package dev.davidteju;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		final int MAX_PER_15_MIN = 300;
		final long MILLIS_IN_15_MIN = 900000;
		final long WAIT_TIME = MILLIS_IN_15_MIN / MAX_PER_15_MIN;
		
		File toPrintTo = new File("sentimentValues.json");
		if (toPrintTo.isFile()) {
			try {
				String input = FileUtils.readFileToString(toPrintTo, (Charset) null);
				var parsedInput = (JSONObject) new JSONParser().parse(input);
				AzureAnalyticsValues.positive = (long) parsedInput.get("positive");
				AzureAnalyticsValues.neutral = (long) parsedInput.get("neutral");
				AzureAnalyticsValues.negative = (long) parsedInput.get("negative");
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
			updateRemoteValues();
			AzureAnalyticsValues.printValues();
			AzureAnalyticsValues.printToFile();
		}
	}
	
	static void updateRemoteValues() {
		try {
			var update = new ProcessBuilder(Path.of("update.zsh").toAbsolutePath().toString());
			var logStream = update.start().getInputStream();
			var errors = update.start().getErrorStream();
			
			logGit(new SequenceInputStream(logStream, errors));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void logGit(InputStream stream) throws IOException {
		Scanner scan = new Scanner(stream).useDelimiter("\n");
		File f1 = new File("gitUpdates.log");
		if (!f1.exists())
			//noinspection ResultOfMethodCallIgnored
			f1.createNewFile();
		BufferedWriter bw = new BufferedWriter(new FileWriter(f1.getName(), true));
		var currentTime = new java.util.Date(System.currentTimeMillis());
		bw.write(currentTime + "\n");
		
		scan.forEachRemaining((line) -> {
			try {
				bw.write(line + "\n\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		bw.close();
		scan.close();
	}
	
	public static void runBatch() {
		try {
			JSONArray parsedResponses = TwitterRecentSearch.runTwitterRequest();
			
			if (isNull(parsedResponses, "Twitter"))
				return;
			
			JSONArray resultArray = AzureSentimentAnalysis.runSentimentAnalysis(parsedResponses);
			
			if (isNull(resultArray, "Azure"))
				return;
			
			for (Object resultObject : resultArray) {
				JSONObject result = (JSONObject) resultObject;
				AzureAnalyticsValues.modifyValues((JSONObject) result.get("confidenceScores"));
			}
		} catch (ParseException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isNull(Object toVerify, String nameOfService) throws InterruptedException {
		if (toVerify == null) {
			AzureAnalyticsValues.printToFile();
			System.out.println(nameOfService + " unresponsive. Will continue in 60 seconds");
			sleep(1000 * 60);
			return true;
		}
		return false;
	}
}
