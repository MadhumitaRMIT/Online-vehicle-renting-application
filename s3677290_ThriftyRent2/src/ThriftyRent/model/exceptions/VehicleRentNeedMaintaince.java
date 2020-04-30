package ThriftyRent.model.exceptions;

public class VehicleRentNeedMaintaince extends Exception {
    public VehicleRentNeedMaintaince(int maxRent) {
        super("Vehicle must perform maintenanceand can be rented for max of "+maxRent+" days!");
    }
}
