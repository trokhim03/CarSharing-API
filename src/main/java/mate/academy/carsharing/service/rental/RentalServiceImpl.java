package mate.academy.carsharing.service.rental;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.dto.rental.RentalReturnRequestDto;
import mate.academy.carsharing.exceptions.CarNotAvailableException;
import mate.academy.carsharing.exceptions.EntityNotFoundException;
import mate.academy.carsharing.mapper.RentalMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.repository.CarRepository;
import mate.academy.carsharing.repository.RentalRepository;
import mate.academy.carsharing.service.telegram.NotificationMessageService;
import mate.academy.carsharing.service.telegram.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final NotificationService notificationService;
    private final NotificationMessageService notificationMessageService;

    @Override
    public RentalResponseDto createRental(Long userId, RentalRequestDto rentalRequestDto) {
        Car car = carRepository
                .findById(rentalRequestDto.getCarId()).orElseThrow(() ->
                        new EntityNotFoundException("Can't find car by id: "
                                + rentalRequestDto.getCarId()));
        if (car.getInventory() < 1) {
            throw new CarNotAvailableException("Car with id "
                    + rentalRequestDto.getCarId() + " is not available");
        }

        car.setInventory(car.getInventory() - 1);

        Rental rental = rentalMapper.toModelWithCarIdAndUserId(rentalRequestDto,
                car.getId(), userId);
        carRepository.save(car);
        rentalRepository.save(rental);
        String message = notificationMessageService.createRentalNotification(rental, car);
        notificationService.sendNotification(message);
        return rentalMapper.toDto(rental);
    }

    @Override
    public Page<RentalResponseDto> getAllByUserId(Pageable pageable,
                                                  Long userId, boolean isActive) {
        Page<Rental> rentalsPage;
        if (isActive) {
            rentalsPage = rentalRepository
                    .findAllByUserIdAndActualReturnDateIsNull(pageable, userId);

        } else {
            rentalsPage = rentalRepository
                    .findAllByUserIdAndActualReturnDateIsNotNull(pageable, userId);
        }
        return rentalsPage.map(rentalMapper::toDto);
    }

    @Override
    public RentalResponseDto getById(Long userId, Long rentalId) {
        Rental rental = rentalRepository.findByIdAndUserId(rentalId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find "
                        + "rental by id:" + rentalId));
        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalResponseDto setDataReturn(RentalReturnRequestDto rentalReturnRequestDto) {
        Rental rental = rentalRepository.findById(rentalReturnRequestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental by id:"
                        + rentalReturnRequestDto.getRentalId()));

        Car car = carRepository.findById(rental.getCar().getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id:"
                        + rental.getCar().getId()));

        rental.setActualReturnDate(rentalReturnRequestDto.getActualReturnDate());
        car.setInventory(car.getInventory() + 1);
        rentalRepository.save(rental);
        carRepository.save(car);
        String message = notificationMessageService.createReturnNotification(rental, car);
        notificationService.sendNotification(message);
        return rentalMapper.toDto(rental);
    }
}
