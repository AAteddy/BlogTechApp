package com.tedsaasfaha.blogapplication.dto;

public class RefreshTokenDTO {
    private String token;

    public RefreshTokenDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
