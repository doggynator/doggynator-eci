/* (C) Doggynator 2022 */
package com.doggynator.api.repo;

import java.util.Optional;

import com.doggynator.api.model.AppUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

  Optional<AppUser> findByEmail(String email);

  boolean existsByEmail(String email);
}
