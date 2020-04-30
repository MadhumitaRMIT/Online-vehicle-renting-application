package ThriftyRent.model.exceptions;

public class InvalidCarIdException extends Exception {
    public InvalidCarIdException() {
        super("Invalid Id input,Car id should start with C_");
    }
}
