package com.innovation.elca.web.tool.elcawebtool.request;

public class Server {

	private String host;
	private String port;
	private String context;
	private String application;
	private String username;
	private String password;

	public Server() {
		this.host = "127.0.0.1";
		this.port = "8081";
		this.context = "ivy";
		this.application = "designer";
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
