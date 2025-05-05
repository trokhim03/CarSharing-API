package mate.academy.carsharing.exceptions;

public class StripePaymentException extends RuntimeException {
    public StripePaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
