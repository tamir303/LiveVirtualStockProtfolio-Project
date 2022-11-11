package Model;

import java.util.LinkedList;
import java.util.Queue;
import APIsRequests.SaveSqlRequest;

public class Transaction {
	private double changePresantage;
	private Queue<BuyPriceQueue> quntity;
	private int totalQuntity;
	private double AvgbuyPrice;

	public Transaction() {
		this.changePresantage = 0;
		this.quntity = new LinkedList<>();
		this.totalQuntity = 0;
		AvgbuyPrice = 0;
	}

	private void updateBuyingPrice() {
		double sum = 0;
		double temp = 0;
		for (BuyPriceQueue q : quntity) {
			sum += q.getQuntity();
			temp += q.getBuyPrice() * q.getQuntity();
		}
		this.AvgbuyPrice = Utils.getRoundNumber(temp / sum);
	}

	public double getChangePresantage() {
		return changePresantage;
	}

	public int getTotalQuntity() {
		return totalQuntity;
	}

	public double getAvgbuyPrice() {
		return AvgbuyPrice;
	}

	public void setChangePresantage(double changePresantage) {
		this.changePresantage = changePresantage;
	}
	public Queue<BuyPriceQueue> getQuntity() {
		return quntity;
	}

	public void setTotalQuntity(int totalQuntity) {
		this.totalQuntity = totalQuntity;
	}

	public void setAvgbuyPrice(double avgbuyPrice) {
		AvgbuyPrice = avgbuyPrice;
	}

	public void buy(int numOfStocks, double currentPrice) {
		this.quntity.add(new BuyPriceQueue(currentPrice, numOfStocks));
		updateBuyingPrice();
		this.totalQuntity += numOfStocks;
	}

	public void sell(int numOfStocks, double currentPrice,int userId) {
		int num = numOfStocks;
		while (num != 0) {
			BuyPriceQueue q = this.quntity.peek();
			if (q == null)
				break;
			if (num >= q.getQuntity()) {
				num -= q.getQuntity();
				SaveSqlRequest.removeQueueRow(q,true,userId);
				quntity.remove();
			} else { // num < q.getQuntity()
				q.setQuntity(q.getQuntity() - num);
				SaveSqlRequest.removeQueueRow(q,false,userId);
				break;
			}
		}
		updateBuyingPrice();
		this.totalQuntity -= numOfStocks;
	}


	

}
