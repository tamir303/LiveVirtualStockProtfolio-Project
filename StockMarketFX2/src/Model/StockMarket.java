package Model;

import java.io.IOException;

import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.json.simple.parser.ParseException;

import APIsRequests.ApiRequset;
import APIsRequests.LoadSQLRequest;
import APIsRequests.SaveSqlRequest;
import Exceptions.DontOwnThisStockException;
import Exceptions.NotEnoughMoneyException;
import Exceptions.NotEnoughStocksToSellException;
import Exceptions.StockAlreadyInListException;
import Exceptions.StockDoesntExistException;
import Exceptions.StockMustBeAnIntegerException;
import Exceptions.StockSymbleNameNotFoundException;
import Exceptions.SymbolIsInvalidException;
import Listener.StockMarketModelEventsListener;

public class StockMarket extends Thread {
	private Portfolio currentUser;
	private ArrayList<Portfolio> allUsers;
	private ArrayList<Stock> allStocks;
	private Broker singleBroker;
	private StockMarketModelEventsListener listener;
	private ApiRequset api;
	private final int TOTAL_USERS = 3;

	public StockMarket() throws StockSymbleNameNotFoundException {
		new SaveSqlRequest();
		new LoadSQLRequest();
		allUsers = new ArrayList<Portfolio>();
		api = ApiRequset.getInstance();
		this.allStocks = new ArrayList<Stock>();
		singleBroker = Broker.getInstance();
		loadLastData();
	}

	private void loadLastData() throws StockSymbleNameNotFoundException {
		LoadSQLRequest.LoadAllStocks(this);
		for (int i = 0; i < TOTAL_USERS; i++) {
			allUsers.add(new Portfolio());
			LoadSQLRequest.LoadPortfolio(allUsers.get(i), allStocks);
			allUsers.get(i).updatePortfolio();
		}
		currentUser = allUsers.get(0);
	}

	public void addStock(String name, String symble) throws StockAlreadyInListException, StockDoesntExistException,
			SymbolIsInvalidException, StockSymbleNameNotFoundException {
		Stock s;
		try {
			if (symble.length() < 2 || symble.contains(" "))
				throw new SymbolIsInvalidException();
			if (Utils.getStock(symble, this.allStocks) == null) {
				s = new Stock(name, symble);
				allStocks.add(s);
				fireAddStock(s.getStockName(), s.getCurrentPrice(), s.getTodaychangePrice());
				s.start();
			} else {
				throw new StockAlreadyInListException();
			}
		} catch (IOException | ParseException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void removeStock(String name) {
		String symble = Utils.getStockSymble(this.allStocks, name);
		Stock s = Utils.getStock(symble, this.allStocks);
		if (s != null) {
			for (Portfolio port : allUsers) {
				if (port.getStock(symble) != null) {
					try {
						sellStockForUser(name, Integer.toString(s.getTotalQuntity(port.getId())));

					} catch (NotEnoughStocksToSellException | StockMustBeAnIntegerException
							| DontOwnThisStockException e) {
					}
					singleBroker.placeOrders();
				}
				SaveSqlRequest.removeStock(s);
				allStocks.remove(s);
			}
		}
	}

	public void changeInterval(String name, String interval) {
		int output = 0;
		LocalDate date = LocalDate.now();
		if (interval.equals("1 year"))
			date = date.minusYears(1);
		else if (interval.equals("6 Month"))
			date = date.minusMonths(6);
		else if (interval.equals("30 days"))
			date = date.minusMonths(1);
		else
			output = 78;
		String symble = Utils.getStockSymble(this.allStocks, name);
		Utils.getStock(symble, allStocks).updateLeakData(output, date);
	}

	public void buyStockForUser(String name, String numOfStocks)
			throws NotEnoughStocksToSellException, StockMustBeAnIntegerException, NotEnoughMoneyException {
		if (!(numOfStocks.matches("[0-9]+")))
			throw new StockMustBeAnIntegerException();
		int num = Integer.parseInt(numOfStocks);
		if (num <= 0)
			throw new StockMustBeAnIntegerException();

		String symble = Utils.getStockSymble(this.allStocks, name);

		Stock s = Utils.getStock(symble, this.allStocks);
		if (s == null)
			return;
		int answer = JOptionPane.showConfirmDialog(null,
				"Cost : " + Utils.getRoundNumber(s.getCurrentPrice() * num) + " Do you wish to continue?", "Buy Stock",
				JOptionPane.YES_NO_OPTION);
		if (answer == 1)
			return;
		if (currentUser.updateLiquidity(s, num, "Buy")) {
			if (currentUser.getStock(symble) == null)
				this.currentUser.addStock(s);
			singleBroker.takeOrder(new BuyStock(this.currentUser.getStock(symble)),
					new OrderDetails(num, currentUser.getId()));
			return;
		}
		throw new NotEnoughMoneyException();
	}

	public void sellStockForUser(String name, String numOfStocks)
			throws NotEnoughStocksToSellException, StockMustBeAnIntegerException, DontOwnThisStockException {
		// need try and catch prase to int
		if (!(numOfStocks.matches("[0-9]+")))
			throw new StockMustBeAnIntegerException();
		int num = Integer.parseInt(numOfStocks);
		String symble = Utils.getStockSymble(this.allStocks, name);

		Stock s = Utils.getStock(symble, this.allStocks);
		if (s == null)
			return;
		int answer = JOptionPane.showConfirmDialog(null,
				"Earn : " + Utils.getRoundNumber(s.getCurrentPrice() * num) + " Do you wish to continue?", "Buy Stock",
				JOptionPane.YES_NO_OPTION);
		if (answer == 1)
			return;
		if (currentUser.getStock(symble) == null) {
			throw new DontOwnThisStockException();
		}
		if (currentUser.updateLiquidity(s, num, "Sale")) {
			singleBroker.takeOrder(new SellStock(this.currentUser.getStock(symble)),
					new OrderDetails(num, currentUser.getId()));
		}
	}

	public void placeOrders() {
		this.singleBroker.placeOrders();
		this.currentUser.updateUser();
		for (Stock s : this.getUser().getUserStocks()) {
			if (s.getTotalQuntity(this.currentUser.getId()) != 0) {
				fireAddStockToUser(s.getStockName(), s.getBuyPrice(this.currentUser.getId()),
						s.getChangePresantage(this.currentUser.getId()), s.getTotalQuntity(this.currentUser.getId()));
			}
		}
	}

	public Portfolio getUser() {
		return currentUser;
	}

	public void registerListener(StockMarketModelEventsListener listener) {
		this.listener = listener;
	}

	public void fireAddStock(String name, double price, double dayPrentage) {
		this.listener.addedStockFromModelToUI(name, price, dayPrentage);
	}

	public void fireupdatePortfolio(double balance, double newWorth, double profit, double prenctage) {
		this.listener.addPortfolioTFromModelToUI(balance, newWorth, profit, prenctage);
	}

	public void fireAddStockToUser(String Name, double buyprice, double changePrecentage, int Quntity) {
		this.listener.addedStockToUserFromModelToUI(Name, buyprice, changePrecentage, Quntity);
	}

	public void run() {
		hardCode();
		while (true) {
			try {
				Thread.sleep(5000);

				this.listener.updatedallStockToMarketFromModelToUI();
			} catch (InterruptedException e) {
			}
			for (Stock stock : allStocks) {
				fireAddStock(stock.getStockName(), stock.getCurrentPrice(), stock.getTodaychangePrice());
			}
			placeOrders();
			fireupdatePortfolio(this.currentUser.getBalance(), this.currentUser.getWorth(),
					this.currentUser.getProfit(), this.currentUser.getProfitPrecentage());
		}
	}

	public void hardCode() {
		try {
			this.addStock("Microsoft", "MSFT");
			this.addStock("Tesla", "TSLA");
			this.addStock("Apple", "AAPL");
			this.addStock("Bitcoin", "BTC%2FUSD");
			this.addStock("Etherum", "ETH%2FUSD");
			this.addStock("Alphabet", "GOOGL");

			/*
			 * this.addStock(null, "BRK.B"); this.addStock(null, "JPM"); this.addStock(null,
			 * "JNJ"); this.addStock(null, "UNH"); this.addStock(null, "ADBE");
			 * this.addStock(null, "PFE"); this.addStock(null, "MA"); this.addStock(null,
			 * "TMO"); this.addStock(null, "COST");
			 */
		} catch (StockAlreadyInListException | StockDoesntExistException | SymbolIsInvalidException
				| StockSymbleNameNotFoundException e) {
		}

	}

	public String[] getStockData(String name, String interval) {
		Stock s = Utils.getStock(Utils.getStockSymble(this.allStocks, name), this.allStocks);
		if (s == null)
			return null;
		changeInterval(name, interval);
		return s.getDatabase();
	}

	public void setCurrentUser(int id) {
		this.currentUser = this.allUsers.get(id);
		fireupdatePortfolio(this.currentUser.getBalance(), this.currentUser.getWorth(), this.currentUser.getProfit(),
				this.currentUser.getProfitPrecentage());
	}

	public void addStock(Stock stock) {
		this.allStocks.add(stock);
		stock.start();
	}
}
