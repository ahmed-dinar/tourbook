package com.bs23.tourbook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
@JsonIgnoreProperties("post")
@EqualsAndHashCode(exclude = "post")
public class PostLike extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @NonNull
  private final User user;

  /**
   *
   * So many going on here!
   * Causing biodirectional loop in ManyToOne & OneToMany in Post
   * EqualsAndHashCode works
   * ToString.Exclude suggested
   * JsonIgnoreProperties works
   * Getter(AccessLevel.NONE) suggested
   * https://stackoverflow.com/a/61717052
   *
   */
  @ManyToOne(targetEntity = Post.class)
  @JoinColumn(name = "post_id", referencedColumnName = "id")
  @Getter(AccessLevel.NONE)
  @ToString.Exclude
  @NonNull
  @OnDelete(action = OnDeleteAction.CASCADE)
  private final Post post;
}
