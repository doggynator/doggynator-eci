/* (C) Doggynator 2022 */
package com.doggynator.api.controllers;

import com.doggynator.api.config.CurrentUser;
import com.doggynator.api.dto.LocalUser;
import com.doggynator.api.dto.StringResponse;
import com.doggynator.api.dto.UserInfoResponse;
import com.doggynator.api.util.GeneralUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(
    maxAge = 3600,
    allowCredentials = "true",
    originPatterns = {"*"})
@RestController
@RequestMapping("/api")
public class UserController {

  @GetMapping("/user/me")
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<UserInfoResponse> getCurrentUser(@CurrentUser final LocalUser user) {
    return ResponseEntity.ok(GeneralUtils.buildUserInfo(user));
  }

  @GetMapping("/user")
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<StringResponse> getUserContent() {
    return ResponseEntity.ok(StringResponse.builder().message("User content goes here").build());
  }

  @GetMapping("/admin")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<StringResponse> getAdminContent() {
    return ResponseEntity.ok(StringResponse.builder().message("Admin content goes here").build());
  }

  @GetMapping("/mod")
  @PreAuthorize("hasAuthority('MODERATOR')")
  public ResponseEntity<StringResponse> getModeratorContent() {
    return ResponseEntity.ok(
        StringResponse.builder().message("Moderator content goes here").build());
  }
}
