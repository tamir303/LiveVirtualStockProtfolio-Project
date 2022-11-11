package application;
	
import java.awt.Dimension;


import Listener.AbstractSMView;
import Model.StockMarket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	static MainController myControllerHandle;

	@Override
	public void start(Stage primaryStage) {
        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
            Parent root = loader.load();
            myControllerHandle = loader.getController();
            
            StockMarket model = new StockMarket();
            AbstractSMView view = myControllerHandle;
            new SceneController(model,view);
            Dimension dm
            = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            Scene scene = new Scene(root,dm.getWidth()/1.5,dm.getHeight()/1.1);
            String css = this.getClass().getResource("application.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
