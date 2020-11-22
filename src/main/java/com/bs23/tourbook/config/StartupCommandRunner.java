/**
 * @author Ahmed Dinar
 * Created 11/18/2020
 */
package com.bs23.tourbook.config;

import com.bs23.tourbook.data.*;
import com.bs23.tourbook.model.*;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Component
public class StartupCommandRunner implements CommandLineRunner {

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private PostCommentRepository postCommentRepository;

  @Autowired
  private PostLikeRepository postLikeRepository;

  @Autowired
  private PinnedPostRepository pinnedPostRepo;

  Faker faker = new Faker();

  @Transactional
  @Override
  public void run(String... args) throws Exception {
    log.info("Running CommandLineRunner...........");

    roleRepository.saveAll(Arrays.asList(
        new Role("ROLE_USER"),
        new Role("ROLE_ADMIN")
    ));

    Role userRole = roleRepository.findByName("ROLE_USER");
    Role adminRole = roleRepository.findByName("ROLE_ADMIN");

    // Fake base password for all user 1111
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String password = encoder.encode("1111");

    userRepository.saveAll(Arrays.asList(
        createUser("admin", encoder.encode("admin"), null, "Super Admin", null, adminRole),
        createUser("jonsnow", password, null, "Aegon Targaryen", null, userRole),
        createUser("khaleesi", password, "dragon@kingslanding.burn", "Daenerys Targaryen", "0171111111", adminRole),
        createUser("sarcasm", password, null, "Chandler Muriel Bing", null, userRole),
        createUser("batman", password, "batman@gotham.com", "Bruce Wayne", null, userRole),
        createUser("witcher", password, null, "Gerlt of Rivia", null, userRole),
        createUser("shelby", password, null, "Thomas Shelby", null, userRole),
        createUser("raven", password, null, "Three eyed raven ", null, userRole),
        createUser("hodor", password, null, "Hodor", null, userRole),
        createUser("monica", password, null, "Monica Geller", null, userRole),
        createUser("joey", password, null, "Joey Tribbiani", null, userRole),
        createUser("totoro", password, null, "Neighbor Totoro", null, userRole)
    ));

    this.insertPost(userRepository.findAll());
  }

  @Transactional
  void insertPost(List<User> users) {
    Stream
        .of("Sylhet", "Bandarban", "Hardhome", "Gotham", "Winterfell", "CoxBazar", "Sreemangal",
            "Saintmartin", "CentralPark", "Rivia", "CastleBlack", "KingsLanding", "HighGarden")
        .forEach(location -> locationRepository.save(new Location(location)));

    List<Location> savedLocations = locationRepository.findAll();
    Random rand = new Random();

    IntStream
        .range(1, 53)
        .forEach(x -> {
          // 46 post entries will be public out of 53
          Post.Privacy privacy = x < 47 ? Post.Privacy.PUBLIC : Post.Privacy.PRIVATE;
          // select a random user as post owner
          User user = users.get(rand.nextInt(users.size()));
          // select a random location
          Location location = savedLocations.get(rand.nextInt(savedLocations.size()));

          // More variation using faker.
          // If random number is even, then post a text about weather
          // if divided by 3 then no post description
          // otherwise select random sentence as post text :D
          String text = x%2 == 0
              ? x%4 == 0
                ? faker.weather().description() + " and " + faker.weather().description() + "!"
                : faker.weather().description() + "!!"
              :  x%3 == 0
                    ? null
                    : faker.lorem().paragraph(rand.nextInt(10) + 5);

          postRepository.save(createPost(privacy, user, location, text));
        });

    List<Post> savedPosts = postRepository.findByPrivacy(Post.Privacy.PUBLIC);

    // Loop over every user & make them comment and like of some random posts
    users
        .forEach(user -> {
          // how many random comments & likes this user likes to do
          int c = rand.nextInt(5) + 3;
          IntStream.range(1, c).forEach(y -> {
            String commentText = y%2 == 0 ? faker.weather().description() : faker.lorem().paragraph(rand.nextInt(5) + 2);
            postCommentRepository.save(new PostComment(commentText, user, savedPosts.get(rand.nextInt(savedPosts.size()))));
            postLikeRepository.save(new PostLike(user, savedPosts.get(rand.nextInt(savedPosts.size()))));
          });
        });

    log.info("Posts saved --> " + savedPosts.size());
    log.info("Comments saved --> " + postCommentRepository.count());
    log.info("Likes saved --> " + postLikeRepository.count());

    List<Post> pss = postRepository.findAll();
    Post p = pss.get(0);
      pinnedPostRepo.save(new PinnedPost(p, p.getUser()));
      log.info("For user " + p.getUser().getUsername() + " Post: " + p.getId());
      log.info("pinnedPostRepo " + pinnedPostRepo.findAll());
  }

  private Post createPost(Post.Privacy privacy, User user, Location location, String text) {
    Post post = new Post(privacy, user, location);
    Optional.ofNullable(text).ifPresent(post::setText);
    return post;
  }

  private User createUser(String username, String password, String email, String fullname, String phone, Role role) {
    User user = new User(username, password, fullname);
    user.getRoles().add(role);
    Optional.ofNullable(phone).ifPresent(user::setPhone);
    Optional.ofNullable(email).ifPresent(user::setEmail);
    return user;
  }
}
