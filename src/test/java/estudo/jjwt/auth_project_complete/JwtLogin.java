package estudo.jjwt.auth_project_complete;

import estudo.jjwt.auth_project_complete.jwt.JwtToken;
import estudo.jjwt.auth_project_complete.jwt.dto.AuthenticateDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

public class JwtLogin {

    public static Consumer<HttpHeaders> authenticate(WebTestClient client,String username, String password){
        String token = client.post()
                .uri("/api/v2/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AuthenticateDto(username,password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody().getToken();
            return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
