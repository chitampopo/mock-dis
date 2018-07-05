package com.innovation.mock.tool.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.mock.tool.controller.WebUIController;

public class WebidResults {
	private static final Logger logger = LoggerFactory.getLogger(WebidResults.class);
	private Request webid_result;

	public WebidResults(Request webid_result) {
		this.webid_result = webid_result;
	}

	public Request getWebid_result() {
		return webid_result;
	}

	public void setWebid_result(Request webid_result) {
		this.webid_result = webid_result;
	}

	public String toJson() throws JsonProcessingException {
		String result = new ObjectMapper().writeValueAsString(this);
		result = result.replace("\"confirmed\":true", "\"confirmed\":\"1\"");
		result = result.replace("\"confirmed\":false", "\"confirmed\":\"0\"");
		result.trim().replaceFirst("\ufeff", "");
		logger.info("Request json: " + result);
		return result;
	}
}
