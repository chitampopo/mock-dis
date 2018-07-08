package com.innovation.mock.tool.entity;

public enum RequestStatus {
	ERROR("0"),
	SUCCESSFUL("1");
	
	private String value;

	RequestStatus(String value){
		this.setValue(value);
	}
	
	public String getValue() {
		return value;
	}

	private void setValue(String value) {
		this.value = value;
	}
}
