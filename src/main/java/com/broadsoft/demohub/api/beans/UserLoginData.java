package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

public class UserLoginData {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String username;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String callback;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String session;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String cookie;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer userId;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long contextId;
	
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	
	
}
