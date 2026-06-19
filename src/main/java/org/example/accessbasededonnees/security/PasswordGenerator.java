package org.example.accessbasededonnees.security;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class PasswordGenerator {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom() ;
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12 , random);
        String hashedPassword = bCryptPasswordEncoder.encode("seryalex");
        System.out.println(hashedPassword);
    }
}

