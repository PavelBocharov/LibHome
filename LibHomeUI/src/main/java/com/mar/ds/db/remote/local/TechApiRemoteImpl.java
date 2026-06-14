package com.mar.ds.db.remote.local;

import com.mar.libhome.controller.TechApiRemote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!production")
public class TechApiRemoteImpl implements TechApiRemote {

    public String checkHealth() {
        log.debug(">> Check DB health: LOCAL");
        log.debug("<< Check DB health: LOCAL, RS: OK");
        return "OK";
    }

}
