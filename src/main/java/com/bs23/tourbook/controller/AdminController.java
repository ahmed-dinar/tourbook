package com.bs23.tourbook.controller;

import com.bs23.tourbook.data.LocationRepository;
import com.bs23.tourbook.model.Location;
import com.bs23.tourbook.service.LocationService;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final LocationRepository locationRepo;
  private final LocationService locationService;

  public AdminController(LocationRepository locationRepo, LocationService locationService) {
    this.locationRepo = locationRepo;
    this.locationService = locationService;
  }

  @GetMapping("/admin-panel")
  public String dashboard(
      Model model
  ) {
    List<Location> locations = locationRepo.findAll();
    model.addAttribute("locations", locations);

    return "pages/admin-panel";
  }

  @PostMapping("/location")
  public ResponseEntity<String> addLocation(
      Location location
  ) {
    locationService.addLocation(location);
    return ResponseEntity.ok("Location added");
  }

  @PostMapping("/location/{id}")
  public ResponseEntity<String> editLocation(
      @Valid Location location,
      BindingResult result,
      @PathVariable("id") Long locationId
  ) {
    if (result.hasErrors()) {
      return new ResponseEntity<>("Invalid data", HttpStatus.BAD_REQUEST);
    }
    location.setId(locationId);
    try {
      locationService.editLocation(location);
      return ResponseEntity.ok("Location Updated");
    } catch (NotFoundException e) {
      return new ResponseEntity<>("Location Not Found", HttpStatus.NOT_FOUND);
    }
  }
}

