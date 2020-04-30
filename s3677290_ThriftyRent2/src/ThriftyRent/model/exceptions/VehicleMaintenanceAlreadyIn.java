package ThriftyRent.model.exceptions;

public class VehicleMaintenanceAlreadyIn extends Exception {
    public VehicleMaintenanceAlreadyIn() {
        super("vehicle is already in maintenancr statusl and cannot perform maintenance!");
    }
}
