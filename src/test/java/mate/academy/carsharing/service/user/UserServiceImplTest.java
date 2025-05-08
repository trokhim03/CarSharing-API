package mate.academy.carsharing.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import mate.academy.carsharing.dto.role.RoleNameRequestDto;
import mate.academy.carsharing.dto.user.UserRegistrationRequestDto;
import mate.academy.carsharing.dto.user.UserResponseDto;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.exception.RegistrationException;
import mate.academy.carsharing.mapper.UserMapper;
import mate.academy.carsharing.model.Role;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.RoleRepository;
import mate.academy.carsharing.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    private Role customerRole;
    private Role managerRole;
    private User user;
    private UserRegistrationRequestDto userRequestDto;
    private UserResponseDto userResponseDto;
    private RoleNameRequestDto roleNameRequestDto;

    @BeforeEach
    void setUp() {
        customerRole = new Role();
        customerRole.setName(Role.RoleName.CUSTOMER);

        managerRole = new Role();
        managerRole.setName(Role.RoleName.MANAGER);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRoles(Set.of());

        userRequestDto = new UserRegistrationRequestDto();
        userRequestDto.setEmail(user.getEmail());
        userRequestDto.setPassword("password123");
        userRequestDto.setRepeatPassword("password123");
        userRequestDto.setFirstName(user.getFirstName());
        userRequestDto.setLastName(user.getLastName());

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());

        roleNameRequestDto = new RoleNameRequestDto();
        roleNameRequestDto.setRoleName(managerRole.getName());
    }

    @Test
    @DisplayName("Register user - returns user DTO when valid request")
    void register_ShouldReturnUserDto_WhenValidRequest() throws RegistrationException {
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(userRequestDto)).thenReturn(user);
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(Role.RoleName.CUSTOMER))
                .thenReturn(Optional.of(customerRole));
        when(userMapper.toUserResponse(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.register(userRequestDto);

        assertThat(result).isEqualTo(userResponseDto);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Register user - throws exception when email exists")
    void register_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);

        RegistrationException exception = assertThrows(RegistrationException.class,
                () -> userService.register(userRequestDto));

        assertEquals("Can't registration user with existing email: " + userRequestDto.getEmail(),
                exception.getMessage());
    }

    @Test
    @DisplayName("Update role - returns updated user DTO when valid request")
    void updateUserRole_ShouldReturnUpdatedUser_WhenValidRequest() {
        User updateRoleUser = new User();
        updateRoleUser.setId(user.getId());
        updateRoleUser.setEmail(user.getEmail());
        updateRoleUser.setPassword(user.getPassword());
        updateRoleUser.setFirstName(user.getFirstName());
        updateRoleUser.setLastName(user.getLastName());
        updateRoleUser.setRoles(Set.of(managerRole));

        Long validId = 1L;

        when(userRepository.findById(validId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleNameRequestDto.getRoleName()))
                .thenReturn(Optional.of(managerRole));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(updateRoleUser);
        when(userMapper.toUserResponse(updateRoleUser)).thenReturn(userResponseDto);

        UserResponseDto expected = userResponseDto;
        UserResponseDto actual = userService.updateUserRole(validId, roleNameRequestDto);

        assertNotNull(actual);
        assertThat(actual).isEqualTo(expected);

        verify(userRepository).findById(validId);
        verify(roleRepository).findByName(roleNameRequestDto.getRoleName());
        verify(userRepository).save(user);
        verify(userMapper).toUserResponse(updateRoleUser);
    }

    @Test
    @DisplayName("Update role - throws exception when user not found")
    void updateUserRole_ShouldThrowException_WhenUserNotFound() {
        Long invalidId = 999L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRole(invalidId, roleNameRequestDto));

        assertEquals("Can't find user by id: " + invalidId, exception.getMessage());
    }

    @Test
    @DisplayName("Get user - returns user DTO when valid ID")
    void getUser_ShouldReturnUserDto_WhenValidId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUser(user.getId());

        assertThat(result).isEqualTo(userResponseDto);
    }

    @Test
    @DisplayName("Get user - throws exception when user not found")
    void getUser_ShouldThrowException_WhenUserNotFound() {
        Long invalidId = 999L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUser(invalidId));

        assertEquals("Can't find user by id: " + invalidId, exception.getMessage());
    }

    @Test
    @DisplayName("Update me - returns updated user DTO when valid request")
    void updateMe_ShouldReturnUpdatedUser_WhenValidRequest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.updateMe(user.getId(), userRequestDto);

        assertThat(result).isEqualTo(userResponseDto);
        verify(userRepository).save(user);
    }
}
