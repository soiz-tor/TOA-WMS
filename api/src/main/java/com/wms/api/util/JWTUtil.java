package com.wms.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JWTUtil {
    // 过期时间24小时
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成签名,24hour后过期
     * @param secret   用户的密码
     * @return 加密的token
     */
    public static String create(String username, String secret) {
        long date = System.currentTimeMillis();
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带username信息
        return JWT.create()
                .withIssuedAt(new Date(date))
                .withIssuer(username)
                .withExpiresAt(new Date(date + EXPIRE_TIME))
                .sign(algorithm);
    }

    /**
     * 校验token是否正确
     * @param token  密钥
     * @param password 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String username, String password) {
        Algorithm algorithm = Algorithm.HMAC256(password);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(username)
                .build();
        verifier.verify(token);
        return true;
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     * @return token中包含的用户名
     */
    public static String getUser(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getIssuer();
    }
}
