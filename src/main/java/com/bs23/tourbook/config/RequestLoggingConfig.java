package com.bs23.tourbook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

  @Bean
  public CommonsRequestLoggingFilter loggingFilter() {
    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludePayload(true);
    filter.setIncludeQueryString(true);
    filter.setIncludeHeaders(false);
    filter.setMaxPayloadLength(10000);
    filter.setAfterMessagePrefix("DATA: ");
    return filter;
  }
}
