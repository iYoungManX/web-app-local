package com.csye6225.Controller;

import com.csye6225.Util.Metrics;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class UnauthController {

    @Autowired
    StatsDClient statsDClient;
    @GetMapping("/healthz")
    public String heahlth(){
        log.info("GET /healthz");
        statsDClient.incrementCounter(Metrics.GET_HEALTHZ);
        return "Healthy";
    }


    @GetMapping("/health")
    public String heahlth2(){
        log.info("GET /healthy");
        statsDClient.incrementCounter(Metrics.GET_HEALTHZ);
        return "Healthy!!!!";
    }


    @GetMapping("/health3")
    public String heahlth3(){
        log.info("GET /healthy");
        statsDClient.incrementCounter(Metrics.GET_HEALTHZ);
        return "Healthy33333!!!!";
    }
}
