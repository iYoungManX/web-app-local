package com.csye6225.Config;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsDClientConfig {
    @Bean
    public StatsDClient statsD() {
        return new NonBlockingStatsDClient("web-request","localhost",8125);
    }
}
