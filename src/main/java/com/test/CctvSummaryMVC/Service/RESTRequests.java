package com.test.CctvSummaryMVC.Service;

import com.test.CctvSummaryMVC.Models.CCTV;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface RESTRequests {
    CompletableFuture<CCTV[]> getCCTVList(final String startPoint) throws InterruptedException;
    CompletableFuture<CCTV> getCCTVData(CCTV cctv, String requestName) throws InterruptedException;
}
