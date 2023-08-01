package com.example.mssaem_backend.global.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig extends CachingConfigurerSupport {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  /**
   * 단일 Topic 사용을 위한 Bean 설정
   */
  @Bean
  public ChannelTopic channelTopic() {
    return new ChannelTopic("chatroom");
  }


  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisHost, redisPort);
  }

  /**
   * redis에 발행(publish)된 메시지 처리를 위한 리스너 설정
   */
  @Bean
  public RedisMessageListenerContainer redisMessageListener(
      RedisConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapter,
      ChannelTopic channelTopic) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(listenerAdapter, channelTopic);
    return container;
  }

  /**
   * 어플리케이션에서 사용할 redisTemplate 설정
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
    return redisTemplate;
  }

  /**
   * 실제 메시지를 처리하는 subscriber 설정 추가
   */
  @Bean
  public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
    return new MessageListenerAdapter(subscriber, "sendMessage");
  }


  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // timestamp 형식 안따르도록 설정
    mapper.registerModules(new JavaTimeModule(), new Jdk8Module()); // LocalDateTime 매핑을 위해 모듈 활성화
    return mapper;
  }

  //레디스 캐시
  @Bean
  public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(RedisSerializationContext
            .SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext
            .SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

    return RedisCacheManager
        .RedisCacheManagerBuilder
        .fromConnectionFactory(redisConnectionFactory)
        .cacheDefaults(redisCacheConfiguration)
        .build();
  }

}
