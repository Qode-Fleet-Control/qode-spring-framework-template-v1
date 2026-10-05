package world.qode.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(WebConfig.class)
class HomeControllerTests {

	private MockMvc mvc;

	@BeforeEach
	void setUp(WebApplicationContext wac) {
		this.mvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	void healthIsOk() throws Exception {
		String body = mvc.perform(get("/health")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals("{\"status\":\"ok\"}", body);
	}

	@Test
	void rootServes() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
	}

}
