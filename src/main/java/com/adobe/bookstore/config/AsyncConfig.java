package com.adobe.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configures asynchronous support. */
@Configuration
public class AsyncConfig implements WebMvcConfigurer {

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    // Sometimes the SSE connection closes due to an AsyncTimeoutException.
    // This should be part of the fix.
    configurer.setDefaultTimeout(360000); // 6 minutes
  }
}
