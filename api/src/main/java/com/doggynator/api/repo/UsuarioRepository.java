/* (C) Doggynator 2022 */
package com.doggynator.api.repo;

import com.doggynator.api.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}
