package com.innovation.mock.tool.entity;

import com.innovation.mock.tool.util.UrlCreator;

public class Server {
	private String project;
	private String serverType;
	private String host;
	private String port;
	private String context;
	private String application;
	private String username;
	private String password;
	private String sshUsername;
	private String sshPassword;

	public Server() {}

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

	public String buildAuthenInfo() {
		return this.username + ":" + this.password;
	}
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	
	
	public String getSshUsername() {
		return sshUsername;
	}

	public void setSshUsername(String sshUsername) {
		this.sshUsername = sshUsername;
	}

	public String getSshPassword() {
		return sshPassword;
	}

	public void setSshPassword(String sshPassword) {
		this.sshPassword = sshPassword;
	}

	public String buildRequestHeader() {
		String url = buildURL();
		String authentication = this.username + ":" + this.password;
		return String.format("POST %s %n %s", url, authentication);
	}
	
	/**
	 * Format: http://{ivy.engine.host}:{ivy.engine.http.port}/{ivy.engine.context}/api/{ivy.request.application}/customernotificationsink
	 * 
	 */
	public String buildURL() {
		UrlCreator urlCreator = new UrlCreator();
		urlCreator.hasHost(this.getHost());
		urlCreator.hasPort(this.getPort());
		urlCreator.hasContext(this.getContext());
		urlCreator.hasApplication(this.getApplication());
		return urlCreator.build();
	}
}
