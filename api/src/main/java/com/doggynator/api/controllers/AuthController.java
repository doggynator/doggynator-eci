/* (C) Doggynator 2022 */
package com.doggynator.api.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.doggynator.api.dto.ApiResponse;
import com.doggynator.api.dto.FacebookLoginRequest;
import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.dto.LoginRequest;
import com.doggynator.api.dto.SignUpRequest;
import com.doggynator.api.dto.StringResponse;
import com.doggynator.api.dto.UserInfoResponse;
import com.doggynator.api.exception.TokenRefreshException;
import com.doggynator.api.exception.UserAlreadyExistAuthenticationException;
import com.doggynator.api.model.RefreshToken;
import com.doggynator.api.security.jwt.JWTManager;
import com.doggynator.api.service.LocalUserDetailService;
import com.doggynator.api.service.RefreshTokenService;
import com.doggynator.api.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(
    maxAge = 3600,
    allowCredentials = "true",
    originPatterns = {"*"})
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private UserService userService;

  @Autowired private JWTManager jwtManager;

  @Autowired private RefreshTokenService refreshTokenService;

  @Autowired private LocalUserDetailService localUserDetailService;

  @PostMapping("/signin")
  @ResponseBody
  public ResponseEntity<UserInfoResponse> authenticateUser(
      @Valid @RequestBody final LoginRequest loginRequest) {

    var authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    var userDetails = (LocalUser) authentication.getPrincipal();

    var jwtCookie = jwtManager.generateJwtCookie(userDetails);

    var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
    var jwtRefreshCookie = jwtManager.generateRefreshJwtCookie(refreshToken.getToken());

    List<String> roles =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
        .body(
            new UserInfoResponse(
                userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    try {
      userService.registerNewUser(signUpRequest);
    } catch (UserAlreadyExistAuthenticationException e) {
      log.error("Exception Ocurred", e);
      return new ResponseEntity<>(
          new ApiResponse(false, "Ya existe un usuario registrado con ese correo electrónico!"),
          HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok()
        .body(
            new ApiResponse(
                true,
                "Usuario registrado exitosamente. Ingresa para empezar a buscar a tu mejor amigo!"));
  }

  @PostMapping("/signout")
  @ResponseBody
  public ResponseEntity<ApiResponse> logoutUser() {
    var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!principal.toString().equalsIgnoreCase("anonymousUser")) {
      var userId = ((LocalUser) principal).getId();
      refreshTokenService.deleteByUserId(userId);
    }

    var jwtCookie = jwtManager.getCleanJwtCookie();
    var jwtRefreshCookie = jwtManager.getCleanJwtRefreshCookie();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
        .body(new ApiResponse(true, "La sesión se ha cerrado!"));
  }

  @PostMapping("/refreshtoken")
  @ResponseBody
  public ResponseEntity<StringResponse> refreshtoken(final HttpServletRequest request) {
    var refreshToken = jwtManager.getJwtRefreshFromCookies(request);

    if (StringUtils.hasText(refreshToken)) {

      return refreshTokenService
          .findByToken(refreshToken)
          .map(refreshTokenService::verifyExpiration)
          .map(RefreshToken::getUser)
          .map(
              user -> {
                var jwtCookie = jwtManager.generateJwtCookie(user);

                return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshToken)
                    .body(StringResponse.builder().message("Token refrescado con éxito!").build());
              })
          .orElseThrow(
              () -> new TokenRefreshException(refreshToken, "Token de refresco no es valido!"));
    }

    return ResponseEntity.badRequest()
        .body(StringResponse.builder().message("Token de refresco exitoso!").build());
  }

  @PostMapping("/facebook/signin")
  public ResponseEntity<UserInfoResponse> facebookAuth(
      @Valid @RequestBody FacebookLoginRequest facebookLoginRequest) {
    log.info("facebook login {}", facebookLoginRequest);
    var userDetails = localUserDetailService.loadUserByToken(facebookLoginRequest.getAccessToken());

    var jwtCookie = jwtManager.generateJwtCookie(userDetails);

    var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
    var jwtRefreshCookie = jwtManager.generateRefreshJwtCookie(refreshToken.getToken());

    List<String> roles =
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
        .body(
            new UserInfoResponse(
                userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
  }
}
