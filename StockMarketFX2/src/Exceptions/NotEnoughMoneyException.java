package Exceptions;

public class NotEnoughMoneyException extends Exception{
	public NotEnoughMoneyException() {
		super("ERROR! Not Enough Money in your Balance");
	}
}
