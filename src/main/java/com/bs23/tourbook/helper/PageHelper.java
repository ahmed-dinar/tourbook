/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PageHelper {

  public Pageable getPageable(Optional<Integer> page, Optional<Integer> size) {
    // TODO: may be move into configuration instead of hardcoed?
    int DEFAULT_PAGE_SIZE = 10;
    return PageRequest.of(page.orElse(1) - 1, size.orElse(DEFAULT_PAGE_SIZE + 1) - 1);
  }
}
