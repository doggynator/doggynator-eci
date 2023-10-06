/* (C) Doggynator 2022 */
package com.doggynator.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.doggynator.api")
@EnableJpaRepositories
@EnableTransactionManagement
public class DoggynatorApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplicationBuilder app = new SpringApplicationBuilder(DoggynatorApplication.class);
    app.run();
  }

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
    return application.sources(DoggynatorApplication.class);
  }
}
