package com.bs23.tourbook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
@JsonIgnoreProperties("post")
@EqualsAndHashCode(exclude = "post")
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
  @Getter(AccessLevel.NONE)
  @ToString.Exclude
  @OnDelete(action = OnDeleteAction.CASCADE)
  private final Post post;
}
