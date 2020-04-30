package ThriftyRent.model.exceptions;

public class VehicleRentMissingCustomer extends Exception {
    public VehicleRentMissingCustomer() {
        super("You must provide id of customer renting the vehicle!");
    }
}
