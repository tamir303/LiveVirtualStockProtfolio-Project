package Model;

import java.util.ArrayList;
public class Utils {
	public static boolean canBuyStock(Portfolio user, Stock s, int numofStocks) {
		if (user.getBalance() - s.getCurrentPrice() * numofStocks < 0)
			return false;

		return true;
	}

	public static Stock getStock(String symble, ArrayList<Stock> array) {
		for (Stock s : array) {
			if (s.getSymbol().equals(symble))
				return s;
		}
		return null;

	}

	public static double getRoundPrecentage(double num1, double num2) {
		if (num2 == 0)
			return 0;
		double prentage = (1 - (num1 / num2)) * 100;
		String temp = String.format("%.2f", prentage);
		return Double.parseDouble(temp);
	}

	public static double getRoundNumber(double num1) {
		String temp = String.format("%.2f", num1);
		return Double.parseDouble(temp);
	}

	public static String getStockSymble(ArrayList<Stock> array, String name) {
		for (Stock s : array)
			if (s.getStockName().equals(name))
				return s.getSymbol();
		return null;
	}

	public static double findMin(String[] data, String interval) {
		String[] arr = new String[3];
		double min=1000000;
		for (int i = data.length - 1; i >= 0; i--) {
			arr = data[i].split(" ");
				min = Math.min(min, Double.parseDouble(arr[1]));

		}
		return Utils.getRoundNumber(min);
	}
	public static double findMax(String[] data, String interval) {
		String[] arr = new String[3];
		double max=0;
		for (int i = data.length - 1; i >= 0; i--) {
			arr = data[i].split(" ");
				max = Math.max(max, Double.parseDouble(arr[1]));

		}
		return Utils.getRoundNumber(max);
	}
}