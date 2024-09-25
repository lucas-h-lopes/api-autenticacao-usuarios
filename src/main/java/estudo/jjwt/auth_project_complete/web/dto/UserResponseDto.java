package estudo.jjwt.auth_project_complete.web.dto;

import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;
    private String email;
    private String role;
}
