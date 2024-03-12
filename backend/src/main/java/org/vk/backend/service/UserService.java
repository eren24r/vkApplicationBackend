package org.vk.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.vk.backend.dto.UserDTO;
import org.vk.backend.entity.user.User;
import org.vk.backend.entity.user.UserRole;
import org.vk.backend.execption.UserExistException;
import org.vk.backend.load.request.SignupReq;
import org.vk.backend.reps.UserRep;

import java.security.Principal;

@Service
public class UserService {
    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private final UserRep userRep;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRep userRep, BCryptPasswordEncoder passwordEncoder) {
        this.userRep = userRep;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(SignupReq userIn){
        User user = new User();
        user.setUsername(userIn.getUsername());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRoles().add(UserRole.ROLE_USERS);
        //user.getRoles().add(UserRole.ROLE_POSTS);
        //user.getRoles().add(UserRole.ROLE_ALBUMS);
        //user.getRoles().add(UserRole.ROLE_ADMIN);

        try {
            LOG.info("Saving User {}", userIn.getUsername());
            return userRep.save(user);
        } catch (Exception exception){
            LOG.error("Error in reg : {}", exception.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exist.");
        }
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setUsername(userDTO.getUsername());

        return userRep.save(user);
    }

    public User getCurrentUser(Principal principal) {
        return getUserByPrincipal(principal);
    }

    private User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRep.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
    }

    public User getUserById(Long id) {
        return userRep.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
