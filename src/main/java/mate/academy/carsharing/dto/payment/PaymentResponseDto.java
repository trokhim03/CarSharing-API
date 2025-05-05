package mate.academy.carsharing.dto.payment;

import java.math.BigDecimal;
import lombok.Data;
import mate.academy.carsharing.model.Payment;

@Data
public class PaymentResponseDto {
    private Long id;

    private Payment.Status status;

    private Payment.Type type;

    private Long rentalId;

    private String sessionUrl;

    private String sessionId;

    private BigDecimal amountToPay;
}
