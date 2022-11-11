package Exceptions;

public class DontOwnThisStockException extends Exception{
	public DontOwnThisStockException() {
		super("ERROR! You Cannot Sell a Stock that you don't Own");
	}
}
