package estudo.jjwt.auth_project_complete.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Setter @Getter @ToString
public class UserCreateDto {

    @NotBlank(message = "Email can't be null, blank or empty")
    @Size(min = 8, max = 254, message = "Email must contain 8 to 254 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]{3,}+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$", message = "Email must be in a valid format (e.g., user@example.com)")
    private String email;
    @NotBlank(message = "Password can't be null, blank or empty")
    @Size(min = 5, max = 10, message = "Password must contain 5 to 10 characters")
    private String password;
}
