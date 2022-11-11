package Model;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import APIsRequests.ApiRequset;
import APIsRequests.LoadSQLRequest;
import APIsRequests.SaveSqlRequest;
import Exceptions.StockDoesntExistException;
import Exceptions.StockSymbleNameNotFoundException;

public class Stock extends Thread {
	private ArrayList<String> dataBase = new ArrayList<String>();
	private double currentPrice;
	private String symbol;
	private String name;
	private double todaychangePrice;
	private double openPrice;
	private HashMap<Integer,Transaction> allTransactions;
	private ApiRequset api;
	private LocalDate lastUpdate;

	public Stock(String name, String symbol)

		throws FileNotFoundException, IOException, ParseException, InterruptedException, StockDoesntExistException, StockSymbleNameNotFoundException {
		this.symbol = symbol;
		api = ApiRequset.getInstance();
		setStockName(name);
		allTransactions = new HashMap<Integer,Transaction>();

	}
	//load from sql constractor
	public Stock(String name, String symbol,String lastUpdate)
			throws FileNotFoundException, IOException, ParseException, InterruptedException, StockDoesntExistException, StockSymbleNameNotFoundException {
		allTransactions = new HashMap<Integer,Transaction>();
		this.lastUpdate=LocalDate.parse(lastUpdate.split(" ")[0]) ;
			this.symbol = symbol;
			api = ApiRequset.getInstance();
			setStockName(name);
		}

	private void setStockName(String name) throws StockDoesntExistException, StockSymbleNameNotFoundException {
		if (name != null) {
			this.name = name;
			return;
		}

		JSONArray l1 = api.setStockName(name, this);
		if (l1 == null||l1.size()==0)
			throw new StockSymbleNameNotFoundException();
		this.name = ((String) ((JSONObject) l1.get(0)).get("instrument_name")).split(" ")[0];// datetime
		if (this.name == null)
			throw new StockDoesntExistException();
	}

	public double buy(int numOfStocks, int userId) {
		if(this.allTransactions.get(userId)==null)
			this.allTransactions.put(userId, new Transaction());
		this.allTransactions.get(userId).buy(numOfStocks, this.currentPrice);
		return numOfStocks * this.currentPrice;
	}

	public double sell(int numOfStocks, int userId) {
		this.allTransactions.get(userId).sell(numOfStocks, this.currentPrice,userId);
		return numOfStocks * this.currentPrice;
	}

	public boolean dataBaseToSql() {
		// A JSON object. Key value pairs are unordered. JSONObject supports
		// java.util.Map interface
		int output = 365;

		JSONArray l1 = api.getDataBase(output, this);
		if (l1 == null)
			return false;
		if(this.lastUpdate!=null)
		if (this.lastUpdate.isEqual(LocalDate.now()))
		{
			return false;
		}
		
		dataBase.clear();
		SaveSqlRequest.deleteOldData(this,true);
		for (int i = 0; i < l1.size(); i++) {
			String price = (String) ((JSONObject) l1.get(i)).get("close");// datetime
			String datetime = (String) ((JSONObject) l1.get(i)).get("datetime");
			SaveSqlRequest.saveDataToSQL(price, datetime, true, this);
			// this.addData(datetime, price);
		}
		lastUpdate = LocalDate.now();
		updateLeakData(output, lastUpdate.minusYears(1));
		return true;
	}

	public void dataBaseDailyToSql() {
	//	if(LocalDate.now().isBefore(lastUpdate.plus(5,)))
	//		return;
		JSONArray l1 = api.getDataBase(78, this);
		if (l1 == null)
			return;
		SaveSqlRequest.deleteOldData(this,false);
		dataBase.clear();
		for (int i = 0; i < l1.size(); i++) {
			String price = (String) ((JSONObject) l1.get(i)).get("close");// datetime
			String datetime = (String) ((JSONObject) l1.get(i)).get("datetime");
			SaveSqlRequest.saveDataToSQL(price, datetime, false, this);
			// this.addData(datetime, price);
		}
	}

	public void updateLeakData(int output, LocalDate date) {
		dataBase.clear();
		if (output == 78) {
			dataBaseDailyToSql();
			LoadSQLRequest.getData(null, this, null);
		} else {
			dataBaseToSql();
			LoadSQLRequest.getData(date, this, lastUpdate);
		}
	}

	public void addLeakData(String date, String price, boolean b) {
		if(b)
		date = date.split(" ")[0];
		else 
		date = date.split(" ")[1];
		// $$ is delimiter
		this.dataBase.add(date + " " + price);
	}

	public String[] getDatabase() {
		return this.dataBase.toArray(new String[0]);
	}

	public void run() {


		try {
			setOpenPrice();
			this.currentPrice = getPriceFromAPI();
		} catch (StockDoesntExistException | IOException | ParseException | InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		this.todaychangePrice = Utils.getRoundPrecentage(this.openPrice, this.currentPrice);
		loadStock();
		dataBaseToSql();
		while (true) {
			try {
				Thread.sleep(30000);
				currentPrice();
				this.todaychangePrice = Utils.getRoundPrecentage(this.openPrice, this.currentPrice);
				for (Transaction trans : allTransactions.values()) 
					trans.setChangePresantage(Utils.getRoundPrecentage(trans.getAvgbuyPrice(), this.currentPrice));
				loadStock();
				// loadStock();
			} catch (InterruptedException | ParseException | IOException e) {
			}

		}
	}

	private void loadStock() {
		SaveSqlRequest.saveStockCurrentState(this);


	}

	private void currentPrice() throws IOException, InterruptedException, ParseException {

		this.currentPrice = getPriceFromAPI();
	}

	private double getPriceFromAPI() throws FileNotFoundException, IOException, ParseException, InterruptedException {

		return api.getPriceFromAPI(this);
	}

	public double getCurrentPrice() {
		return Utils.getRoundNumber(this.currentPrice);
	}

	public String getSymbol() {
		return symbol;
	}

	public double getBuyPrice(int userId) {
		return allTransactions.get(userId).getAvgbuyPrice();
	}

	public double getChangePresantage(int userId) {
		return allTransactions.get(userId).getChangePresantage();
	}

	public int getTotalQuntity(int userId) {
		return allTransactions.get(userId).getTotalQuntity();
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getTodaychangePrice() {
		return todaychangePrice;
	}

	public String getStockName() {
		return name;
	}

	public Transaction getStockTrans(int userId) {
		if(allTransactions.get(userId)==null)
			allTransactions.put(userId,new Transaction());
		return allTransactions.get(userId);
	}

	public LocalDate getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(LocalDate lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public void setOpenPrice() throws StockDoesntExistException {
		JSONArray l1 = api.getDataBase(2, this);
		this.openPrice = Double.parseDouble((String) ((JSONObject) l1.get(1)).get("close"));// datetime

	}
	public double getOpenPrice() {
		return openPrice;
	}

}
