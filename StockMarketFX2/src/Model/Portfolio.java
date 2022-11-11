package Model;

import java.util.Collection;
import java.util.HashMap;
import APIsRequests.SaveSqlRequest;
import Exceptions.NotEnoughStocksToSellException;

public class Portfolio {
	private HashMap<String, Stock> myStocks;
	private double balance;
	private double worth;
	private double profit;
	private final double START_GAME_MONEY =1000000;
	private double profitPrecentage;
	private static int ID=1;
	private int id;

	public Portfolio() {
		myStocks = new HashMap<String, Stock>();
		this.balance = START_GAME_MONEY;
		this.profit=0;
		this.profitPrecentage=0;
		id=ID;
		ID++;
	}

	public void updateUser() {
		this.worth=0;
		for (Stock s : myStocks.values())
			worth += s.getCurrentPrice() * s.getTotalQuntity(this.id);
		this.worth=Utils.getRoundNumber(worth);
		
		this.profit=Utils.getRoundNumber(calcPrortfolioProfit());
		//if Profit == nan profit=0;
		if(Double.isNaN(this.profit))
			this.profit=0;
		this.profitPrecentage=Utils.getRoundPrecentage(START_GAME_MONEY-this.balance,this.worth);
		updatePortfolio();
	}

	public void addliquidity(double liquid) { 
		this.balance += liquid;
	}
	public void updatePortfolio() {
		SaveSqlRequest.updatePortfolio(this);
	}
	
	public void addStock(Stock s){
		myStocks.put(s.getSymbol(), s);
	}

	public double calcPrortfolioProfit() {
		double sum = 0;
		for (String symble : this.myStocks.keySet())
			sum += calcProfitPerStock(symble);
		return sum;
	}

	public double calcProfitPerStock(String symble) {
		Stock s = this.myStocks.get(symble);
		double change = Math
				.abs(s.getBuyPrice(this.id) * s.getTotalQuntity(this.id) - s.getCurrentPrice()*s.getTotalQuntity(this.id));
		if (s.getCurrentPrice() - s.getBuyPrice(this.id) >= 0)
			return change;
		else
			return -change;
	}

	public boolean updateLiquidity(Stock s, int numOfStocks, String option) throws NotEnoughStocksToSellException {
		switch (option) {
		case "Buy":
			if (this.getBalance() - s.getCurrentPrice() * numOfStocks < 0)
				return false;
			this.balance -= s.getCurrentPrice() * numOfStocks;
			this.balance= Utils.getRoundNumber(balance);
			return true;
		case "Sale":
			if (this.myStocks.get(s.getSymbol()).getTotalQuntity(this.id)< numOfStocks)
				throw new NotEnoughStocksToSellException();
			this.balance += s.getCurrentPrice() * numOfStocks;
			this.balance= Utils.getRoundNumber(balance);
			return true;
		}
		return false;

	}
	public double getBalance() {
		return balance;
	}

	public double getWorth() {
		return worth;
	}

	public Stock getStock(String symble) {
		return myStocks.get(symble);
	}
	public Collection<Stock> getUserStocks() {
		return this.myStocks.values();
	}
	
	public double getProfit() {
		return profit;
	}

	public double getProfitPrecentage() {
		return profitPrecentage;
	}

	public int getId() {
		return id;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public void setProfitPrecentage(double profitPrecentage) {
		this.profitPrecentage = profitPrecentage;
	}

	public void setId(int id) {
		this.id = id;
	}



}
