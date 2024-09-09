package com.adobe.bookstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookstoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(BookstoreApplication.class, args);
  }

  /** Defines a prototype scope bean for autowiring {@link Logger} instances. */
  @Bean
  @Scope(value = "prototype")
  public Logger logger(InjectionPoint injectionPoint) {
    Class<?> clazz = injectionPoint.getMember().getDeclaringClass();
    return LoggerFactory.getLogger(clazz);
  }
}
