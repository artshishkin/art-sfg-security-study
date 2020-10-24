package com.artarkatesoft.securitystudy.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.util.StringTokenizer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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

    @Test
    void testNoOpPasswordEncoder() {
        //given
        PasswordEncoder noOp = NoOpPasswordEncoder.getInstance();

        //when
        String noOpPassword = noOp.encode(PASSWORD);

        //then
        System.out.println(PASSWORD);
        System.out.println(noOpPassword);
        assertEquals(PASSWORD, noOpPassword);
    }

    @Test
    void testLdap() {
        //given
        PasswordEncoder ldap = new LdapShaPasswordEncoder();

        //when
        String ldapPassword1 = ldap.encode(PASSWORD);
        String ldapPassword2 = ldap.encode(PASSWORD);

        //then
        System.out.println(ldapPassword1);
        System.out.println(ldapPassword2);
        assertNotEquals(ldapPassword1, ldapPassword2);
        assertTrue(ldap.matches(PASSWORD, ldapPassword1));
        assertTrue(ldap.matches(PASSWORD, ldapPassword2));

        System.out.println("----------------------");
        System.out.println("getting pwd for config");
        System.out.println("----------------------");
        Stream.of("123", "pass222", "tiger")
                .forEach(pwd -> System.out.printf("%12s\t| %s\n", pwd, ldap.encode(pwd)));
        System.out.println("----------------------");
    }

    @Test
    void testSha256() {
        //given
        PasswordEncoder sha256Encoder = new StandardPasswordEncoder();

        //when
        String sha256Password1 = sha256Encoder.encode(PASSWORD);
        String sha256Password2 = sha256Encoder.encode(PASSWORD);

        //then
        System.out.println(sha256Password1);
        System.out.println(sha256Password2);
        assertNotEquals(sha256Password1, sha256Password2);
        assertTrue(sha256Encoder.matches(PASSWORD, sha256Password1));
        assertTrue(sha256Encoder.matches(PASSWORD, sha256Password2));

        System.out.println("----------------------");
        System.out.println("getting pwd for config");
        System.out.println("----------------------");
        Stream.of("123", "pass222", "tiger")
                .forEach(pwd -> System.out.printf("%12s\t| %s\n", pwd, sha256Encoder.encode(pwd)));
        System.out.println("----------------------");
    }

    @Test
    void testBCrypt() {
        //given
        PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        //when
        String bcryptPassword1 = bCryptPasswordEncoder.encode(PASSWORD);
        String bcryptPassword2 = bCryptPasswordEncoder.encode(PASSWORD);

        //then
        System.out.println(bcryptPassword1);
        System.out.println(bcryptPassword2);
        assertNotEquals(bcryptPassword1, bcryptPassword2);
        assertTrue(bCryptPasswordEncoder.matches(PASSWORD, bcryptPassword1));
        assertTrue(bCryptPasswordEncoder.matches(PASSWORD, bcryptPassword2));

        int defaultStrength = 10;
        StringTokenizer tokenizer = new StringTokenizer(bcryptPassword1, "$");
        tokenizer.nextToken();
        String strengthString = tokenizer.nextToken();
        assertThat(strengthString).isEqualTo("%d", defaultStrength);


        System.out.println("----------------------");
        System.out.println("getting pwd for config");
        System.out.println("----------------------");
        Stream.of("123", "pass222", "tiger")
                .forEach(pwd -> System.out.printf("%12s\t| %s\n", pwd, bCryptPasswordEncoder.encode(pwd)));
        System.out.println("----------------------");
    }

    @Test
    @Disabled("Just for playing with STRENGTH value")
    void testBCrypt_playStrength() {
        //given
        PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(15);

        //when
        log.info("start");
        String bcryptPassword1 = bCryptPasswordEncoder.encode(PASSWORD);
        log.info("1: {}", bcryptPassword1);
//        String bcryptPassword2 = bCryptPasswordEncoder.encode(PASSWORD);
        String bcryptPassword2 = bCryptPasswordEncoder.encode("tiger");
        log.info("2: {}", bcryptPassword2);

        //then
        assertNotEquals(bcryptPassword1, bcryptPassword2);
//        assertTrue(bCryptPasswordEncoder.matches(PASSWORD, bcryptPassword1));
//        log.info("match 1");
//        assertTrue(bCryptPasswordEncoder.matches(PASSWORD, bcryptPassword2));
//        log.info("match 2");
    }


}
