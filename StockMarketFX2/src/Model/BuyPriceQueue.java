package Model;

public class BuyPriceQueue {
	private double buyPrice;
	private int quntity;
	private static int ID=1;
	private int id;

	public BuyPriceQueue(double buyPrice, int quntity) {
		this.buyPrice = buyPrice;
		this.quntity = quntity;
		this.id=ID;
		ID++;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public int getQuntity() {
		return quntity;
	}

	public int getId() {
		return id;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public void setQuntity(int quntity) {
		this.quntity = quntity;
	}


}
