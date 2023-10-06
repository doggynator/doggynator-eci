/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.util.List;

import com.doggynator.api.model.Perro;
import com.doggynator.api.model.Usuario;

public interface PerroService {

  List<Perro> getPerroForUsuario(Usuario usuario);

  List<Perro> getPerroForUsuarioWithKnn(Usuario usuario);

  List<Perro> getDogForUserFromModel(Usuario usuario);
}
