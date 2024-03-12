package org.vk.backend.dto;

import org.vk.backend.entity.user.UserRole;

import java.util.HashSet;
import java.util.Set;

public class UserDTO {
    private Long id;
    private String username;
    private Set<UserRole> roles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }
}
