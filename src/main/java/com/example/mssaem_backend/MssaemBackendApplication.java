package com.example.mssaem_backend;

import jakarta.annotation.PostConstruct;
import java.util.Locale;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class MssaemBackendApplication {

  @PostConstruct
  public void started() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    Locale.setDefault(Locale.KOREA);
  }

  public static void main(String[] args) {
    SpringApplication.run(MssaemBackendApplication.class, args);
  }

}
