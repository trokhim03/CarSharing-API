package mate.academy.carsharing.service.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.dto.rental.RentalReturnRequestDto;
import mate.academy.carsharing.exception.CarNotAvailableException;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.mapper.RentalMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.CarRepository;
import mate.academy.carsharing.repository.RentalRepository;
import mate.academy.carsharing.service.telegram.NotificationMessageService;
import mate.academy.carsharing.service.telegram.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    @InjectMocks
    private RentalServiceImpl rentalService;

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private NotificationMessageService notificationMessageService;

    private Car car;
    private User user;
    private Rental rental;
    private RentalRequestDto rentalRequestDto;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setInventory(10);

        user = new User();
        user.setId(1L);

        rental = new Rental();
        rental.setId(1L);
        rental.setCar(car);

        rentalRequestDto = new RentalRequestDto()
                .setCarId(car.getId())
                .setRentalDate(LocalDate.now())
                .setReturnDate(LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("Create rental - returns rental DTO when valid request")
    void createRental_ShouldReturnRentalDto_WhenValidRequest() {
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalMapper.toModelWithCarIdAndUserId(any(), any(), any())).thenReturn(rental);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toDto(rental)).thenReturn(new RentalResponseDto());
        when(notificationMessageService.createRentalNotification(any(),
                any())).thenReturn("Message");

        RentalResponseDto result = rentalService.createRental(user.getId(), rentalRequestDto);

        assertNotNull(result);
        verify(carRepository).save(car);
        verify(notificationService).sendNotification(any());
    }

    @Test
    @DisplayName("Get all rentals - returns active rentals when isActive=true")
    void getAllByUserId_ShouldReturnActiveRentals_WhenIsActiveTrue() {
        Pageable pageable = Pageable.ofSize(10);
        when(rentalRepository.findAllByUserIdAndActualReturnDateIsNull(pageable, user.getId()))
                .thenReturn(Page.empty());

        Page<RentalResponseDto> result = rentalService.getAllByUserId(pageable, user.getId(), true);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Get by ID - returns rental DTO when valid ID")
    void getById_ShouldReturnRentalDto_WhenValidId() {
        when(rentalRepository.findByIdAndUserId(rental.getId(), user.getId()))
                .thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(new RentalResponseDto());

        RentalResponseDto result = rentalService.getById(rental.getId(), user.getId());

        assertNotNull(result);
    }

    @Test
    @DisplayName("Set return date - returns updated rental when valid request")
    void setDataReturn_ShouldReturnUpdatedRental_WhenValidRequest() {
        RentalReturnRequestDto returnRequestDto = new RentalReturnRequestDto()
                .setRentalId(rental.getId())
                .setActualReturnDate(LocalDate.now());

        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(notificationMessageService.createReturnNotification(any(),
                any())).thenReturn("Message");
        when(rentalMapper.toDto(rental)).thenReturn(new RentalResponseDto());

        RentalResponseDto result = rentalService.setDataReturn(returnRequestDto);

        assertNotNull(result);
        assertEquals(returnRequestDto.getActualReturnDate(), rental.getActualReturnDate());
        verify(carRepository).save(car);
        verify(notificationService).sendNotification(any());
    }

    @Test
    @DisplayName("Create rental - throws exception when car not available")
    void createRental_ShouldThrowException_WhenCarNotAvailable() {
        car.setInventory(0);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        Exception exception = assertThrows(CarNotAvailableException.class,
                () -> rentalService.createRental(user.getId(), rentalRequestDto));

        assertEquals("Car with id " + car.getId() + " is not available", exception.getMessage());
    }

    @Test
    @DisplayName("Create rental - throws exception when car not found")
    void createRental_ShouldThrowException_WhenCarNotFound() {
        Long invalidId = 999L;
        when(carRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> rentalService.createRental(user.getId(),
                        rentalRequestDto.setCarId(invalidId)));

        assertEquals("Can't find car by id: " + invalidId, exception.getMessage());
    }

    @Test
    @DisplayName("Set return date - throws exception when rental not found")
    void setDataReturn_ShouldThrowException_WhenRentalNotFound() {
        Long invalidId = 999L;
        when(rentalRepository.findById(invalidId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> rentalService.setDataReturn(new RentalReturnRequestDto()
                        .setRentalId(invalidId)));

        assertEquals("Can't find rental by id:" + invalidId, exception.getMessage());
    }

    @Test
    @DisplayName("Set return date - throws exception when car not found")
    void setDataReturn_ShouldThrowException_WhenCarNotFound() {
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(carRepository.findById(car.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> rentalService.setDataReturn(new RentalReturnRequestDto()
                        .setRentalId(rental.getId())));

        assertEquals("Can't find car by id:" + car.getId(), exception.getMessage());
    }
}
