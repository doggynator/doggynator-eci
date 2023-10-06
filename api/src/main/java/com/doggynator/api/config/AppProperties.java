/* (C) Doggynator 2022 */
package com.doggynator.api.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
  private final Auth auth = new Auth();
  private final OAuth2 oauth2 = new OAuth2();
  private final Facebook facebook = new Facebook();
  private final Model model = new Model();

  @Getter
  @Setter
  public static class Auth {
    private String tokenSecret;
    private long tokenExpirationMsec;
    private String jwtCookieName;
    private String jwtRefreshCookieName;
    private long tokenRefreshExpirationMsec;
  }

  public static final class OAuth2 {
    private List<String> authorizedRedirectUris = new ArrayList<>();

    public List<String> getAuthorizedRedirectUris() {
      return authorizedRedirectUris;
    }

    public OAuth2 authorizedRedirectUris(final List<String> authorizedRedirectUris) {
      this.authorizedRedirectUris = authorizedRedirectUris;
      return this;
    }
  }

  @Getter
  @Setter
  public static final class Facebook {
    private GraphApi graphApi;
  }

  @Getter
  @Setter
  public static final class GraphApi {
    private String server;
    private String me;
    private String meFields;
    private String meFieds;
    private String groups;
    private String groupsFields;
    private String groupsLimit;
    private String feed;
    private String feedFields;
    private String feedLimit;
    private String feedSince;
  }

  @Getter
  @Setter
  public static final class Model {
    private String propertiesHost;
    private String propertiesUri;
    private String uriModel;
  }
}
