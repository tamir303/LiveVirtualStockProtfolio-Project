package Exceptions;

public class StockSymbleNameNotFoundException  extends Exception{
	public StockSymbleNameNotFoundException() {
		super("ERROR! Stock Symble was not found");
	}
}
