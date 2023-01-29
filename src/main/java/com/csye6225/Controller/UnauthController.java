package com.csye6225.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnauthController {
    @GetMapping("/healthz")
    public String heahlth(){
        return "Healthy";
    }
}
