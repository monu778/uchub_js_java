package com.broadsoft.demohub.api.beans;

public class Authorization {
	
	private String accessToken;
	private String refresToken;
	
	public Authorization(){
		
	}
	public Authorization(String accessToken, String refresToken) {
		super();
		this.accessToken = accessToken;
		this.refresToken = refresToken;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefresToken() {
		return refresToken;
	}
	public void setRefresToken(String refresToken) {
		this.refresToken = refresToken;
	}
	

}
