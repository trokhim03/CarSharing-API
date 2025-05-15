package mate.academy.carsharing.service.rental;

import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.dto.rental.RentalReturnRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto createRental(Long userId, RentalRequestDto rentalRequestDto);

    Page<RentalResponseDto> getAllByUserId(Pageable pageable, Long userId, boolean isActive);

    RentalResponseDto getById(Long userId, Long rentalId);

    RentalResponseDto setDataReturn(RentalReturnRequestDto rentalReturnRequestDto);
}
