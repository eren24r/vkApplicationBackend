package org.vk.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.vk.backend.entity.user.User;
import org.vk.backend.reps.UserRep;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRep userRep;

    @Autowired
    public CustomUserDetailsService(UserRep userRep) {
        this.userRep = userRep;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRep.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
        return build(user);
    }

    public User loadUserById(Long id){
        return userRep.findUserById(id).orElse(null);
    }

    public static User build(User user){
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
        return new User(user.getId(), user.getUsername(), user.getPassword(), authorities);
    }
}
