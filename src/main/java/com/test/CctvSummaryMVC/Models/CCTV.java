package com.test.CctvSummaryMVC.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CCTV {
    int id;

    String sourceDataUrl;
    CCTVurlType UrlType;
    String videoUrl;

    String tokenDataUrl;
    String value;
    int ttl;

    long lastCheckTime; //время последнего опроса камеры
    boolean isChanged = false; // Признак изменения данных камеры (sourceDataUrl или tokenDataUrl);
}
