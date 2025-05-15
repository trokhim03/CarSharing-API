package mate.academy.carsharing.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @NotNull
    @Positive
    private Long rentalId;
}
