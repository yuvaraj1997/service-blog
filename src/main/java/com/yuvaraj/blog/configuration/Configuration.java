package com.yuvaraj.blog.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public PasswordEncoder encoder() {
        //TODO: Think to secure more
        return new BCryptPasswordEncoder();
    }
}
