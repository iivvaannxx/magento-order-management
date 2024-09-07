package com.adobe.bookstore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BookstoreApplicationTests {

  /** The Spring app {@link ApplicationContext}. */
  @Autowired private ApplicationContext context;

  /** The instance of the {@link BookstoreApplication}. */
  @Autowired private BookstoreApplication bookstoreApplication;

  @Test
  void contextLoads() {}

  /** Tests that the {@link Logger} bean is present in the context. */
  @Test
  void loggerBean_shouldBePresent() {

    Logger logger = getLoggerBean();

    assertThat(logger).isNotNull();
    assertThat(BookstoreApplicationTests.class.getName()).isEqualTo(logger.getName());
  }

  /** Returns the {@link Logger} bean from the context. */
  private Logger getLoggerBean() {

    // We can't use the context.getBean(Logger.class) method.
    // It throws because there's no InjectionPoint available.
    return bookstoreApplication.logger(
        new InjectionPoint(BookstoreApplicationTests.class.getDeclaredFields()[0]));
  }
}
