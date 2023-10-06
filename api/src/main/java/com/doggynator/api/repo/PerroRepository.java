/* (C) Doggynator 2022 */
package com.doggynator.api.repo;

import com.doggynator.api.model.Perro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerroRepository extends JpaRepository<Perro, Long> {}
