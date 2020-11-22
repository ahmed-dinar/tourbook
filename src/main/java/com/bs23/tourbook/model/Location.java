package com.bs23.tourbook.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class Location extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private boolean visible = true;

  @Column(nullable = false, unique = true)
  @NonNull
  private String name;

  @Column(nullable = true)
  private final double latitude = 6.6;

  @Column(nullable = true)
  private final double longitude = 6.6;
}
