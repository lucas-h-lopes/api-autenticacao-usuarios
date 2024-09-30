package estudo.jjwt.auth_project_complete.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtUtils {

    private static final String SECRET_KEY = "01234567890-0987654321-123443211234-01235u23123";
    private static final Integer MINUTES_TO_EXPIRE = 30;

    private JwtUtils() {
    }

    private static SecretKey generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private static Date getExpirationDate(Date initial) {
        LocalDateTime dateTime = initial.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        dateTime = dateTime.plusMinutes(MINUTES_TO_EXPIRE);
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static JwtToken generateToken(String email, String role){
        Date initial = new Date();
        Date toExpire = getExpirationDate(initial);

        String token = Jwts.builder().header().add("typ", "JWT").and()
                .subject(email)
                .claim("role", role)
                .issuedAt(initial)
                .expiration(toExpire)
                .signWith(generateKey())
                .compact();

        return new JwtToken(token);
    }
    private static Claims getClaimsFromToken(String token){
        try{
            return Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(removeBearer(token)).getPayload();
        }catch (JwtException e){
            log.info("Invalid token {}", token);
        }
        return null;
    }

    public static String getSubjectFromToken(String token){
        return getClaimsFromToken(token).getSubject();
    }

    public static boolean isTokenValid(String token){
        try{
            Jwts.parser().verifyWith(generateKey()).build().parseSignedClaims(removeBearer(token));
            return true;
        }catch(JwtException e){
            log.warn("Invalid token {}. Error message: {}", token, e.getMessage());
        }
        return false;
    }

    private static String removeBearer(String token){
        if(token.startsWith("Bearer ")){
            return token.substring("Bearer ".length());
        }
        return token;
    }
}
