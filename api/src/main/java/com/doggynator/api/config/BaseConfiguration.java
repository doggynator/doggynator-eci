/* (C) Doggynator 2022 */
package com.doggynator.api.config;

import java.nio.charset.StandardCharsets;

import lombok.NoArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@NoArgsConstructor
public class BaseConfiguration {

  private static final String MSG_BASE = "classpath:messages";

  @Bean
  public MessageSource messageSource() {
    var messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename(MSG_BASE);
    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
    return messageSource;
  }
}
