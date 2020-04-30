package ThriftyRent.interfaces;

import ThriftyRent.model.RentalRecord;
import ThriftyRent.model.Vehicle;
/*
 * used to notify listeners when vehicle is rented
 * */
public interface OnVehicleRented {
    public void onVehicleRented(Vehicle vehicle, RentalRecord rentalRecord);
}
