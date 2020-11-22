package com.bs23.tourbook.service;

import com.bs23.tourbook.data.LocationRepository;
import com.bs23.tourbook.model.Location;
import com.bs23.tourbook.model.User;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class LocationService {

  private final LocationRepository locationRepo;

  public LocationService(LocationRepository locationRepo) {
    this.locationRepo = locationRepo;
  }

  /**
   *
   * @param location
   */
  public void addLocation(Location location) {
    locationRepo.save(location);
  }

  /**
   *
   * @param location
   * @throws NotFoundException
   */
  public void editLocation(Location location) throws NotFoundException {
    Location savedLoc = locationRepo
        .findById(location.getId())
        .orElseThrow(() -> new NotFoundException("No Location found!"));

    savedLoc.setName(location.getName());
    savedLoc.setVisible(location.isVisible());

    locationRepo.save(savedLoc);
  }
}
