/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@RequiredArgsConstructor
public class PinnedPost extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne
  @JoinColumn(name = "post_id", referencedColumnName = "id")
  @NonNull
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Post post;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @NonNull
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;
}
