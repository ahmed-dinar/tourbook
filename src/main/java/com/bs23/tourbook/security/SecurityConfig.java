package com.bs23.tourbook.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Qualifier("userPrincipalDetailsService")
  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(encoder());
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationEntryPoint authEntryPoint() {
    return new CustomAuthenticationEntryPoint();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
          .antMatchers(HttpMethod.POST,"/posts", "/posts/**").hasRole("USER")
          .antMatchers(HttpMethod.DELETE,"/posts", "/posts/**").hasRole("USER")
          .antMatchers(HttpMethod.PATCH,"/posts", "/posts/**").hasRole("USER")
          .antMatchers("/").permitAll()
          .antMatchers("/h2-console/**").permitAll()
          .antMatchers("/**").permitAll()

        .and()
          .exceptionHandling()
            .authenticationEntryPoint(authEntryPoint())

        .and()
          .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/", true)

        .and()
          .logout()
          .logoutSuccessUrl("/")

        .and()
          .csrf()
            .disable()
          .headers()
            .frameOptions()
            .disable();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
        .ignoring()
        .antMatchers("/h2-console/**");
  }
}
