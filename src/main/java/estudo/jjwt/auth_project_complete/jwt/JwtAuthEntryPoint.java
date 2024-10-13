package estudo.jjwt.auth_project_complete.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setHeader("www-authenticate", "Bearer realm='/api/v2/auth'");
        //response.sendError(401); lança um retorno 401 que não permite retornar corpo
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"You must be authenticated to perform this action.\"}");
    }
}
