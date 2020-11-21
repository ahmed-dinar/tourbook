/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.service;

import com.bs23.tourbook.data.PinnedPostRepository;
import com.bs23.tourbook.data.PostRepository;
import com.bs23.tourbook.helper.PageHelper;
import com.bs23.tourbook.helper.Pair;
import com.bs23.tourbook.model.PinnedPost;
import com.bs23.tourbook.model.Post;
import com.bs23.tourbook.model.User;
import com.bs23.tourbook.model.UserPrincipal;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class PostService {

  private final PageHelper pageHelper;
  private final PostRepository postRepo;
  private final PinnedPostRepository pinnedPostRepo;

  public PostService(PageHelper pageHelper, PostRepository postRepo, PinnedPostRepository pinnedPostRepo) {
    this.pageHelper = pageHelper;
    this.postRepo = postRepo;
    this.pinnedPostRepo = pinnedPostRepo;
  }

  /**
   *
   * @param page
   * @param size
   * @param userPrincipal
   * @param user
   * @return
   */
  public Pair<Optional<PinnedPost>, Page<Post>> getUserPosts(
      Optional<Integer> page,
      Optional<Integer> size,
      UserPrincipal userPrincipal,
      User user
  ) {
    Optional<PinnedPost> pinnedPost = pinnedPostRepo.findByUser_Id(user.getId());

    boolean isSameUser = userPrincipal != null && userPrincipal.getUser().getUsername().equals(user.getUsername());
    Collection<Post.Privacy> postPrivacy = Stream
        .of(Post.Privacy.PUBLIC, Post.Privacy.PRIVATE)
        .filter(s -> isSameUser || s.equals(Post.Privacy.PUBLIC))
        .collect(Collectors.toList());
    Pageable pageable = pageHelper.getPageable(page, size);

    Page<Post> posts = postRepo.findByUser_IdAndIdNotAndPrivacyInOrderByCreatedAtDesc(
        user.getId(),
        pinnedPost.map(PinnedPost::getId).orElse((long) -1),
        postPrivacy,
        pageable
    );

    return Pair.of(pinnedPost, posts);
  }

  /**
   *
   * @param page
   * @param size
   * @param userPrincipal
   * @return
   */
  public Page<Post>  getPublicPosts(
      Optional<Integer> page,
      Optional<Integer> size,
      UserPrincipal userPrincipal
  ) {
    Pageable pageable = pageHelper.getPageable(page, size);
    return postRepo.findByPrivacyEqualsOrderByCreatedAtDesc(Post.Privacy.PUBLIC, pageable);
  }

  /**
   *
   * @param postId
   * @param user
   * @throws AccessDeniedException
   * @throws NotFoundException
   */
  @Transactional
  public void addOrUpdatePinnedPost(Long postId, User user) throws AccessDeniedException, NotFoundException {
    Optional<Post> postOptional = postRepo.findById(postId);

    if (postOptional.isEmpty()) {
      throw new NotFoundException("Post not found");
    }

    Post post = postOptional.get();if(!post.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("Access Denied man!");
    }

    if (post.getPrivacy() == Post.Privacy.PRIVATE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Private Post can not be pinned!");
    }

    Optional<PinnedPost> pinnedPost = pinnedPostRepo.findByUser_Id(user.getId());
    pinnedPost.ifPresentOrElse(
        p -> {
          p.setPost(post);
          pinnedPostRepo.save(p);
        },
        () -> {
          PinnedPost savePost = new PinnedPost();
          savePost.setPost(post);
          savePost.setUser(user);
          pinnedPostRepo.save(savePost);
        });
  }

  /**
   *
   * @param postId
   * @param user
   * @throws AccessDeniedException
   * @throws NotFoundException
   */
  @Transactional
  public void removePost(Long postId, User user) throws AccessDeniedException, NotFoundException {
    Optional<Post> postOptional = postRepo.findById(postId);

    if (postOptional.isEmpty()) {
      throw new NotFoundException("Post not found");
    }

    Post post = postOptional.get();
    if(!post.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("Access Denied man!");
    }

    postRepo.deleteById(postId);
  }

  /**
   *
   * @param postId
   * @param user
   * @throws AccessDeniedException
   * @throws NotFoundException
   */
  @Transactional
  public void unpinPost(Long postId, User user) throws AccessDeniedException, NotFoundException {
    Optional<PinnedPost> postOptional = pinnedPostRepo.findByPost_Id(postId);

    if (postOptional.isEmpty()) {
      throw new NotFoundException("Post not found");
    }

    if(!postOptional.get().getPost().getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("Access Denied man!");
    }

    pinnedPostRepo.deleteByPost_Id(postId);
  }
}
