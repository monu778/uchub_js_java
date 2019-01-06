package com.broadsoft.demohub.api.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Status {

	private String statusCode;
	private String statusMsg;
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getStatusMsg() {
		return statusMsg;
	}
	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
	

}