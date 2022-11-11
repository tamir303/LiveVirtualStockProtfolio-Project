package Listener;

import javafx.scene.chart.XYChart.Series;

public interface AbstractSMView {
	void registerListener(StockMarketUIEventsListener listener);
	void addedStockToUserUI(String Name, double buyprice, double changePrecentage, int quntity);
	void addPortfolioToMarketUI(double balance, double newWorth, double profit, double prenctage);
	void updatedallStockMarketUI();
	void addedStockUI(String name, double price, double daylyPrenctage);
	void createChart(String symble, Series stockData);
	void exceptionMessage(String msg);
	void chooseUserFromUI(int id);

}
