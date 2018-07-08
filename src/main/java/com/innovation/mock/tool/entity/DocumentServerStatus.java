package com.innovation.mock.tool.entity;

public enum DocumentServerStatus {
	WAITING_UPLOAD("0"),
	FILE_IS_CHECKING("1"),
	FILE_CHECKED("2"),
	ERROR("3");
	
	private String value;
	
	DocumentServerStatus(String value){
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	private void setValue(String value) {
		this.value = value;
	}
}
