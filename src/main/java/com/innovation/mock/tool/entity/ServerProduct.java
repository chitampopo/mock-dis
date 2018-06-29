package com.innovation.mock.tool.entity;

public class ServerProduct {
	private String name;
	private String host;
	private String port;
	private String sshPort;
	private String disFolder;
	private String environmentApplication;

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
}
