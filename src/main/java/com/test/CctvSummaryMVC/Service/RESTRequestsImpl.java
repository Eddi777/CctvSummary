package com.test.CctvSummaryMVC.Service;

import com.test.CctvSummaryMVC.Models.CCTV;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class RESTRequestsImpl implements RESTRequests {

    RestTemplate restTemplate;

    public RESTRequestsImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Async
    @Override
    public CompletableFuture<CCTV[]> getCCTVList(final String startPoint) {
        return CompletableFuture.supplyAsync(() -> {
            return restTemplate.getForObject(
                    startPoint,
                    CCTV[].class);
        });

    }

    @Async
    @Override
    public CompletableFuture<CCTV> getCCTVData(final CCTV cctv, String url) {
        return CompletableFuture.supplyAsync(() -> {
            return restTemplate.getForObject(
                url,
                CCTV.class);
        });
    }
}