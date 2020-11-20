package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.RoleRepository;
import com.bs23.tourbook.data.UserRepository;
import com.bs23.tourbook.model.RegistrationForm;
import com.bs23.tourbook.model.User;
import com.bs23.tourbook.model.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/register")
public class RegistrationController {

  private final String REGISTER_PAGE = "pages/registration";
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final RoleRepository roleRepository;

  public RegistrationController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.encoder = encoder;
  }

  @GetMapping
  public String registerForm(Model model, @AuthenticationPrincipal UserPrincipal user) {
    if (user != null) {
      return "redirect:/";
    }
    model.addAttribute("registerForm", new RegistrationForm());
    return REGISTER_PAGE;
  }

  @PostMapping
  public String process(@Valid @ModelAttribute("registerForm") RegistrationForm registerForm, BindingResult result) {
    if (result.hasErrors()) {
      return REGISTER_PAGE;
    }

    User user = registerForm.toUser(encoder);

    // TODO: ROLE_USER should move to properties config instead of hardcoded?
    Optional
        .ofNullable(roleRepository.findByName("ROLE_USER"))
        .map(Set::of)
        .ifPresent(user::setRoles);

    userRepository.save(user);
    return "redirect:/login";
  }
}
