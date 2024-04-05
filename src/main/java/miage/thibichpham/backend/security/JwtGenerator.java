package miage.thibichpham.backend.security;

import java.util.Date;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtGenerator {
  @SuppressWarnings("deprecation")
  public String generateToken(Authentication authentication, String userType) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expiryDate = new Date(currentDate.getTime() +
        SecurityConstant.JWT_EXPIRATION);

    String token = Jwts.builder()
        .setSubject(username)
        .setIssuedAt(currentDate)
        .setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS256, SecurityConstant.JWT_SECERT)
        .claim("usertype", userType)
        .compact();
    return token;
  }

  @SuppressWarnings("deprecation")
  public String getUsernameFromJWT(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(SecurityConstant.JWT_SECERT)
        .parseClaimsJws(token)
        .getBody();
    return claims.getSubject();
  }

  public String getUserTypeFromJWT(String token) {
    Claims claims = Jwts.parser()
        .setSigningKey(SecurityConstant.JWT_SECERT)
        .parseClaimsJws(token)
        .getBody();
    return claims.get("usertype").toString();
  }

  @SuppressWarnings("deprecation")
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(SecurityConstant.JWT_SECERT).parseClaimsJws(token);
      return true;
    } catch (Exception ex) {
      throw new AuthenticationCredentialsNotFoundException("JWT token is not valid" + token);
    }
  }
}
