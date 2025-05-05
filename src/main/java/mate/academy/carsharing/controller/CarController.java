package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.car.CarRequestDto;
import mate.academy.carsharing.dto.car.CarResponseDto;
import mate.academy.carsharing.service.car.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car manager",
        description = "Endpoints for managing cars")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get all cars",
            description = "Get a paginated list of all available cars")
    @GetMapping
    public Page<CarResponseDto> getCars(Pageable pageable) {
        return carService.getCars(pageable);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create a new car",
            description = "Create a new car with the provided details")
    @PostMapping
    public CarResponseDto createCar(@RequestBody CarRequestDto carRequestDto) {
        return carService.createCar(carRequestDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get car by ID",
            description = "Get a single car by its unique identifier")
    @GetMapping("/{carId}")
    public CarResponseDto findById(@PathVariable Long carId) {
        return carService.findById(carId);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update car by ID",
            description = "Update an existing car with new data")
    @PutMapping("/{carId}")
    public CarResponseDto updateById(@PathVariable Long carId,
                                     @RequestBody CarRequestDto carRequestDto) {
        return carService.updateById(carId, carRequestDto);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete car by ID",
            description = "Delete a car by its unique identifier")
    @DeleteMapping("/{carId}")
    public void deleteById(@PathVariable Long carId) {
        carService.deleteById(carId);
    }
}
