package com.news.portal;

import com.news.portal.config.WebMvcConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
@EnableConfigurationProperties(WebMvcConfig.class)
@SpringBootTest
class PortalApplicationTests {

	@Test
	void contextLoads() {
	}

}
