package Listener;


public interface StockMarketModelEventsListener {
	void addedStockFromModelToUI(String name, double price,double dayPrentage);

	void addPortfolioTFromModelToUI(double balance, double newWorth, double profit, double prenctage);

	void addedStockToUserFromModelToUI(String name, double buyprice, double changePrecentage, int quntity);

	void updatedallStockToMarketFromModelToUI();
}
