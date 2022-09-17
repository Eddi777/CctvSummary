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

    private Map<Integer, CCTV> cctvMap = new ConcurrentHashMap<>();
    @Value("${app.CCTV_DATA_UPDATE_PERIOD}")
    private long CCTV_DATA_UPDATE_PERIOD; //Duration between update each CCTV, 10 sec
    @Value("${app.CCTV_LIST_UPDATE_PERIOD}")
    private long CCTV_LIST_UPDATE_PERIOD; //Duration between update list of all CCTVs, 60 sec
    private long cctvLastUpdateTime;
    @Value("${app.START_POINT}")
    private String START_POINT; // = "http://www.mocky.io/v2/5c51b9dd3400003252129fb5"; //start point for update list of all CCTVs

    public Set<CCTV> getCCTVList(){
        if (cctvMap.isEmpty()) {
            firstFillCCTVList();
        }
        if ((System.nanoTime() - cctvLastUpdateTime) > CCTV_LIST_UPDATE_PERIOD) {
            updateCCTVList();
        }
        return new HashSet<>(cctvMap.values());
    }

    public Set<CCTVDTO> getCCTVDetails(){
        if (cctvMap.isEmpty()) {
            firstFillCCTVList();
        }
        if ((System.nanoTime() - cctvLastUpdateTime) > CCTV_LIST_UPDATE_PERIOD) {
            updateCCTVList();
        }
        for (CCTV cctv: cctvMap.values()) {
            if ((System.nanoTime() - cctv.getLastCheckTime()) > CCTV_DATA_UPDATE_PERIOD ||
                    cctv.isChanged()) {
            cctvUpdate(cctv);
            cctvLastUpdateTime = System.nanoTime();
            }
        }
        return cctvMap.values().stream().
                map(cctVtoCCTVDTO::cctvToDto).
                collect(Collectors.toSet());
    }

    private void firstFillCCTVList() {
        CCTV[] resp = null;
        try {
            resp = restRequests.getCCTVList(START_POINT).get();
            System.out.println(Arrays.toString(resp));
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println("Start point is wrong");
            System.out.println(ex);
        }
        if (resp != null) {
            cctvMap.putAll(
                    Arrays.stream(resp).
                            map(item -> {
                            item.setChanged(true);
                            return item;
                            }).
                            collect(Collectors.toMap(CCTV::getId, Function.identity())));
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
                cctvMap.get(i).setChanged(false);
                cctvMap.get(i).setLastCheckTime(System.nanoTime());
            }
            CompletableFuture.allOf(lst.toArray(new CompletableFuture[0]))
                    .exceptionally(ex -> null)
                    .join();
        }
        cctvLastUpdateTime = System.nanoTime();
        System.out.println(cctvMap);
    }

    private void updateCCTVList() {
        CCTV[] resp = restTemplate.getForObject(
                START_POINT,
                CCTV[].class);
        if (resp == null){
            System.out.println("Start point is out of service");
            cctvLastUpdateTime = System.nanoTime();
        } else {
            cctvMap = Arrays.stream(resp).
                    map(item -> {
                        item.setChanged(true);
                        return item;
                    }).
                    collect(Collectors.toMap(CCTV::getId, Function.identity()));
        }
        cctvLastUpdateTime = System.nanoTime();
    }

    private void cctvUpdate(CCTV oCCTV) {
        CCTV cctv = new CCTV();
        cctv.setId(oCCTV.getId());
        cctv.setTokenDataUrl(oCCTV.getTokenDataUrl());
        cctv.setSourceDataUrl(oCCTV.getSourceDataUrl());
            //get request for SourceDataUrl
        CCTV resp = restTemplate.getForObject(
                cctv.getSourceDataUrl(),
                CCTV.class);
        if (!(resp ==null)){
            cctv.setUrlType(resp.getUrlType());
            cctv.setVideoUrl(resp.getVideoUrl());
        }
            //get request for TokenDataUrl
        resp = restTemplate.getForObject(
                cctv.getTokenDataUrl(),
                CCTV.class);
        if (!(resp ==null)) {
            cctv.setValue(resp.getValue());
            cctv.setTtl(resp.getTtl());
        }
        cctv.setLastCheckTime(System.nanoTime());
        cctv.setChanged(false);
        synchronized (cctvMap) {
            cctvMap.replace(cctv.getId(), cctv);
        }
    }
}
