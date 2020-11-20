package com.bs23.tourbook.security;

import com.bs23.tourbook.data.UserRepository;
import com.bs23.tourbook.model.User;
import com.bs23.tourbook.model.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserPrincipalDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  UserPrincipalDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
    return user.map(UserPrincipal::new).orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
