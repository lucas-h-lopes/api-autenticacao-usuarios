package estudo.jjwt.auth_project_complete.web.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserChangePasswordDto {

    private String currentPassword;
    @Size(min = 5, max = 10, message = "New password must contain 5 to 10 characters")
    private String newPassword;
    @Size(min = 5, max = 10, message = "Confirm password must contain 5 to 10 characters")
    private String confirmPassword;
}
