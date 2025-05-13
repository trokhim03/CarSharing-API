package mate.academy.carsharing.service.payment.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.exception.StripePaymentException;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements StripePaymentService {
    private static final String CURRENCY = "usd";
    private static final long SESSION_EXPIRATION_HOURS = 24;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public Session createPaymentSession(Payment payment, Rental rental, BigDecimal amount) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + payment.getId())
                    .setCancelUrl(cancelUrl + payment.getId())
                    .addLineItem(createLineItem(rental, amount))
                    .setExpiresAt(calculateExpirationTime())
                    .build();

            return Session.create(params);
        } catch (StripeException e) {
            throw new StripePaymentException("Failed to create Stripe session", e);
        }
    }

    @Override
    public void setPaymentSessionUrl(Payment payment, Session session) {
        payment.setSessionUrl(session.getUrl());
    }

    @Override
    public Payment.Status checkPaymentStatus(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return mapStripeStatusToPaymentStatus(session.getStatus());
        } catch (StripeException e) {
            throw new StripePaymentException("Failed to check payment status", e);
        }
    }

    private Payment.Status mapStripeStatusToPaymentStatus(String status) {
        return switch (status) {
            case "complete", "succeeded", "paid" -> Payment.Status.PAID;
            case "pending", "canceled", "failed" -> Payment.Status.PENDING;
            default -> throw new IllegalStateException("Unexpected value: " + status);
        };
    }

    private Long calculateExpirationTime() {
        return Instant.now()
                .plusSeconds(SESSION_EXPIRATION_HOURS * 60 * 60)
                .getEpochSecond();
    }

    private SessionCreateParams.LineItem createLineItem(Rental rental, BigDecimal amount) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(CURRENCY)
                                .setUnitAmount(convertToCents(amount))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Rental #" + rental.getId())
                                                .build())
                                .build())
                .build();
    }

    private Long convertToCents(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
}
