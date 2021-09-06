package ru.javaops.topjava2.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.javaops.topjava2.util.JsonUtil;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@EnableCaching
public class AppConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("!test")
    Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    //    https://stackoverflow.com/a/46947975/548473
    @Bean
    Module module() {
        return new Hibernate5Module();
    }

    @Autowired
    public void storeObjectMapper(ObjectMapper objectMapper) {
        JsonUtil.setMapper(objectMapper);
    }

    @Bean
    public CaffeineCache restaurants() {
        return new CaffeineCache("restaurants",
                Caffeine.newBuilder()
                        .maximumSize(1)
                        .expireAfterAccess(300, TimeUnit.SECONDS)
                        .build());
    }

    @Bean
    public CaffeineCache dishes() {
        return new CaffeineCache("dishes",
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterAccess(300, TimeUnit.SECONDS)
                        .build());
    }
    @Bean
    public CaffeineCache userVote () {
        return new CaffeineCache("userVote",
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterAccess(300, TimeUnit.SECONDS)
                        .build());
    }
    @Bean
    public CaffeineCache rootVote () {
        return new CaffeineCache("rootVote",
                Caffeine.newBuilder()
                        .maximumSize(50)
                        .expireAfterAccess(30, TimeUnit.MINUTES)
                        .build());
    }
    @Bean
    public CaffeineCache users(){
        return new CaffeineCache("users",
                Caffeine.newBuilder()
                        .maximumSize(1)
                        .expireAfterAccess(60, TimeUnit.SECONDS)
                        .build());
    }


}