/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.doggynator.api.config.AppProperties;
import com.doggynator.api.enums.Agresividad;
import com.doggynator.api.enums.Estilo;
import com.doggynator.api.enums.Salario;
import com.doggynator.api.enums.Salir;
import com.doggynator.api.enums.Tamano;
import com.doggynator.api.enums.TipoFamilia;
import com.doggynator.api.enums.Vivienda;
import com.doggynator.api.enums.pelaje;
import com.doggynator.api.model.Perro;
import com.doggynator.api.model.RecomenderPojo;
import com.doggynator.api.model.Usuario;
import com.doggynator.api.model.megusta;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@Slf4j
public class PerroServiceImpl implements PerroService {

  private static final int KNN_LEN = 12;
  private static final int USER_CAN_LEN = 12;
  @PersistenceContext private EntityManager entityManager;
  private static final int TIMEOUT = 5000;
  @Autowired private AppProperties appProperties;
  // @Autowired private MeGustaService mgService;

  List<Long> userLikes;

  /**
   * dogList : Lista de perros que se ajustan al perfil del usuario y las reglas de las fundaciones
   * return Lista de n elementos random escogidoappPropertiess de la lista de entrada
   */
  private List<Perro> getRandomDogsNoRepeat(List<Perro> dogList, int n) {

    List<Perro> randomList = new ArrayList<Perro>(n);
    int numberOfElements = n;

    for (int i = 0; i < numberOfElements; i++) {
      int randomIndex = new Random(System.currentTimeMillis()).nextInt(dogList.size());
      Perro randomElement = dogList.get(randomIndex);
      randomList.add(randomElement);
      dogList.remove(randomIndex);
    }
    return randomList;
  }

  @Override
  public List<Perro> getPerroForUsuario(Usuario user) {
    List<Perro> complientPerros = getPerrosWithMandatoryBussinessRules(user);
    List<java.util.function.Predicate<Perro>> predicates = getNoMandatoryPredicates(user);
    List<Perro> perroWithNoMandatory = new ArrayList<>();
    for (java.util.function.Predicate<Perro> predicate : predicates) {
      perroWithNoMandatory.addAll(
          (Collection<? extends Perro>)
              complientPerros.stream().filter(predicate).collect(Collectors.toList()));
    }

    List<Perro> perroWithNoMandatoryDistinct =
        perroWithNoMandatory.stream().distinct().collect(Collectors.toList());
    if (perroWithNoMandatoryDistinct.size() > USER_CAN_LEN) {
      return getRandomDogsNoRepeat(perroWithNoMandatoryDistinct, USER_CAN_LEN);
    } else {
      perroWithNoMandatoryDistinct.addAll(
          getRandomDogsNoRepeat(
              complientPerros, USER_CAN_LEN - perroWithNoMandatoryDistinct.size()));
      return perroWithNoMandatoryDistinct;
    }
  }

  private List<java.util.function.Predicate<Perro>> getNoMandatoryPredicates(Usuario user) {
    List<java.util.function.Predicate<Perro>> predicates = new ArrayList<>();
    if (TipoFamilia.HIJOS.getTipoFamilia().equalsIgnoreCase(user.getTipoFamilia())) {
      java.util.function.Predicate<Perro> predicate = (Perro p) -> p.getEdad() > 5;
      predicates.add(predicate);
    }
    if (Salario.Salario2.getSalario().equalsIgnoreCase(user.getSueldo())) {
      java.util.function.Predicate<Perro> predicate =
          (Perro p) -> p.getAtencionesespeciales() == 1 || p.getEdad() > 5;
      predicates.add(predicate);
    }
    if (Vivienda.CASA.toString().equalsIgnoreCase(user.getVivienda())) {
      java.util.function.Predicate<Perro> predicate =
          (Perro p) ->
              p.getEdad() > 5
                  || (p.getEdad() < 5 && Tamano.GRANDE.getTamano().equalsIgnoreCase(p.getTamano()));
      predicates.add(predicate);
    }

    return predicates;
  }

  private List<Perro> getPerrosWithMandatoryBussinessRules(Usuario user) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Perro> query = cb.createQuery(Perro.class);
    Root<Perro> perro = query.from(Perro.class);
    userLikes = getDogsIdLiked(user.getUserid());
    List<Predicate> predicates = new ArrayList<>();

    if (TipoFamilia.HIJOS.getTipoFamilia().equalsIgnoreCase(user.getTipoFamilia())) {
      predicates.add(cb.equal(perro.get("ninos"), 1));
      predicates.add(cb.equal(perro.get("agresividad"), Agresividad.BAJO.toString().toLowerCase()));
    }
    if (Estilo.ORDEN.getEstilo().equalsIgnoreCase(user.getEstilo())) {
      predicates.add(cb.equal(perro.get("pelaje"), pelaje.CORTO.toString().toLowerCase()));
    }
    if (Salir.OUTDOOR.toString().equalsIgnoreCase(user.getSalir())) {
      predicates.add(cb.equal(perro.get("actividadfisica"), 1));
    }
    if (Objects.nonNull(user.getViajes()) && user.getViajes() == 1) {
      predicates.add(cb.lessThan(perro.get("edad"), 5));
    }
    if (!userLikes.isEmpty()) {
      predicates.add(cb.not(perro.get("perroid").in(userLikes)));
    }

    query.select(perro).where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

    return entityManager.createQuery(query).getResultList();
  }

  private List<Long> getDogsIdLiked(Long userid) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> query = cb.createQuery(Long.class);
    Root<megusta> megusta = query.from(megusta.class);
    query
        .select(megusta.get("id").get("perroid"))
        .where(cb.equal(megusta.get("id").get("userid"), userid))
        .distinct(true);

    return entityManager.createQuery(query).getResultList();
  }

  @Override
  public List<Perro> getPerroForUsuarioWithKnn(Usuario user) {
    List<Perro> complientPerros = getPerrosWithMandatoryBussinessRules(user);
    List<java.util.function.Predicate<Perro>> predicates = getNoMandatoryPredicates(user);
    List<Integer> clusters = getDogsClusterLiked(userLikes);
    List<Perro> perroWithClusters = new ArrayList<>();
    perroWithClusters.addAll(
        complientPerros.stream()
            .filter(p -> clusters.contains(p.getCluster()))
            .collect(Collectors.toList()));
    List<Perro> perroWithNoMandatory = new ArrayList<>();
    for (java.util.function.Predicate<Perro> predicate : predicates) {
      perroWithNoMandatory.addAll(
          (Collection<? extends Perro>)
              perroWithClusters.stream().filter(predicate).collect(Collectors.toList()));
    }

    List<Perro> perroWithNoMandatoryDistinct =
        perroWithNoMandatory.stream().distinct().collect(Collectors.toList());
    if (perroWithNoMandatoryDistinct.size() >= KNN_LEN) {
      return getRandomDogsNoRepeat(perroWithNoMandatoryDistinct, KNN_LEN);
    } else if (perroWithClusters.size() >= KNN_LEN) {
      return getRandomDogsNoRepeat(perroWithClusters, KNN_LEN);
    } else {
      perroWithNoMandatoryDistinct.addAll(
          perroWithClusters.stream().distinct().collect(Collectors.toList()));
      if (perroWithNoMandatoryDistinct.size() < KNN_LEN) {
        perroWithNoMandatoryDistinct.addAll(
            getRandomDogsNoRepeat(complientPerros, KNN_LEN - perroWithNoMandatoryDistinct.size()));
      } else if (perroWithNoMandatoryDistinct.size() > KNN_LEN) {
        return getRandomDogsNoRepeat(perroWithNoMandatoryDistinct, KNN_LEN);
      }
      return perroWithNoMandatoryDistinct;
    }
  }

  private List<Integer> getDogsClusterLiked(List<Long> dogsIds) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
    Root<Perro> dogs = query.from(Perro.class);
    query.select(dogs.get("cluster")).where(dogs.get("perroid").in(dogsIds)).distinct(true);

    return entityManager.createQuery(query).getResultList();
  }

  @Override
  public List<Perro> getDogForUserFromModel(Usuario user) {
    var modelConf = appProperties.getModel();
    List<Perro> complientPerros = getPerrosWithMandatoryBussinessRules(user);
    List<RecomenderPojo> dfRecommendation = new ArrayList<>();
    for (Perro perro : complientPerros) {
      var recomendation =
          RecomenderPojo.builder()
              .perroid(perro.getPerroid())
              .dogEdad(perro.getEdad())
              .tamano(perro.getTamano())
              .sexo(perro.getSexo())
              .color(perro.getColor())
              .pelaje(perro.getPelaje())
              .atencionesespeciales(perro.getAtencionesespeciales())
              .ninos(perro.getNinos())
              .entrenado(perro.getEntrenado())
              .esterilizado(perro.getEsterilizado())
              .perros(perro.getOtrosperros())
              .actividad_y(perro.getActividadfisica())
              .agresividad(perro.getAgresividad())
              .userid(user.getUserid())
              .viajes(user.getViajes())
              .tipofamilia(user.getTipoFamilia())
              .actividad_x(user.getActividad())
              .salir(user.getSalir())
              .estilo(user.getEstilo())
              .vivienda(user.getVivienda())
              .estrato(user.getEstrato())
              .sueldo(user.getSueldo())
              .trabajo(user.getTrabajo())
              .userEdad(user.getEdad())
              .build();
      dfRecommendation.add(recomendation);
    }
    JSONObject dogs = null;
    if (dfRecommendation.size() > 0) {
      try {
        dogs =
            this.postRequest(
                    modelConf.getPropertiesHost(), modelConf.getUriModel(), dfRecommendation)
                .block();
      } catch (WebClientException e) {
        log.error("Error while calling the model", e);
        return List.of();
      }
    }

    List<Integer> dogsIds = (List<Integer>) dogs.get("perros");
    log.info("dogs:\n", dogsIds);
    // saveRecommendation(dogsIds, user.getUserid());
    List<Perro> modelRecommended = getDogsRecommended(dogsIds);

    return modelRecommended;
  }

  /* private void saveRecommendation(List<Integer> dogsIds, Long userid) {
    for (Integer perroid : dogsIds) {
      log.info("recom perroid:", perroid);
      mgService.saveMg(userid, perroid.longValue(), 2.5);
    }
  } */

  private List<Perro> getDogsRecommended(List<Integer> dogsIds) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Perro> query = cb.createQuery(Perro.class);
    Root<Perro> perro = query.from(Perro.class);

    query.select(perro).where(perro.get("perroid").in(dogsIds));

    return entityManager.createQuery(query).getResultList();
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
              log.info(response.bodyToMono(JSONObject.class).toString());
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
}
