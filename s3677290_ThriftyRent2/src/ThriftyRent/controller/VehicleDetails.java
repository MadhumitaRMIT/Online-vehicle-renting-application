package ThriftyRent.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import ThriftyRent.interfaces.OnVehicleAdded;
import ThriftyRent.model.RentalRecord;
import ThriftyRent.model.Van;
import ThriftyRent.model.Vehicle;
import ThriftyRent.util.DateTime;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class VehicleDetails extends BaseController {

	public TextField model;
	public TextField vehicle_type;
	public TextField make;
	public TextField year;
	public TextField seats;
	public TextField lmd;

	public ImageView image;
	public Button performMaintenenceButton;
	public ListView<RentalRecord> records_list;
	public Button rentButton;
	private Vehicle vehicle;
	private boolean initialized = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialize();
	}

	public void initialize() {
		initialized = true;
		if (vehicle != null) {

			make.setText(vehicle.getMake());
			model.setText(vehicle.getModel());
			year.setText(vehicle.getYear() + "");
			if (vehicle.getLastMaintenanceDate() != null)
				lmd.setText(vehicle.getLastMaintenanceDate().getFormattedDate());
			else
				lmd.setText("Not maintenanced yet");
			seats.setText(vehicle.getNumberOfPassengers() + "");
			if (vehicle instanceof Van)
				vehicle_type.setText("Van");
			else
				vehicle_type.setText("Car");
			if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
				rentButton.setText("Return");
			} else if (vehicle.getStatus() == Vehicle.VehicleStatus.Maintenance)
				performMaintenenceButton.setText("Complete Maintenance");
			try {
				if (vehicle.getImage() == null || "".equals(vehicle.getImage().trim()))
					vehicle.setImage("No_image_available.png");
				File file = new File("images", vehicle.getImage());
				if (file.exists())
					image.setImage(new Image("file:" + file.getAbsolutePath()));
			} catch (Exception e) {
				//System.err.println("36" + e.getMessage());
			}
			records_list.setCellFactory(param -> new RentalRecordItemController());
			//vehicle.getRentalRecords().sorted();
			records_list.setItems(vehicle.getRentalRecords());
			records_list.refresh();
		}

	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
		if (initialized)
			initialize();
	}
/*
 * called when maintenance button clicked to perform maintenance if vehiclenot 
 * in maintenace else complete maintenance
 * */
	public void performMaintenence(ActionEvent actionEvent) {
		if (performMaintenenceButton.getText().toLowerCase().contains("com")) {
			try {

				Dialog dialog = new Dialog();
				HBox hBox = new HBox();
				DatePicker datePicker = new DatePicker();
				datePicker.setMinWidth(300);

				hBox.setPadding(new Insets(10, 10, 10, 10));
				HBox.setMargin(datePicker, new Insets(0, 20, 0, 20));
				Label label = new Label("Complete Maintenance Date");
				Button submit = new Button("Complete Maintenance");
				submit.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						try {
							if (datePicker.getValue() == null) {
								showError("Complete Maintenance Error", "You must select return date!");
								return;
							}
							vehicle.completeMaintenance(new DateTime(datePicker.getValue().getDayOfMonth(),
									datePicker.getValue().getMonthValue(), datePicker.getValue().getYear()));
							lmd.setText(vehicle.getLastMaintenanceDate().getFormattedDate());
							performMaintenenceButton.setText("Perform Maintenance");
							dialog.hide();
						} catch (Exception e) {
							showError("Complete Maintenance Error", e.getMessage());
						}
					}
				});
				hBox.getChildren().addAll(label, datePicker, submit);
				dialog.getDialogPane().setContent(hBox);
				dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
					@Override
					public void handle(DialogEvent event) {
						dialog.hide();
					}
				});
				dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
				dialog.showAndWait();
			} catch (Exception e) {
				showError("Maintenance Error", e.getMessage());
			}
		} else
			try {
				vehicle.performMaintenance();
				performMaintenenceButton.setText("Complete Maintenance");
			} catch (Exception e) {
				showError("Maintenance Error", e.getMessage());
			}
	}

	public void rent(ActionEvent actionEvent) {
		if (vehicle.getStatus() == Vehicle.VehicleStatus.Available)
			rent();
		else if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented)
			returnVehicle();
		else
			showError("Rent Error", "Vehichle in maintenance and cannot be rented until it complete maintenance!");
	}

	public void rent() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/rent_vehicle.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			Scene scene = rentButton.getScene();
			stage.initOwner(scene.getWindow());
			RentVehicle controller = loader.getController();
			controller.setOnVehicleRented((vehicle, rentalRecord) -> {
				records_list.setItems(vehicle.getRentalRecords());
				records_list.refresh();
				initialize();
			}, vehicle);
			stage.setTitle("Rent Vehicle");
			stage.setScene(new Scene(root));
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void returnVehicle() {
		Dialog dialog = new Dialog();
		HBox hBox = new HBox();
		DatePicker datePicker = new DatePicker();
		datePicker.setMinWidth(300);

		hBox.setPadding(new Insets(10, 10, 10, 10));
		HBox.setMargin(datePicker, new Insets(0, 20, 0, 20));
		Label label = new Label("Return Date");
		Button submit = new Button("Return");
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					if (datePicker.getValue() == null) {
						showError("Return Error", "You must select return date!");
						return;
					}
					vehicle.returnVehicle(new DateTime(datePicker.getValue().getDayOfMonth(),
							datePicker.getValue().getMonthValue(), datePicker.getValue().getYear()));
					rentButton.setText("Rent");
					dialog.hide();
				} catch (Exception e) {
					showError("Return Error", e.getMessage());
				}
			}
		});
		hBox.getChildren().addAll(label, datePicker, submit);
		dialog.getDialogPane().setContent(hBox);
		dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
			@Override
			public void handle(DialogEvent event) {
				dialog.hide();
			}
		});
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		dialog.showAndWait();
	}

	@Override
	public Window getScene() {
		return records_list.getScene().getWindow();
	}

	public void openNewVehicle(ActionEvent actionEvent) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/add_vehicle.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			Scene scene = vehicle_type.getScene();
			stage.initOwner(scene.getWindow());
			AddVehicle controller = loader.getController();
			controller.setOnVehicleAdded(new OnVehicleAdded() {
				@Override
				public void onVehicleAdded(Vehicle vehicle) {
					vehicles.add(vehicle);
				}
			});
			stage.setTitle("New Vehicle");
			stage.setScene(new Scene(root));
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
