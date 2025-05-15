package mate.academy.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalResponseDto {
    private LocalDate rentalDate;

    private LocalDate returnDate;

    private LocalDate actualReturnDate;

    private Long carId;

    private Long userId;
}
