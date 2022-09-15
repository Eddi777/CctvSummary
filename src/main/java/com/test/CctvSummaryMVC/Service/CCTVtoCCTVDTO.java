package com.test.CctvSummaryMVC.Service;

import com.test.CctvSummaryMVC.Models.CCTV;
import com.test.CctvSummaryMVC.Models.CCTVDTO;

public interface CCTVtoCCTVDTO {
    CCTVDTO cctvToDto(CCTV cctv);
}
