package com.mar.ds.db.remote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.mar.libhome.utils.RestApiUtils.*;

/**
 * Репозиторий работы с таблицей изменений карточки.
 */
@Slf4j
@Service
public class TechApiRemote {

    @Value("${libhome.db.url}")
    private String host;

    @Value("${libhome.db.port}")
    private Integer port;

    public void checkHealth() {
        String url = getUri(host, port) + "/actuator/health";
        log.debug(">> Check DB health: {}", url);
        String rsJson = get(url);
        log.debug("<< Check DB health: {}, RS: {}", url, rsJson);
    }

}
