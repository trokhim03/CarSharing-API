package mate.academy.carsharing.service.car;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.dto.car.CarRequestDto;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.mapper.CarMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;
    private Car oldCar;
    private CarRequestDto carRequestDto;
    private CarResponseDto carResponseDto;

    @BeforeEach
    void setUp() {
        car = new Car()
                .setId(1L)
                .setModel("M5 F90")
                .setBrand("BMW")
                .setType(Car.Type.SEDAN)
                .setInventory(10)
                .setDailyFee(BigDecimal.valueOf(250));

        oldCar = new Car()
                .setId(1L)
                .setModel("M5 F90")
                .setBrand("BMW")
                .setType(Car.Type.SEDAN)
                .setInventory(5)
                .setDailyFee(BigDecimal.valueOf(150));

        carRequestDto = new CarRequestDto()
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());

        carResponseDto = new CarResponseDto()
                .setId(car.getId())
                .setModel(car.getModel())
                .setBrand(car.getBrand())
                .setType(car.getType())
                .setInventory(car.getInventory())
                .setDailyFee(car.getDailyFee());
    }

    @Test
    @DisplayName("Create car - returns car DTO when valid request")
    void createCar_ShouldReturnCarDto_WhenValidRequest() {
        when(carMapper.toModel(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.createCar(carRequestDto);

        assertThat(result).isEqualTo(carResponseDto);
        verify(carMapper).toModel(carRequestDto);
        verify(carRepository).save(car);
        verify(carMapper).toDto(car);
    }

    @Test
    @DisplayName("Get cars - returns page of cars when valid pageable")
    void getCars_ShouldReturnPageOfCars_WhenValidPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Car> carPage = new PageImpl<>(List.of(car), pageable, 1);

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        Page<CarResponseDto> result = carService.getCars(pageable);

        assertThat(result.getContent()).hasSize(1).contains(carResponseDto);
        verify(carRepository).findAll(pageable);
        verify(carMapper).toDto(car);
    }

    @Test
    @DisplayName("Find by ID - returns car DTO when valid ID")
    void findById_ShouldReturnCarDto_WhenValidId() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);

        CarResponseDto result = carService.findById(1L);

        assertThat(result).isEqualTo(carResponseDto);
        verify(carRepository).findById(1L);
        verify(carMapper).toDto(car);
    }

    @Test
    @DisplayName("Update by ID - returns updated car DTO when valid ID and request")
    void updateById_ShouldReturnUpdatedCarDto_WhenValidIdAndRequest() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(oldCar));
        when(carMapper.toDto(oldCar)).thenReturn(carResponseDto);

        CarResponseDto result = carService.updateById(1L, carRequestDto);

        assertThat(result).isEqualTo(carResponseDto);
        verify(carRepository).findById(1L);
        verify(carMapper).updateModelFromDto(carRequestDto, oldCar);
        verify(carRepository).save(oldCar);
        verify(carMapper).toDto(oldCar);
    }

    @Test
    @DisplayName("Delete by ID - successfully deletes car when valid ID")
    void deleteById_ShouldDeleteCar_WhenValidId() {
        carService.deleteById(1L);
        verify(carRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Find by ID - throws exception when invalid ID")
    void findById_ShouldThrowException_WhenInvalidId() {
        Long invalidId = 999L;
        when(carRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.findById(invalidId)
        );

        assertThat(exception.getMessage()).isEqualTo("Can't find car with id: " + invalidId);
        verify(carRepository).findById(invalidId);
    }

    @Test
    @DisplayName("Update by ID - throws exception when invalid ID")
    void updateById_ShouldThrowException_WhenInvalidId() {
        Long invalidId = 999L;
        when(carRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> carService.updateById(invalidId, carRequestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Can't find car with id: " + invalidId);
        verify(carRepository).findById(invalidId);
    }
}
