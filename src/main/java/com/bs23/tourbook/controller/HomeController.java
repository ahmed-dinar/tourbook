package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.LocationRepository;
import com.bs23.tourbook.data.PostRepository;
import com.bs23.tourbook.model.Location;
import com.bs23.tourbook.model.Post;
import com.bs23.tourbook.model.UserPrincipal;
import com.bs23.tourbook.service.PostService;
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

  private final PostService postService;
  private final LocationRepository locationRepo;

  public HomeController(PostService postService, LocationRepository locationRepo) {
    this.postService = postService;
    this.locationRepo = locationRepo;
  }

  @GetMapping
  public String getHome(
      Model model,
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<Integer> size,
      @AuthenticationPrincipal UserPrincipal userPrincipal
  ) {
    Page<Post> posts = postService.getPublicPosts(page, size, userPrincipal);
    List<Location> locations = locationRepo.findAll();

    model.addAttribute("posts", posts);
    model.addAttribute("locations", locations);
    model.addAttribute("privacyList", Post.Privacy.values());

    return "pages/home";
  }
}
