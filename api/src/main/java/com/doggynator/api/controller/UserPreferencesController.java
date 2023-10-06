/* (C) Doggynator 2022 */
package com.doggynator.api.controller;

import java.util.List;
import java.util.Optional;

import com.doggynator.api.config.CurrentUser;
import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.dto.UserPreferencesRequest;
import com.doggynator.api.model.UserStage;
import com.doggynator.api.service.UserStageService;
import com.doggynator.api.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(
    maxAge = 3600,
    allowCredentials = "true",
    originPatterns = {"*"})
@RestController
public class UserPreferencesController {

  @Autowired UsuarioService usuarioService;
  @Autowired UserStageService ustageService;

  @PostMapping("/saveUserPreferences")
  public ResponseEntity<Void> saveUserPreferences(
      @CurrentUser final LocalUser user, @RequestBody List<UserPreferencesRequest> uPreferences) {
    Long userId =
        uPreferences.stream()
            .filter(u -> u.getOption().equals("userid"))
            .map(UserPreferencesRequest::getValue)
            .findFirst()
            .map(Long::parseLong)
            .orElse(null);
    usuarioService.saveUpreferences(uPreferences, userId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/getUserStage")
  public ResponseEntity<Optional<UserStage>> getDogsForUserKnn(final Long userid) {
    Optional<UserStage> stage = ustageService.findStage(userid);
    return ResponseEntity.ok(stage);
  }

  @GetMapping("/getUserPreferences")
  public ResponseEntity<List<UserPreferencesRequest>> getUserPreferences(final Long userid)
      throws InterruptedException {
    List<UserPreferencesRequest> uPreferences;
    uPreferences = usuarioService.getUserPreferences(userid);
    return ResponseEntity.ok(uPreferences);
  }
}
