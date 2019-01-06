package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
	
	private String username;
	private String callback;
	@JsonProperty("id")
	private Integer id;
	@JsonProperty("confirmation")
	private Integer confirmation;
	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(Integer confirmation) {
		this.confirmation = confirmation;
	}

}
