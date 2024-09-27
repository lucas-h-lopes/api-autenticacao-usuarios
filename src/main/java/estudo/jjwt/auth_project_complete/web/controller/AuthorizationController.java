package estudo.jjwt.auth_project_complete.web.controller;

import estudo.jjwt.auth_project_complete.jwt.JwtUserDetailsService;
import estudo.jjwt.auth_project_complete.jwt.dto.AuthenticateDto;
import estudo.jjwt.auth_project_complete.web.exception.CustomExceptionBody;
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
public class AuthorizationController {

    @Autowired
    private JwtUserDetailsService service;

    @Autowired
    private AuthenticationManager authenticationManager;

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
