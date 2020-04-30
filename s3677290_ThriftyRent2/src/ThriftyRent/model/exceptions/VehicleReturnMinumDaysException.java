package ThriftyRent.model.exceptions;

public class VehicleReturnMinumDaysException extends Exception {
    public VehicleReturnMinumDaysException(int minimumDays) {
        super("Vehicle Can't be returned as minimum rent duration must exceed "+minimumDays+" days!");
    }
}
