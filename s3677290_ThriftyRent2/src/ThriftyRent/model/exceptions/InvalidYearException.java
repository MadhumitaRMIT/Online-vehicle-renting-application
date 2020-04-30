package ThriftyRent.model.exceptions;

public class InvalidYearException extends Exception {
    public InvalidYearException() {
        super("Invalid year input");
    }
}
