package com.bs23.tourbook.model;

import lombok.*;
import lombok.extern.apachecommons.CommonsLog;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class User extends BaseEntity  {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, unique = true, length = 30)
  @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters long")
  @NonNull
  private String username;

  @Column(nullable = false)
  @NonNull
  private String password;

  @Column(nullable = true, unique = true)
  @Email(message = "Invalid Email")
  private String email;

  @Size(max = 100, message = "Fullname maximum 100 characters long")
  @NonNull
  private String fullName;

  @Pattern(regexp="(^$|[0-9]{10})")
  private String phone;

  @Column
  private boolean isAccountNonExpired = true;

  @Column
  private boolean isAccountNonLocked = true;

  @Column
  private boolean isCredentialsNonExpired = true;

  @Column
  private boolean isEnabled = true;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_role",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
  )
  private Set<Role> roles = new HashSet<>();
}
