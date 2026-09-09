package com.mar.ds.db.remote;

import com.mar.libhome.controller.TechApiRemote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static com.mar.libhome.utils.RestApiUtils.get;

@Slf4j
@Service
@Profile("production")
public class TechApiRemoteImpl implements TechApiRemote {

    @Value("${db.card.url}")
    private String cardUrl;

    public String checkHealth() {
        String url = cardUrl + "/actuator/health";
        log.debug(">> Check DB health: {}", url);
        String rsJson = get(url);
        log.debug("<< Check DB health: {}, RS: {}", url, rsJson);
        return rsJson;
    }

}
