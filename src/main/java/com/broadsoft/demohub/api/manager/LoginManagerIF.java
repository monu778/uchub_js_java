package com.broadsoft.demohub.api.manager;

import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.LoginDetails;
import com.broadsoft.demohub.api.beans.UserLoginData;

@Component
public interface LoginManagerIF {

	public UserLoginData userLogin(LoginDetails loginDetails) throws Exception;
	public String userLogout(LoginDetails loginDetails) throws Exception;
}
