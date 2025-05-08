package mate.academy.carsharing.exception;

public class StripePaymentException extends RuntimeException {
    public StripePaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
