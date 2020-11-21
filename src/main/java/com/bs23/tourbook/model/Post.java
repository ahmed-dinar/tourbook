package com.bs23.tourbook.model;

import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class Post extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NonNull
  private Privacy privacy;

  @Column(nullable = true)
  @Length(max = 1000)
  private String text;

  @ManyToOne(fetch = FetchType.EAGER)
  @NonNull
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @NonNull
  private Location location;

  /**
   * This is EAGER For now, need to show likes & user images in posts page
   * May be moved to Ajax for images & User @Formula just to return counts?
   */
  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "post",
      orphanRemoval = true
  )
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<PostLike> likes = new HashSet<>();

  /**
   * May be use @OneToMany & FetchType.LAZY? Not sure about performance yet
   */
  @Formula("SELECT COUNT(pc.id) FROM post_comment pc WHERE pc.post_id = id")
  private Long comments;

  public static enum Privacy {
    PUBLIC, PRIVATE
  }
}
