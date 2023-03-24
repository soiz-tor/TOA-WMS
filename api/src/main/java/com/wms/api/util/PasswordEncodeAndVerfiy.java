package com.wms.api.util;

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class PasswordEncodeAndVerfiy {

    public static String encode(String rawPassword) {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder("secret", 10, 256, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        return encoder.encode(rawPassword);
    }

    public static boolean verify(String rawPassword, String encodedPassword) {
        Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder("secret", 10, 256, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        return encoder.matches(rawPassword, encodedPassword);
    }
}
