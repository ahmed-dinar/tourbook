package com.bs23.tourbook.data;

import com.bs23.tourbook.model.Post;
import com.bs23.tourbook.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
  List<PostComment> findByPost(Post post);
  List<PostComment> findByPost_Id(Long postId);
}
