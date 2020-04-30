package ThriftyRent.model.exceptions;

public class VehicleRentAlreadyRentedException extends Exception {
    public VehicleRentAlreadyRentedException() {
        super("Vehicle Can't be rented as it's already rented by another customer!");
    }
}
