package mate.academy.carsharing.dto.car;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.carsharing.model.Car;

@Data
@Accessors(chain = true)
public class CarRequestDto {
    @NotBlank
    private String model;

    @NotBlank

    private String brand;

    @NotBlank
    private Car.Type type;

    @NotBlank
    @Min(0)
    private int inventory;

    @NotBlank
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal dailyFee;
}
