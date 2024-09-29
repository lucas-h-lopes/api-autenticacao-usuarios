package estudo.jjwt.auth_project_complete.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class UserChangePasswordDto {
    @NotBlank(message = "Current password can't be null, blank or empty")
    private String currentPassword;
    @NotBlank(message = "New password can't be null, blank or empty")
    @Size(min = 5, max = 10, message = "New password must contain 5 to 10 characters")
    private String newPassword;
    @NotBlank(message = "Confirm password can't be null, blank or empty")
    @Size(min = 5, max = 10, message = "Confirm password must contain 5 to 10 characters")
    private String confirmPassword;
}
