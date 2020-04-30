package ThriftyRent.model.exceptions;

public class VehicleRentInMaintenanceException extends Exception {
    public VehicleRentInMaintenanceException() {
        super("Vehicle Can't be rented as it's in maintenance!");
    }
}
