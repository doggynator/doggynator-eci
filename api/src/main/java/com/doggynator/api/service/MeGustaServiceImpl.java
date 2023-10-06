/* (C) Doggynator 2022 */
package com.doggynator.api.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.doggynator.api.dto.UserPreferencesRequest;
import com.doggynator.api.enums.USettingsStage;
import com.doggynator.api.model.MeGustaId;
import com.doggynator.api.model.UserStage;
import com.doggynator.api.model.megusta;
import com.doggynator.api.repo.UserStageRepository;
import com.doggynator.api.repo.megustaRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MeGustaServiceImpl implements MeGustaService {

  @Autowired megustaRepository mgRepository;
  @Autowired private UserStageRepository ustageRepository;

  @Override
  public void saveMeGusta(List<UserPreferencesRequest> megustanReq, Long userId) {
    for (UserPreferencesRequest mgr : megustanReq) {
      if (!mgr.getOption().equals("userid")) {
        saveMg(userId, Long.parseLong(mgr.getOption()), mgr.getValue().equals("0") ? 1.0 : 5.0);
        UserStage ustage = new UserStage(userId, USettingsStage.RECOMMENDER.getStage());
        ustageRepository.save(ustage);
      }
    }
  }

  @Override
  public void saveMg(Long userId, Long perroid, Double mgValue) {
    megusta mg;
    MeGustaId id;
    mg = new megusta();
    id = new MeGustaId();
    id.setUserid(userId);
    id.setPerroid(perroid);
    mg.setId(id);
    // var p = new Perro();
    // p.setPerroid(Long.parseLong(mgr.getOption()));
    // mg.setPerro(p);
    // var u = new Usuario();
    // u.setUserid(userId);
    // mg.setUsuario(u);

    mg.setMegusta(mgValue);
    mg.setRated_date(new Timestamp((new Date()).getTime()));
    log.info("MG**** {}", mg);
    mgRepository.save(mg);
  }
}
