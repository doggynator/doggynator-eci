/* (C) Doggynator 2023 */
package com.doggynator.api.repo;

import com.doggynator.api.model.UserStage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStageRepository extends JpaRepository<UserStage, Long> {}
