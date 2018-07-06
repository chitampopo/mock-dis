package com.innovation.mock.tool.entity;

public enum DocumentStatus {
	WAITING_UPLOAD("0"),
	FILE_IS_CHECKING("1"),
	FILE_CHECKED("2"),
	ERROR("3");
	
	private String value;
	
	DocumentStatus(String value){
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
