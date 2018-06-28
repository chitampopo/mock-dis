package com.innovation.mock.tool.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties("metadata")
@PropertySource("classpath:application.properties")
public class Metadata {
	private Request request;
	private Server server;

	public Metadata() {
		this.request = new Request();
		this.server = new Server();
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

}
