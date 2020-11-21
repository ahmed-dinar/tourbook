package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.PostRepository;
import com.bs23.tourbook.model.Post;
import com.bs23.tourbook.model.PostForm;
import com.bs23.tourbook.model.UserPrincipal;
import com.bs23.tourbook.service.PostService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;
  private final PostRepository postRepo;

  public PostController(PostService postService, PostRepository postRepo) {
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
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void pinPost(
      @PathVariable("postId") Long postId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) throws AccessDeniedException, NotFoundException {
    postService.addOrUpdatePinnedPost(postId, userPrincipal.getUser());
  }

  @PostMapping("/{postId}/unpin")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unpinPost(
      @PathVariable("postId") Long postId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) throws AccessDeniedException, NotFoundException {
    postService.unpinPost(postId, userPrincipal.getUser());
  }

  @DeleteMapping("/{postId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePost(
      @PathVariable("postId") Long postId,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) throws AccessDeniedException, NotFoundException {
    postService.removePost(postId, userPrincipal.getUser());
  }
}
