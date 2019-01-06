package com.broadsoft.demohub.api.manager;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.broadsoft.demohub.api.adapter.HubAdapter;
import com.broadsoft.demohub.api.beans.LoginDetails;

public class HubManager implements HubManagerIF {
	
	@Autowired
	HubAdapter hubAdapter; 

	@Override
	public String getNotification() throws Exception {
		return hubAdapter.getNotification();
	}

	@Override
	public String notification(HttpServletRequest httpRequest) throws Exception {
		return hubAdapter.notification(httpRequest);
	}
	
	@Override
	public String mailLogout(String auth) throws Exception {
		return hubAdapter.mailLogout(auth);
	}
	
	@Override
	public String calendarLogout(String auth) throws Exception {
		return hubAdapter.calendarLogout(auth);
	}
	
	@Override
	public String driveLogout(String auth) throws Exception {
		return hubAdapter.driveLogout(auth);
	}

}
