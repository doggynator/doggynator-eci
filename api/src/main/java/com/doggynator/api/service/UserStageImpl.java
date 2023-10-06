/* (C) Doggynator 2023 */
package com.doggynator.api.service;

import java.util.Optional;

import com.doggynator.api.model.UserStage;
import com.doggynator.api.repo.UserStageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserStageImpl implements UserStageService {

  @Autowired UserStageRepository ustageRepository;

  @Override
  public Optional<UserStage> findStage(Long UserId) {
    return ustageRepository.findById(UserId);
  }
}
