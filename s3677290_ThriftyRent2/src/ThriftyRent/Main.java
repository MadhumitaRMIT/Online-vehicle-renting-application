package ThriftyRent;

import ThriftyRent.controller.BaseController;
import ThriftyRent.model.SQLiteJDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("view/main_window.fxml"));
		primaryStage.setTitle("ThriftyRent");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	/*
	 * this function is called when user trigger close  button 
	 * we save data into database 
	 * */
	@Override
	public void stop() throws Exception {
		super.stop();
		try {
			SQLiteJDBC sqLiteJDBC = new SQLiteJDBC();
			//save data when user attempt to close
			sqLiteJDBC.insertIntoTable(BaseController.vehicles);
		} catch (Exception e) {
			//System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
