package estudo.jjwt.auth_project_complete;

import estudo.jjwt.auth_project_complete.web.dto.UserChangePasswordDto;
import estudo.jjwt.auth_project_complete.web.dto.UserCreateDto;
import estudo.jjwt.auth_project_complete.web.dto.UserResponseDto;
import estudo.jjwt.auth_project_complete.web.exception.CustomExceptionBody;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Sql(scripts = "/sql/users/insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserItTest {

    @Autowired
    WebTestClient testClient;

    private final String URI = "/api/v2/users";

    @Test
    public void postUserCreate_validData_shouldBeStatus201() {
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
    public void postUser_invalidEmailFormat_shouldBeStatus400() {

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
    public void postUser_invalidPasswordMinSize_shoudBeStatus400() {

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
    public void postUser_invalidPasswordMaxSize_shoudBeStatus400() {

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
    public void getUser_existingUser_shouldBeStatus200() {
        UserResponseDto dto = testClient.get()
                .uri(URI + "/{id}", 200)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getEmail()).isEqualTo("ana33@hotmail.com");
        Assertions.assertThat(dto.getId()).isEqualTo(200);
        Assertions.assertThat(dto.getRole()).isEqualTo("MIN_ACCESS");

        dto = testClient
                .get()
                .uri(URI + "/{id}", 200)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getId()).isEqualTo(200);
        Assertions.assertThat(dto.getRole()).isEqualTo("MIN_ACCESS");
        Assertions.assertThat(dto.getEmail()).isEqualTo("ana33@hotmail.com");

        dto = testClient
                .get()
                .uri(URI + "/{id}", 400)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getEmail()).isEqualTo("admin@gmail.com");
        Assertions.assertThat(dto.getId()).isEqualTo(400);
        Assertions.assertThat(dto.getRole()).isEqualTo("ADMIN");
    }

    @Test
    public void getUser_minAccess_existingAnotherUser_shouldBeStatus403(){
        CustomExceptionBody error = testClient.get()
                .uri(URI + "/{id}", 300)
                .headers(JwtLogin.authenticate(testClient,"ana33@hotmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(error).isNotNull();
    }

    @Test
    public void getUser_noAuthenticatedUser_shouldBeStatus401(){
        Map result = testClient
                .get()
                .uri(URI + "/{id}", 200)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get("error")).isEqualTo("You must be authenticated to perform this action.");
    }

    @Test
    public void getUser_nonexistentUser_shouldBeStatus404() {
        CustomExceptionBody result = testClient.get().uri(URI + "/{id}", 6000)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo(String.format("User with id '%d' was not found", 6000));
    }

    @Test
    public void getAllUser_dbHasUsers_shouldBeStatus200() {

        List<UserResponseDto> dtos = testClient.get().uri(URI)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(dtos).isNotNull();
        Assertions.assertThat(dtos).size().isEqualTo(4);
    }

    @Test
    public void getUsers_unauthenticatedUser_shouldBeStatus401(){
        Map result = testClient.get()
                .uri(URI)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.get("error")).isEqualTo("You must be authenticated to perform this action.");
    }

    @Test
    public void getUsers_minAccess_shouldBeStatus403(){
        CustomExceptionBody error = testClient
                .get()
                .uri(URI)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(error).isNotNull();
    }

    @Test
    public void putUser_validData_shouldBeStatus204() {
        testClient.put().uri(URI + "/{id}", 200)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .bodyValue(new UserChangePasswordDto("123456", "123455", "123455"))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class)
                .returnResult().getResponseBody();

        testClient.put()
                .uri(URI + "/{id}", 400)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .bodyValue(new UserChangePasswordDto("123456", "123455", "123455"))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class)
                .returnResult().getResponseBody();
    }

    @Test
    public void putUser_unauthenticatedUser_shouldBeStatus401(){
        Map error = testClient
                .put()
                .uri(URI + "/{}", 200)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UserChangePasswordDto("123456", "123455", "123455"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(error).isNotNull();
        Assertions.assertThat(error.get("error")).isEqualTo("You must be authenticated to perform this action.");
    }

    @Test
    public void putUser_differentUser_shouldBeStatus403(){
        testClient.put()
                .uri(URI + "/{id}", 300)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .bodyValue(new UserChangePasswordDto("123456","123455","123455"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(CustomExceptionBody.class)
                .returnResult()
                .getResponseBody();

        testClient.put()
                .uri(URI + "/{id}", 300)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .bodyValue(new UserChangePasswordDto("123456","123455","123455"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
    }

    @Test
    public void putUser_invalidCurrentPassword_shouldBeStatus400() {
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 200)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .bodyValue(new UserChangePasswordDto("123", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo("Current password does not match");

    }

    @Test
    public void putUser_newPasswordDoesNotMatch_shouldBeStatus400() {

        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 200)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .bodyValue(new UserChangePasswordDto("123456", "123455", "123457"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo("Passwords does not match");
    }

    @Test
    public void putUser_newPasswordSameAsCurrentPassword_shouldBeStatus400() {
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 200)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .bodyValue(new UserChangePasswordDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo("New password is equal to current password");
    }

    @Test
    public void putUser_newPasswordInvalidMinSize_shouldBeStatus400() {
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 200)
                .bodyValue(new UserChangePasswordDto("123456", "1234", "1234"))
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
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
    public void putUser_newPasswordInvalidMaxSize_shouldBeStatus400() {
        CustomExceptionBody result = testClient.put().uri(URI + "/{id}", 200)
                .bodyValue(new UserChangePasswordDto("123456", "12345678900", "12345678900"))
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
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
    public void deleteUser_existingUser_shouldBeStatus204() {
        testClient.delete().uri(URI + "/{id}", 200)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(204)
                .expectBody(Void.class)
                .returnResult().getResponseBody();

        testClient.delete()
                .uri(URI + "/{id}", 100)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class)
                .returnResult().getResponseBody();

        testClient.delete()
                .uri(URI + "/{id}", 400)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(204)
                .expectBody(Void.class)
                .returnResult().getResponseBody();
    }

    @Test
    public void deleteUser_differentUser_shouldBeStatus403(){
        CustomExceptionBody error = testClient.delete()
                .uri(URI + "/{id}", 100)
                .headers(JwtLogin.authenticate(testClient, "ana33@hotmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(error).isNotNull();
        Assertions.assertThat(error.getStatus()).isEqualTo(403);
    }

    @Test
    public void deleteUser_nonexistentUser_shouldBeStatus404() {

        CustomExceptionBody result = testClient.delete().uri(URI + "/{id}", 50)
                .headers(JwtLogin.authenticate(testClient, "admin@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(CustomExceptionBody.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getMessage()).isEqualTo(String.format("User with id '%d' was not found", 50));

    }
}
