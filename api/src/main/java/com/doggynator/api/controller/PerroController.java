/* (C) Doggynator 2022 */
package com.doggynator.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.doggynator.api.model.Perro;
import com.doggynator.api.model.Usuario;
import com.doggynator.api.service.PerroService;
import com.doggynator.api.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(
    maxAge = 3600,
    allowCredentials = "true",
    originPatterns = {"*"})
@RestController
public class PerroController {

  @Autowired UsuarioService usuarioService;
  @Autowired PerroService perroService;

  @GetMapping("/dogsForUser")
  public ResponseEntity<List<Perro>> getDogsForUser(final Long userid) {
    Optional<Usuario> usuario = usuarioService.findUsuarioByUserid(userid);
    List<Perro> perros = new ArrayList<>();
    if (usuario.isPresent()) {
      perros = perroService.getPerroForUsuario(usuario.get());
    }

    return ResponseEntity.ok(perros);
  }

  @GetMapping("/dogsForUserWithKnn")
  public ResponseEntity<List<Perro>> getDogsForUserKnn(final Long userid) {
    Optional<Usuario> usuario = usuarioService.findUsuarioByUserid(userid);
    List<Perro> perros = new ArrayList<>();
    if (usuario.isPresent()) {
      perros = perroService.getPerroForUsuarioWithKnn(usuario.get());
    }

    return ResponseEntity.ok(perros);
  }

  @GetMapping("/getDogForUserFromModel")
  public ResponseEntity<List<Perro>> getDogForUserFromModel(final Long userid) {
    Optional<Usuario> usuario = usuarioService.findUsuarioByUserid(userid);
    List<Perro> perros = new ArrayList<>();
    if (usuario.isPresent()) {
      perros = perroService.getDogForUserFromModel(usuario.get());
    }

    return ResponseEntity.ok(perros);
  }
}
