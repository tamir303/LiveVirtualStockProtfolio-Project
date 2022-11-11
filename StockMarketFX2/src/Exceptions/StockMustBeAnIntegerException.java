package Exceptions;

public class StockMustBeAnIntegerException extends Exception{
	public StockMustBeAnIntegerException() {
		super("ERROR! Invalid number of Stocks");
	}
}
