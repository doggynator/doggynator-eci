/* (C) Doggynator 2022 */
package com.doggynator.api.security.jwt;

import java.time.Duration;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.doggynator.api.config.AppProperties;
import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.model.AppUser;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Slf4j
@Component
public class JWTManager {

  private static final String COOKIE_PATH = "/api";
  private static final String REFRESH_COOKIE_PATH = "/api/auth/refreshtoken";
  private static final Duration COOCKIE_MAX_AGE = Duration.ofHours(24L);
  private static final Duration COOCKIE_CLEAR_MAX_AGE = Duration.ofSeconds(1L);

  @Autowired private AppProperties appProperties;

  public ResponseCookie generateJwtCookie(final LocalUser userPrincipal) {
    var jwt = generateTokenFromEmail(userPrincipal.getEmail());
    return generateCookie(appProperties.getAuth().getJwtCookieName(), jwt, COOKIE_PATH);
  }

  public ResponseCookie generateJwtCookie(final AppUser user) {
    var jwt = generateTokenFromEmail(user.getEmail());
    return generateCookie(appProperties.getAuth().getJwtCookieName(), jwt, COOKIE_PATH);
  }

  public ResponseCookie generateRefreshJwtCookie(final String refreshToken) {
    return generateCookie(
        appProperties.getAuth().getJwtRefreshCookieName(), refreshToken, REFRESH_COOKIE_PATH);
  }

  public String getJwtFromCookies(final HttpServletRequest request) {
    return getCookieValueByName(request, appProperties.getAuth().getJwtCookieName());
  }

  public String getJwtRefreshFromCookies(final HttpServletRequest request) {
    return getCookieValueByName(request, appProperties.getAuth().getJwtRefreshCookieName());
  }

  public ResponseCookie getCleanJwtCookie() {
    return ResponseCookie.from(appProperties.getAuth().getJwtCookieName(), "")
        .path(COOKIE_PATH)
        .maxAge(COOCKIE_CLEAR_MAX_AGE)
        .build();
  }

  public ResponseCookie getCleanJwtRefreshCookie() {
    return ResponseCookie.from(appProperties.getAuth().getJwtRefreshCookieName(), "")
        .path(REFRESH_COOKIE_PATH)
        .maxAge(COOCKIE_CLEAR_MAX_AGE)
        .build();
  }

  public String getEmailFromJwtToken(final String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateJwtToken(final String authToken) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes()))
          .build()
          .parseClaimsJws(authToken);
      return true;
    } catch (SecurityException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  public String generateTokenFromEmail(final String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(
            new Date((new Date()).getTime() + appProperties.getAuth().getTokenExpirationMsec()))
        .signWith(Keys.hmacShaKeyFor(appProperties.getAuth().getTokenSecret().getBytes()))
        .compact();
  }

  private ResponseCookie generateCookie(final String name, final String value, final String path) {
    return ResponseCookie.from(name, value)
        .path(path)
        .maxAge(COOCKIE_MAX_AGE)
        .httpOnly(true)
        .build();
    // ResponseCookie.from(name,
    // value).path(path).maxAge(COOCKIE_MAX_AGE).httpOnly(true).secure(true).build();
  }

  private String getCookieValueByName(final HttpServletRequest request, final String name) {
    var cookie = WebUtils.getCookie(request, name);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }
}
