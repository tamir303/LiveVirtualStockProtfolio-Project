package APIsRequests;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import Exceptions.StockDoesntExistException;
import Exceptions.StockSymbleNameNotFoundException;
import Model.BuyPriceQueue;
import Model.Portfolio;
import Model.Stock;
import Model.StockMarket;
import Model.Utils;

public class LoadSQLRequest {
	private static String dbUrl = "jdbc:mysql://localhost/stockdatabase";
	private static String username = "root";
	private static String password = "";
	private static Connection conn = null;

	public LoadSQLRequest() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbUrl, username, password);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}


//get DAte Interval
	public static void getData(LocalDate date, Stock stock, LocalDate lastUpdate) {
		// TODO Auto-generated method stub
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs;
			if (date == null) {

				rs = stmt
						.executeQuery("SELECT *" + " FROM oneday_data" + " WHERE stock_id='" + stock.getSymbol() + "'");
				while (rs.next()) {
					stock.addLeakData(rs.getString("dates"), rs.getString("dates_price"), false);
				}
			} else {
				rs = stmt.executeQuery("SELECT *" + " FROM year_data" + " WHERE dates>='" + date.toString() + "'"
						+ "	AND dates<= '" + lastUpdate.toString() + "' AND stock_id='" + stock.getSymbol() + "'");
				while (rs.next()) {
					stock.addLeakData(rs.getString("dates"), rs.getString("dates_price"), true);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void LoadPortfolio(Portfolio user, ArrayList<Stock> allStocks) {
		try {
			Statement stmt = conn.createStatement();
			Statement stmt2 = conn.createStatement();

			ResultSet rs, trs;
			rs = stmt.executeQuery("SELECT *" + " FROM portfolio WHERE portfolio_id ='"+user.getId()+"'");
			while (rs.next()) {
				user.setProfit(rs.getDouble("profit"));
				user.setBalance(rs.getDouble("balance"));
				user.setProfitPrecentage(rs.getDouble("profit_percentage"));
			}
			rs.close();
			rs = stmt.executeQuery("SELECT *" + " FROM user_transaction WHERE portfolio_id ='"+user.getId()+"'");

			while (rs.next()) {
				Stock s = Utils.getStock(rs.getString("stock_id"), allStocks);
				s.getStockTrans(user.getId()).setAvgbuyPrice(rs.getDouble("avg_buyprice"));
				s.getStockTrans(user.getId()).setTotalQuntity(rs.getInt("quantity"));
				trs = stmt2.executeQuery("SELECT *" + " FROM transaction_log WHERE stock_id='" + s.getSymbol() + "' AND portfolio_id ='"+user.getId()+"'");

				while (trs.next()) {
					s.getStockTrans(user.getId()).getQuntity()
							.add(new BuyPriceQueue(trs.getDouble("buy_price"), trs.getInt("quantity")));
				}
				trs.close();
				user.addStock(s);
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void LoadAllStocks(StockMarket SM) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs;
			rs = stmt.executeQuery("SELECT *" + " FROM stock");
			while (rs.next()) {
				SM.addStock(new Stock(rs.getString("stock_name"),rs.getString("stock_id"),rs.getString("data_lastupdate")));
			}
			rs.close();
		} catch (SQLException | IOException | ParseException | InterruptedException | StockDoesntExistException | StockSymbleNameNotFoundException e) {
		}
	}
}
