package com.devrassicpark.midnightoil.constants;

public class SecurityConstant {

    public static final long EXPIRATION_TIME = 432_000_000; // 5days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt_Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String MIDNIGHTOIL_LLC = "MidnightOil, LLC";
    public static final String MIDNIGHTOIL_ADMINISTRATION = "Overtime management portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/employee/login", "/employee/register",
            "employee/resetpassword/**", "/employee/image/**"};
}
