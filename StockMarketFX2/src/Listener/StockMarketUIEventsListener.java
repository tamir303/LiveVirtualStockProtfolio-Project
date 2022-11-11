package Listener;

public interface StockMarketUIEventsListener {
	void buyStockFromUI(String name,String numOfStocks);
	void sellStockFromUI(String name,String numOfStocks);
	String[] requestDatabaseFromUI(String symble, String interval);
	void addStockToModel(String symble);
	void removeStockFromModel(String symble);
	void chooseUser(int id);
}
