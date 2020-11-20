package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.PostRepository;
import com.bs23.tourbook.model.Post;
import com.bs23.tourbook.model.PostForm;
import com.bs23.tourbook.model.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/posts")
public class PostController {

  private final PostRepository postRepo;

  public PostController(PostRepository postRepo) {
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
}
