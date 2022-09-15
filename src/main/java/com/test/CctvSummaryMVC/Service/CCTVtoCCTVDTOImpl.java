package com.test.CctvSummaryMVC.Service;

import com.test.CctvSummaryMVC.Models.CCTV;
import com.test.CctvSummaryMVC.Models.CCTVDTO;
import org.springframework.stereotype.Service;

@Service
public class CCTVtoCCTVDTOImpl implements CCTVtoCCTVDTO{

    @Override
    public CCTVDTO cctvToDto(CCTV cctv) {
        return new CCTVDTO(cctv.getId(),
                cctv.getUrlType(),
                cctv.getVideoUrl(),
                cctv.getValue(),
                cctv.getTtl());
    }
}
