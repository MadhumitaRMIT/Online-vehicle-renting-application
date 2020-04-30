package ThriftyRent.model.exceptions;

public class InvalidCarPassengersNumberException extends Exception {
    public InvalidCarPassengersNumberException() {
        super("Invalid car number of passengers,number of passengers for car can be only 4 or 7 car");
    }
}
