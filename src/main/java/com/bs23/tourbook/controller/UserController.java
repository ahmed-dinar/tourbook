/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.LocationRepository;
import com.bs23.tourbook.data.UserRepository;
import com.bs23.tourbook.helper.Pair;
import com.bs23.tourbook.model.*;
import com.bs23.tourbook.service.PostService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

  private final UserRepository userRepo;
  private final PostService postService;
  private final LocationRepository locationRepo;

  public UserController(UserRepository userRepo, PostService postService, LocationRepository locationRepo) {
    this.userRepo = userRepo;
    this.postService = postService;
    this.locationRepo = locationRepo;
  }

  @GetMapping("/{username}")
  public String getUserProfile(
      @PathVariable String username,
      Model model,
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<Integer> size,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) throws ResponseStatusException {
    User user = Optional
        .ofNullable(userRepo.findByUsername(username))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found"));

    Pair<Optional<PinnedPost>, Page<Post>> userPosts = postService.getUserPosts(page, size, userPrincipal, user);
    List<Location> locations = locationRepo.findAllByVisibleNot(false);

    model.addAttribute("user", user);
    model.addAttribute("pinnedPost",  userPosts.getFirst().orElse(null));
    model.addAttribute("posts", userPosts.getSecond());
    model.addAttribute("locations", locations);
    model.addAttribute("privacyList", Post.Privacy.values());

    return "pages/profile";
  }
}
