package com.test.CctvSummaryMVC.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
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
        return "\n{" +
                "\n'id': " + id +
                "\n'UrlType': " + UrlType +
                "\n'videoUrl': " + videoUrl +
                "\n'value': " + value +
                "\n'ttl': " + ttl +
                "\n}";
    }
}
