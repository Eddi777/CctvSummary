package com.test.CctvSummaryMVC.Service;

import com.test.CctvSummaryMVC.Models.CCTV;
import com.test.CctvSummaryMVC.Models.CCTVDTO;

import java.util.List;

public interface CCTVService {
    List<CCTVDTO> getCCTVDetails();
    List<CCTV> getCCTVList();
}
