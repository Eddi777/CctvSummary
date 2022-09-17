package com.test.CctvSummaryMVC.Service;

import com.test.CctvSummaryMVC.Models.CCTV;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

public class RESTRequestsImpl implements RESTRequests {

    RestTemplate restTemplate;

    public RESTRequestsImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Async
    @Override
    public CompletableFuture<CCTV[]> getCCTVList(final String startPoint) throws InterruptedException {
        CCTV[] resp = restTemplate.getForObject(
                startPoint,
                CCTV[].class);
        if (resp == null) {
            throw new InterruptedException();
        } else {
            return CompletableFuture.completedFuture(resp);
        }
    }

    @Async
    @Override
    public CompletableFuture<CCTV> getCCTVData(final CCTV cctv, final String requestName) throws InterruptedException {
        CCTV resp = restTemplate.getForObject(
                requestName,
                CCTV.class);
        if (resp == null) {
            throw new InterruptedException();
        } else {
            return CompletableFuture.completedFuture(resp);
        }
    }
}