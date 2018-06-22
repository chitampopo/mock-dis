package com.innovation.elca.web.tool.elcawebtool.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Request {

	private boolean confirmed;
	private String transaction_id;
	private String cob_id;
	private String accountHolder;
	private String webid_action_id;
	private UserData user_data;
	private UserDataPass user_data_pass;
	private CallCenterData callcenter_data;

	public Request() {
		this.confirmed = true;
		this.cob_id = "COB000001";
		this.accountHolder = "0";
		this.webid_action_id = "889000009";
		//this.transaction_id = "COB000001-0";

		this.user_data = new UserData();
		this.user_data.setFirstname("Account");
		this.user_data.setLastname("Holder");
		this.user_data.setCity("Zürich");
		this.user_data.setStreet("Bahnhofstrasse");
		this.user_data.setStreet_number("42");
		this.user_data.setZip("8000");
		this.user_data.setCountry("CH");
		this.user_data.setDob("17.08.1990");

		this.user_data_pass = new UserDataPass();
		this.user_data_pass.setExhibition_authority("MyAuthority");
		this.user_data_pass.setExpires_date("31.12.21");
		this.user_data_pass.setDate_of_issue("01.01.10");
		this.user_data_pass.setPass_nr("987654321");
		this.user_data_pass.setPass_type("Personalausweis");
		this.user_data_pass.setBirthplace("MyBirthplace");
		this.user_data_pass.setBirthname("MyBirthname");
		this.user_data_pass.setNationality("CH");

		this.callcenter_data = new CallCenterData();
		this.callcenter_data.setCallcenter_data_id("null");
		this.callcenter_data.setCallcenter_id("null");
	}

	@JsonIgnore
	public String getCob_id() {
		return cob_id;
	}

	public void setCob_id(String cob_id) {
		this.cob_id = cob_id;
	}

	@JsonIgnore
	public String getAccountHolder() {
		return accountHolder;
	}

	public void setAccountHolder(String accountHolder) {
		this.accountHolder = accountHolder;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getTransaction_id() {
		return getCob_id() + "-" + getAccountHolder();
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getWebid_action_id() {
		return webid_action_id;
	}

	public void setWebid_action_id(String webid_action_id) {
		this.webid_action_id = webid_action_id;
	}

	public UserData getUser_data() {
		return user_data;
	}

	public void setUser_data(UserData user_data) {
		this.user_data = user_data;
	}

	public UserDataPass getUser_data_pass() {
		return user_data_pass;
	}

	public void setUser_data_pass(UserDataPass user_data_pass) {
		this.user_data_pass = user_data_pass;
	}

	public CallCenterData getCallcenter_data() {
		return callcenter_data;
	}

	public void setCallcenter_data(CallCenterData callcenter_data) {
		this.callcenter_data = callcenter_data;
	}

}
