package com.bs23.tourbook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @CreationTimestamp
  @Column(nullable = false)
  private Date createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Date updatedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @NonNull
  private Privacy privacy;

  @Column(nullable = false)
  private Boolean isPinned = false;

  @Column(nullable = true)
  @Length(max = 1000)
  private String text;

  @ManyToOne()
  @NonNull
  private User user;

  @ManyToOne
  @NonNull
  private Location location;

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.EAGER,
      mappedBy = "post",
      orphanRemoval = true
  )
  @LazyCollection(LazyCollectionOption.EXTRA)
  private Set<PostLike> likes = new HashSet<>();

  // TODO: Need to be clear about biodirectional stackoverflow issue
  // https://stackoverflow.com/questions/56009334/hibernate-stackoverflowerror-with-onetomany-and-manytoone-mapping
  // https://stackoverflow.com/questions/30995226/onetomany-get-size-of-collection
  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "post",
      orphanRemoval = true
  )
  @LazyCollection(LazyCollectionOption.EXTRA)
  private Set<PostComment> comments = new HashSet<>();

  public static enum Privacy {
    PUBLIC, PRIVATE
  }
}
