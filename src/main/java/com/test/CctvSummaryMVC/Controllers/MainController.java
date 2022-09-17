package com.test.CctvSummaryMVC.Controllers;

import com.test.CctvSummaryMVC.Models.CCTV;
import com.test.CctvSummaryMVC.Models.CCTVDTO;
import com.test.CctvSummaryMVC.Service.CCTVServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@RestController
@RequestMapping("/")
public class MainController {

    final CCTVServiceImpl cctvServiceImpl;
    final RestTemplate restTemplate;

    public MainController(CCTVServiceImpl cctvServiceImpl, RestTemplate restTemplate) {
        this.cctvServiceImpl = cctvServiceImpl;
        this.restTemplate = restTemplate;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<CCTV>> getCCTVList(){
        return new ResponseEntity<>(
                cctvServiceImpl.getCCTVList(),
                HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<Set<CCTVDTO>> getCCTVData(){
        return new ResponseEntity<>(
                cctvServiceImpl.getCCTVDetails(),
                HttpStatus.OK);
    }
}
