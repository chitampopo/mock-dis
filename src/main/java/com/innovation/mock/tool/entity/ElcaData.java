package com.innovation.mock.tool.entity;

import java.util.Random;

public class ElcaData {

	private String elcaId;
	private String server;
	private String project;
	private String dossierId;
	private String documentId;
	private String documentType;
	private String bankNumber;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getDossierId() {
		return dossierId;
	}

	public void setDossierId(String dossierId) {
		this.dossierId = dossierId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getElcaId() {
		Random rand = new Random();
		this.elcaId = "3000000000" + (rand.nextInt(9999) + 1111);
		return this.elcaId;
	}

	public void setElcaId(String elcaId) {
		this.elcaId = elcaId;
	}

	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

}
