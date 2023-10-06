/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.util.List;
import java.util.Optional;

import com.doggynator.api.dto.UserPreferencesRequest;
import com.doggynator.api.model.Usuario;

import net.minidev.json.JSONObject;

public interface UsuarioService {
  Optional<Usuario> findUsuarioByUserid(Long id);

  void saveUpreferences(List<UserPreferencesRequest> usuario, Long userId);

  void saveUpreferencesFromFb(JSONObject preferencesJson, Long userId);

  List<UserPreferencesRequest> getUserPreferences(Long userid) throws InterruptedException;
}
