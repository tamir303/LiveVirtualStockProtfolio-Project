package application;


import Exceptions.DontOwnThisStockException;
import Exceptions.NotEnoughMoneyException;
import Exceptions.NotEnoughStocksToSellException;
import Exceptions.StockAlreadyInListException;
import Exceptions.StockDoesntExistException;
import Exceptions.StockMustBeAnIntegerException;
import Exceptions.StockSymbleNameNotFoundException;
import Exceptions.SymbolIsInvalidException;
import Listener.AbstractSMView;
import Listener.StockMarketModelEventsListener;
import Listener.StockMarketUIEventsListener;
import Model.StockMarket;

public class SceneController implements StockMarketModelEventsListener, StockMarketUIEventsListener {

	private StockMarket model;
	private AbstractSMView view;

	public SceneController(StockMarket model, AbstractSMView view) {
		this.model = model;
		this.model.registerListener(this);
		this.view = view;
		this.view.registerListener(this);
		//this.model.hardCode();
		this.model.start();

	}

	@Override
	public void buyStockFromUI(String name, String numOfStocks) {
		try {
			this.model.buyStockForUser(name, numOfStocks);
		} catch (NotEnoughStocksToSellException | StockMustBeAnIntegerException | NotEnoughMoneyException e) {
			view.exceptionMessage(e.getMessage());
		}
	}

	@Override
	public void sellStockFromUI(String name, String numOfStocks) {
		try {
			this.model.sellStockForUser(name, numOfStocks);
		} catch (NotEnoughStocksToSellException | StockMustBeAnIntegerException | DontOwnThisStockException e) {
			view.exceptionMessage(e.getMessage());

		}
	}

	@Override
	public void addedStockFromModelToUI(String name, double price, double dayPrentage) {
		this.view.addedStockUI(name, price, dayPrentage);
	}

	@Override
	public void addPortfolioTFromModelToUI(double balance, double newWorth, double profit, double prenctage) {
		this.view.addPortfolioToMarketUI(balance, newWorth, profit, prenctage);
	}

	@Override
	public void addedStockToUserFromModelToUI(String name, double buyprice, double changePrecentage, int quntity) {
		this.view.addedStockToUserUI(name, buyprice, changePrecentage, quntity);
	}

	@Override
	public void updatedallStockToMarketFromModelToUI() {
		this.view.updatedallStockMarketUI();
	}

	@Override
	public String[] requestDatabaseFromUI(String name, String interval) {
		return this.model.getStockData(name, interval);
	}

	@Override
	public void addStockToModel(String symble) {
		try {
			this.model.addStock(null, symble);
		} catch (StockAlreadyInListException | StockDoesntExistException | SymbolIsInvalidException | StockSymbleNameNotFoundException e) {
			view.exceptionMessage(e.getMessage());
		}
	}

	@Override
	public void removeStockFromModel(String symble) {
		this.model.removeStock(symble);
	}

	@Override
	public void chooseUser(int id) {

		this.model.setCurrentUser(id);		
	}

}