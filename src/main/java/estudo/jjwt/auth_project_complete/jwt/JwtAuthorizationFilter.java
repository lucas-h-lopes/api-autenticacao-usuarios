package estudo.jjwt.auth_project_complete.jwt;

import estudo.jjwt.auth_project_complete.service.UserService;
import estudo.jjwt.auth_project_complete.service.exception.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService service;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, UserNotFoundException {
        String token = request.getHeader("Authorization");

        boolean permittedPaths = (request.getRequestURI().equals("/api/v2/users")
                || request.getRequestURI().equals("/api/v2/auth"))
                && request.getMethod().equalsIgnoreCase("POST");

        if (!permittedPaths) {
            if ((token == null || !token.startsWith("Bearer "))) {
                log.warn("Token is null or missing 'Bearer '");
                printLogPathInfo(request);
                filterChain.doFilter(request, response);
                return;
            }

            if (!JwtUtils.isTokenValid(token)) {
                log.warn("Invalid or expired token");
                printLogPathInfo(request);
                filterChain.doFilter(request, response);
                return;
            }
            String email = JwtUtils.getSubjectFromToken(token);

            if (userService.loadUserByEmail(email) == null) {
                log.warn("User {} from token {} does not exists", email, token);
                printLogPathInfo(request);
                filterChain.doFilter(request, response);
                return;
            }
            authenticate(email, request);
        }
        filterChain.doFilter(request, response);
    }

    public void authenticate(String username, HttpServletRequest request) {
        UserDetails userDetails = service.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken token =
                UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());

        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private void printLogPathInfo(HttpServletRequest request) {
        log.info("Path: {}", request.getRequestURI());
        log.info("Method: {}", request.getMethod());
    }
}
