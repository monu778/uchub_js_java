package com.broadsoft.demohub.api.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.broadsoft.demohub.api.beans.LoginDetails;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.beans.UserLoginData;
import com.broadsoft.demohub.api.manager.LoginManagerIF;

@Service
@Path("/loginservice")
public class LoginService {
	private Logger logger = Logger.getLogger(LoginService.class);
	
	@Autowired
	LoginManagerIF loginManager;
	@Autowired
	UserLoginData userLoginData;
	@Autowired
	UserDataBean userDataBean;
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	public Response userLogin(LoginDetails loginDetails , @Context HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
        try {
			logger.info("Request Type : "
			      + httpRequest.getRequestURL().toString()
			      + "\nPOST input for userLogin :"
			      + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
			            loginDetails));
		 
			logger.info("Login User "+loginDetails.getName());
			userLoginData = loginManager.userLogin(loginDetails);
			
			} catch (Exception e) {
				logger.error("Exception Occured while User Login "+e.getMessage());
			}
        	logger.info("User Login Response "+userLoginData);
			return Response.status(200).entity(userLoginData).build();
	}
	
	@POST
	@Path("/logout")
	public Response userLogout(LoginDetails loginDetails , @Context HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		String response = null;
        try {
			logger.info("Request Type : "
			      + httpRequest.getRequestURL().toString()
			      + "\nPOST input for userLogout :"
			      + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
			            loginDetails));
		 
			logger.info("key "+loginDetails.getKey());
			
				response = loginManager.userLogout(loginDetails);
			} catch (Exception e) {
				logger.error("Exception Occured while User Logout "+e.getMessage());
			}
        	logger.info("User Logout Response "+response);
			return Response.status(200).entity(response).build();
	}

}
