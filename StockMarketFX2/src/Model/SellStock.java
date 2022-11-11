package Model;

import APIsRequests.SaveSqlRequest;

public class SellStock implements Order {
	private Stock stock;

	public SellStock(Stock stock) {
		this.stock = stock;
	}

	@Override
	public void execute(int numOfStocks,int userId) {
		this.stock.sell(numOfStocks,userId);
		SaveSqlRequest.saveTranscactionSellStock(stock,userId);

	}

}
