package estudo.jjwt.auth_project_complete.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {

    @Size(min = 8, max = 254, message = "Email must contain 8 to 254 characters")
    @Email(message = "Email must be in a valid format (e.g., user@example.com)")
    private String email;
    @Size(min = 5, max = 10, message = "Password must contain 5 to 10 characters")
    private String password;
}
