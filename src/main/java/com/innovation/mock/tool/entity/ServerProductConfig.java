package com.innovation.mock.tool.entity;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(name = "props", value = "classpath:application.properties", ignoreResourceNotFound = false)
@ConfigurationProperties(prefix = "config")
public class ServerProductConfig {
	List<ServerProduct> serverProducts;

	public List<ServerProduct> getServerProducts() {
		return serverProducts;
	}

	public void setServerProducts(List<ServerProduct> serverProducts) {
		this.serverProducts = serverProducts;
	}

}
