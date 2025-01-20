
package com.tedsaasfaha.blogapplication.util;


import io.github.cdimascio.dotenv.Dotenv;

public class JwtConstant {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
    public static final long ACCESS_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour
    public static final long REFRESH_TOKEN_VALIDITY = 12 * 60 * 60 * 1000; // 12 hours
}
//