
package com.tedsaasfaha.blogapplication.util;


import io.github.cdimascio.dotenv.Dotenv;

public class JwtConstant {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String SECRET_KEY = dotenv.get("JWT_SECRET_KEY");
    public static final long EXPIRATION_TIME = 60 * 60 * 1000;
}
//