package com.broadsoft.demohub.api.manager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.broadsoft.demohub.api.adapter.LoginAdapter;
import com.broadsoft.demohub.api.beans.LoginDetails;
import com.broadsoft.demohub.api.beans.UserLoginData;
import com.broadsoft.demohub.api.service.LoginService;


public class LoginManager implements LoginManagerIF{

	private Logger logger = Logger.getLogger(LoginService.class);
	
	@Autowired
	LoginAdapter loginAdapter;
	
	@Override
	public UserLoginData userLogin(LoginDetails loginDetails) throws Exception{
	 return loginAdapter.userLogin(loginDetails);
	}
	
	@Override
	public String userLogout(LoginDetails loginDetails) throws Exception{
	 return loginAdapter.userLogout(loginDetails);
	}

}
