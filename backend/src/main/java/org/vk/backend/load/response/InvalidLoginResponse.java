package org.vk.backend.load.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InvalidLoginResponse {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public InvalidLoginResponse(){
        this.username = "Invalid Username";
        this.password = "Invalid Password";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
