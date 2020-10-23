package com.artarkatesoft.securitystudy.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PasswordEncodersTest {

    public static final String PASSWORD = "password";

    @Test
    void md5HashingExample() {
        //when
        String hash1 = DigestUtils.md5DigestAsHex(PASSWORD.getBytes());
        String hash2 = DigestUtils.md5DigestAsHex(PASSWORD.getBytes());

        //then
        System.out.println(hash1);
        assertEquals(hash1, hash2);
    }

    @Test
    void md5HashingExample_Salted() {
        //when
        String hash1 = DigestUtils.md5DigestAsHex(PASSWORD.getBytes());
        String salted = PASSWORD + "ThisIsMySaltValueVEEEERYLONG";
        String hash2 = DigestUtils.md5DigestAsHex(salted.getBytes());

        //then
        System.out.println(hash1);
        System.out.println(hash2);
        assertNotEquals(hash1, hash2);
    }
}
