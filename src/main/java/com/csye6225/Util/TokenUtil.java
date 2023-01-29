package com.csye6225.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtil {

    private String secretKey = "9jnerlff23u8ed01np9g6ysbhsh0dvcs";

    /**
     * 加密token.
     */
    public String getToken(Long id,String username) {
        //这个是放到负载payLoad 里面,魔法值可以使用常量类进行封装.
        String token = JWT
                .create()
                .withClaim("id", id.toString())
                .withClaim("username", username)
                .withClaim("timeStamp", System.currentTimeMillis())
                .sign(Algorithm.HMAC256(secretKey));
        return token;
    }

    /**
     * 解析token.
     * {
     * "userId": "weizhong",
     * "userRole": "ROLE_ADMIN",
     * "timeStamp": "134143214"
     * }
     */
    public Map<String, String> parseToken(String token) {
        HashMap<String, String> map = new HashMap<String, String>();
        DecodedJWT decodedjwt = JWT.require(Algorithm.HMAC256(secretKey))
                .build().verify(token);
        Claim id = decodedjwt.getClaim("id");
        Claim username = decodedjwt.getClaim("username");
        Claim timeStamp = decodedjwt.getClaim("timeStamp");
        map.put("id",id.asString());
        map.put("username", username.asString());
        map.put("timeStamp", timeStamp.asLong().toString());
        return map;
    }
}