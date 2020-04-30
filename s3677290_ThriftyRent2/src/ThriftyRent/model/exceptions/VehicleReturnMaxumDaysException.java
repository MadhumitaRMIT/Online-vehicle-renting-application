package ThriftyRent.model.exceptions;

public class VehicleReturnMaxumDaysException extends Exception {
    public VehicleReturnMaxumDaysException(int maximumDays) {
        super("Vehicle return date cannot exceed "+maximumDays+" days!");
    }
}
