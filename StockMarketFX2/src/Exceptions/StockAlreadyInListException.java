package Exceptions;

public class StockAlreadyInListException extends Exception {
	public StockAlreadyInListException()
	{
		super("This stock is already in your list!");
	}

}
