package mate.academy.carsharing.service.car;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.car.CarRequestDto;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.mapper.CarMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.CarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarMapper carMapper;
    private final CarRepository carRepository;

    @Override
    public CarResponseDto createCar(CarRequestDto carRequestDto) {
        Car car = carMapper.toModel(carRequestDto);
        carRepository.save(car);
        return carMapper.toDto(car);
    }

    @Override
    public Page<CarResponseDto> getCars(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toDto);
    }

    @Override
    public CarResponseDto findById(Long carId) {
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new EntityNotFoundException("Can't find car with id: " + carId)
        );
        return carMapper.toDto(car);
    }

    @Override
    public CarResponseDto updateById(Long carId, CarRequestDto carRequestDto) {
        Car car = carRepository.findById(carId).orElseThrow(
                () -> new EntityNotFoundException("Can't find car with id: " + carId)
        );
        carMapper.updateModelFromDto(carRequestDto, car);
        carRepository.save(car);
        return carMapper.toDto(car);
    }

    @Override
    public void deleteById(Long carId) {
        carRepository.deleteById(carId);
    }
}
