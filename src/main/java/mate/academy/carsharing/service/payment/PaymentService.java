package mate.academy.carsharing.service.payment;

import mate.academy.carsharing.dto.payment.PaymentRequestDto;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    Page<PaymentResponseDto> getWithUserId(Pageable pageable, Long userId);

    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);

    void checkSuccessfulPayment(Long paymentId);
}
