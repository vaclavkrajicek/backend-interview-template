package com.liftago.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DemoApplicationTests {

	protected MockMvc mvc;
	protected String uri = "/emails";

	@Autowired
	WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	protected String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}

	protected <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	protected String getControllerPostRequestJsonString(String email, String name) throws JsonProcessingException {
		ControllerPostRequest controllerPostRequest = new ControllerPostRequest();
		controllerPostRequest.setEmail(email);
		controllerPostRequest.setName(name);
		return this.mapToJson(controllerPostRequest);
	}

	protected MvcResult callRest(String inputJson) throws Exception {
		return mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
	}

	@Test
	public void testEmailPost() throws Exception {
		// first insert -> OK
		MvcResult mvcResultSuccess = callRest(getControllerPostRequestJsonString("john@email.com", "John"));
		assertEquals(HttpStatus.CREATED.value(), mvcResultSuccess.getResponse().getStatus());
		ControllerPostResponse mapFromJson = mapFromJson(mvcResultSuccess.getResponse().getContentAsString(),
				ControllerPostResponse.class);
		assertTrue(mapFromJson.result);

		// identical name + different email insert -> OK
		assertEquals(HttpStatus.CREATED.value(),
				callRest(getControllerPostRequestJsonString("johnny@email.com", "John")).getResponse().getStatus());

		// different name + different email insert -> OK
		assertEquals(HttpStatus.CREATED.value(),
				callRest(getControllerPostRequestJsonString("bob@email.com", "Robert")).getResponse().getStatus());

		// different name + identical email -> FAIL
		MvcResult mvcResultFailure = callRest(getControllerPostRequestJsonString("john@email.com", "Johnny"));
		assertEquals(HttpStatus.CONFLICT.value(), mvcResultFailure.getResponse().getStatus());
		assertFalse(
				mapFromJson(mvcResultFailure.getResponse().getContentAsString(), ControllerPostResponse.class).result);

		// identical name + identical email -> FAIL
		assertEquals(HttpStatus.CONFLICT.value(),
				callRest(getControllerPostRequestJsonString("john@email.com", "John")).getResponse().getStatus());
	}
}
