package Model;

import java.util.HashMap;
public class Broker {
	private	HashMap<Order,OrderDetails> orderList = new 	HashMap<Order,OrderDetails>();
	private static Broker _instance;
	
	private Broker() {
	}
	public static Broker getInstance()
	{
		if(_instance==null) return new Broker();
		else
		return null;
	}
	public void takeOrder(Order order,OrderDetails det) {
		orderList.put(order, det);
	}
	public void placeOrders() {
		for (Order order:orderList.keySet())
			order.execute(orderList.get(order).getNumOfStocks(),orderList.get(order).getUserId());
		orderList.clear();
	}
}
