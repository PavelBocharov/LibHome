package com.mar.libhome.db.conf;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.mar.libhome.db.entity")
@EnableJpaRepositories(basePackages = "com.mar.libhome.db.repo")
public class DatabaseConfiguration {
}
