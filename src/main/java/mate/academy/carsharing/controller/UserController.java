package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.role.RoleNameRequestDto;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Management",
        description = "Endpoints for managing user accounts and roles")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get current user info",
            description = "Retrieve authenticated user's information")
    @GetMapping("/me")
    public UserResponseDto getUser(Authentication authentication) {
        Long authenticationUserId = getAuthenticationUserId(authentication);
        return userService.getUser(authenticationUserId);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update user role",
            description = "Update user's role (Admin only)")
    @PutMapping("/update/{userId}/role")
    public UserResponseDto updateRole(@PathVariable Long userId,
                                      @RequestBody @Valid RoleNameRequestDto roleNameRequestDto) {
        return userService.updateUserRole(userId, roleNameRequestDto);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update current user info",
            description = "Update authenticated user's personal information")
    @PutMapping("/me")
    public UserResponseDto updateUserInfo(
            Authentication authentication,
            @RequestBody @Valid UserRegistrationRequestDto userRegistrationRequestDto) {
        Long authenticationUserId = getAuthenticationUserId(authentication);
        return userService.updateMe(authenticationUserId, userRegistrationRequestDto);
    }

    private Long getAuthenticationUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }
}
