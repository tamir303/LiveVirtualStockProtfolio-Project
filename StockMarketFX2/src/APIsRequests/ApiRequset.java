package APIsRequests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Exceptions.StockDoesntExistException;
import Model.Stock;
import Model.Utils;

public class ApiRequset {
	private String apiKey;
	private String apiHost;
	private String dir;
	private static ApiRequset instance;

	private ApiRequset() {
		apiKey = "0f72d450camsh67e9d4e30a86b56p1a822djsn549747ea54c4";
		apiHost = "twelve-data1.p.rapidapi.com";
		dir = "C:\\Users\\yuval\\eclipse-workspace\\StockMarket\\";
	}

	public static ApiRequset getInstance() {
		if (instance == null)
			instance = new ApiRequset();
		return instance;
	}

	public JSONArray getDataBase(int output, Stock stock) {
		String interval = "1day";
		if (output == 78)
			interval = "5min";
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://twelve-data1.p.rapidapi.com/time_series?symbol=" + stock.getSymbol()
						+ "&interval=" + interval + "&outputsize=" + output + "&format=json"))
				.header("x-rapidapi-host", apiHost).header("x-rapidapi-key", apiKey)
				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
		HttpResponse<String> response;
		try {
			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			String str = response.body();
			try (FileWriter file = new FileWriter(dir + "BigData\\Data\\" + stock.getSymbol() + "Data" + ".json")) {
				// We can write any JSONArray or JSONObject instance to the file
				file.write(str);
				file.close();
			} catch (IOException e) {
			}
		} catch (IOException | InterruptedException e1) {

		}
		JSONParser jsonParser = new JSONParser();
		// Write JSON file t

		JSONArray companyList = new JSONArray();
		Object obj;
		try {
			obj = jsonParser.parse(new FileReader(dir + "BigData\\Data\\" + stock.getSymbol() + "Data" + ".json"));
			// A JSON object. Key value pairs are unordered. JSONObject supports
			// java.util.Map interface
			JSONArray l1 = (JSONArray) ((JSONObject) obj).get("values");
			return l1;

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray setStockName(String name, Stock stock) throws StockDoesntExistException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://twelve-data1.p.rapidapi.com/symbol_search?symbol=" + stock.getSymbol()
						+ "&outputsize=2"))
				.header("x-rapidapi-host", apiHost).header("x-rapidapi-key", apiKey)
				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
		HttpResponse<String> response;
		try {
			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		try {
			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			String str = response.body();
			try (FileWriter file = new FileWriter(dir + "BigData" + stock.getSymbol() + "Data" + ".json")) {
				// We can write any JSONArray or JSONObject instance to the file
				file.write(str);
				file.close();
			} catch (IOException e) {
			}
		} catch (IOException | InterruptedException e1) {

		}
		JSONParser jsonParser = new JSONParser();
		// Write JSON file t

		JSONArray companyList = new JSONArray();
		Object obj;
		try {
			obj = jsonParser.parse(new FileReader(dir + "BigData" + stock.getSymbol() + "Data" + ".json"));
			// A JSON object. Key value pairs are unordered. JSONObject supports
			// java.util.Map interface
			JSONArray l1 = (JSONArray) ((JSONObject) obj).get("data");
			return l1;
		} catch (IOException | ParseException e) {
		}
		return null;

	}

	public double getPriceFromAPI(Stock stock)
	
	
			throws FileNotFoundException, IOException, ParseException, InterruptedException {
		String res=null;
		Object obj=null;
		while(res==null) {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=" + stock.getSymbol()
						+ "&format=json&outputsize=30"))
				.header("x-rapidapi-host", apiHost).header("x-rapidapi-key", apiKey)
				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		String str = response.body();
		JSONParser jsonParser = new JSONParser();
		try (FileWriter file = new FileWriter(dir + "BigData\\RealTime\\" + stock.getSymbol() + "RealTime" + ".json")) {
			file.write(str);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		obj = jsonParser
				.parse(new FileReader(dir + "BigData\\RealTime\\" + stock.getSymbol() + "RealTime" + ".json"));
		res =((String) ((JSONObject) obj).get("price"));
	}
		return Utils.getRoundNumber((Double.parseDouble(res)));
	}
}
