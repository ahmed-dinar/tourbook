package com.bs23.tourbook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/login").setViewName("pages/login");
  }

//  @Override
//  public void addInterceptors(InterceptorRegistry registry) {
//    OpenSessionInViewInterceptor openSessionInViewInterceptor = new OpenSessionInViewInterceptor();
//    openSessionInViewInterceptor.setSessionFactory(getSessionFactory().getObject());
//
//    registry.addWebRequestInterceptor(openSessionInViewInterceptor).addPathPatterns("/**");
//  }
}
