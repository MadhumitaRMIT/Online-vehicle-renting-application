package ThriftyRent.model.exceptions;

public class VehicleNotInMaintenance extends Exception {
    public VehicleNotInMaintenance() {
        super("vehicle must be in maintenance status to call comolete maintenance!");
    }
}
