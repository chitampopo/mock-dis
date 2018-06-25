package com.innovation.mock.tool.util;

public abstract class UrlBuilder {

	public abstract void hasHost(String host);
	public abstract void hasPort(String port);
	public abstract void hasContext(String context);
	public abstract void hasApplication(String application);
	
	public abstract String build();
}
