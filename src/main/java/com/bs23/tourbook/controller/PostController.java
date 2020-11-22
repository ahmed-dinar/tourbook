package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.PostLikeRepository;
import com.bs23.tourbook.data.PostRepository;
import com.bs23.tourbook.model.*;
import com.bs23.tourbook.service.PostService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/posts")
public class PostController {

  private final PostLikeRepository postLikeRepo;
  private final PostService postService;
  private final PostRepository postRepo;

  public PostController(PostLikeRepository postLikeRepo, PostService postService, PostRepository postRepo) {
    this.postLikeRepo = postLikeRepo;
    this.postService = postService;
    this.postRepo = postRepo;
  }

  @PostMapping
  public String processPost(@Valid PostForm postForm, BindingResult result, @AuthenticationPrincipal UserPrincipal user) {
    if (result.hasErrors()) {
      return "redirect:/posts";
    }

    Post post = new Post(Post.Privacy.valueOf(postForm.getPrivacy()), user.getUser(), postForm.getLocation());
    Optional.ofNullable(postForm.getText()).ifPresent(post::setText);
    postRepo.save(post);

    return "redirect:/";
  }

  @PostMapping("/{postId}/pin")
  public ResponseEntity<String> pinPost(
      @PathVariable("postId") Long postId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  )  {
    try {
      postService.addOrUpdatePinnedPost(postId, userPrincipal.getUser());
      return ResponseEntity.ok("Post Updated");
    } catch (NotFoundException e) {
      return new ResponseEntity<>("Post Not Found", HttpStatus.NOT_FOUND);
    } catch (ResponseStatusException e) {
      return new ResponseEntity<>("You Can't Pin Private Post!", HttpStatus.BAD_REQUEST);
    } catch (AccessDeniedException e) {
      return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/{postId}/unpin")
  public ResponseEntity<String> unpinPost(
      @PathVariable("postId") Long postId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    try {
      postService.unpinPost(postId, userPrincipal.getUser());
      return ResponseEntity.ok("Post Unpinned!");
    } catch (NotFoundException e) {
      return new ResponseEntity<>("Post Not Found", HttpStatus.NOT_FOUND);
    } catch (AccessDeniedException e) {
      return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<String> deletePost(
      @PathVariable("postId") Long postId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  )  {
    try {
      postService.removePost(postId, userPrincipal.getUser());
      return ResponseEntity.ok("Post Deleted!");
    } catch (NotFoundException e) {
      return new ResponseEntity<>("Post Not Found", HttpStatus.NOT_FOUND);
    } catch (AccessDeniedException e) {
      return new ResponseEntity<>("Access Denied", HttpStatus.FORBIDDEN);
    }
  }

  @PutMapping("/{postId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<PostForm> updatePost(
      @Valid PostForm postForm,
      BindingResult result,
      @PathVariable("postId") Long postId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) throws AccessDeniedException, NotFoundException {
    if (result.hasErrors()) {
      return  ResponseEntity.badRequest().body(postForm);
    }

    postForm.setId(postId);
    postService.updatePost(postForm, userPrincipal.getUser());
    return ResponseEntity.ok().body(postForm);
  }

  @GetMapping("/{postId}")
  public String postDetail(
      @PathVariable("postId") Long postId,
      Model model,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) throws NotFoundException {
    Post post = postService.postDetail(postId);
    List<PostLike> likes = postLikeRepo.findByPost_Id(post.getId());

    model.addAttribute("post", post);
    model.addAttribute("likes", likes);

    return "pages/post-detail";
  }

  @GetMapping("/{postId}/comments")
  public String getComments(
    @PathVariable("postId") Long postId,
    Model model,
    @RequestParam(required = false) Optional<Integer> page,
    @RequestParam(required = false) Optional<Integer> size,
    @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    Page <PostComment> comments = postService.comments(postId, page, size);
    model.addAttribute("comments", comments);
    return "pages/comment-list";
  }
}
