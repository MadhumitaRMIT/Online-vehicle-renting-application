package ThriftyRent.model.exceptions;

public class CarRentMinimumThreeDays extends Exception {
    public CarRentMinimumThreeDays() {
        super("Car must be rented for at least 3 days in friday and saturday!");
    }
}
