package ThriftyRent.model;

import static ThriftyRent.util.Utils.getNewLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ThriftyRent.model.exceptions.VehicleMaintenanceRented;
import ThriftyRent.model.exceptions.VehicleNotRented;
import ThriftyRent.model.exceptions.VehicleReturnMinumDaysException;
import ThriftyRent.util.DateTime;
import ThriftyRent.util.Labels;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class Vehicle {
	protected static int i;
	protected String vehicle_id;
	protected String make;
	protected String model;
	protected String image = "No_image_available.png";
	protected DateTime lastMaintenanceDate;
	protected int numberOfPassengers;
	protected VehicleStatus status;// 0 rented; 1 available ; 2 under maintenance
	protected int year;
	protected int price;
	protected int latePrice;
	protected ObservableList<RentalRecord> rentalRecords;

	public Vehicle() {
		status = VehicleStatus.Available;
		rentalRecords = FXCollections.observableArrayList();
	}

	@Override
	public String toString() {
		return vehicle_id + ":" + year + ":" + make + ":" + model + ":" + numberOfPassengers + ":" + status + ":"
				+ lastMaintenanceDate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		if (image != null && image.trim().length() != 0)
			this.image = image;
	}

	public String getVehicle_id() {
		return vehicle_id;
	}

	public void setVehicle_id(String vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public DateTime getLastMaintenanceDate() {
		return lastMaintenanceDate;
	}

	public void setLastMaintenanceDate(DateTime lastMaintenanceDate) {
		this.lastMaintenanceDate = lastMaintenanceDate;
	}

	public int getNumberOfPassengers() {
		return numberOfPassengers;
	}

	public void setNumberOfPassengers(int numberOfPassengers) {
		this.numberOfPassengers = numberOfPassengers;
		if (this instanceof Car) {
			if (numberOfPassengers == 4)
				price = 78;
			else
				price = 113;
			latePrice = price * 125 / 100;
		} else {
			price = 235;
			latePrice = 299;
		}
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setLatePrice(int latePrice) {
		this.latePrice = latePrice;
	}

	public int getPrice() {
		return price;
	}

	public int getLatePrice() {
		return latePrice;
	}

	public VehicleStatus getStatus() {
		return status;
	}

	public void setStatus(VehicleStatus status) {
		this.status = status;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public ObservableList<RentalRecord> getRentalRecords() {
		return rentalRecords;
	}

	public void setRentalRecords(ObservableList<RentalRecord> rentalRecords) {
		this.rentalRecords = rentalRecords;
	}

	/*
	 * This method should check for pre-conditions to determine if that vehicle can
	 * be rented. For example, this method will return false when that vehicle is
	 * currently being rented or is under maintenance. You should check any other
	 * possible conditions which would make this method return false. If the vehicle
	 * is available for rent, this method will perform all necessary operations to
	 * update the information stored in this vehicle object based on the input
	 * parameters. For example, updating the vehicle status, creating a new rental
	 * record, updating the rental record collection of the vehicle, and any other
	 * operations you consider necessary.
	 *
	 * @param String customerId customer id who will rent the vehicle
	 * 
	 * @param @<code>DateTime</code>rentDate of vehicle rent
	 * 
	 * @param int numOfRentDay numOfRentDay the vehicle will be rented
	 */
	public abstract RentalRecord rent(String customerId, DateTime rentDate, int numOfRentDay) throws Exception;

	/*
	 * This method should check for pre-conditions to determine if that vehicle can
	 * be returned. For example, this method will return false when the given
	 * returnDate is prior to the rentDate stored in the rental record. You should
	 * check any other possible conditions which would make this method return
	 * false. If the vehicle can be returned, this method will perform all necessary
	 * operations to update the information stored in this vehicle object based on
	 * the input parameters. For example, updating the vehicle status, updating the
	 * corresponding rental record with the rental fee, the late fee, and any other
	 * operations you consider necessary
	 * 
	 * @param @<code>DateTime</code>returnDate return date of vehicle.
	 */
	public void returnVehicle(DateTime returnDate) throws Exception {
		if (rentalRecords.size() == 0 || rentalRecords.get(rentalRecords.size() - 1).getActualReturnDate() != null)
			throw new VehicleNotRented();
		if (DateTime.diffDays(returnDate, rentalRecords.get(rentalRecords.size() - 1).getRentDate()) < 1) {
			throw new VehicleReturnMinumDaysException(0);
		} else if (DateTime.diffDays(returnDate, rentalRecords.get(rentalRecords.size() - 1)
				.getRentDate()) < rentalRecords.get(rentalRecords.size() - 1).getMinimum()) {
			throw new VehicleReturnMinumDaysException(rentalRecords.get(rentalRecords.size() - 1).getMinimum());
		} else {
			RentalRecord rentalRecord = rentalRecords.get(rentalRecords.size() - 1);
			rentalRecord.setActualReturnDate(returnDate);
			status = VehicleStatus.Available;
			rentalRecord.setRentalFee(calculateFees(rentalRecord));
			rentalRecord.setLateFee(calculateLateFees(rentalRecord));
		}
	}

	private double calculateFees(RentalRecord rentalRecord) {
		int diff = DateTime.diffDays(rentalRecord.getActualReturnDate(), rentalRecord.getEstimatedReturnDate());
		if (diff > 0)
			diff = DateTime.diffDays(rentalRecord.getEstimatedReturnDate(), rentalRecord.getRentDate());
		else
			diff = DateTime.diffDays(rentalRecord.getActualReturnDate(), rentalRecord.getRentDate());
		return price * diff;
	}

	private double calculateLateFees(RentalRecord rentalRecord) {
		int diff = DateTime.diffDays(rentalRecord.getActualReturnDate(), rentalRecord.getEstimatedReturnDate());
		if (diff > 0)
			return latePrice * diff;
		return 0;
	}

	/*
	 * This method should check for pre-conditions to determine if maintenance
	 * operations can be performed in that vehicle. For example, this method will
	 * return false when the vehicle is currently being rented. You should check any
	 * other possible conditions which would make this method return false. If the
	 * vehicle is ready for maintenance, this method will perform all necessary
	 * operations to update the information stored in this vehicle object when a
	 * maintenance happens. Finally, this method will return true if the vehicle is
	 * now under maintenance.
	 */
	public void performMaintenance() throws VehicleMaintenanceRented {
		if (status == VehicleStatus.Available)
			status = VehicleStatus.Maintenance;
		else if (status == VehicleStatus.Maintenance)
			throw new VehicleMaintenanceRented();
		else
			throw new VehicleMaintenanceRented();
	}

	/*
	 * This method should check for pre-conditions. For example, when this vehicle
	 * is currently being rented, it does not make sense to call completeMaintenance
	 * method on this vehicle object, and therefore this method should return false.
	 * You should check any other possible conditions which would make this method
	 * return false. If it is possible to complete maintenance, this method will
	 * perform all necessary operations to update the information stored in this
	 * vehicle object now that maintenance has been finished. Finally, this method
	 * will return true to indicate that the maintenance of this vehicle has
	 * finished.
	 * 
	 * @param @<code>DateTime</code>completionDate completion date of vehicle
	 * maintenance.
	 */
	public void completeMaintenance(DateTime completionDate) throws Exception {
		if (status == VehicleStatus.Maintenance) {
			if (completionDate == null)
				throw new Exception("You must specify complete Maintenance Date!");
			lastMaintenanceDate = completionDate;
			status = VehicleStatus.Available;
		} else
			throw new VehicleMaintenanceRented();
	}

	/*
	 * The returned string contains all information about the vehicle
	 */
	public String getDetails() {
		return Labels.VehicleIDLabel + vehicle_id + getNewLine() + Labels.YearDateLabel + year + getNewLine()
				+ Labels.makeLabel + make + getNewLine() + Labels.madeLabel + model + getNewLine()
				+ Labels.numberOfSeatsLabel + numberOfPassengers + getNewLine() + Labels.StatusLabel + status
				+ getNewLine();

	}

	public static enum VehicleStatus {
		Rented, Available, Maintenance, ALL;

		public static VehicleStatus from(String attr) {
			if (attr == null || attr.trim().length() == 0)
				return Available;
			if ("m".equalsIgnoreCase(attr.charAt(0) + ""))
				return Maintenance;
			if ("r".equalsIgnoreCase(attr.charAt(0) + ""))
				return Rented;
			return Available;
		}

		@Override
		public String toString() {
			if (this == ALL)
				return "All Vehicles";
			return super.toString();
		}
	}

	public static List<Vehicle> getdummy() {
		try {
			List<Vehicle> vehicles = new ArrayList<>();
			Van v1 = new Van("V_1", "Toyota", "hiace", "" + 2012, "van1.jpg", 15);
			Van v2 = new Van("V_2", "ford", "wagon", "" + 2012, "van1.jpg", 15);
			Van v3 = new Van("V_3", "Toyota", "hiace", "" + 2012, "van2.jpg", 15);
			Van v4 = new Van("V_4", "Toyota", "hiace", "" + 2012, "van1.jpg", 15);
			Van v5 = new Van("V_5", "Toyota", "hiace", "" + 2012, "van2.jpg", 15);
			Car c1 = new Car("C_1", "Toyota", "corolla", "" + 2012, "3.jpg", 4);
			Car c2 = new Car("C_2", "Toyota", "corolla", "" + 2012, "1.jpg", 4);
			Car c3 = new Car("C_3", "Toyota", "tundra", "" + 2012, "2.jpg", 7);
			Car c4 = new Car("C_4", "Toyota", "camry", "" + 2012, "3.jpg", 4);
			Car c5 = new Car("C_5", "Toyota", "land crusor", "" + 2012, "2.jpg", 4);
			Car c6 = new Car("C_6", "hyndai", "santa", "" + 2012, "1.jpg", 4);
			Car c7 = new Car("C_7", "hyndai", "tucson", "" + 2012, "3.jpg", 4);
			Car c8 = new Car("C_8", "hyndai", "elantra", "" + 2012, "1.jpg", 7);
			Car c9 = new Car("C_9", "Toyota", "corolla", "" + 2012, "2.jpg", 4);
			Car c10 = new Car("C_10", "Toyota", "corolla", "" + 2012, "3.jpg", 7);
			vehicles.add(v1);
			vehicles.add(v2);
			vehicles.add(v3);
			vehicles.add(v4);
			vehicles.add(v5);
			vehicles.add(c1);
			vehicles.add(c2);
			vehicles.add(c3);
			vehicles.add(c4);
			vehicles.add(c5);
			vehicles.add(c6);
			vehicles.add(c7);
			vehicles.add(c8);
			vehicles.add(c9);
			vehicles.add(c10);
			for (i = 0; i < vehicles.size(); i++) {
				Random rand=new Random(System.currentTimeMillis());
				Vehicle v = vehicles.get(i);
				v.rent("C_" + rand.nextInt(100), new DateTime(), 3);
				DateTime r = new DateTime(3);
				v.returnVehicle(new DateTime(3));

				r = new DateTime(r, 1);
				v.rent("C_" +  rand.nextInt(100), r, 3);
				r = new DateTime(r, 3);
				v.returnVehicle(r);

				r = new DateTime(r, 1);
				v.rent("C_" +  rand.nextInt(100), r, 3);
				r = new DateTime(r, 4);
				v.returnVehicle(r);
			}
			return vehicles;
		} catch (Exception e) {
			//System.out.println(i + " " + e.getMessage());
		}
		return new ArrayList<>();
	}
}
