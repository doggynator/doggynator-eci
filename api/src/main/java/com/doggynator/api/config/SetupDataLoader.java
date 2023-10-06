/* (C) Doggynator 2022 */
package com.doggynator.api.config;

import java.util.Calendar;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.doggynator.api.dto.SocialProvider;
import com.doggynator.api.model.AppRole;
import com.doggynator.api.model.AppUser;
import com.doggynator.api.model.Role;
import com.doggynator.api.repo.RoleRepository;
import com.doggynator.api.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

  private static final String ADMIN = "Admin";

  private boolean alreadySetup = false;

  @Value("${app.auth.adminEmail:admin@doggynator.com}") private String adminEmail;

  @Value("${app.auth.adminPass:@admin0}") private String adminPass;

  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    if (alreadySetup) {
      return;
    }
    var roles =
        Stream.of(Role.values())
            .map(r -> AppRole.builder().name(r).build())
            .map(this::createRoleIfNotFound)
            .collect(Collectors.toSet());
    createUserIfNotFound(adminEmail, roles);
    alreadySetup = true;
  }

  private final AppUser createUserIfNotFound(final String email, final Set<AppRole> roles) {
    var user = userRepository.findByEmail(email);
    return user.orElseGet(
        () -> {
          var newUser = new AppUser();
          newUser.setUsername(ADMIN);
          newUser.setEmail(email);
          newUser.setPassword(passwordEncoder.encode(adminEmail));
          newUser.setRoles(roles);
          newUser.setProvider(SocialProvider.LOCAL.getProviderType());
          newUser.setEnabled(true);
          var now = Calendar.getInstance().getTime();
          newUser.setCreatedDate(now);
          newUser.setModifiedDate(now);
          return userRepository.save(newUser);
        });
  }

  private final AppRole createRoleIfNotFound(final AppRole role) {
    var savedRole = roleRepository.findByName(role.getName());
    if (savedRole == null) {
      savedRole = roleRepository.save(role);
    }
    return savedRole;
  }
}
