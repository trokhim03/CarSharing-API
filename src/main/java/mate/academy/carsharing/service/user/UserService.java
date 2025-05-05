package mate.academy.carsharing.service.user;

import mate.academy.carsharing.dto.role.RoleNameRequestDto;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.exceptions.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    UserResponseDto updateUserRole(Long userId, RoleNameRequestDto roleNameRequestDto);

    UserResponseDto getUser(Long userId);

    UserResponseDto updateMe(Long id, UserRegistrationRequestDto userRegistrationRequestDto);
}
