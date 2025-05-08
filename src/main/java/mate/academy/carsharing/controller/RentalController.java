package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.dto.rental.RentalReturnRequestDto;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.service.rental.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental Management",
        description = "Endpoints for managing car rentals")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create new rental",
            description = "Create a new car rental for authenticated user")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RentalResponseDto createRental(Authentication authentication,
                                          @RequestBody @Valid RentalRequestDto rentalRequestDto) {
        Long userId = getAuthenticationUserId(authentication);
        return rentalService.createRental(userId, rentalRequestDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get rental by ID",
            description = "Get specific rental details by ID for authenticated user")
    @GetMapping("/{rentalId}")
    public RentalResponseDto getById(Authentication authentication, @PathVariable Long rentalId) {
        Long userId = getAuthenticationUserId(authentication);
        return rentalService.getById(userId, rentalId);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get user rentals",
            description = "Get paginated list of rentals for"
                    + " specific user with active status filter")
    @GetMapping
    public Page<RentalResponseDto> getAllByUserId(
            Pageable pageable,
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active") boolean isActive) {
        return rentalService.getAllByUserId(pageable, userId, isActive);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Return rental car",
            description = "Set actual return date and process rental completion")
    @PostMapping("/return")
    public RentalResponseDto setDataReturn(
            @RequestBody @Valid RentalReturnRequestDto rentalReturnRequestDto) {
        return rentalService.setDataReturn(rentalReturnRequestDto);
    }

    private Long getAuthenticationUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
