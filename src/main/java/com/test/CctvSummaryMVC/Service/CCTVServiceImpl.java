package com.test.CctvSummaryMVC.Service;

import com.test.CctvSummaryMVC.Models.CCTV;
import com.test.CctvSummaryMVC.Models.CCTVDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:application.properties")
public class CCTVServiceImpl {

    final RestTemplate restTemplate;
    final CCTVtoCCTVDTO cctVtoCCTVDTO;

    public CCTVServiceImpl(RestTemplate restTemplate, CCTVtoCCTVDTO cctVtoCCTVDTO) {
        this.restTemplate = restTemplate;
        this.cctVtoCCTVDTO = cctVtoCCTVDTO;
    }

    public Map<Integer, CCTV> cctvMap = new HashMap<>();

    @Value("${app.CCTV_DATA_UPDATE_PERIOD}")
    private long CCTV_DATA_UPDATE_PERIOD; //Duration between update each CCTV, 10 sec
    @Value("${app.CCTV_LIST_UPDATE_PERIOD}")
    private long CCTV_LIST_UPDATE_PERIOD; //Duration between update list of all CCTVs, 60 sec
    @Value("${app.START_POINT}")
    private String START_POINT; // = "http://www.mocky.io/v2/5c51b9dd3400003252129fb5"; //start point for update list of all CCTVs

    public Set<CCTV> getCCTVList(){
        if (cctvMap.isEmpty()) {
            updateCCTVList();
        }
        return new HashSet<>(cctvMap.values());
    }
    public Set<CCTVDTO> getCCTVDetails(){
        if (cctvMap.isEmpty()) {
            updateCCTVList();
            CCTV newCCTV;
            for (Integer item: cctvMap.keySet()) {
                newCCTV = cctvUpdate(cctvMap.get(item));
                synchronized (cctvMap) {
                    cctvMap.replace(item, newCCTV);
                }
            }
        }
        return cctvMap.values().stream().
                map(cctVtoCCTVDTO::cctvToDto).
                collect(Collectors.toSet());
    }
    private void updateCCTVList() {
        CCTV[] resp = restTemplate.getForObject(
                START_POINT,
                CCTV[].class);
        if (resp == null){
            cctvMap.clear();
        } else {
            synchronized (cctvMap) {
                cctvMap = Arrays.stream(resp).
                        map(item -> {
                            item.setChanged(true);
                            return item;
                        }).
                        collect(Collectors.toMap(CCTV::getId, Function.identity()));
            }
        }
    }

    private CCTV cctvUpdate(CCTV cctv) {
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
        System.out.println(cctv);
        System.out.println(resp);
        return cctv;
    }
}
