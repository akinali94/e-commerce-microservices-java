package com.example.ad_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class AdServiceApplication {


    private static final Logger logger = LoggerFactory.getLogger(AdServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AdServiceApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        logApplicationStartup(env);
    }

    /**
     * Logs application startup information
     */
    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        
        String serverPort = env.getProperty("server.port", "9555");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";
        
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.warn("The host name could not be determined, using 'localhost' as fallback");
        }

        logger.info("\n----------------------------------------------------------\n" +
                    "🚀 Application '{}' is running!\n" +
                    "----------------------------------------------------------\n" +
                    " Local:      {}://localhost:{}{}\n" +
                    " External:   {}://{}:{}{}\n" +
                    " Profile(s): {}\n" +
                    " Started at: {}\n" +
                    "----------------------------------------------------------",
                env.getProperty("spring.application.name", "ad-service"),
                protocol,
                serverPort,
                contextPath,
                protocol,
                hostAddress,
                serverPort,
                contextPath,
                env.getActiveProfiles().length == 0 ? 
                    env.getDefaultProfiles() : env.getActiveProfiles(),
                java.time.LocalDateTime.now()
        );
    }

}
