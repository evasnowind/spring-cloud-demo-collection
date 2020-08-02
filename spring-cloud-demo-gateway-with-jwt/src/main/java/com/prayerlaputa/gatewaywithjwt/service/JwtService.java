package com.prayerlaputa.gatewaywithjwt.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

/**
 * @author chenglong.yu
 * created on 2020/8/1
 */
@Component
public class JwtService {

    public static final String SECRET_KEY_STR = "0123456789_0123456789_0123456789";
    private static final long MILLIS_PER_SECOND = 1000;
    private static final long TIME_OUT_SECOND = 60 * MILLIS_PER_SECOND;
    private static SecretKey SECRET_KEY = new SecretKeySpec(SECRET_KEY_STR.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    public String generateToken(String payload) {

        return Jwts.builder()
                // 设置主题（声明信息）
                .setSubject(payload)
                .setExpiration(new Date(System.currentTimeMillis() + TIME_OUT_SECOND))
                // 设置安全密钥（生成签名所需的密钥和算法）
                .signWith(SECRET_KEY)
                .compact();
    }

    public String parseToken(String jwt) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    public boolean isValidToken(String jwt) {
        try {
            parseToken(jwt);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
