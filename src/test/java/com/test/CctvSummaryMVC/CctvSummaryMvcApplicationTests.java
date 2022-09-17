package com.test.CctvSummaryMVC;

import com.test.CctvSummaryMVC.Controllers.MainController;
import com.test.CctvSummaryMVC.Models.CCTV;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CctvSummaryMvcApplicationTests {

	@Autowired
	MainController mainController;
	@Test
	void contextLoads() {
	}

	@Test
	void cctvListRequest(){
//		List<CCTV> list = mainController.getCCTVList().getBody();
//		assert (list.size() != 0);

	}

}
