package ThriftyRent.model.exceptions;

public class InvalidVanPassengersNumberException extends Exception {
    public InvalidVanPassengersNumberException() {
        super("Invalid number of passengers,number of passengers can be only 15 for van");
    }
}
