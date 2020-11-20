package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.LocationRepository;
import com.bs23.tourbook.data.PostRepository;
import com.bs23.tourbook.model.Location;
import com.bs23.tourbook.model.Post;
import com.bs23.tourbook.model.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {

  private final PostRepository postRepo;
  private final LocationRepository locationRepo;

  public HomeController(PostRepository postRepo, LocationRepository locationRepo) {
    this.postRepo = postRepo;
    this.locationRepo = locationRepo;
  }

  @GetMapping
  public String getHome(
      Model model,
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<Integer> size,
      @AuthenticationPrincipal UserPrincipal user
  ) {
    // TODO: may be move into configuration instead of hardcoed?
    int DEFAULT_PAGE_SIZE = 10;

    Pageable pageable = PageRequest.of(page.orElse(1) - 1, size.orElse(DEFAULT_PAGE_SIZE + 1) - 1);
    Page<Post> posts = postRepo.findByPrivacyEqualsOrderByCreatedAtDesc(Post.Privacy.PUBLIC, pageable);
    model.addAttribute("posts", posts);

    List<Location> locations = locationRepo.findAll();
    model.addAttribute("locations", locations);
    model.addAttribute("privacyList", Post.Privacy.values());

    return "pages/home";
  }
}
