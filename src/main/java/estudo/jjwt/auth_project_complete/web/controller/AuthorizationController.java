package estudo.jjwt.auth_project_complete.web.controller;

import estudo.jjwt.auth_project_complete.jwt.JwtToken;
import estudo.jjwt.auth_project_complete.jwt.JwtUserDetailsService;
import estudo.jjwt.auth_project_complete.jwt.dto.AuthenticateDto;
import estudo.jjwt.auth_project_complete.web.exception.CustomExceptionBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v2/auth")
@Tag(name = "Autenticação", description = "Recurso que permite autenticar usuários")
public class AuthorizationController {

    @Autowired
    private JwtUserDetailsService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(summary = "Autentica um usuário", description = "Autentica um usuário já existente no sistema.", responses = {
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso!", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = JwtToken.class))
            }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionBody.class))
            })
    })
    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticateDto dto, HttpServletRequest request){
        try{
            UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            authenticationManager.authenticate(token);

            log.info("{} successfully authenticated!", dto.getEmail());
            return ResponseEntity.ok(service.getToken(dto.getEmail()));
        }catch(AuthenticationException e){
            log.warn("Invalid credentials for user {}", dto.getEmail());
        }
        CustomExceptionBody exception = new CustomExceptionBody(request, HttpStatus.BAD_REQUEST, "Invalid credentials for user '" + dto.getEmail() + "'");
        return ResponseEntity.badRequest().body(exception);
    }
}
