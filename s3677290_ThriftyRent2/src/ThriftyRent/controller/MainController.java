package ThriftyRent.controller;

import static ThriftyRent.util.Constants.ALLMAKERS;
import static ThriftyRent.util.Constants.CAR;
import static ThriftyRent.util.Constants.VAN;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import ThriftyRent.interfaces.OnVehicleAdded;
import ThriftyRent.model.Car;
import ThriftyRent.model.Van;
import ThriftyRent.model.Vehicle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainController extends BaseController {

	//comobox for vehicles type filter
	public ComboBox<String> vehicle_type;
	//comobox for number of seats filter
	public ComboBox<String> number_seats;
	//combox for vehicle status filter
	public ComboBox<Vehicle.VehicleStatus> status;
	//combox for vehicle make filter
	public ComboBox<String> make;
	
	//listview to hold vehicles list
	public ListView<Vehicle> vehicles_list;
	Scene scene;

	//these booleans are used to check if filter function for comboxoes are called by user
	//or not as first time we add data first item is selected by default
	//and we want only to filter vehicles upon user selection 
	boolean checkFirstCall = true;
	boolean check2Call = true;
	boolean check3Call = true;
	boolean check4Call = true;

	public MainController() {
		//initiate vehicles list
		vehicles = FXCollections.observableArrayList();
		//import current ata from database 
		vehicles.addAll(importDataBase());
		//vehicles.addAll(Vehicle.getdummy());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//add listener to update list when user add new vehicle
		vehicles.addListener(new ListChangeListener<Vehicle>() {
			@Override
			public void onChanged(Change<? extends Vehicle> c) {
				vehicles_list.setItems(getFilteredList());
				vehicles_list.refresh();
			}
		});
		//add listener for mouse click and open details window
		vehicles_list.setCellFactory(param -> new VehicleItemController() {
			@Override
			protected void updateItem(Vehicle item, boolean empty) {
				setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if (item != null && !empty)
							openVehicleDetails(item, vehicles_list);
					}
				});
				super.updateItem(item, empty);
			}
		});
		//this is another way for sopening details window on sellected vehicle changed
//        vehicles_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Vehicle>() {
//            @Override
//            public void changed(ObservableValue<? extends Vehicle> observable, Vehicle oldValue, Vehicle newValue) {
//                if (newValue != null) {
//                    openVehicleDetails(newValue);
//                }
//            }
//        });
		//add filters data
		vehiclesTypeList.add(0, "All Vehicles");

		vehicle_type.setItems(vehiclesTypeList);
		vehicle_type.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (checkFirstCall) {
					checkFirstCall = false;
					return;
				}
				vehicles_list.setItems(getFilteredList());
				vehicles_list.refresh();
			}
		});
		vehicle_type.getSelectionModel().select(0);

		number_seats.setItems(getNumberOfSeats());
		number_seats.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (check2Call) {
					check2Call = false;
					return;
				}
				vehicles_list.setItems(getFilteredList());
				vehicles_list.refresh();
			}
		});
		number_seats.getSelectionModel().select(0);

		status.setItems(getStatusList());
		status.valueProperty().addListener(new ChangeListener<Vehicle.VehicleStatus>() {
			@Override
			public void changed(ObservableValue<? extends Vehicle.VehicleStatus> observable,
					Vehicle.VehicleStatus oldValue, Vehicle.VehicleStatus newValue) {
				if (check3Call) {
					check3Call = false;
					return;
				}
				vehicles_list.setItems(getFilteredList());
				vehicles_list.refresh();
			}
		});
		status.getSelectionModel().select(0);

		make.setItems(getMakersList());
		make.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (check4Call) {
					check4Call = false;
					return;
				}
				vehicles_list.setItems(getFilteredList());
				vehicles_list.refresh();
			}
		});
		make.getSelectionModel().select(0);

		vehicles_list.setItems(getFilteredList());
		vehicles_list.refresh();
	}

	private ObservableList<String> getNumberOfSeats() {
		ObservableList<String> numbers = FXCollections.observableArrayList();
		numbers.add("All Vehicles");
		numbers.add("4 Seats");
		numbers.add("7 Seats");
		numbers.add("15 Seats");
		return numbers;
	}

	/*
	 * used to create list for vehicle status filter
	 * @return observalelist hold vehiche statuses filter
	 * */
	private ObservableList<Vehicle.VehicleStatus> getStatusList() {
		ObservableList<Vehicle.VehicleStatus> vehicleStatuses = FXCollections.observableArrayList();
		vehicleStatuses.add(Vehicle.VehicleStatus.ALL);
		vehicleStatuses.add(Vehicle.VehicleStatus.Available);
		vehicleStatuses.add(Vehicle.VehicleStatus.Rented);
		vehicleStatuses.add(Vehicle.VehicleStatus.Maintenance);
		return vehicleStatuses;
	}

	private ObservableList<String> getMakersList() {
		ObservableList<String> makers = FXCollections.observableArrayList();
		makers.add(ALLMAKERS);
		for (Vehicle v : vehicles)
			if (!makers.contains(v.getMake()))
				makers.add(v.getMake());
		return makers;
	}

	@Override
	public Window getScene() {
		return vehicles_list.getScene().getWindow();
	}

	/*
	 * open dtails window for selected vehicle
	 * @params Vehicle selected viehcicle to show details
	 * */
	public void openVehicleDetails(Vehicle vehicle) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/vehicle_details.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			scene = vehicle_type.getScene();
			stage.initOwner(scene.getWindow());
			VehicleDetails controller = loader.getController();
			controller.setVehicle(vehicle);
			stage.setTitle("Vehicle Details");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/*
 * open window to add new vehicle
 * **/
	public void openNewVehicle(ActionEvent actionEvent) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/add_vehicle.fxml"));
			Parent root = loader.load();
			Stage stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			scene = vehicle_type.getScene();
			stage.initOwner(scene.getWindow());
			AddVehicle controller = loader.getController();
			controller.setOnVehicleAdded(new OnVehicleAdded() {
				@Override
				public void onVehicleAdded(Vehicle vehicle) {
					vehicles.add(vehicle);
					vehicles_list.setItems(getFilteredList());
					vehicles_list.refresh();
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
//return list of vehicle based on selected filters
	private ObservableList<Vehicle> getFilteredList() {
		String type = vehicle_type.getValue().trim();
		String seats = number_seats.getValue().trim();
		int numberOfSeats = -1;
		try {
			numberOfSeats = Integer.parseInt(number_seats.getValue().substring(0, 2).trim());
		} catch (Exception e) {
		}
		String make_string = make.getValue();
		return filteredList(type, status.getValue(), numberOfSeats, make_string);
	}

	public ObservableList<Vehicle> filteredList(String type, Vehicle.VehicleStatus status, int numberOfSeatss,
			String make) {
		return vehicles.filtered(new Predicate<Vehicle>() {
			@Override
			public boolean test(Vehicle vehicle) {
				if (VAN.equalsIgnoreCase(type))
					if (!(vehicle instanceof Van))
						return false;
				if (CAR.equalsIgnoreCase(type))
					if (!(vehicle instanceof Car))
						return false;
				if (status != Vehicle.VehicleStatus.ALL && status != vehicle.getStatus())
					return false;
				if (numberOfSeatss > 0 && vehicle.getNumberOfPassengers() != numberOfSeatss)
					return false;
				if (!ALLMAKERS.equalsIgnoreCase(make))
					return vehicle.getMake().equalsIgnoreCase(make);
				return true;
			}
		});
	}

}
