package mate.academy.carsharing.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @NotBlank
    @Positive
    private Long rentalId;
}
