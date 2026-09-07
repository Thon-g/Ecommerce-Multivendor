package com.abs.app;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Disabled as it attempts to load full application context without database in CI/CD")
class EcommerceMultivendorApplicationTests {

	@Test
	void contextLoads() {
	}

}
