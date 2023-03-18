package com.csye6225.Controller;

import com.csye6225.Util.Metrics;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnauthController {

    @Autowired
    StatsDClient statsDClient;
    @GetMapping("/healthz")
    public String heahlth(){
        statsDClient.incrementCounter(Metrics.GET_HEALTHZ);
        return "Healthy";
    }
}
