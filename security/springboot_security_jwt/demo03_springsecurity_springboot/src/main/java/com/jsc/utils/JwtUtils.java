package com.jsc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsc.pojo.jwt.JwtConstans;
import com.jsc.pojo.jwt.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtUtils {
    /**
     * 私钥加密token
     *
     * @param userInfo      载荷中的数据
     * @param privateKey    私钥
     * @param expireMinutes 过期时间，单位秒
     * @return
     * @throws Exception
     */
    public static String generateToken(UserInfo userInfo, PrivateKey privateKey, int expireMinutes) throws Exception {
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_ID, userInfo.getId())
                .claim(JwtConstans.JWT_KEY_USER_NAME, userInfo.getUsername())
                .claim(JwtConstans.JWT_KEY_MESSAGE, userInfo.getMessage())
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    /**
     * 私钥加密token
     *
     * @param userInfo      载荷中的数据
     * @param privateKey    私钥字节数组
     * @param expireMinutes 过期时间，单位秒
     * @return
     * @throws Exception
     */
    public static String generateToken(UserInfo userInfo, byte[] privateKey, int expireMinutes) throws Exception {
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_ID, userInfo.getId())
                .claim(JwtConstans.JWT_KEY_USER_NAME, userInfo.getUsername())
                .claim(JwtConstans.JWT_KEY_MESSAGE, userInfo.getMessage())
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256, RsaUtils.getPrivateKey(privateKey))
                .compact();
    }

    /**
     * 普通密文加密token
     *
     * @param userInfo      载荷中的数据
     * @param secret        密文
     * @param expireMinutes 过期时间，单位秒
     * @return
     * @throws Exception
     */
    public static String generateToken(UserInfo userInfo, String secret, int expireMinutes) throws Exception {
        Set<SimpleGrantedAuthority> authorities = userInfo.getAuthorities();
        List<String> authorityList = authorities.stream().map(a -> a.getAuthority()).collect(Collectors.toList());
        return Jwts.builder()
                .claim(JwtConstans.JWT_KEY_ID, userInfo.getId())
                .claim(JwtConstans.JWT_KEY_USER_NAME, userInfo.getUsername())
                .claim(JwtConstans.JWT_KEY_MESSAGE, userInfo.getMessage())
                .claim(JwtConstans.JWT_KEY_AUTHORITY,new ObjectMapper().writeValueAsString(authorityList))
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 公钥解析token
     *
     * @param token     用户请求中的token
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    private static Jws<Claims> parserToken(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    /**
     * 公钥解析token
     *
     * @param token     用户请求中的token
     * @param publicKey 公钥字节数组
     * @return
     * @throws Exception
     */
    private static Jws<Claims> parserToken(String token, byte[] publicKey) throws Exception {
        return Jwts.parser().setSigningKey(RsaUtils.getPublicKey(publicKey))
                .parseClaimsJws(token);
    }

    /**
     * 密文解析token
     *
     * @param token  用户请求中的token
     * @param secret 公钥字节数组
     * @return
     * @throws Exception
     */
    private static Jws<Claims> parserToken(String token, String secret) throws Exception {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token);
    }

    /**
     * 获取token中的用户信息
     *
     * @param token     用户请求中的令牌
     * @param publicKey 公钥
     * @return 用户信息
     * @throws Exception
     */
    public static UserInfo getInfoFromToken(String token, PublicKey publicKey) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, publicKey);
        Claims body = claimsJws.getBody();
        return getAuthorities(body);
    }

    /**
     * 获取token中的用户信息
     *
     * @param token     用户请求中的令牌
     * @param publicKey 公钥
     * @return 用户信息
     * @throws Exception
     */
    public static UserInfo getInfoFromToken(String token, byte[] publicKey) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, publicKey);
        Claims body = claimsJws.getBody();
        return getAuthorities(body);
    }

    /**
     * 使用密文获取token中的用户信息
     *
     * @param token  用户请求中的令牌
     * @param secret 密文
     * @return 用户信息
     * @throws Exception
     */
    public static UserInfo getInfoFromToken(String token, String secret) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, secret);
        Claims body = claimsJws.getBody();
        return getAuthorities(body);
    }

    /**
     * 获取权限authorities集合
     *
     * @param body
     * @return
     * @throws java.io.IOException
     */
    private static UserInfo getAuthorities(Claims body) throws java.io.IOException {
        String authorities_json = ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_AUTHORITY));
        ObjectMapper mapper = new ObjectMapper();
        List<String> authorities = mapper.readValue(authorities_json, mapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class));
        Set<SimpleGrantedAuthority> authoritySet = authorities.stream().map(a -> new SimpleGrantedAuthority(a)).collect(Collectors.toSet());
        return new UserInfo(
                ObjectUtils.toLong(body.get(JwtConstans.JWT_KEY_ID)),
                ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_USER_NAME)),
                ObjectUtils.toString(body.get(JwtConstans.JWT_KEY_MESSAGE)),
                authoritySet
        );
    }
}