package org.vk.backend.load.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JWTTokenSuccessResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("access_token")
    private String tkn;

    public JWTTokenSuccessResponse(boolean success, String tkn){
        this.success = success;
        this.tkn = tkn;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTkn() {
        return tkn;
    }

    public void setTkn(String tkn) {
        this.tkn = tkn;
    }
}
