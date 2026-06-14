package com.mar.ds.db.remote;

import com.mar.libhome.controller.TechApiRemote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static com.mar.libhome.utils.RestApiUtils.get;
import static com.mar.libhome.utils.RestApiUtils.getUri;

@Slf4j
@Service
@Profile("production")
public class TechApiRemoteImpl implements TechApiRemote {

    @Value("${libhome.db.url}")
    private String host;

    @Value("${libhome.db.port}")
    private Integer port;

    public String checkHealth() {
        String url = getUri(host, port) + "/actuator/health";
        log.debug(">> Check DB health: {}", url);
        String rsJson = get(url);
        log.debug("<< Check DB health: {}, RS: {}", url, rsJson);
        return rsJson;
    }

}
