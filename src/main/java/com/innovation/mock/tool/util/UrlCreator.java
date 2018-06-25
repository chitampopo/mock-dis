package com.innovation.mock.tool.util;

/**
 * Format:
 * http://{ivy.engine.host}:{ivy.engine.http.port}/{ivy.engine.context}/api/{ivy.request.application}/customernotificationsink
 * 
 */
public class UrlCreator extends UrlBuilder {

	private String url = "http://";

	@Override
	public void hasHost(String host) {
		url += host;
	}

	@Override
	public void hasPort(String port) {
		url += ":" + port;
	}

	@Override
	public void hasContext(String context) {
		url += "/" + context + "/api";
	}

	@Override
	public void hasApplication(String application) {
		url += "/" + application;
	}

	@Override
	public String build() {
		url += "/customernotificationsink";
		return url;
	}
}
