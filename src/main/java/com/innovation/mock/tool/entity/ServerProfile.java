package com.innovation.mock.tool.entity;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerProfile {
	private String name;
	private String host;
	private String port;
	private String sshPort;
	private String sshUsername;
	private String sshPassword;
	private String disFolder;
	private String environmentApplication;
	private String elcaWsUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getSshPort() {
		return sshPort;
	}

	public void setSshPort(String sshPort) {
		this.sshPort = sshPort;
	}

	public String getDisFolder() {
		return disFolder;
	}

	public void setDisFolder(String disFolder) {
		this.disFolder = disFolder;
	}

	public String getEnvironmentApplication() {
		return environmentApplication;
	}

	public void setEnvironmentApplication(String environmentApplication) {
		this.environmentApplication = environmentApplication;
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

	public String getElcaWsUrl() {
		return elcaWsUrl;
	}

	public void setElcaWsUrl(String elcaWsUrl) {
		this.elcaWsUrl = elcaWsUrl;
	}
	
}
