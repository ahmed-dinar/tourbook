package com.bs23.tourbook.data;

import com.bs23.tourbook.model.Role;
import com.bs23.tourbook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);
  User findByEmail(String Email);
  List<User> findAllByRolesName(String role);
}
