package mate.academy.carsharing.service.car;

import mate.academy.carsharing.dto.car.CarRequestDto;
import mate.academy.carsharing.dto.car.CarResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarResponseDto createCar(CarRequestDto carRequestDto);

    Page<CarResponseDto> getCars(Pageable pageable);

    CarResponseDto findById(Long carId);

    CarResponseDto updateById(Long carId, CarRequestDto carRequestDto);

    void deleteById(Long carId);
}
