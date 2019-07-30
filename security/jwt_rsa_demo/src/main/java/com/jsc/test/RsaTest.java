package com.jsc.test;

import com.jsc.pojo.UserInfo;
import com.jsc.utils.JwtUtils;
import com.jsc.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaTest {

    private static final String pubKeyPath = "D:/jsc/rsa/rsa.pub";
    private static final String priKeyPath = "D:/jsc/rsa/rsa.pri";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private String token;

    @Test
    public void createFile(){
        File file = new File("D:/jsc/rsa");
        if (!file.exists()){
            file.mkdirs();
        }
    }

    @Test
    public void getPublicKeyTest() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(pubKeyPath);
        System.out.println(publicKey);
    }

    @Test
    public void getPrivateKeyTest() throws Exception {
        PrivateKey privateKey = RsaUtils.getPrivateKey(priKeyPath);
        System.out.println(priKeyPath);
    }

    /**
     * 使用RsaUtils生成公钥和私钥
     */
    @Test
    public void generateKeyTest() throws Exception{
        RsaUtils.generateKey(pubKeyPath,priKeyPath,"jsc");
    }


    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void generateTokenTest() throws Exception {
        token = JwtUtils.generateToken(new UserInfo(20L, "jsc","hello jwt"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void parseTokenTest() throws Exception {
        token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoianNjIiwibWVzc2FnZSI6ImhlbGxvIGp3dCIsImV4cCI6MTU2Mzc3NzYwNX0.dik9BgZT9DGD7luzmLe8CHas8PYgltJfOZmMXcJ0WNyeqqb-A6GWvLUyibsk2BsVs03iSQkzZftN83FWvdhg2kK-HoxTUMxMP5BIIJz9opD3LSTHQTMBqTYz37dJMAdcQ12bm8zvMwaTZ044PI--ZWg3vkU7sUfoXVZuYHedURo";
        //解析token
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("userInfo = " + userInfo);
    }
}
