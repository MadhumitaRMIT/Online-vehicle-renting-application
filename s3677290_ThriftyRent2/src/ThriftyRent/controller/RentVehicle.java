package ThriftyRent.controller;

import static ThriftyRent.util.Utils.isEmpty;

import java.net.URL;
import java.util.ResourceBundle;

import ThriftyRent.interfaces.OnVehicleRented;
import ThriftyRent.model.RentalRecord;
import ThriftyRent.model.Vehicle;
import ThriftyRent.util.DateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RentVehicle extends BaseController {
	@FXML
	public TextField customerID;
	public TextField numbr_days;
	public DatePicker rent_date;
	OnVehicleRented onVehicleRented;
	Vehicle vehicle;

	public void setOnVehicleRented(OnVehicleRented onVehicleRented, Vehicle vehicle) {
		this.vehicle = vehicle;
		this.onVehicleRented = onVehicleRented;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	private boolean validateInputs() {
		if (isEmpty(customerID)) {
			showError("No Customer Error", "Please Enter Customer ID");
			return false;
		}
		if (isEmpty(numbr_days)) {
			showError("No Days Error", "Please Enter Number Of Days to Rent the Vehicle");
			return false;
		}
		if (rent_date.getValue() == null) {
			showError("No Date Error", "Please Enter Rent Date of the Vehicle");
			return false;
		}
		return true;
	}

	public void rentVehicle(ActionEvent actionEvent) {
		try {
			if (validateInputs()) {
				int numDays = Integer.parseInt(numbr_days.getText());
				RentalRecord rent_vehicle = vehicle.rent(customerID.getText(),
						new DateTime(rent_date.getValue().getDayOfMonth(), rent_date.getValue().getMonthValue(),
								rent_date.getValue().getYear()),
						numDays);
				onVehicleRented.onVehicleRented(vehicle, rent_vehicle);
				Node source = (Node) actionEvent.getSource();
				Stage stage = (Stage) source.getScene().getWindow();
				stage.close();
			}
		} catch (Exception e) {
			showError("Error Renting Vehicle : ", e.getMessage());
		}
	}
}
