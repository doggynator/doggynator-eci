/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.util.List;

import com.doggynator.api.dto.UserPreferencesRequest;

public interface MeGustaService {
  void saveMeGusta(List<UserPreferencesRequest> megustan, Long userId);

  void saveMg(Long userId, Long perroid, Double mgValue);
}
