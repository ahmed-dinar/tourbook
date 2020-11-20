package com.bs23.tourbook.helper;

import com.bs23.tourbook.data.LocationRepository;
import com.bs23.tourbook.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LocationIdConverter implements Converter<Long, Location> {
  private final LocationRepository locationRepository;

  @Autowired
  public LocationIdConverter(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  @Override
  public Location convert(Long id) {
    return locationRepository.findById(id).orElse(null);
  }
}
