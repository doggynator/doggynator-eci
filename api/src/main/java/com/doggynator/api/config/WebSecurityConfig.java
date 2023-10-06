/* (C) Doggynator 2022 */
package com.doggynator.api.config;

import java.util.Arrays;

import com.doggynator.api.security.jwt.TokenAuthenticationFilter;
import com.doggynator.api.security.oauth2.CustomOAuth2UserService;
import com.doggynator.api.security.oauth2.CustomOidcUserService;
import com.doggynator.api.security.oauth2.OAuth2AccessTokenResponseConverterWithDefaults;
import com.doggynator.api.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.doggynator.api.security.oauth2.OAuth2AuthenticationSuccessHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private UserDetailsService userDetailsService;

  @Autowired private CustomOAuth2UserService customOAuth2UserService;

  @Autowired CustomOidcUserService customOidcUserService;

  @Autowired private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  @Autowired private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  @Autowired
  private AuthorizationRequestRepository<OAuth2AuthorizationRequest>
      cookieAuthorizationRequestRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired
  public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {

    http.cors()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .httpBasic()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
        .and()
        .authorizeRequests()
        .antMatchers(
            "/assets/**",
            "/**",
            "/error",
            "/api/all",
            "/api/auth/**",
            "/oauth2/**",
            "/facebook/signin")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2Login()
        .authorizationEndpoint()
        .authorizationRequestRepository(cookieAuthorizationRequestRepository)
        .and()
        .redirectionEndpoint()
        .and()
        .userInfoEndpoint()
        .oidcUserService(customOidcUserService)
        .userService(customOAuth2UserService)
        .and()
        .tokenEndpoint()
        .accessTokenResponseClient(authorizationCodeTokenResponseClient())
        .and()
        .successHandler(oAuth2AuthenticationSuccessHandler)
        .failureHandler(oAuth2AuthenticationFailureHandler);

    // Add our custom Token based authentication filter
    http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
  }

  // This bean is load the user specific data when form login is used.
  @Override
  public UserDetailsService userDetailsService() {
    return userDetailsService;
  }

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
      authorizationCodeTokenResponseClient() {
    var tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    tokenResponseHttpMessageConverter.setAccessTokenResponseConverter(
        new OAuth2AccessTokenResponseConverterWithDefaults());
    var restTemplate =
        new RestTemplate(
            Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
    restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
    var tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
    tokenResponseClient.setRestOperations(restTemplate);
    return tokenResponseClient;
  }
}
