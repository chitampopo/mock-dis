package com.innovation.mock.tool.util;

import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.WebidResults;

public class RequestUtil {

	@Value("${metadata.server.username}")
	private static String userName;
	
	@Value("${metadata.server.password}")
	private static String password;
	
	public static String buildAuthenticationHeader() {
		String rawAuthen = userName + ":" + password;
		byte[] base64CredsBytes = Base64.encodeBase64(rawAuthen.getBytes());
		return new String(base64CredsBytes);
	}
	
	public static HttpHeaders buildHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", "Basic " + buildAuthenticationHeader());
		return headers;
	}
	
	public static String buildJsonforWebIdResponse(Metadata metadata) throws JsonProcessingException {
		return new WebidResults(metadata.getRequest()).toJson();
	}

	public static HttpEntity<MultiValueMap<String, String>> buildRequestInformation(Metadata metadata, HttpHeaders headers) throws JsonProcessingException {
		MultiValueMap<String, String> parts = new LinkedMultiValueMap<String, String>();
        parts.add("webid_response", buildJsonforWebIdResponse(metadata));
        parts.add("your_transaction_id", metadata.getRequest().getTransaction_id());
        parts.add("webid_action_id", metadata.getRequest().getWebid_action_id());
        parts.add("webid_confirmed", metadata.getRequest().isConfirmed() ? "true" : "false");
        parts.add("webid_doc_signed", "");
        parts.add("webid_server_timestamp", "362054604");
        return new HttpEntity<MultiValueMap<String,String>>(parts, headers);
	}
}
