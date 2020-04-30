package ThriftyRent.model.exceptions;

public class VehicleNotRented extends Exception {
    public VehicleNotRented() {
        super("vehicle must be in rented first so it can be returned!");
    }
}
