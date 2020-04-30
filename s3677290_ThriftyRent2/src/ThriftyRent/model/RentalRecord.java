package ThriftyRent.model;

import static ThriftyRent.util.Labels.RecordDateLabel;
import static ThriftyRent.util.Labels.RecordIDLabel;
import static ThriftyRent.util.Labels.actualReturnDateLabel;
import static ThriftyRent.util.Labels.estimatedReturnDateLabel;
import static ThriftyRent.util.Labels.lateFeeLabel;
import static ThriftyRent.util.Labels.rentalFeeLabel;
import static ThriftyRent.util.Utils.getNewLine;

import ThriftyRent.util.DateTime;

public class RentalRecord implements Comparable {

	private String vehicle_id;
	private String record_id;// vehicleId_ + customerId_ + rentDate (8 digit format:
	// ddmmyyyy)
	private DateTime rentDate;
	private DateTime estimatedReturnDate;
	private DateTime actualReturnDate;
	private double rentalFee;
	private double lateFee;
	private int minimum;

	public void setVehicle_id(String vehicle_id) {
		this.vehicle_id = vehicle_id;
	}

	public String getVehicle_id() {
		return vehicle_id;
	}

	public int getMinimum() {
		return minimum;
	}

	private void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public String getRecord_id() {
		return record_id;
	}

	public void setRecord_id(String record_id) {
		this.record_id = record_id;
	}

	public DateTime getRentDate() {
		return rentDate;
	}

	public void setRentDate(DateTime rentDate) {
		String day = rentDate.getNameOfDay().toLowerCase().trim();
		if (day.equalsIgnoreCase("friday") || day.equalsIgnoreCase("saturday"))
			minimum = 3;
		else
			minimum = 2;
		this.rentDate = rentDate;
	}

	public DateTime getEstimatedReturnDate() {
		return estimatedReturnDate;
	}

	public void setEstimatedReturnDate(DateTime estimatedReturnDate) {
		this.estimatedReturnDate = estimatedReturnDate;
	}

	public DateTime getActualReturnDate() {
		return actualReturnDate;
	}

	public void setActualReturnDate(DateTime actualReturnDate) {
		this.actualReturnDate = actualReturnDate;
	}

	public double getRentalFee() {
		return rentalFee;
	}

	public void setRentalFee(double rentalFee) {
		this.rentalFee = rentalFee;
	}

	public double getLateFee() {
		return lateFee;
	}

	public void setLateFee(double lateFee) {
		this.lateFee = lateFee;
	}

	public String getDetails() {
		return RecordIDLabel + getRecord_id() + getNewLine() + RecordDateLabel + getRentDate() + getNewLine()
				+ estimatedReturnDateLabel + getEstimatedReturnDate() + getNewLine()
				+ (null == getActualReturnDate() ? "" : (actualReturnDateLabel + getActualReturnDate()) + getNewLine())
				+ ((getRentalFee() == 0.0) ? "" : (rentalFeeLabel + getRentalFee()) + getNewLine())
				+ ((getLateFee() == 0.0) ? "" : (lateFeeLabel + getLateFee()));
	}

	@Override
	public String toString() {
		return getRecord_id() + ":" + getRentDate() + ":" + getEstimatedReturnDate() + ":"
				+ (null == getActualReturnDate() ? "none" : getActualReturnDate()) + ":"
				+ ((getRentalFee() == 0.0) ? "none" : getRentalFee()) + ":"
				+ ((getLateFee() == 0.0) ? "none" : getLateFee());
	}

	@Override
	public int compareTo(Object arg0) {
		if (arg0 instanceof RentalRecord) {
			DateTime d1, d2;
			if (((RentalRecord) arg0).getActualReturnDate() == null)
				d2 = ((RentalRecord) arg0).getEstimatedReturnDate();
			else
				d2 = ((RentalRecord) arg0).getActualReturnDate();
			if (actualReturnDate == null)
				d1 = estimatedReturnDate;
			else
				d1 = actualReturnDate;
			return DateTime.diffDays(d1, d2);
		}
		return 0;
	}
}
