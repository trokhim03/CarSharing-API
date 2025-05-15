package mate.academy.carsharing.service.payment.stripe;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;

public interface StripePaymentService {
    Session createPaymentSession(Payment payment, Rental rental, BigDecimal amount);

    void setPaymentSessionUrl(Payment payment, Session session);

    Payment.Status checkPaymentStatus(String sessionId);
}
