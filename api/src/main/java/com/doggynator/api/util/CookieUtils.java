/* (C) Doggynator 2022 */
package com.doggynator.api.util;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NoArgsConstructor;
import org.springframework.util.SerializationUtils;

@NoArgsConstructor
public class CookieUtils {

  public static Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
    var cookies = request.getCookies();

    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          return Optional.of(cookie);
        }
      }
    }

    return Optional.empty();
  }

  public static void addCookie(
      final HttpServletResponse response, final String name, final String value, final int maxAge) {
    var cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  public static void deleteCookie(
      final HttpServletRequest request, final HttpServletResponse response, final String name) {
    var cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (var cookie : cookies) {
        if (cookie.getName().equals(name)) {
          cookie.setValue("");
          cookie.setPath("/");
          cookie.setMaxAge(0);
          response.addCookie(cookie);
        }
      }
    }
  }

  public static String serialize(final Object object) {
    return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
  }

  public static <T> T deserialize(final Cookie cookie, final Class<T> cls) {
    return cls.cast(
        SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
  }
}
