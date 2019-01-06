package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInvite {

	@JsonProperty("id")
	private int id;
	@JsonProperty("confirmation")
	private int confirmation;
	@JsonProperty("confirmmessage")
	private String confirmationMessage;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getConfirmation() {
		return confirmation;
	}
	public void setConfirmation(int confirmation) {
		this.confirmation = confirmation;
	}
	public String getConfirmationMessage() {
		return confirmationMessage;
	}
	public void setConfirmationMessage(String confirmationMessage) {
		this.confirmationMessage = confirmationMessage;
	}
	
}
