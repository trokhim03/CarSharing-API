package mate.academy.carsharing.exceptions;

public class TelegramNotificationException extends RuntimeException {
    public TelegramNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
