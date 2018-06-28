package com.innovation.mock.tool.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties("ftp")
@PropertySource("classpath:application.properties")
public class FtpInfo {

	private boolean enable;
	private String host;
	private int port;
	private String username;
	private String password;

	public FtpInfo(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public FtpInfo() {
		this.enable = true;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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
