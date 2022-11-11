package application;

import java.net.URL;
import java.util.ResourceBundle;

import Listener.AbstractSMView;
import Listener.StockMarketUIEventsListener;
import Model.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class MainController implements Initializable, AbstractSMView {
	@FXML
	private AnchorPane myAnchor;
	@FXML
	private ListView<String> allStocks;
	@FXML
	private ListView<String> myPortfolio;

	private String stockChoice;
	@FXML
	private Button BbuyStock;
	@FXML
	private TextField tnumOfStocks;
	@FXML
	private Button BsellStock;
	@FXML
	private ListView<String> myStocks;
	@FXML
	private AreaChart myChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML
	private Label label;
	@FXML
	private ComboBox<String> chChart;
	@FXML
	private Label cursorCoords;
	@FXML
	private Label minValue;
	@FXML
	private Label maxValue;
	@FXML
	private TextField tfSearch;
	@FXML
	private Button addButton;
	@FXML
	private Button removeButton;
	@FXML
	private StockMarketUIEventsListener listener;
	@FXML
	private Label lChangeInterval;
	@FXML
	private ComboBox<Integer> chUserID;
	private final String DEFAULT_CONTROL_INNER_BACKGROUND = "derive(-fx-base,80%)";
	private final String GREENHIGHLIGHTED_CONTROL_INNER_BACKGROUND = "derive(palegreen, 50%)";
	private final String REDHIGHLIGHTED_CONTROL_INNER_BACKGROUND = "FF6666";
	private double openPriceInterval=0;
	private double CurrentPriceInterval=0;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		chChart.getItems().addAll("1 year", "6 Month", "30 days", "1 day");
		chChart.setValue("1 year");
		chUserID.getItems().addAll(1,2,3);
		chUserID.setValue(1);
		myChart.setAnimated(false);
		myChart.getXAxis().setTickMarkVisible(false);
		myChart.getYAxis().setTickLabelFill(Color.WHITE);
		myChart.getXAxis().setTickLabelFill(Color.WHITE);
		myChart.getYAxis().setAutoRanging(false);
		myChart.setStyle("CHART_COLOR_1: rgb(" + 127 + ")");
		myStocks.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				return new ListCell<String>() {
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null || empty) {
							setText(null);
							setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
						} else {
							setText(item);
							String str = ((item.split("%"))[0]).split("\t")[3];
							if (Double.parseDouble((str)) > 0) {
								setStyle("-fx-control-inner-background: " + GREENHIGHLIGHTED_CONTROL_INNER_BACKGROUND
										+ ";");
							} else if (Double.parseDouble((str)) < 0) {
								setStyle("-fx-control-inner-background:" + REDHIGHLIGHTED_CONTROL_INNER_BACKGROUND
										+ ";");
							} else {
								setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");

							}
						}
					}
				};
			}
		});
		allStocks.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				return new ListCell<String>() {
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null || empty) {
							setText(null);
							setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");
						} else {
							setText(item);
							String str = ((item.split("%"))[0]).split("\t")[4];
							if (Double.parseDouble((str)) > 0) {
								setStyle("-fx-control-inner-background: " + GREENHIGHLIGHTED_CONTROL_INNER_BACKGROUND
										+ ";");
							} else if (Double.parseDouble((str)) < 0) {
								setStyle("-fx-control-inner-background:" + REDHIGHLIGHTED_CONTROL_INNER_BACKGROUND
										+ ";");
							} else {
								setStyle("-fx-control-inner-background: " + DEFAULT_CONTROL_INNER_BACKGROUND + ";");

							}
						}
					}
				};
			}
		});
		allStocks.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (allStocks.getSelectionModel().getSelectedItem() != null) {
					if (myChart.getData().size() != 0)
						myChart.getData().remove(0);
					stockChoice = allStocks.getSelectionModel().getSelectedItem().split("\t")[0];
					CurrentPriceInterval = Double
							.parseDouble((allStocks.getSelectionModel().getSelectedItem().split("\t")[2]));
					XYChart.Series StockData = new XYChart.Series();
					xAxis.setLabel(stockChoice);
					createChart(stockChoice, StockData);

				}
			}
		});

		chChart.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (myChart.getData().size() != 0)
					myChart.getData().remove(0);
				XYChart.Series StockData = new XYChart.Series();
				xAxis.setLabel(stockChoice);
				createChart(stockChoice, StockData);
			}
		});
		chUserID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {

			@Override
			public void changed(ObservableValue<? extends Integer> arg0, Integer arg1, Integer arg2) {
				chooseUserFromUI(chUserID.getValue());				
			}
		});

	}

	public void handle(MouseEvent mouseEvent) {
		cursorCoords.setText(String.format("(%s, %.2f)", xAxis.getValueForDisplay(mouseEvent.getX()),
				yAxis.getValueForDisplay(mouseEvent.getY())));
	}

	public void numOfStocks(ActionEvent e) {
		tnumOfStocks.getText();
	}

	public void stockSymbleSearch(ActionEvent e) {
		tfSearch.getText();
	}

	public void searchStock(ActionEvent e) {
		stockSymbleSearch(e);
		this.listener.addStockToModel(tfSearch.getText());
	}

	public void removeStock(ActionEvent e) {
		if (allStocks.getSelectionModel().getSelectedItem() != null) {
			this.listener.removeStockFromModel(allStocks.getSelectionModel().getSelectedItem().split("\t")[0]);
			allStocks.getItems().remove(allStocks.getSelectionModel().getSelectedIndex());
		}
	}

	public void buyStock(ActionEvent e) {
		numOfStocks(e);
		this.listener.buyStockFromUI(stockChoice, tnumOfStocks.getText());
	}

	public void sellStock(ActionEvent e) {
		numOfStocks(e);
		this.listener.sellStockFromUI(stockChoice, tnumOfStocks.getText());
	}

	@Override
	public void addedStockUI(String name, double price, double daylyPrenctage) {
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				allStocks.getItems().add(name + "\t\t" + price + "\t\t   " + daylyPrenctage + "%");
			}
		});

	}

	@Override
	public void updatedallStockMarketUI() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				allStocks.getItems().clear();
				myPortfolio.getItems().clear();
				myStocks.getItems().clear();
				label.setText("");
			}
		});

	}

	@Override
	public void addPortfolioToMarketUI(double balance, double newWorth, double profit, double prenctage) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				myPortfolio.getItems().add("Balance :	" + balance);
				myPortfolio.getItems().add("Total Stock Worth :   " + newWorth);
				myPortfolio.getItems().add("Profit :\t" + profit + "\t" + prenctage + " %");

			}
		});

	}

	@Override
	public void addedStockToUserUI(String Name, double buyprice, double changePrecentage, int quntity) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				myStocks.getItems()
						.add(Name + "\t" + buyprice + "\t	" + changePrecentage + "% \t" + "   " + quntity);
			}
		});
	}

	@Override
	public void registerListener(StockMarketUIEventsListener listener) {
		this.listener = listener;
	}

	@Override
	public void createChart(String name, Series stockData) {
		String interval = chChart.getSelectionModel().getSelectedItem();
		XYChart.Series StockData = new XYChart.Series();
		String[] data = this.listener.requestDatabaseFromUI(stockChoice, interval);
		myChart.setVisible(true);
		if (data == null)
			return;
		String[] arr = new String[3];
		double min = Utils.findMin(data, interval), max = Utils.findMax(data, interval);
		// int count = data.length;
		openPriceInterval = Utils.getRoundNumber(Double.parseDouble(data[0].split(" ")[1]));
		double intervalPrecenage= Utils.getRoundPrecentage(openPriceInterval,this.CurrentPriceInterval);
		double change= Utils.getRoundNumber(this.CurrentPriceInterval-openPriceInterval);
		String str;
		if(change>0)str="+";
		else str="";
		lChangeInterval.setText(interval+" change:\n"+"("+intervalPrecenage+"%"+")"+str+change);
		for (int i = 0; i < data.length; i++) {
			arr = data[i].split(" ");
			StockData.getData().add(new XYChart.Data(arr[0], Double.parseDouble(arr[1])));
		}
		minValue.setText("High : " + min);
		maxValue.setText("Low : " + max);
		if (interval.equals("1 day")) {
			((ValueAxis<Number>) myChart.getYAxis()).setLowerBound((int) min);
			((ValueAxis<Number>) myChart.getYAxis()).setUpperBound((int) max);
		} else {
			((ValueAxis<Number>) myChart.getYAxis()).setLowerBound((int) Utils.getRoundNumber(min - (min / 10)));
			((ValueAxis<Number>) myChart.getYAxis()).setUpperBound((int) Utils.getRoundNumber(max + (max / 10)));
		}
		myChart.getData().add(StockData);
	}

	@Override
	public void exceptionMessage(String msg) {
		label.setText(msg);
	}

	@Override
	public void chooseUserFromUI(int id) {
		myPortfolio.getItems().clear();
		myStocks.getItems().clear();
		this.listener.chooseUser(id-1);
	}

}
