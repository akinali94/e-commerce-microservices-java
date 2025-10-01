package com.example.currency_service.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application Configuration
 * read from application.properties
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private String name = "Currency Service";
    private String version = "1.0.0";
    private Cache cache = new Cache();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public static class Cache {
        private boolean enabled = true;
        private long duration = 3600000; // 1 hour in ms

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }
}
