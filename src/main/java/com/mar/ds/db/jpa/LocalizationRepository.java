package com.mar.ds.db.jpa;

import com.mar.ds.db.entity.Localization;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface LocalizationRepository extends JpaRepository<Localization, Long> {

    Logger log = Logger.getLogger(LocalizationRepository.class.getSimpleName());

    Localization findByKeyIs(String key);

    default String saveFindRuLocalByKey(String key) {
        try {
            if (key.startsWith("~~")) {
                return "*" + findByKeyIs(key.substring(2)).getRu();
            } else {
                return "*" + findByKeyIs(key).getRu();
            }
        } catch (Exception ex) {
            log.log(Level.WARNING, String.format("Key: %s. Msg: %s", key, ExceptionUtils.getRootCauseMessage(ex)));
        }
        return key;
    }

}
