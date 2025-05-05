package mate.academy.carsharing.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @NotBlank
    @Email
    @Size(min = 8, max = 30)
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
