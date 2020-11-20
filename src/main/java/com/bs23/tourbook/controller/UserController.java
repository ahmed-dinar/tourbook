/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.PostRepository;
import com.bs23.tourbook.data.UserRepository;
import com.bs23.tourbook.model.Post;
import com.bs23.tourbook.model.User;
import com.bs23.tourbook.model.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

  private final UserRepository userRepo;
  private final PostRepository postRepo;

  public UserController(UserRepository userRepo, PostRepository postRepo) {
    this.userRepo = userRepo;
    this.postRepo = postRepo;
  }

  @GetMapping("/{username}")
  public String getUserProfile(
      @PathVariable String username,
      Model model,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    User user = Optional
        .ofNullable(userRepo.findByUsername(username))
        .orElseThrow(() -> new UsernameNotFoundException("No user found"));

    if (userPrincipal != null && userPrincipal.getUser().getUsername().equals(username)) {
      List<Post> posts = postRepo.findByUser_IdAndIsPinnedOrderByCreatedAtDesc(user.getId(), false);
      List<Post> pinnedPost = postRepo.findByUser_IdAndIsPinnedOrderByCreatedAtDesc(user.getId(), true);
      model.addAttribute("posts", posts);
      model.addAttribute("pinnedPost", pinnedPost.isEmpty() ? null : pinnedPost.get(0));
    }

    return "pages/profile";
  }
}
