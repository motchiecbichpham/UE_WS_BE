// package miage.thibichpham.backend.security;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.function.Function;

// @Component
// public class JwtUtil {

// @Value("${jwt.secret}")
// private String secret;

// public String generateToken(String email) {
// Map<String, Object> claims = new HashMap<>();
// return Jwts.builder()
// .setClaims(claims)
// .setSubject(email)
// .setIssuedAt(new Date())
// .setExpiration(new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 *
// 1000)) // 15 days
// .signWith(SignatureAlgorithm.HS512, secret)
// .compact();
// }

// public String extractEmail(String token) {
// return extractClaim(token, Claims::getSubject);
// }

// public Date extractExpiration(String token) {
// return extractClaim(token, Claims::getExpiration);
// }

// public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
// final Claims claims = extractAllClaims(token);
// return claimsResolver.apply(claims);
// }

// private Claims extractAllClaims(String token) {
// return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
// }

// public Boolean isTokenExpired(String token) {
// return extractExpiration(token).before(new Date());
// }
// }