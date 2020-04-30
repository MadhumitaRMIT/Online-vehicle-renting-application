package ThriftyRent.model.exceptions;

public class InvalidVanIdException extends Exception {
    public InvalidVanIdException() {
        super("Invalid Id input,Van id should start with V_");
    }
}
