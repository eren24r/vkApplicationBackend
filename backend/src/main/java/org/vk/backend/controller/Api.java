package org.vk.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.vk.backend.entity.user.User;
import org.vk.backend.entity.user.UserRole;
import org.vk.backend.service.UserService;
import org.vk.backend.load.request.LoginReq;
import org.vk.backend.load.response.JWTTokenSuccessResponse;
import org.vk.backend.load.response.MessageResponse;
import org.vk.backend.security.SecurityConst;

import java.security.Principal;

@RestController
@RequestMapping("/vk/api/")
@CrossOrigin("http://localhost:8080/")
public class Api {

    @Autowired
    private UserService userService;
    private RestTemplate restTemplate;

    public static final Logger LOG = LoggerFactory.getLogger(Api.class);

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_USERS))) {
            LOG.info("User has Role");

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/users";
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<Object> getPosts(Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_POSTS))) {
            LOG.info("User has Role");

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/posts?userId=" + user.getId();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/posts/{postId}")
    @ResponseBody
    public ResponseEntity<Object> getPostById(@PathVariable("postId") String postId,
                                              Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_POSTS))) {
            LOG.info("PostsId: User has Role");

            LOG.info("x : " + postId);

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/posts/" + Long.parseLong(postId);
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/posts/{postId}/comments")
    @ResponseBody
    public ResponseEntity<Object> getCommentsOfPostById(@PathVariable("postId") String postId,
                                              Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_POSTS))) {
            LOG.info("PostsId: User has Role");

            LOG.info("x : " + postId);

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/posts/" + Long.parseLong(postId) + "/comments";
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/comments{postId}")
    @ResponseBody
    public ResponseEntity<Object> getCommentOfPostById(@RequestParam(name = "postId") String postId,
                                                        Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_POSTS))) {
            LOG.info("PostsId: User has Role");

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/comments?postId=" + Long.parseLong(postId);
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/posts{title}{body}")
    public ResponseEntity<Object> postPost(@RequestParam(name = "title", defaultValue = "...") String title,
                                           @RequestParam(name = "body", defaultValue = "...") String body,
                                           Principal principal){
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_POSTS))) {
            LOG.info("User has Role");

            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("userId", String.valueOf(user.getId()));
            parameters.add("postId", "101");
            parameters.add("title", title);
            parameters.add("body", body);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/posts";
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("User posted a Post. Response: " + response.getBody());
            } else {
                return new ResponseEntity<>("Error posting the Post", response.getStatusCode());
            }

        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/posts/{postId}{title}{body}")
    public ResponseEntity<Object> updatePostById(@PathVariable(name = "postId") String postId,
                                                 @RequestParam(name = "title", defaultValue = "...") String title,
                                           @RequestParam(name = "body", defaultValue = "...") String body,
                                           Principal principal){
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_POSTS))) {
            LOG.info("User has Role");

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/posts?userId=" + user.getId() + "&title=" + title + "&body=" + body;
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            return new ResponseEntity<>("User posted a Post", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }

    //post DELETE
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable(name = "postId") String postId,
                                           Principal principal){
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_POSTS))) {
            LOG.info("User has Role");

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/posts/" + Long.parseLong(postId);

            try {
                restTemplate.delete(apiUrl);
                return new ResponseEntity<>("Post deleted successfully", HttpStatus.OK);
            } catch (HttpClientErrorException ex) {
                return new ResponseEntity<>("Error deleting the Post: " + ex.getRawStatusCode(), ex.getStatusCode());
            }

        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/albums")
    public ResponseEntity<Object> getAlbums(Principal principal) {
        User user = userService.getCurrentUser(principal);
        if (user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ADMIN)) ||
                user.getRoles().stream().anyMatch(person -> person.equals(UserRole.ROLE_ALBUMS))) {
            LOG.info("User has Role");

            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = "https://jsonplaceholder.typicode.com/albums";
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }else{
            return new ResponseEntity<>("User doesn't have the required role", HttpStatus.BAD_REQUEST);
        }
    }
}
