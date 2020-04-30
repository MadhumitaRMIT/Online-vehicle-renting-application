package ThriftyRent.model;

import static ThriftyRent.util.Utils.isEmpty;

import ThriftyRent.model.exceptions.InvalidVanIdException;
import ThriftyRent.model.exceptions.InvalidVanPassengersNumberException;
import ThriftyRent.model.exceptions.InvalidYearException;
import ThriftyRent.model.exceptions.VehicleRentAlreadyRentedException;
import ThriftyRent.model.exceptions.VehicleRentInMaintenanceException;
import ThriftyRent.model.exceptions.VehicleRentMissingCustomer;
import ThriftyRent.model.exceptions.VehicleRentMissingDate;
import ThriftyRent.model.exceptions.VehicleRentNeedMaintaince;
import ThriftyRent.model.exceptions.VehicleRentZeroNumberOfDays;
import ThriftyRent.util.DateTime;

public class Van extends Vehicle {
	private static final int MAINTAINENCEINTERVAL = 12;

	public Van() {
		price = 235;
		latePrice = 299;
	}

	public Van(String id, String make, String model, String year, String image, int number_of_passengers)
			throws Exception {
		if (id.length() < 2 || !"V_".equals(id.substring(0, 2)))
			throw new InvalidVanIdException();
		this.vehicle_id = id;
		this.make = make;
		this.model = model;
		if (image != null && image.trim().length() != 0)
			this.image = image;
		price = 235;
		latePrice = 299;
		if (number_of_passengers != 15)
			throw new InvalidVanPassengersNumberException();
		this.setNumberOfPassengers(15);
		try {
			this.year = Integer.parseInt(year);
		} catch (Exception e) {
			throw new InvalidYearException();
		}

	}

	@Override
	public RentalRecord rent(String customerId, DateTime rentDate, int numOfRentDay) throws Exception {
		if (status == VehicleStatus.Maintenance)
			throw new VehicleRentInMaintenanceException();
		if (status == VehicleStatus.Rented)
			throw new VehicleRentAlreadyRentedException();
		if (rentDate == null)
			throw new VehicleRentMissingDate();
		if(DateTime.diffDays(new DateTime(),rentDate )>0)
			throw new Exception("rent date must be in future!");
		if (numOfRentDay < 1)
			throw new VehicleRentZeroNumberOfDays();
		if (isEmpty(customerId))
			throw new VehicleRentMissingCustomer();
		RentalRecord rentalRecord = new RentalRecord();
		String id = vehicle_id + "_" + customerId + "_" + rentDate.toString().replace("/", "");
		rentalRecord.setRecord_id(id);
		rentalRecord.setRentDate(rentDate);
		rentalRecord.setEstimatedReturnDate(new DateTime(rentDate, numOfRentDay));
		DateTime estimatedMainenanceDate = new DateTime(lastMaintenanceDate, MAINTAINENCEINTERVAL);
		if (DateTime.diffDays(estimatedMainenanceDate, new DateTime(lastMaintenanceDate)) > MAINTAINENCEINTERVAL)
			throw new VehicleRentNeedMaintaince(
					DateTime.diffDays(estimatedMainenanceDate, lastMaintenanceDate) - MAINTAINENCEINTERVAL);
		rentalRecords.add(rentalRecord);
		status = VehicleStatus.Rented;
		return rentalRecord;
	}
}
