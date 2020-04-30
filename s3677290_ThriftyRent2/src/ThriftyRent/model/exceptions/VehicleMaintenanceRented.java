package ThriftyRent.model.exceptions;

public class VehicleMaintenanceRented extends Exception {
    public VehicleMaintenanceRented() {
        super("vehicle must be in available status to call perform maintenance!");
    }
}
