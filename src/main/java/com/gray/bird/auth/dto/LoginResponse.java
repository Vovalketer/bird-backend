package com.gray.bird.auth.dto;

import jakarta.servlet.http.Cookie;

public record LoginResponse(Cookie accessToken, Cookie refreshToken) {
}
