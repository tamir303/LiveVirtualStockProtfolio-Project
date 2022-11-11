package Model;

import APIsRequests.SaveSqlRequest;

public class BuyStock implements Order {
	private Stock stock;

	public BuyStock(Stock stock) {
		this.stock = stock;
	}

	@Override
	public void execute(int numOfStocks,int userId) {
		stock.buy(numOfStocks,userId);
		SaveSqlRequest.saveTransactionBuyStock(stock,userId);


	}
}
