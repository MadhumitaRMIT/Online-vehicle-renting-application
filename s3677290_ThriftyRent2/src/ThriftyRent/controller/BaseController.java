package ThriftyRent.controller;

import static ThriftyRent.util.Constants.CAR;
import static ThriftyRent.util.Constants.VAN;
import static ThriftyRent.util.Utils.isEmpty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ThriftyRent.model.Car;
import ThriftyRent.model.RentalRecord;
import ThriftyRent.model.SQLiteJDBC;
import ThriftyRent.model.Van;
import ThriftyRent.model.Vehicle;
import ThriftyRent.model.Vehicle.VehicleStatus;
import ThriftyRent.util.DateTime;
import ThriftyRent.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * <p>
 * abstract class for holding mutual variables and methods between controllers
 * </p>
 **/
public abstract class BaseController implements Initializable {

	public static ObservableList<Vehicle> vehicles;
	ObservableList<String> vehiclesTypeList;
	ObservableList<Integer> passengersNumbersList;
	private Alert error, success;

	BaseController() {
		success = new Alert(Alert.AlertType.INFORMATION);
		vehiclesTypeList = FXCollections.observableArrayList();
		vehiclesTypeList.add(CAR);
		vehiclesTypeList.add(VAN);
		passengersNumbersList = FXCollections.observableArrayList();
		passengersNumbersList.add(4);
		passengersNumbersList.add(7);
		passengersNumbersList.add(15);
	}

	protected void showError(String title, String header, String content) {
		if (success == null)
			success = new Alert(Alert.AlertType.INFORMATION);
		success.setTitle(title);
		success.setHeaderText(header);
		success.setContentText(content);

	}

	protected void showError(String title, String content) {
		if (error == null)
			error = new Alert(Alert.AlertType.ERROR);
		error.setTitle(title);
		error.setContentText(content);
		error.setHeaderText(null);
		error.showAndWait();
	}

	protected void showSuccess(String title, String content) {
		if (success == null)
			success = new Alert(Alert.AlertType.INFORMATION);
		success.setTitle(title);
		success.setHeaderText(content);
		success.setContentText(null);
		success.showAndWait();
	}
//save vehicles to file
	public void exort(ActionEvent actionEvent) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select folder");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		File dir = directoryChooser.showDialog(getScene());
		if (dir == null)
			return;
		File file = new File(dir.getAbsolutePath() + "/export_data.txt");
		if (file.exists())
			file.delete();
		saveToFile(file);
	}

	private void saveToFile(File file) {
		try {
			PrintWriter writer;
			writer = new PrintWriter(file);
			for (Vehicle v : vehicles) {
				writer.println(v.toString() + ":" + v.getImage());
				if (v.getRentalRecords() != null && v.getRentalRecords().size() > 0)
					for (int j = v.getRentalRecords().size() - 1; j > -1; j--) {
						writer.println(v.getRentalRecords().get(j).toString());
					}
			}
			writer.close();
		} catch (IOException ex) {
			//System.out.println(ex);
		}
	}

	public Window getScene() {
		return null;
	}

	public void importfromfile(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Vehicles file", "*.txt"));
		File file = fileChooser.showOpenDialog(getScene());
		if (file != null) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line = "";
				line = bufferedReader.readLine();
				while (line != null) {
					String[] attrs = line.split(":");
					String vehicleId = null;
					if (vehicles != null && vehicles.size() > 0)
						vehicleId = vehicles.get(vehicles.size() - 1).getVehicle_id();
					if (!isEmpty(vehicleId) && !isEmpty(attrs[0])
							&& attrs[0].toLowerCase().contains(vehicleId.toLowerCase())) {
						RentalRecord record = new RentalRecord();
						if (attrs.length > 0)
							record.setRecord_id(attrs[0]);
						if (attrs.length > 1)
							record.setRentDate(new DateTime(attrs[1]));
						if (attrs.length > 2)
							record.setEstimatedReturnDate(new DateTime(attrs[2]));
						if (attrs.length > 3 && !"none".equalsIgnoreCase(attrs[2]) && attrs[2].length() > 8)
							record.setActualReturnDate(new DateTime(attrs[2]));
						if (attrs.length > 4)
							record.setRentalFee(Utils.toDouble(attrs[4]));
						if (attrs.length > 5)
							record.setLateFee(Utils.toDouble(attrs[5]));
						vehicles.get(vehicles.size() - 1).getRentalRecords().add(0, record);
					} else {
						Vehicle v;
						if ("V".equalsIgnoreCase(line.charAt(0) + ""))
							v = new Van();
						else
							v = new Car();
						v.setImage(attrs[attrs.length - 1]);
						if (attrs.length > 1)
							v.setVehicle_id(attrs[0]);
						if (attrs.length > 2)
							v.setYear(Integer.parseInt(attrs[1]));
						if (attrs.length > 3)
							v.setMake(attrs[2]);
						if (attrs.length > 4)
							v.setModel(attrs[3]);
						if (attrs.length > 5)
							v.setNumberOfPassengers(Integer.parseInt(attrs[4]));
						if (attrs.length > 6)
							v.setStatus(VehicleStatus.from(attrs[5]));

						if (attrs.length > 7 && attrs[6] != null && !"null".equalsIgnoreCase(attrs[6])
								&& attrs[6].trim().length() > 8) {
							String[] s = attrs[6].split("/");
							if (s != null && s.length > 2) {
								int day = Integer.parseInt(s[0]);
								int mon = Integer.parseInt(s[1]);
								int y = Integer.parseInt(s[2]);
								v.setLastMaintenanceDate(new DateTime(day, mon, y));
							}
						}
						vehicles.add(v);
					}
					line = bufferedReader.readLine();
				}
			} catch (Exception e) {
				showError("Import error", e.getMessage());
			}
		}
	}

	List<Vehicle> importDataBase() {
		List<Vehicle> vehiclesList = new ArrayList();
		try {
			SQLiteJDBC sqLiteJDBC = new SQLiteJDBC();
			//this line read data from database 
			 vehiclesList.addAll(sqLiteJDBC.selectFromDB());

			 //if nothing found in database generate vehicles
			if (vehiclesList.size() == 0)
				vehiclesList.addAll(Vehicle.getdummy());
		} catch (Exception e) {
			showError("Read Error", e.getMessage());
		}
		return vehiclesList;
	}

	public void savetoDataBase(ActionEvent actionEvent) {
		try {
			//System.err.println("Saving..................");
			SQLiteJDBC sqLiteJDBC = new SQLiteJDBC();
			sqLiteJDBC.insertIntoTable(vehicles);
			//System.err.println("saved Successfully");
		} catch (Exception e) {
			showError("Save Error", e.getMessage());
		}
	}

	public void close(ActionEvent actionEvent) {
		//System.err.println("Exiting..................");
		savetoDataBase(actionEvent);
		javafx.application.Platform.exit();
		//System.err.println("Exited");
		//System.exit(0);
	}
}