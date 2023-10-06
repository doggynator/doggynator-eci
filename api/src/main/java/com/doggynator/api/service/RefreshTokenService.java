/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.doggynator.api.config.AppProperties;
import com.doggynator.api.exception.TokenRefreshException;
import com.doggynator.api.model.RefreshToken;
import com.doggynator.api.repo.RefreshTokenRepository;
import com.doggynator.api.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

  @Autowired private AppProperties appProperties;
  @Autowired private RefreshTokenRepository refreshTokenRepository;

  @Autowired private UserRepository userRepository;

  public Optional<RefreshToken> findByToken(final String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken createRefreshToken(final Long userId) {
    var refreshToken = new RefreshToken();
    userRepository.findById(userId).ifPresent(refreshToken::setUser);
    refreshToken.setExpirationDate(
        Instant.now().plusMillis(appProperties.getAuth().getTokenRefreshExpirationMsec()));
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  public RefreshToken verifyExpiration(final RefreshToken token) {
    if (token.getExpirationDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(
          token.getToken(), "Refresh token was expired. Please make a new signin request");
    }

    return token;
  }

  @Transactional
  public int deleteByUserId(final Long userId) {
    return userRepository.findById(userId).map(refreshTokenRepository::deleteByUser).orElse(0);
  }
}
