package mate.academy.carsharing.dto.role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.carsharing.model.Role;

@Data
@Accessors(chain = true)
public class RoleNameRequestDto {
    @NotNull
    private Role.RoleName roleName;
}
