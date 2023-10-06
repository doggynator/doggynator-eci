/* (C) Doggynator 2022 */
package com.doggynator.api.repo;

import com.doggynator.api.model.AppRole;
import com.doggynator.api.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<AppRole, Long> {

  AppRole findByName(Role name);
}
