package com.bs23.tourbook.data;

import com.bs23.tourbook.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByPrivacyEqualsOrderByCreatedAtDesc(Post.Privacy privacy, Pageable pageable);
  List<Post> findByPrivacy(Post.Privacy privacy);
  List<Post> findByUser_IdAndIsPinnedOrderByCreatedAtDesc(Long user_id, Boolean isPinned);
}
