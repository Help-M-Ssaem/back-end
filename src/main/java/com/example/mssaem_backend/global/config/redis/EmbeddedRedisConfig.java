package com.example.mssaem_backend.global.config.redis;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig {

  @Value("${spring.data.redis.port}")
  private int redisPort;
  private RedisServer redisServer;

  @PostConstruct
  public void redisServer() throws IOException {
    redisServer = RedisServer.builder()
        .port(redisPort)
        //.redisExecProvider(customRedisExec) //com.github.kstyrc (not com.orange.redis-embedded)
        .setting("maxmemory 128M") //maxheap 128M
        .build();
    redisServer.start();
  }
  @PreDestroy
  public void stopRedis() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }

}