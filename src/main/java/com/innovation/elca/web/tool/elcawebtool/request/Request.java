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

	public Request() {}

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
