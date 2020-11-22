/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.service;


import com.bs23.tourbook.data.RoleRepository;
import com.bs23.tourbook.data.UserRepository;
import com.bs23.tourbook.model.RegistrationForm;
import com.bs23.tourbook.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

  private final UserRepository userRepo;
  private final PasswordEncoder encoder;
  private final RoleRepository roleRepository;

  public UserService(UserRepository userRepo, PasswordEncoder encoder, RoleRepository roleRepository) {
    this.userRepo = userRepo;
    this.encoder = encoder;
    this.roleRepository = roleRepository;
  }

  /**
   *
   * @param registerForm
   */
  public void registerUser(RegistrationForm registerForm) {
    User user = registerForm.toUser(encoder);

    // TODO: ROLE_USER should move to properties config instead of hardcoded
    Optional
        .ofNullable(roleRepository.findByName("ROLE_USER"))
        .map(Set::of)
        .ifPresent(user::setRoles);

    userRepo.save(user);
  }
}
