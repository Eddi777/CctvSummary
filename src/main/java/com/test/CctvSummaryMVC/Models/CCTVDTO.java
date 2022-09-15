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
        return "<br/>{" +
                "<br/>  'id': " + id +
                "<br/>  'UrlType': " + UrlType +
                "<br/>  'videoUrl': " + videoUrl +
                "<br/>  'value': " + value +
                "<br/>  'ttl': " + ttl +
                "<br/>}";
    }
}
