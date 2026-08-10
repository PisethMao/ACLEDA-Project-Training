package com.acleda.training.studentmanagement.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class RsaKeyGenerator {
    static void main() throws Exception {
        KeyPairGenerator generator =
                KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair =
                generator.generateKeyPair();
        String privateKey =
                "-----BEGIN PRIVATE KEY-----\n"
                        + Base64.getMimeEncoder(
                        64,
                        "\n".getBytes()
                ).encodeToString(
                        keyPair.getPrivate().getEncoded()
                )
                        + "\n-----END PRIVATE KEY-----";
        String publicKey =
                "-----BEGIN PUBLIC KEY-----\n"
                        + Base64.getMimeEncoder(
                        64,
                        "\n".getBytes()
                ).encodeToString(
                        keyPair.getPublic().getEncoded()
                )
                        + "\n-----END PUBLIC KEY-----";
        Files.writeString(
                Path.of("private.pem"),
                privateKey
        );
        Files.writeString(
                Path.of("public.pem"),
                publicKey
        );
        System.out.println(
                "RSA keys generated successfully"
        );
    }
}