package ThriftyRent.model.exceptions;

public class VehicleRentMinimumDays extends Exception {
    public VehicleRentMinimumDays(int days) {
        super("Vehicle must be rented for at least "+days+" days!");
    }
}
