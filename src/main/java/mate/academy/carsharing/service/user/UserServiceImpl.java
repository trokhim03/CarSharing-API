package mate.academy.carsharing.service.user;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(userRegistrationRequestDto.getEmail())) {
            throw new RegistrationException("Can't register user "
                    + "with existing email: " + userRegistrationRequestDto.getEmail());

        }
        User user = userMapper.toModel(userRegistrationRequestDto);
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));

        Role defaultRole = roleRepository.findByName(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Role "
                        + Role.RoleName.CUSTOMER.name() + " not found"));
        user.setRoles(Set.of(defaultRole));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseDto updateUserRole(Long userId, RoleNameRequestDto roleNameRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by id: " + userId));
        Role role = roleRepository
                .findByName(roleNameRequestDto.getRoleName())
                .orElseThrow(() -> new EntityNotFoundException("Can't find role by role name: "
                        + roleNameRequestDto.getRoleName()));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponseDto getUser(Long userId) {
        User user = userRepository
                .findById(userId).orElseThrow(
                        () -> new EntityNotFoundException("Can't find user by id: " + userId));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseDto updateMe(Long id,
                                    UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user email: "
                        + userRegistrationRequestDto
                        .getEmail()));
        userMapper.updateModelFromDto(userRegistrationRequestDto, user);

        if (!userRegistrationRequestDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));
        }
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
