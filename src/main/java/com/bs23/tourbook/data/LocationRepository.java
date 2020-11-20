package com.bs23.tourbook.data;

import com.bs23.tourbook.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
  Location findByName(String name);
}
