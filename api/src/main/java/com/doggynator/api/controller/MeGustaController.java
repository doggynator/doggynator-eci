/* (C) Doggynator 2022 */
package com.doggynator.api.controller;

import java.util.List;

import com.doggynator.api.dto.UserPreferencesRequest;
import com.doggynator.api.service.MeGustaService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(
    maxAge = 3600,
    allowCredentials = "true",
    originPatterns = {"*"})
@RestController
@Slf4j
public class MeGustaController {
  @Autowired MeGustaService meGustaService;

  @PostMapping("/saveMeGusta")
  public ResponseEntity<Void> saveMeGusta(@RequestBody List<UserPreferencesRequest> megustanReq) {
    Long userId =
        megustanReq.stream()
            .filter(u -> u.getOption().equals("userid"))
            .map(UserPreferencesRequest::getValue)
            .findFirst()
            .map(Long::parseLong)
            .orElse(null);
    meGustaService.saveMeGusta(megustanReq, userId);
    return ResponseEntity.ok().build();
  }
}
