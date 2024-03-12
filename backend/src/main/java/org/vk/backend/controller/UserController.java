package org.vk.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vk.backend.facade.UserFacade;
import org.vk.backend.service.UserService;
import org.vk.backend.vallidation.ResponseErrorValidator;
import org.vk.backend.dto.UserDTO;
import org.vk.backend.entity.user.*;

import java.security.Principal;

@RestController
@RequestMapping("/vk/user")
@CrossOrigin("http://localhost:8080/")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private ResponseErrorValidator responseErrorValidator;
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal){
        User user = userService.getCurrentUser(principal);
        UserDTO userDTO = userFacade.userToUserDTO(user);

        LOG.info("info about user ");

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId){
        User user = userService.getUserById(Long.parseLong(userId));
        UserDTO userDTO = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

}
