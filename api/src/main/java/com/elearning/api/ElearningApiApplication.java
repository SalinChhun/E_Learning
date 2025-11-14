package com.elearning.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.elearning", "com.elearning.common"})
@EnableConfigurationProperties
@EntityScan("com.elearning.common")
@EnableJpaRepositories("com.elearning.common")
@EnableCaching
@EnableJpaAuditing
public class ElearningApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElearningApiApplication.class, args);
    }

}
