package com.broadsoft.demohub.api.manager;

import javax.servlet.http.HttpServletRequest;

import com.broadsoft.demohub.api.beans.LoginDetails;

public interface HubManagerIF {
	
	public String  getNotification() throws Exception;
	public String  notification(HttpServletRequest httpRequest) throws Exception;
	public String  mailLogout(String auth) throws Exception;
	public String  calendarLogout(String auth) throws Exception;
	public String  driveLogout(String auth) throws Exception;
}
