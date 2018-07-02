package com.innovation.mock.tool.entity;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties")
@ConfigurationProperties(prefix = "config")
public class ServerProfileCollection {
	List<ServerProfile> serverProfiles;

	public List<ServerProfile> getServerProfiles() {
		return serverProfiles;
	}

	public void setServerProfiles(List<ServerProfile> serverProfiles) {
		this.serverProfiles = serverProfiles;
	}
}
