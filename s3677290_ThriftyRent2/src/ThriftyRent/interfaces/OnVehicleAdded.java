package ThriftyRent.interfaces;

import ThriftyRent.model.Vehicle;
/*
 * used to notify listeners when new vehicle is added
 * */
public interface OnVehicleAdded {
    public void onVehicleAdded(Vehicle vehicle);
}
