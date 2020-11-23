/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.service;

import com.bs23.tourbook.data.PinnedPostRepository;
import com.bs23.tourbook.data.PostCommentRepository;
import com.bs23.tourbook.data.PostRepository;
import com.bs23.tourbook.data.UserRepository;
import com.bs23.tourbook.helper.PageHelper;
import com.bs23.tourbook.helper.Pair;
import com.bs23.tourbook.model.*;
import javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class PostService {

  private final UserRepository userRepo;
  private final PageHelper pageHelper;
  private final PostRepository postRepo;
  private final PinnedPostRepository pinnedPostRepo;
  private final PostCommentRepository postCommentRepo;

  public PostService(UserRepository userRepo, PageHelper pageHelper, PostRepository postRepo, PinnedPostRepository pinnedPostRepo, PostCommentRepository postCommentRepo) {
    this.userRepo = userRepo;
    this.pageHelper = pageHelper;
    this.postRepo = postRepo;
    this.pinnedPostRepo = pinnedPostRepo;
    this.postCommentRepo = postCommentRepo;
  }

  /**
   *
   * @param postForm
   * @param user
   */
  public void addPost(PostForm postForm, User user) {
    Post post = new Post(Post.Privacy.valueOf(postForm.getPrivacy()), user, postForm.getLocation());
    Optional.ofNullable(postForm.getText()).ifPresent(post::setText);
    postRepo.save(post);
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
    Long pinnedId = pinnedPost.map(p -> p.getPost().getId()).orElse((long) -1);

    boolean isSameUser = isSameLoggedUser(userPrincipal, user);
    Collection<Post.Privacy> postPrivacy = Stream.of(Post.Privacy.PUBLIC, Post.Privacy.PRIVATE)
        .filter(s -> isSameUser || s.equals(Post.Privacy.PUBLIC))
        .collect(Collectors.toList());

    Pageable pageable = pageHelper.getPageable(page, size);
    Page<Post> posts = postRepo.findByUser_IdAndIdNotAndPrivacyInOrderByCreatedAtDesc(
        user.getId(),
        pinnedId,
        postPrivacy,
        pageable
    );
    return Pair.of(pinnedPost, posts);
  }

  /**
   *
   * @param page
   * @param size
   * @return
   */
  public Page<Post>  getPublicPosts(
      Optional<Integer> page,
      Optional<Integer> size
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

    Post post = postOptional.get();
    if(!post.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("Access Denied man!");
    }

    if (post.getPrivacy() == Post.Privacy.PRIVATE) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Private Post can not be pinned!");
    }

    Optional<PinnedPost> pinnedPost = pinnedPostRepo.findByUser_Id(user.getId());
    Consumer<PinnedPost> updateExisting = p -> {
      p.setPost(post);
      pinnedPostRepo.save(p);
    };

    pinnedPost.ifPresentOrElse(updateExisting, () -> {
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
      throw new AccessDeniedException("Access Denied!");
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
      throw new AccessDeniedException("Access Denied!");
    }

    pinnedPostRepo.deleteByPost_Id(postId);
  }


  /**
   *
   * @param postForm
   * @param user
   * @throws AccessDeniedException
   * @throws NotFoundException
   */
  @Transactional
  public void updatePost(PostForm postForm, User user) throws AccessDeniedException, NotFoundException {
    Optional<Post> postOptional = postRepo.findById(postForm.getId());

    if (postOptional.isEmpty()) {
      throw new NotFoundException("Post not found");
    }

    if(!postOptional.get().getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("Access Denied!");
    }

    Post post = postOptional.get();
    post.setPrivacy(Post.Privacy.valueOf(postForm.getPrivacy()));
    post.setLocation(postForm.getLocation());
    Optional.ofNullable(postForm.getText()).ifPresent(post::setText);
    postRepo.save(post);
  }

  /**
   *
   * @param postId
   * @return
   * @throws NotFoundException
   */
  public Post postDetail(Long postId) throws NotFoundException {
    return postRepo.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));
  }

  /**
   *
   * @param postId
   * @param page
   * @param size
   * @return
   */
  public Page<PostComment> comments(
      Long postId,
      Optional<Integer> page,
      Optional<Integer> size
  ) {
    Pageable pageable = pageHelper.getPageable(page, size);
    return postCommentRepo.findByPost_IdOrderByCreatedAtDesc(postId, pageable);
  }

  /**
   *
   * @param userPrincipal
   * @param user
   * @return
   */
  private boolean isSameLoggedUser(UserPrincipal userPrincipal, User user) {
    return userPrincipal != null && userPrincipal.getUser().getUsername().equals(user.getUsername());
  }


  /**
   * Get posts based on username query & authetication
   * @param username
   * @param page
   * @param size
   * @param userPrincipal
   * @return
   */
  public Pair<Optional<PinnedPost>, Page<Post>> getPosts(
      Optional<String> username,
      Optional<Integer> page,
      Optional<Integer> size,
      Optional<UserPrincipal> userPrincipal
  ) throws ResponseStatusException {
    if (username.isEmpty()) {
      return Pair.of(Optional.empty(), getPublicPosts(page, size));
    }

    User user = Optional.ofNullable(userRepo.findByUsername(username.get()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found"));
    return getUserPosts(page, size, userPrincipal.orElse(null), user);
  }
}
