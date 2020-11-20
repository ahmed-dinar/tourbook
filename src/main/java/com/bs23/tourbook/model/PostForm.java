package com.bs23.tourbook.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PostForm {
  @Size(max = 1000, message = "Status should be max 1000 characters long")
  private String text;

  @NotEmpty(message = "Privacy required")
  @NotNull
  private String privacy;

  @NotNull
  private Location location;
}
