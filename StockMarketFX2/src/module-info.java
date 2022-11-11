module StockMarketFX2 {
	requires javafx.controls;
	requires java.net.http;
	requires json.simple;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.base;
	requires java.desktop;
	requires jdk.compiler;
	requires java.sql;
	
	opens application to javafx.graphics, javafx.fxml;
}
