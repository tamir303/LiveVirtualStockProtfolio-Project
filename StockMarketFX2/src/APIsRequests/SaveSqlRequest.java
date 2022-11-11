package APIsRequests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import Model.BuyPriceQueue;
import Model.Portfolio;
import Model.SellStock;
import Model.Stock;
import Model.Transaction;

public class SaveSqlRequest {
	private static String dbUrl = "jdbc:mysql://localhost/stockdatabase";
	private static String username = "root";
	private static String password = "";
	private static Connection conn = null;

	public SaveSqlRequest() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbUrl, username, password);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	public static void saveTransactionBuyStock(Stock s, int userId) {
		Transaction trans = s.getStockTrans(userId);

		try {
			Statement stmt = conn.createStatement();
		      conn.setAutoCommit(false);
			String str = "SELECT * from user_transaction where (stock_id='" + s.getSymbol() + "' AND portfolio_id='"+userId+"')";
			ResultSet val2 = stmt.executeQuery(str);

			if (val2.next() == true) {

				String value = "UPDATE user_transaction SET quantity='" + s.getTotalQuntity(userId) + "', avg_buyprice='"
						+ s.getBuyPrice(userId) + "'" + "WHERE stock_id='" + s.getSymbol() + "'AND portfolio_id='"+userId+"'";
				stmt.executeUpdate(value);
				value = "INSERT INTO transaction_log (stock_id,portfolio_id,quantity,buy_price) VALUES('" + s.getSymbol() + "','"
						+userId+"','"+ trans.getQuntity().peek().getQuntity() + "','" + s.getCurrentPrice()
						+ "')";
				stmt.executeUpdate(value);
		        conn.commit();

			} else {
				String value = "INSERT INTO user_transaction (stock_id,portfolio_id,quantity,avg_buyprice)" + " VALUES('"
						+ s.getSymbol() + "','" +userId+"','"+ s.getTotalQuntity(userId) + "','" + +s.getBuyPrice(userId) + "')";
				stmt.executeUpdate(value);
				value = "INSERT INTO transaction_log (stock_id,portfolio_id,quantity,buy_price) VALUES('" + s.getSymbol() + "','"
						+userId+"','"+ trans.getQuntity().peek().getQuntity() + "','" + trans.getQuntity().peek().getBuyPrice()
						+ "')";
				stmt.executeUpdate(value);
		        conn.commit();
			}
		     conn.setAutoCommit(true);
		} catch (SQLException e) 
		{
	          try {
				conn.rollback();
				//s.sell(trans.getQuntity().peek().getQuntity());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}
	public static void saveTranscactionSellStock(Stock stock, int userId) {

		try { 
			Statement stmt = conn.createStatement();
		      conn.setAutoCommit(false);

			if (stock.getTotalQuntity(userId) == 0) {
				String s = "DELETE FROM user_transaction WHERE stock_id='" + stock.getSymbol() + "' AND portfolio_id='"+userId+"'";
				stmt.executeUpdate(s);
		        conn.commit();

			} else {
				String s = "UPDATE user_transaction SET quantity='" + stock.getTotalQuntity(userId) + "' WHERE stock_id='"
						+ stock.getSymbol() + "' AND portfolio_id='"+userId+"'";
				stmt.executeUpdate(s);
		        conn.commit();
			}
		      conn.setAutoCommit(true);

		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	public static void updatePortfolio(Portfolio portfolio) {

		try {
			Statement stmt = conn.createStatement();
			String str = "SELECT * from portfolio where (portfolio_id='" + portfolio.getId() + "')";
			ResultSet val2 = stmt.executeQuery(str);

			if (val2.next() == true) {
				String s = "UPDATE portfolio SET balance='" + portfolio.getBalance() + "', worth='"
						+ portfolio.getWorth() + "', profit_Percentage=" + "'" + portfolio.getProfitPrecentage() + "'"
						+ ", profit='" + portfolio.getProfit() + "'" + " WHERE portfolio_id='" + portfolio.getId()
						+ "'";
				stmt.executeUpdate(s);
			} else {
				String s = "INSERT INTO portfolio (portfolio_id,balance,profit,worth,profit_percentage) VALUES('"
						+ portfolio.getId() + "','" + portfolio.getBalance() + "','" + portfolio.getProfit() + "','"
						+ portfolio.getWorth() + "','" + portfolio.getProfitPrecentage() + "')";
				stmt.executeUpdate(s);

			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}
	
	public static void saveDataToSQL(String price, String datetime, boolean bol, Stock s) {
		try {
			Statement stmt = conn.createStatement();
			String str;
			if (bol) {
				str = "INSERT INTO year_data (stock_id,dates,dates_price) VALUES ('" + s.getSymbol() + "','" + datetime
						+ "','" + price + "')";
				stmt.executeUpdate(str);
			} else {

				str = "INSERT INTO oneday_data (stock_id,dates,dates_price) VALUES ('" + s.getSymbol() + "','"
						+ datetime + "','" + price + "')";
				stmt.executeUpdate(str);
			}
		} catch (SQLException e) {
		}
	}
	public static void saveStockCurrentState(Stock stock) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet val2 = stmt.executeQuery("SELECT * from stock where (stock_id='" + stock.getSymbol() + "')");

			if (val2.next() == true) {
				String s = "UPDATE stock SET current_price='" + stock.getCurrentPrice() + "'" + " WHERE stock_id='"
						+ stock.getSymbol() + "'";
				stmt.executeUpdate(s);
			} else {
				String s = "INSERT INTO stock (stock_id,stock_name,current_price,data_lastupdate) VALUES('" + stock.getSymbol() + "','"
						+ stock.getStockName() + "','" + stock.getCurrentPrice() +"','"+LocalDate.now().toString()+"')";
				stmt.executeUpdate(s);
			}
		} catch (SQLException e) {
		}
	}

	public static void deleteOldData(Stock s, boolean bol) {
		try {
			Statement stmt = conn.createStatement();
			String str;
			if (bol) {
				str = "DELETE from year_data where stock_id='" + s.getSymbol() + "'";
				stmt.executeUpdate(str);
			} else {
				str = "DELETE from oneday_data where stock_id='" + s.getSymbol() + "'";
				stmt.executeUpdate(str);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void removeQueueRow(BuyPriceQueue q, boolean bol, int userId) {
		try {
			Statement stmt = conn.createStatement();
			if (bol) {
				String s = "DELETE FROM transaction_log WHERE transaction_order='" + q.getId() + "' AND portfolio_id='"+userId+"'";
				stmt.executeUpdate(s);
			} else {
				String s = "UPDATE transaction_log SET quantity='" + q.getQuntity() + "' WHERE transaction_order='"
						+ q.getId() + "' AND portfolio_id='"+userId+"'";
				stmt.executeUpdate(s);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeStock(Stock s) {
		try {
			Statement stmt = conn.createStatement();
			String str = "DELETE FROM  stock WHERE stock_id='" + s.getSymbol() + "'";
			stmt.executeUpdate(str);
		} catch (SQLException e) {
			e.printStackTrace();

		}
	}
}
