package com.alten.booking.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("prd")
@EnableReactiveMongoRepositories(basePackages = "com.alten.booking.infrastructure.repository")
@EnableReactiveMongoAuditing
public class MongoConfig {
}