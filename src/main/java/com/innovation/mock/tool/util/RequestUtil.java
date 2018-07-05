package com.innovation.mock.tool.util;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.mock.tool.controller.WebUIController;
import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.WebidResults;

public class RequestUtil {

	private static final Logger logger = LoggerFactory.getLogger(WebUIController.class);
	
	public static String buildAuthenticationHeader(String username, String password) {
		String rawAuthen = username + ":" + password;
		byte[] base64CredsBytes = Base64.encodeBase64(rawAuthen.getBytes());
		String authenInfoEncoded = new String(base64CredsBytes);
		logger.info("Authen Info: " + authenInfoEncoded);
		return authenInfoEncoded;
	}
	
	public static HttpHeaders buildHeaders(String username, String password) {
		logger.info("Username: " + username + ", password: " + password);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", "Basic " + buildAuthenticationHeader(username, password));
		logger.info("Authen Info: " + headers);
		return headers;
	}
	
	public static String buildJsonforWebIdResponse(Metadata metadata) throws JsonProcessingException {
		String json = new WebidResults(metadata.getRequest()).toJson();
		logger.info("Web ID Response - Request body: " + json);
		return Base64.encodeBase64String(json.getBytes());
	}

	public static HttpEntity<MultiValueMap<String, String>> buildRequestInformation(Metadata metadata, HttpHeaders headers) throws JsonProcessingException {
		MultiValueMap<String, String> parts = new LinkedMultiValueMap<String, String>();
        parts.add("webid_response", buildJsonforWebIdResponse(metadata));
        parts.add("your_transaction_id", metadata.getRequest().getTransaction_id());
        parts.add("webid_action_id", metadata.getRequest().getWebid_action_id());
        parts.add("webid_confirmed", metadata.getRequest().isConfirmed() ? "1" : "0");
        parts.add("webid_doc_signed", "");
        parts.add("webid_server_timestamp", "362054604");
        return new HttpEntity<MultiValueMap<String,String>>(parts, headers);
	}
}
