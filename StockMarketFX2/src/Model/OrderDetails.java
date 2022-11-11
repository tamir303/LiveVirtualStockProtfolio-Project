package Model;

public class OrderDetails {
private int numOfStocks;
private int userId;

public OrderDetails(int numOfStocks, int userId) {
	this.numOfStocks = numOfStocks;
	this.userId = userId;
}

public int getNumOfStocks() {
	return numOfStocks;
}

public int getUserId() {
	return userId;
}

}
