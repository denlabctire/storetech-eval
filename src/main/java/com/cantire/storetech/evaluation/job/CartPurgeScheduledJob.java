package com.cantire.storetech.evaluation.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cantire.storetech.evaluation.service.CartPurgingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartPurgeScheduledJob {

    private final CartPurgingService cartPurgingService;
    
    @Scheduled(cron = "${jobs.purge.cron}")
    public void runPurge() {
        cartPurgingService.purgeCarts();
    }
}
