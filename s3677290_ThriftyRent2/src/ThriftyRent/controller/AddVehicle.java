package ThriftyRent.controller;

import static ThriftyRent.util.Constants.VAN;
import static ThriftyRent.util.Utils.isEmpty;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

import ThriftyRent.interfaces.OnVehicleAdded;
import ThriftyRent.model.Car;
import ThriftyRent.model.Van;
import ThriftyRent.model.Vehicle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
/*
 * new vehicle window controller 
 * */
public class AddVehicle extends BaseController {
	@FXML
	public TextField id;
	public TextField image;
	@FXML
	ComboBox<Integer> number_passengers;
	@FXML
	ComboBox<String> vehicleType;
	@FXML
	TextField year;
	@FXML
	TextField make;
	@FXML
	TextField model;
	private FileChooser fileChooser;
	ThriftyRent.interfaces.OnVehicleAdded onVehicleAdded;

	public void setOnVehicleAdded(OnVehicleAdded onVehicleAdded) {
		this.onVehicleAdded = onVehicleAdded;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fileChooser = new FileChooser();
		fileChooser.setTitle("Select Vehicle Image");
		FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg",
				"*.gif");
		fileChooser.getExtensionFilters().add(extensionFilter);
		vehicleType.setValue("Select Vehicle Type");
		vehicleType.setItems(vehiclesTypeList);
		vehicleType.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				String id_start_String = "";
				if (vehicleType.getValue().equalsIgnoreCase(VAN))
					id_start_String = "V_";
				else
					id_start_String = "C_";
				String currentID = id.getText();
				if (currentID == null || currentID.trim().length() < 2)
					id.setText(id_start_String);
				else
					id.replaceText(0, 2, id_start_String);
			}
		});
		id.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.length() < 2 && oldValue.length() >= 2 && ("V_".equals(oldValue) || "C_".equals(oldValue)))
					id.setText(oldValue);
			}
		});
		number_passengers.setValue(0);
		number_passengers.setItems(passengersNumbersList);
	}

	private boolean validateInputs() {
		if (!vehiclesTypeList.contains(vehicleType.getValue())) {
			showError("Undefined Vehicle Type", "Please Select Vehicle Type");
			return false;
		}
		if (isEmpty(id)) {
			showError("No Id Error", "Please Enter ID");
			return false;
		}
		if (isEmpty(year)) {
			showError("No Year Error", "Please Enter Year");
			return false;
		}
		if (isEmpty(model)) {
			showError("No Model Error", "Please Enter Model");
			return false;
		}
		if (isEmpty(id)) {
			showError("No Make Error", "Please Enter Make");
			return false;
		}
		if (!passengersNumbersList.contains(number_passengers.getValue())) {
			showError("Number Of Passengers Error", "Please Select Number Of Passengers");
			return false;
		}
		return true;
	}

	public void addVehicle(ActionEvent actionEvent) {
		try {
			if (validateInputs()) {
				Vehicle vehicle;
				String v_id = id.getText();
				String v_make = make.getText();
				String v_model = model.getText();
				String v_year = year.getText();
				int number_of_passengers = number_passengers.getValue();
				if ("V".equalsIgnoreCase(id.getText().toUpperCase().charAt(0) + "")) {
					vehicle = new Van(v_id, v_make, v_model, v_year, getImage(), number_of_passengers);
				} else
					vehicle = new Car(v_id, v_make, v_model, v_year, getImage(), number_of_passengers);
				if (onVehicleAdded != null)
					onVehicleAdded.onVehicleAdded(vehicle);
				Node source = (Node) actionEvent.getSource();
				Stage stage = (Stage) source.getScene().getWindow();
				stage.close();
			}
		} catch (Exception e) {
			showError("Error Adding Vehicle", e.getMessage());
		}
	}

	private String getImage() {
		try {
			String img = image.getText();
			if (!img.trim().equals("") && img.trim().length() > 4) {
				File temp = new File("images");
				if (!temp.exists()) {
					temp.mkdir();
					//System.err.println("mkdir");
					//System.err.println(temp.toPath());
				}
				File file = new File(img);
				File out = new File("images", id.getText().trim() + img.substring(img.length() - 4));
				if (file.exists()) {
					Files.copy(file.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				return out.getName();
			}
		} catch (Exception e) {
			showError("Error reading image", e.getMessage());
		}
		return "";
	}

	public void selectImage(ActionEvent actionEvent) {
		File f = fileChooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());
		if (f != null) {
			image.setText(f.getAbsolutePath());
		}
	}
}
