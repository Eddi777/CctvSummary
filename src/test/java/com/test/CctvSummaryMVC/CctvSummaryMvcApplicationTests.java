package com.test.CctvSummaryMVC;

import com.test.CctvSummaryMVC.Controllers.MainController;
import com.test.CctvSummaryMVC.Models.CCTV;
import com.test.CctvSummaryMVC.Models.CCTVDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
class CctvSummaryMvcApplicationTests {

	@Autowired
	MainController mainController;
	@Test
	void contextLoads() {
	}

	@Test
	void cctvListRequest(){
		Set<CCTV> list = mainController.getCCTVList().getBody();
		System.out.println(list);
		assert (list != null);
	}
	@Test
	void cctvDataRequest(){
		Set<CCTVDTO> list = mainController.getCCTVData().getBody();
		System.out.println(list);
		assert (list != null);
	}
}
