package ThriftyRent.model.exceptions;

public class InvalidCarException extends Exception {
    public InvalidCarException() {
        super("Invalid Id input,car id should start with C_");
    }
}
