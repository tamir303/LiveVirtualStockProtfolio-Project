package Exceptions;

public class StockDoesntExistException extends Exception {
	public StockDoesntExistException() {
		super("This stock is already in your list!");
	}
}
