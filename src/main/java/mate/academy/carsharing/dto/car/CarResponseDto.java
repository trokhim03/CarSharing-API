package mate.academy.carsharing.dto.car;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.carsharing.model.Car;

@Data
@Accessors(chain = true)
public class CarResponseDto {
    private Long id;

    private String model;

    private String brand;

    private Car.Type type;

    private int inventory;

    private BigDecimal dailyFee;
}
