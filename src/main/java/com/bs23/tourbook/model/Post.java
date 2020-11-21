package com.bs23.tourbook.model;

import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.persistence.Entity;

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

  @ManyToOne
  @NonNull
  private User user;

  @ManyToOne
  @NonNull
  private Location location;

  /**
   * May be use @OneToMany & FetchType.LAZY? Not sure about performance yet
   */
  @Formula("SELECT COUNT(pl.id) FROM post_like pl WHERE pl.post_id = id")
  private Long likes;

  /**
   * May be use @OneToMany & FetchType.LAZY? Not sure about performance yet
   */
  @Formula("SELECT COUNT(pc.id) FROM post_comment pc WHERE pc.post_id = id")
  private Long comments;

  public static enum Privacy {
    PUBLIC, PRIVATE
  }
}
