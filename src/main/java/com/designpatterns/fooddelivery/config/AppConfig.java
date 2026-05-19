package com.designpatterns.fooddelivery.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Application configuration for caching and transaction management.
 *
 * @author Design Patterns Team
 * @version 1.0.0
 */
@Configuration
@EnableCaching
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class AppConfig {
}
