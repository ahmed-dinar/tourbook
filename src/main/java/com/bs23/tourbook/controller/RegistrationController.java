package com.bs23.tourbook.controller;

import com.bs23.tourbook.model.RegistrationForm;
import com.bs23.tourbook.model.UserPrincipal;
import com.bs23.tourbook.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {

  private final String REGISTER_PAGE = "pages/registration";
  private final UserService userService;

  public RegistrationController(UserService userService) {
    this.userService = userService;
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
    userService.registerUser(registerForm);
    return "redirect:/login";
  }
}
