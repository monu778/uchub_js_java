package com.broadsoft.demohub.api.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class UserDataBean implements InitializingBean {
	
	private Logger logger = Logger.getLogger(UserDataBean.class);
	
	public Map<String , UserLoginData> userDataMap ;
	public String callbackUrl;
	public String calendarCallbackUrl;
	public String driveCallbackUrl;
	
	public Map<String , UserLoginData> userCalendarDataMap ;
	public Map<String, UserLoginData> userDriveDataMap;
	public Map<String, UserLoginData> getUserDriveDataMap() {
		return userDriveDataMap;
	}

	public void setUserDriveDataMap(Map<String, UserLoginData> userDriveDataMap) {
		this.userDriveDataMap = userDriveDataMap;
	}

	

	public Map<String, UserLoginData> getUserCalendarDataMap() {
		return userCalendarDataMap;
	}

	public void setUserCalendarDataMap(Map<String, UserLoginData> userCalendarDataMap) {
		this.userCalendarDataMap = userCalendarDataMap;
	}

	public String getCalendarCallbackUrl() {
		return calendarCallbackUrl;
	}

	public void setCalendarCallbackUrl(String calendarCallbackUrl) {
		this.calendarCallbackUrl = calendarCallbackUrl;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	public String getDriveCallbackUrl() {
		return driveCallbackUrl;
	}

	public void setDriveCallbackUrl(String driveCallbackUrl) {
		this.driveCallbackUrl = driveCallbackUrl;
	}

	public Map<String , UserLoginData> getUserDataMap() {
		return userDataMap;
	}

	public void setUserDataMap(Map<String , UserLoginData> userDataMap) {
		this.userDataMap = userDataMap;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		logger.info("Initializin map");
	}
	
	public void initMap(){
		
		logger.info("Initializing all the  maps");
		userDataMap = new HashMap<String , UserLoginData>();
		userCalendarDataMap  = new HashMap<String , UserLoginData>();
		userDriveDataMap = new HashMap<String , UserLoginData>();
	}
}
