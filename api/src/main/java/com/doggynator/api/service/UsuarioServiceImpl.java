/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.doggynator.api.dto.UserPreferencesRequest;
import com.doggynator.api.enums.Actividad;
import com.doggynator.api.enums.Estilo;
import com.doggynator.api.enums.Estrato;
import com.doggynator.api.enums.Salario;
import com.doggynator.api.enums.Salir;
import com.doggynator.api.enums.TipoFamilia;
import com.doggynator.api.enums.Trabajo;
import com.doggynator.api.enums.UPreferencesType;
import com.doggynator.api.enums.USettingsStage;
import com.doggynator.api.enums.Vivienda;
import com.doggynator.api.model.UserStage;
import com.doggynator.api.model.Usuario;
import com.doggynator.api.repo.UserStageRepository;
import com.doggynator.api.repo.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioServiceImpl implements UsuarioService {

  @Autowired private UsuarioRepository userRepository;
  @Autowired private UserStageRepository ustageRepository;

  @Override
  public Optional<Usuario> findUsuarioByUserid(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public void saveUpreferences(List<UserPreferencesRequest> uPreferences, Long userId) {
    Usuario user = getUser(uPreferences, userId);
    userRepository.save(user);
    UserStage ustage = new UserStage(userId, USettingsStage.LIKES.getStage());
    ustageRepository.save(ustage);
  }

  private Usuario getUser(List<UserPreferencesRequest> uPreferences, Long userId) {
    Usuario user = new Usuario();
    user.setUserid(userId);
    for (UserPreferencesRequest preference : uPreferences) {
      if (preference.getOption().equals(UPreferencesType.FAMILIA.name().toLowerCase())) {
        user.setTipoFamilia(
            preference.getValue().equals(TipoFamilia.NOHIJOS.getTipoFamiliaReq())
                ? TipoFamilia.NOHIJOS.getTipoFamilia()
                : TipoFamilia.HIJOS.getTipoFamilia());
      }
      if (preference.getOption().equals(UPreferencesType.CASA.name().toLowerCase())) {
        user.setVivienda(
            preference.getValue().equals(Vivienda.CASA.getVivienda())
                ? Vivienda.CASA.toString()
                : Vivienda.APARTAMENTO.toString());
      }
      if (preference.getOption().equals(UPreferencesType.ESTILO.name().toLowerCase())) {
        user.setEstilo(
            preference.getValue().equals(Estilo.ORDEN.getEstiloReq())
                ? Estilo.ORDEN.getEstilo()
                : Estilo.DESORDEN.getEstilo());
      }
      if (preference.getOption().equals(UPreferencesType.SALIR.name().toLowerCase())) {
        user.setSalir(
            preference.getValue().equals(Salir.INDOOR.getSalir())
                ? Salir.INDOOR.toString()
                : Salir.OUTDOOR.toString());
      }
      if (preference.getOption().equals(UPreferencesType.ACTIVIDAD.name().toLowerCase())) {
        user.setActividad(
            preference.getValue().equals(Actividad.NETFLIX.getActividad())
                ? Actividad.NETFLIX.toString()
                : Actividad.CINE.toString());
      }
      if (preference.getOption().equals(UPreferencesType.TRABAJO.name().toLowerCase())) {
        user.setTrabajo(
            preference.getValue().equals(Trabajo.WFH.getTrabajo())
                ? Trabajo.WFH.toString()
                : Trabajo.OFICINA.toString());
      }
      if (preference.getOption().equals(UPreferencesType.ESTRATO.name().toLowerCase())) {
        user.setEstrato(Estrato.fromEstratoReq(preference.getValue()).getEstratoReq());
      }
      if (preference.getOption().equals(UPreferencesType.SALARIO.name().toLowerCase())) {
        user.setSueldo(Salario.fromSalarioReq(preference.getValue()).getSalario());
      }
      if (preference.getOption().equals(UPreferencesType.VIAJES.name().toLowerCase())) {
        user.setViajes(preference.getValue().equals("true") ? 1 : 0);
      }
      if (preference.getOption().equals(UPreferencesType.EDAD.name().toLowerCase())) {
        user.setEdad(Integer.parseInt(preference.getValue()));
      }
    }
    return user;
  }

  @Override
  public void saveUpreferencesFromFb(JSONObject pJson, Long userId) {
    log.info("UPreferences for user: {}", userId);
    Usuario user = new Usuario();
    user.setUserid(userId);
    String edad = pJson.get("Edad").toString();
    String viaja = pJson.get("Viajes").toString();
    String plan = pJson.get("Plan").toString();

    final String noInfo = "no informa";
    boolean canSave = false;
    if (!noInfo.equalsIgnoreCase(viaja)) {
      if (viaja.equals("si")) {
        user.setViajes(1);
      } else {
        user.setViajes(0);
      }
      canSave = true;
    }
    if (!noInfo.equalsIgnoreCase(plan)) {
      if (viaja.equals("si")) {
        user.setActividad(Actividad.CINE.toString());
      } else {
        user.setActividad(Actividad.NETFLIX.toString());
      }
      canSave = true;
    }

    if (canSave) {
      // py devolvio datos
      user.setFpy(1);
    } else {

      // py no devolvio datos
      user.setFpy(2);
    }
    userRepository.save(user);
  }

  @Override
  public List<UserPreferencesRequest> getUserPreferences(Long userid) throws InterruptedException {
    Optional<Usuario> user = userRepository.findById(userid);
    List<UserPreferencesRequest> upReq = new ArrayList<>();
    if (user.isPresent()) {
      upReq = getUPreferencesReques(user);
    }
    return upReq;
  }

  private List<UserPreferencesRequest> getUPreferencesReques(Optional<Usuario> user) {
    UserPreferencesRequest upReqTravel = new UserPreferencesRequest();
    upReqTravel.setOption(UPreferencesType.VIAJES.name().toLowerCase());
    if (user.get().getViajes() == null) {
      upReqTravel.setValue("null");
    } else if (user.get().getViajes() == 1) {
      upReqTravel.setValue("true");
    } else {
      upReqTravel.setValue("false");
    }

    UserPreferencesRequest upReqPlan = new UserPreferencesRequest();
    upReqPlan.setOption(UPreferencesType.ACTIVIDAD.name().toLowerCase());

    if (Actividad.CINE.toString().equals(user.get().getActividad())) {
      upReqPlan.setValue(Actividad.CINE.getActividad());
    } else if (Actividad.NETFLIX.toString().equals(user.get().getActividad())) {
      upReqPlan.setValue(Actividad.NETFLIX.getActividad());
    } else {
      upReqPlan.setValue("null");
    }

    UserPreferencesRequest upReqPy = new UserPreferencesRequest();
    upReqPy.setOption("fpy");
    upReqPy.setValue("2");
    return Arrays.asList(upReqTravel, upReqPlan, upReqPy);
  }
}
