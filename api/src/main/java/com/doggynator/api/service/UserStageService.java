/* (C) Doggynator 2023 */
package com.doggynator.api.service;

import java.util.Optional;

import com.doggynator.api.model.UserStage;

public interface UserStageService {
  Optional<UserStage> findStage(Long UserId);
}
