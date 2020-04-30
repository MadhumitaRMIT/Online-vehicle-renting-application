package ThriftyRent.model;

import static ThriftyRent.util.Utils.isEmpty;

import ThriftyRent.model.exceptions.CarRentMinimumThreeDays;
import ThriftyRent.model.exceptions.InvalidCarIdException;
import ThriftyRent.model.exceptions.InvalidCarPassengersNumberException;
import ThriftyRent.model.exceptions.InvalidYearException;
import ThriftyRent.model.exceptions.VehicleNotRented;
import ThriftyRent.model.exceptions.VehicleRentAlreadyRentedException;
import ThriftyRent.model.exceptions.VehicleRentInMaintenanceException;
import ThriftyRent.model.exceptions.VehicleRentMinimumDays;
import ThriftyRent.model.exceptions.VehicleRentMissingCustomer;
import ThriftyRent.model.exceptions.VehicleRentMissingDate;
import ThriftyRent.model.exceptions.VehicleReturnMaxumDaysException;
import ThriftyRent.util.DateTime;

public class Car extends Vehicle {
	public Car() {

	}

	public Car(String id, String make, String model, String year, String image, int number_of_passengers)
			throws Exception {
		if (id.length() < 2 || !"C_".equals(id.substring(0, 2)))
			throw new InvalidCarIdException();
		this.vehicle_id = id;
		this.make = make;
		this.model = model;
		if (image != null && image.trim().length() != 0)
			this.image = image;
		if (number_of_passengers != 4 && number_of_passengers != 7)
			throw new InvalidCarPassengersNumberException();
		if (number_of_passengers == 4)
			price = 78;
		else
			price = 113;
		latePrice = price * 125 / 100;
		this.setNumberOfPassengers(number_of_passengers);
		try {
			this.year = Integer.parseInt(year);
		} catch (Exception e) {
			throw new InvalidYearException();
		}
	}

	@Override
	public RentalRecord rent(String customerId, DateTime rentDate, int numOfRentDay) throws Exception {
		if (rentDate == null)
			throw new VehicleRentMissingDate();
		if(DateTime.diffDays(new DateTime(),rentDate )>0)
			throw new Exception("rent date must be in future!");
		RentalRecord rentalRecord = new RentalRecord();
		String day = rentDate.getNameOfDay().toLowerCase().trim();
		if (day.equalsIgnoreCase("friday") || day.equalsIgnoreCase("saturday")) {
			if (numOfRentDay < 3)
				throw new CarRentMinimumThreeDays();
		} else if (numOfRentDay < 2)
			throw new VehicleRentMinimumDays(2);
		if (status == VehicleStatus.Maintenance)
			throw new VehicleRentInMaintenanceException();
		if (status == VehicleStatus.Rented)
			throw new VehicleRentAlreadyRentedException();
		if (isEmpty(customerId))
			throw new VehicleRentMissingCustomer();

		String id = vehicle_id + "_" + customerId + "_" + rentDate.toString().replace("/", "");
		rentalRecord.setRecord_id(id);
		rentalRecord.setRentDate(rentDate);
		rentalRecord.setEstimatedReturnDate(new DateTime(rentDate, numOfRentDay));
		rentalRecords.add(rentalRecord);
		status = VehicleStatus.Rented;
		return rentalRecord;
	}

	@Override
	public void returnVehicle(DateTime returnDate) throws Exception {
		if (rentalRecords.size() == 0 || rentalRecords.get(rentalRecords.size() - 1).getActualReturnDate() != null)
			throw new VehicleNotRented();
		if (DateTime.diffDays(returnDate, rentalRecords.get(rentalRecords.size() - 1).getRentDate()) > 14)
			throw new VehicleReturnMaxumDaysException(14);
		super.returnVehicle(returnDate);
	}
}
