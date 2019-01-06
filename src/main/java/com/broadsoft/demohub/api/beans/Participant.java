package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Participant {

	@JsonProperty("type")
	private int type;
	@JsonProperty("mail")
	private String mail;
	@JsonProperty("field")
	private String field;
	@JsonProperty("display_name")
	private String displayName;
	@JsonProperty("id")
	private long id;
	@JsonProperty("confirmation")
	private Integer confirmation;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Integer getConfirmation() {
		return confirmation;
	}
	public void setConfirmation(Integer confirmation) {
		this.confirmation = confirmation;
	}
	
}
