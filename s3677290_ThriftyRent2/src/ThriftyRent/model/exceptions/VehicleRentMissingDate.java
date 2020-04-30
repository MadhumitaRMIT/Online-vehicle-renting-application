package ThriftyRent.model.exceptions;

public class VehicleRentMissingDate extends Exception {
    public VehicleRentMissingDate() {
        super("Rent Date must be specified to rent vehicle!");
    }
}
