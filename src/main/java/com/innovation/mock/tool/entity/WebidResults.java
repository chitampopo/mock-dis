package com.innovation.mock.tool.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebidResults {
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
		return new ObjectMapper().writeValueAsString(this.webid_result);
	}
}
