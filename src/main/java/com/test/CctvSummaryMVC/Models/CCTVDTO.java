package com.test.CctvSummaryMVC.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
public class CCTVDTO {
    int id;
    CCTVurlType UrlType;
    String videoUrl;
    String value;
    int ttl;

    @Override
    public String toString() {
        return "{" +
                "'id': " + id +
                "'UrlType': " + UrlType +
                "'videoUrl': " + videoUrl +
                "'value': " + value +
                "'ttl': " + ttl +
                "}";
    }
}
