package mate.academy.carsharing.service.telegram;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageService {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public String createRentalNotification(Rental rental, Car car) {
        return String.format(
                """
                        🚗 New rental created!
                        ----------------------------
                        Car: %s %s
                        Type: %s
                        Available cars: %d
                        Rental dates: %s - %s
                        Rental ID: %d
                        ----------------------------
                        Thank you for choosing our service!
                        """,
                car.getBrand(),
                car.getModel(),
                car.getType().name(),
                car.getInventory(),
                rental.getRentalDate().format(DATE_FORMATTER),
                rental.getReturnDate().format(DATE_FORMATTER),
                rental.getId()
        );
    }

    public String createReturnNotification(Rental rental, Car car) {
        return String.format(
                """
                        🔙 Car returned!
                        ----------------------------
                        Car: %s %s
                        Rental ID: %d
                        Planned return date: %s
                        Actual return date: %s
                        Delay: %s
                        Available cars of this model: %d
                        ----------------------------
                        Thank you for using our service!
                        """,
                car.getBrand(),
                car.getModel(),
                rental.getId(),
                rental.getReturnDate().format(DATE_FORMATTER),
                rental.getActualReturnDate().format(DATE_FORMATTER),
                calculateDelay(rental.getReturnDate(), rental.getActualReturnDate()),
                car.getInventory()
        );
    }

    public String createPaymentNotification(Payment payment, Rental rental, Car car) {
        return String.format(
                """
                        💳 New payment created!
                        ----------------------------
                        Car: %s %s
                        Rental ID: %d
                        Payment type: %s
                        Amount to pay: %.2f $
                        Status: %s
                        ----------------------------             
                        """,
                car.getBrand(),
                car.getModel(),
                rental.getId(),
                payment.getType().name(),
                payment.getAmountToPay(),
                payment.getStatus().name()
        );
    }

    public String createSuccessfulPaymentNotification(Payment payment) {
        return String.format(
                """
                        ✅ Payment successful!
                        ----------------------------               
                        Rental ID: %d
                        Payment type: %s
                        Amount paid: %.2f $
                        Payment date: %s
                        ----------------------------
                        Thank you for your payment! Receipt has been sent to your email.
                        """,
                payment.getRental().getId(),
                payment.getType().name(),
                payment.getAmountToPay(),
                LocalDate.now().format(DATE_FORMATTER)
        );
    }

    private String calculateDelay(LocalDate returnDate, LocalDate actualReturnDate) {
        if (actualReturnDate.isAfter(returnDate)) {
            long days = ChronoUnit.DAYS.between(returnDate, actualReturnDate);
            return days + " day(s) delay";
        }
        return "no delay";
    }
}
