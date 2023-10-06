/* (C) Doggynator 2022 */
package com.doggynator.api.service.facebook;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.doggynator.api.config.AppProperties;
import com.doggynator.api.service.UsuarioService;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@Slf4j
public class FacebookUserDataHandler {

  private static final int TIMEOUT = 5000;

  @Autowired private AppProperties appProperties;
  @Autowired private UsuarioService uService;

  public void handle(final String userToken, final Long userId) {

    var fbGraphApiConf = appProperties.getFacebook().getGraphApi();
    var modelConf = appProperties.getModel();

    var meRequest =
        getRequest(
                fbGraphApiConf.getServer(),
                fbGraphApiConf.getMe(),
                Map.of("access_token", userToken, "fields", fbGraphApiConf.getMeFields()))
            .block(Duration.ofSeconds(10L));
    var groupsRequest =
        getRequest(
                fbGraphApiConf.getServer(),
                fbGraphApiConf.getGroups(),
                Map.of(
                    "access_token",
                    userToken,
                    "fields",
                    fbGraphApiConf.getGroupsFields(),
                    "limit",
                    fbGraphApiConf.getGroupsLimit()))
            .block(Duration.ofSeconds(10L));
    var feedRequest =
        getRequest(
                fbGraphApiConf.getServer(),
                fbGraphApiConf.getFeed(),
                Map.of(
                    "access_token",
                    userToken,
                    "fields",
                    fbGraphApiConf.getFeedFields(),
                    "limit",
                    fbGraphApiConf.getFeedLimit(),
                    "since",
                    fbGraphApiConf.getFeedSince()))
            .block(Duration.ofSeconds(10L));

    meRequest.put("groups", groupsRequest);
    meRequest.put("feed", feedRequest);

    var properties =
        this.postRequest(modelConf.getPropertiesHost(), modelConf.getPropertiesUri(), meRequest)
            .block();
    log.info("facebook data:\n", meRequest);
    this.uService.saveUpreferencesFromFb(properties, userId);
    log.info("model properties for user {}:\n{}", userId, properties);
  }

  private Mono<JSONObject> getRequest(
      final String host, final String uri, final Map<String, String> params) {

    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
            .responseTimeout(Duration.ofMillis(TIMEOUT))
            .doOnConnected(
                conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS)));

    var client =
        WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(host)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    var uriSpec = client.get();

    var parameters = Params.builder().params(params).build();
    var spec =
        uriSpec.uri(
            uriBuilder ->
                uriBuilder
                    .pathSegment(uri.split("/"))
                    .queryParams(parameters.getParameters())
                    .build(parameters.getVariables()));
    return spec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
        .accept(MediaType.APPLICATION_JSON)
        .acceptCharset(StandardCharsets.UTF_8)
        .ifNoneMatch("*")
        .ifModifiedSince(ZonedDateTime.now())
        .exchangeToMono(
            response -> {
              if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(JSONObject.class);
              } else if (response.statusCode().is4xxClientError()) {
                return Mono.just(
                    new JSONObject(Map.of("ERROR", response.statusCode().getReasonPhrase())));
              } else {
                return response.createException().flatMap(Mono::error);
              }
            });
  }

  private <T> Mono<JSONObject> postRequest(final String host, final String uri, final T body) {

    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
            .responseTimeout(Duration.ofMillis(TIMEOUT))
            .doOnConnected(
                conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS)));

    var client =
        WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(host)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    var uriSpec = client.post();
    var bodySpec = uriSpec.uri(uriBuilder -> uriBuilder.pathSegment(uri.split("/")).build());
    var headersSpec = bodySpec.body(Mono.just(body), body.getClass());

    return headersSpec
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON)
        .acceptCharset(StandardCharsets.UTF_8)
        .ifNoneMatch("*")
        .ifModifiedSince(ZonedDateTime.now())
        .exchangeToMono(
            response -> {
              if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(JSONObject.class);
              } else if (response.statusCode().is4xxClientError()) {
                return Mono.just(
                    new JSONObject(Map.of("ERROR", response.statusCode().getReasonPhrase())));
              } else {
                return response.createException().flatMap(Mono::error);
              }
            });
  }

  private static final class Params {
    private static final String VAR_FORMAT = "{%s}";
    @Getter private final Map<String, String> variables;
    @Getter private final LinkedMultiValueMap<String, String> parameters;

    @Builder
    public Params(Map<String, String> params) {
      this.variables = new HashMap<>(params.size());
      this.parameters = new LinkedMultiValueMap<String, String>(params.size());
      params.forEach(
          (k, v) -> {
            this.parameters.add(k, String.format(VAR_FORMAT, k));
            this.variables.put(k, v);
          });
    }
  }
}
