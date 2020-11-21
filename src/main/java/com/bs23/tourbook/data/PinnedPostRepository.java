/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.data;

import com.bs23.tourbook.model.PinnedPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PinnedPostRepository extends JpaRepository<PinnedPost, Long> {
  Optional<PinnedPost> findByUser_Id(Long userId);
  Optional<PinnedPost> findByPost_Id(Long postId);
  void deleteByPost_Id(Long postId);
}
