package ThriftyRent.model.exceptions;

public class InvalidIdException extends Exception {
    public InvalidIdException() {
        super("Invalid Id input");
    }
}
