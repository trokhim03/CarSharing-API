package mate.academy.carsharing.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.carsharing.model.Role;

@Data
@Accessors(chain = true)
public class RoleNameRequestDto {
    @NotBlank
    private Role.RoleName roleName;
}
