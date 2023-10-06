/* (C) Doggynator 2022 */
package com.doggynator.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@Slf4j
public class WebConfig implements WebMvcConfigurer {

  private static final long MAX_AGE_SECS = 3600;

  @Autowired private MessageSource messageSource;

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    log.info(
        "###############################################################################################################################");
    registry
        .addMapping(CorsConfiguration.ALL)
        // .allowedOrigins("http://localhost:8080", "http://localhost:4200")
        .allowedOrigins(CorsConfiguration.ALL)
        .allowedMethods(CorsConfiguration.ALL)
        // .allowedOriginPatterns(CorsConfiguration.ALL)
        .allowCredentials(true)
        .allowedHeaders(CorsConfiguration.ALL)
        .maxAge(MAX_AGE_SECS);
  }

  @Override
  public Validator getValidator() {
    var validator = new LocalValidatorFactoryBean();
    validator.setValidationMessageSource(messageSource);
    return validator;
  }
}
