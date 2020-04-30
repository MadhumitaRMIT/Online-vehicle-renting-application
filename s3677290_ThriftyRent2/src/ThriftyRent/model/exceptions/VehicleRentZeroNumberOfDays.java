package ThriftyRent.model.exceptions;

public class VehicleRentZeroNumberOfDays extends Exception {
    public VehicleRentZeroNumberOfDays() {
        super("Vehicle must be rented for at least one day!");
    }
}
