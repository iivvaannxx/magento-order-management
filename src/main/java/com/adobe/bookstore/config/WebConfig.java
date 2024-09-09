package com.adobe.bookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Configures the web application. */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /** Configures the view controllers (routing). */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("forward:/index.html");
    registry.addViewController(("/orders")).setViewName("forward:/orders.html");
  }
}
