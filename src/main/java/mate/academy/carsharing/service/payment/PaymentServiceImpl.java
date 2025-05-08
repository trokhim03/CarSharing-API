package mate.academy.carsharing.service.payment;

import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.payment.PaymentRequestDto;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.exception.PaymentException;
import mate.academy.carsharing.mapper.PaymentMapper;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.repository.PaymentRepository;
import mate.academy.carsharing.repository.RentalRepository;
import mate.academy.carsharing.service.payment.stripe.StripePaymentService;
import mate.academy.carsharing.service.telegram.NotificationMessageService;
import mate.academy.carsharing.service.telegram.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal DAILY_FINE_MULTIPLIER = BigDecimal.valueOf(1.4);
    private final PaymentRepository paymentRepository;
    private final StripePaymentService stripePaymentService;
    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;
    private final NotificationService notificationService;
    private final NotificationMessageService notificationMessageService;

    @Override
    public Page<PaymentResponseDto> getWithUserId(Pageable pageable, Long userId) {
        return paymentRepository.findAllByRental_UserId(pageable, userId)
                .map(paymentMapper::toDto);
    }

    @Override
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        Rental rental = rentalRepository.findById(paymentRequestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental by id:"
                        + paymentRequestDto.getRentalId()));

        Payment.Type type = determinePaymentType(rental);

        if (paymentRepository.existsByRentalIdAndStatus(rental.getId(), Payment.Status.PENDING)) {
            throw new PaymentException("Active payment already exists for this rental");
        }

        BigDecimal amountToPay = calculateAmountToPay(rental, type);

        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setType(type);
        payment.setStatus(Payment.Status.PENDING);
        payment.setAmountToPay(amountToPay);
        payment.setSessionId("none");
        payment.setSessionUrl("http://none.none");

        paymentRepository.save(payment);

        Session paymentSession = stripePaymentService
                .createPaymentSession(payment, rental, amountToPay);

        stripePaymentService.setPaymentSessionUrl(payment, paymentSession);
        payment.setSessionId(paymentSession.getId());
        String message = notificationMessageService
                .createPaymentNotification(payment, rental, rental.getCar());
        notificationService.sendNotification(message);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public void checkSuccessfulPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find payment by id: " + paymentId));

        Payment.Status oldStatus = payment.getStatus();
        Payment.Status newStatus = stripePaymentService.checkPaymentStatus(payment.getSessionId());

        if (oldStatus != newStatus) {
            payment.setStatus(newStatus);
            paymentRepository.save(payment);
        }
        if (newStatus == Payment.Status.PAID) {
            String message = notificationMessageService
                    .createSuccessfulPaymentNotification(payment);
            notificationService.sendNotification(message);
        }
    }

    private Payment.Type determinePaymentType(Rental rental) {
        if (rental.getActualReturnDate() == null) {
            throw new PaymentException("Cannot create payment - car is not returned yet");
        }

        if (rental.getActualReturnDate().isAfter(rental.getReturnDate())) {
            return Payment.Type.FINE;
        }

        return Payment.Type.PAYMENT;
    }

    private BigDecimal calculateAmountToPay(Rental rental, Payment.Type type) {
        long basicRentalDays = Math.max(1, rental.getRentalDate()
                .until(rental.getReturnDate(), ChronoUnit.DAYS));
        BigDecimal basicCost = rental.getCar().getDailyFee()
                .multiply(BigDecimal.valueOf(basicRentalDays));

        if (type == Payment.Type.PAYMENT) {
            return basicCost;
        } else {
            long overdueDays = Math.max(1, rental.getReturnDate()
                    .until(rental.getActualReturnDate(), ChronoUnit.DAYS));
            BigDecimal fine = rental.getCar().getDailyFee()
                    .multiply(BigDecimal.valueOf(overdueDays))
                    .multiply(DAILY_FINE_MULTIPLIER);

            return basicCost.add(fine);
        }
    }
}
