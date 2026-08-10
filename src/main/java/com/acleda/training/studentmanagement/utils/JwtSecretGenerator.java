package com.acleda.training.studentmanagement.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

public class JwtSecretGenerator {
    static void main() {
        byte[] key =
                Jwts.SIG.HS256
                        .key()
                        .build()
                        .getEncoded();
        String secret =
                Encoders.BASE64URL.encode(key);
        System.out.println(secret);
    }
}
