package com.bs23.tourbook.model;

import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class PostComment extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  @Length(max = 1000)
  @NonNull
  private final String text;

  @ManyToOne
  @NonNull
  private final User user;

  @ManyToOne(targetEntity = Post.class)
  @JoinColumn(name = "post_id", referencedColumnName = "id")
  @NonNull
  @OnDelete(action = OnDeleteAction.CASCADE)
  private final Post post;
}
