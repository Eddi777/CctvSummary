package com.test.CctvSummaryMVC.Service;

import com.test.CctvSummaryMVC.Models.CCTV;
import com.test.CctvSummaryMVC.Models.CCTVDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:application.properties")
public class CCTVServiceImpl {

    final RestTemplate restTemplate;
    final CCTVtoCCTVDTO cctVtoCCTVDTO;
    final RESTRequests restRequests;

    public CCTVServiceImpl(RestTemplate restTemplate, CCTVtoCCTVDTO cctVtoCCTVDTO, RESTRequests restRequests) {
        this.restTemplate = restTemplate;
        this.cctVtoCCTVDTO = cctVtoCCTVDTO;
        this.restRequests = restRequests;
    }

    private final Map<Integer, CCTV> cctvMap = new ConcurrentHashMap<>();
    @Value("${app.CCTV_LIST_UPDATE_PERIOD}")
    private long CCTV_LIST_UPDATE_PERIOD; //Duration between update list and CCTVs
    private long cctvLastUpdateTime;
    @Value("${app.START_POINT}")
    private String START_POINT; // = "http://www.mocky.io/v2/5c51b9dd3400003252129fb5"; //start point for update list of all CCTVs

    public Set<CCTV> getCCTVList(){
        if (cctvMap.isEmpty()) {
            firstFillCCTVList();
        }
        if ((System.nanoTime() - cctvLastUpdateTime) > CCTV_LIST_UPDATE_PERIOD) {
            cctvListUpdate();
        }
        return new HashSet<>(cctvMap.values());
    }

    public Set<CCTVDTO> getCCTVDetails(){
        if (cctvMap.isEmpty()) {
            firstFillCCTVList();
        }
        if ((System.nanoTime() - cctvLastUpdateTime) > CCTV_LIST_UPDATE_PERIOD) {
            cctvListUpdate();
        }
        return cctvMap.values().stream().
                map(cctVtoCCTVDTO::cctvToDto).
                collect(Collectors.toSet());
    }

    private void firstFillCCTVList() {
        CCTV[] resp = new CCTV[0];
        try {
            resp = restRequests.getCCTVList(START_POINT).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Start point is broken");
        }
        if (resp == null) {
            System.out.println("Start point is broken");
            cctvMap.put(1, new CCTV());
        }else {
            cctvMap.putAll(
                    Arrays.stream(resp).collect(
                            Collectors.toMap(CCTV::getId, Function.identity())));
            List<CompletableFuture<CCTV>> lst = new ArrayList<>();
            for (int i: cctvMap.keySet()){
                CompletableFuture<CCTV> r1 = restRequests.
                        getCCTVData(cctvMap.get(i), cctvMap.get(i).getSourceDataUrl()).
                        thenApply(item -> {
                            cctvMap.get(i).setUrlType(item.getUrlType());
                            cctvMap.get(i).setVideoUrl(item.getVideoUrl());
                            return null;
                        });
                lst.add(r1);
                CompletableFuture<CCTV> r2 = restRequests.
                        getCCTVData(cctvMap.get(i), cctvMap.get(i).getTokenDataUrl()).
                        thenApply(respCCTV -> {
                            cctvMap.get(i).setValue(respCCTV.getValue());
                            cctvMap.get(i).setTtl(respCCTV.getTtl());
                            return null;
                        });

                lst.add(r2);

            }
            CompletableFuture.allOf(lst.toArray(new CompletableFuture[0]))
                    .exceptionally(ex -> null)
                    .join();
        }
        cctvLastUpdateTime = System.nanoTime();
    }

    private void cctvListUpdate() {
        restRequests.getCCTVList(START_POINT).
                thenApply(items -> {
                    for (CCTV cctv : items) {
                        cctvUpdate(cctv);
                    }
                    return null;
                });
    }

    private void cctvUpdate(CCTV cctv) {
        cctvMap.putIfAbsent(cctv.getId(), cctv);
        cctvMap.get(cctv.getId()).setSourceDataUrl(cctv.getSourceDataUrl());
        restRequests.getCCTVData(cctv, cctv.getSourceDataUrl()).
                thenApply(res -> {
                    cctvMap.get(cctv.getId()).setUrlType(res.getUrlType());
                    cctvMap.get(cctv.getId()).setVideoUrl(res.getVideoUrl());
                    return null;
                });
        cctvMap.get(cctv.getId()).setTokenDataUrl(cctv.getTokenDataUrl());
        restRequests.getCCTVData(cctv, cctv.getTokenDataUrl()).
                thenApply(res -> {
                    cctvMap.get(cctv.getId()).setValue(res.getValue());
                    cctvMap.get(cctv.getId()).setTtl(res.getTtl());
                    return null;
                });
        cctvLastUpdateTime = System.nanoTime();
    }
}
