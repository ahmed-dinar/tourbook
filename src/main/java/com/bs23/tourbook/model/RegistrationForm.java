package com.bs23.tourbook.model;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RegistrationForm {

  @NotEmpty
  @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters long")
  private String username;

  @NotEmpty
  @Size(min = 3, max = 40, message = "password must be between 3 and 40 characters long")
  private String password;

  @NotEmpty
  private String fullname;

  @Pattern(regexp="(^$|[0-9]{10})")
  private String phone;

  @Email(message = "Invalid email")
  private String email;

  public User toUser(PasswordEncoder encoder) {
    User user = new User(username, encoder.encode(password), fullname);
    if (phone != null) {
      user.setPhone(phone);
    }
    if (email != null) {
      user.setEmail(email);
    }
    return user;
  }
}
