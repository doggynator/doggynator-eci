/* (C) Doggynator 2022 */
package com.doggynator.api.repo;

import java.util.Optional;

import com.doggynator.api.model.AppUser;
import com.doggynator.api.model.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Modifying
  int deleteByUser(AppUser user);
}
