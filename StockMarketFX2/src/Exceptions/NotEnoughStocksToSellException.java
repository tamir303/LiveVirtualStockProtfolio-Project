package Exceptions;

public class NotEnoughStocksToSellException extends Exception {
	public NotEnoughStocksToSellException() {
		super("ERROR! Not Enough Stocks in Portfolio");
	}
}
