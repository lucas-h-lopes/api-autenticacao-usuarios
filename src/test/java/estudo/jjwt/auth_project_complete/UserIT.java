package estudo.jjwt.auth_project_complete;


import estudo.jjwt.auth_project_complete.web.dto.UserChangePasswordDto;
import estudo.jjwt.auth_project_complete.web.dto.UserCreateDto;
import estudo.jjwt.auth_project_complete.web.dto.UserResponseDto;
import estudo.jjwt.auth_project_complete.web.exception.CustomExceptionBody;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/users/insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserIT {

    @Autowired
    WebTestClient testClient;
    private final String URI = "/api/v2/users";

    @Test
    @Sql(scripts = "/sql/users/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void postUserCreate_validData_shouldBeStatus201(){
        UserResponseDto dto = testClient
                .post().uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("testee2@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getEmail()).isEqualTo("testee2@gmail.com");
        Assertions.assertThat(dto.getId()).isEqualTo(1);
        Assertions.assertThat(dto.getRole()).isEqualTo("MIN_ACCESS");

    }
    @Test
    public void postUser_invalidEmailFormat_shouldBeStatus400(){
        CustomExceptionBody result = testClient.post().uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("testealmeida", "123456"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(400);
        Assertions.assertThat(result.getMessage()).isEqualTo("Validation error(s)");
        Assertions.assertThat(result.getFoundErrors().get("email")).isEqualTo("Email must be in a valid format (e.g., user@example.com)");
    }
    @Test
    public void postUser_invalidPasswordMinSize_shoudBeStatus400(){
        CustomExceptionBody result = testClient.post().uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("tee@a.co", "1234"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(400);
        Assertions.assertThat(result.getMessage()).isEqualTo("Validation error(s)");
        Assertions.assertThat(result.getFoundErrors().get("password")).isEqualTo("Password must contain 5 to 10 characters");
    }

    @Test
    public void postUser_invalidPasswordMaxSize_shoudBeStatus400(){
        CustomExceptionBody result = testClient.post().uri(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserCreateDto("tee@a.co", "12345678900"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getStatus()).isEqualTo(400);
        Assertions.assertThat(result.getMessage()).isEqualTo("Validation error(s)");
        Assertions.assertThat(result.getFoundErrors().get("password")).isEqualTo("Password must contain 5 to 10 characters");
    }
    @Test
    public void getUser_existingUser_shouldBeStatus200 (){
        UserResponseDto dto = testClient.get()
                .uri(URI + "/{id}",2)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getEmail()).isEqualTo("ana33@hotmail.com");
        Assertions.assertThat(dto.getId()).isEqualTo(2);
        Assertions.assertThat(dto.getRole()).isEqualTo("MIN_ACCESS");
    }

    @Test
    public void getUser_nonexistentUser_shouldBeStatus404(){
        CustomExceptionBody result = testClient.get().uri(URI + "/{id}", 500)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo(String.format("User with id '%d' was not found", 500));
    }
    @Test
    public void getAllUser_dbHasUsers_shouldBeStatus200(){
        List<UserResponseDto> dtos = testClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(dtos).isNotNull();
        Assertions.assertThat(dtos).size().isEqualTo(3);
    }
    @Test
    @Sql(scripts = "/sql/users/delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void getAllUser_dbDoesNotHasUsers_shouldBeStatus200(){
        List<UserResponseDto> dto = testClient.get().uri(URI)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDto.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.size()).isEqualTo(0);
    }

    @Test
    public void putUser_validData_shouldBeStatus204(){
        testClient.put().uri(URI + "/{id}", 1).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserChangePasswordDto("5513aB@", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class)
                .returnResult().getResponseBody();
    }

    @Test
    public void putUser_invalidCurrentPassword_shouldBeStatus400(){
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 1)
                .bodyValue(new UserChangePasswordDto("123", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo("Current password does not match");
    }

    @Test
    public void putUser_newPasswordDoesNotMatch_shouldBeStatus400(){
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 1)
                .bodyValue(new UserChangePasswordDto("5513aB@", "123456","123455"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo("Passwords does not match");
    }
    @Test
    public void putUser_newPasswordSameAsCurrentPassword_shouldBeStatus400(){
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 1)
                .bodyValue(new UserChangePasswordDto("5513aB@", "5513aB@", "5513aB@"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo("New password is equal to current password");
    }
    @Test
    public void putUser_newPasswordInvalidMinSize_shouldBeStatus400(){
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 1)
                .bodyValue(new UserChangePasswordDto("5513aB@", "1234", "1234"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo("Validation error(s)");
        Assertions.assertThat(result.getFoundErrors().get("newPassword")).isEqualTo("New password must contain 5 to 10 characters");
        Assertions.assertThat(result.getFoundErrors().get("confirmPassword")).isEqualTo("Confirm password must contain 5 to 10 characters");
    }
    @Test
    public void putUser_newPasswordInvalidMaxSize_shouldBeStatus400(){
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 1)
                .bodyValue(new UserChangePasswordDto("5513aB@", "12345678900", "12345678900"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo("Validation error(s)");
        Assertions.assertThat(result.getFoundErrors().get("newPassword")).isEqualTo("New password must contain 5 to 10 characters");
        Assertions.assertThat(result.getFoundErrors().get("confirmPassword")).isEqualTo("Confirm password must contain 5 to 10 characters");
    }
    @Test
    public void putUser_nonexistentUser_shouldBeStatus404(){
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 30)
                .bodyValue(new UserChangePasswordDto("5513aB@", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo(String.format("User with id '%d' was not found", 30));
    }
    @Test
    public void deleteUser_existingUser_shouldBeStatus204(){
        testClient.delete().uri(URI + "/{id}", 1)
                .exchange()
                .expectStatus().isEqualTo(204)
                .expectBody(Void.class)
                .returnResult().getResponseBody();
    }
    @Test
    public void deleteUser_nonexistentUser_shouldBeStatus404(){
        CustomExceptionBody result = testClient.delete().uri(URI + "/{id}", 50)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo(String.format("User with id '%d' was not found", 50));
    }
}
