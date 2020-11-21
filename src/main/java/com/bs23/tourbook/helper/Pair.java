/**
 * @author Ahmed Dinar
 * Created 11/20/2020
 */
package com.bs23.tourbook.helper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

// Funny, huh? https://stackoverflow.com/a/4471509
// Is it harmful? :/
@Data
@AllArgsConstructor(staticName = "of", access = AccessLevel.PUBLIC)
public class Pair<F, S> {
  private F first;
  private S second;
}