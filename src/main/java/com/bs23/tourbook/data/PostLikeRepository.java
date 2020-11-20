package com.bs23.tourbook.data;

import com.bs23.tourbook.model.Post;
import com.bs23.tourbook.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
  List<PostLike> findByPost(Post post);
  List<PostLike> findByPost_Id(Long postId);
}
