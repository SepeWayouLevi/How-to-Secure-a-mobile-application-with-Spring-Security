package org.example.accessbasededonnees;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude =
        org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration.class)
@EnableScheduling
public class AccessbasededonneesApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessbasededonneesApplication.class, args);
    }

}
