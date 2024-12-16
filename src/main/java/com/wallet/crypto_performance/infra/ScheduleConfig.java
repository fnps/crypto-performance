package com.wallet.crypto_performance.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class ScheduleConfig {

    private static final Logger log = LoggerFactory.getLogger(ScheduleConfig.class);
    private final long period;

    public ScheduleConfig(ApplicationArguments args) {
        if (!args.containsOption("frequency")){
            log.warn("No frequency argument value, 5s will be applied.");
            this.period = 5000L;
        } else {
            this.period = Long.parseLong(args.getOptionValues("period").get(0));
        }
    }

    public long getPeriod() {
        return period;
    }
}