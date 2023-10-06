/* (C) Doggynator 2022 */
package com.doggynator.api.repo;

import com.doggynator.api.model.MeGustaId;
import com.doggynator.api.model.megusta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface megustaRepository extends JpaRepository<megusta, MeGustaId> {}
