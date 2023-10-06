/* (C) Doggynator 2022 */
package com.doggynator.api.security.oauth2;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doggynator.api.config.AppProperties;
import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.exception.BadRequestException;
import com.doggynator.api.security.jwt.JWTManager;
import com.doggynator.api.util.CookieUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Autowired private JWTManager jwtManager;

  @Autowired private AppProperties appProperties;

  @Autowired
  private HttpCookieOAuth2AuthorizationRequestRepository
      httpCookieOAuth2AuthorizationRequestRepository;

  @Override
  public void onAuthenticationSuccess(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Authentication authentication)
      throws IOException, ServletException {
    var targetUrl = determineTargetUrl(request, response, authentication);

    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  @Override
  protected String determineTargetUrl(
      final HttpServletRequest request,
      final HttpServletResponse response,
      Authentication authentication) {
    Optional<String> redirectUri =
        CookieUtils.getCookie(
                request,
                HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
            .map(Cookie::getValue);

    if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
      throw new BadRequestException(
          "Lo sentimos! No pudimos hacer una dirección por que no fue autorizada y no se completo la autenticación");
    }

    var userPrincipal = (LocalUser) authentication.getPrincipal();

    var targetUrl = redirectUri.orElse(getDefaultTargetUrl());
    var token = jwtManager.generateTokenFromEmail(userPrincipal.getUser().getEmail());

    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("token", token)
        .build()
        .toUriString();
  }

  protected void clearAuthenticationAttributes(
      final HttpServletRequest request, final HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(
        request, response);
  }

  private boolean isAuthorizedRedirectUri(final String uri) {
    var clientRedirectUri = URI.create(uri);
    return appProperties.getOauth2().getAuthorizedRedirectUris().stream()
        .anyMatch(
            authorizedRedirectUri -> {
              var authorizedURI = URI.create(authorizedRedirectUri);
              return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                  && authorizedURI.getPort() == clientRedirectUri.getPort();
            });
  }
}
