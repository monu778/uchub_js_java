package com.broadsoft.demohub.api.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.LoginDetails;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.beans.UserLoginData;
import com.broadsoft.demohub.api.config.ConfigManager;
import com.broadsoft.demohub.api.manager.HubManagerIF;
import com.broadsoft.demohub.api.manager.LoginManagerIF;
import com.broadsoft.demohub.api.response.manager.ResponseBuilder;
import com.broadsoft.demohub.api.response.manager.ResponseObjectManager;
import com.broadsoft.demohub.api.util.Utility;
import com.broadsoft.demohub.api.security.HashEncoderDecoder;

@Service
@Path("/")
public class HubService {
	
	
	private Logger logger = Logger.getLogger(HubService.class);
	
	@Autowired
	ResponseBuilder responseBuilder;
	@Autowired
	ResponseObjectManager responseObjectManager;
	@Autowired
	LoginManagerIF loginManager;
	@Autowired
	UserLoginData userLoginData;
	@Autowired
	UserDataBean userDataBean;
	@Autowired
	HashEncoderDecoder hashEncoderDecoder;
	@Autowired
	HubManagerIF hubManager;
	@Autowired
	ConfigManager configManager;
	
	@OPTIONS
	@Path("/microapp")
	public Response getMicroapp()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/microapp")
	public Response getMicroapp(@Context HttpServletRequest httpRequest) throws URISyntaxException{
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		Enumeration<String> headerParams = httpRequest.getParameterNames();
		while(headerParams != null && headerParams.hasMoreElements())
			logger.info("incoming headers"+headerParams.nextElement());
		String auth = httpRequest.getParameter("auth");
		logger.info("You called the micro app route! for "+auth);
		if(!Utility.isAuthenticated(auth)){
			return Response.status(200).entity(errorResponse).build();
		}
		
		String baseRequestUrl = configManager.getPropertyAsString("BASE_REQUEST_URL");
		logger.info("Redirecting to URL : "+baseRequestUrl+"/public/microapp.html");
		URI uri = new URI(baseRequestUrl+"/public/microapp.html");
		return responseBuilder.buildResponse(uri);
	}
	
	@OPTIONS
	@Path("/calendar/microapp")
	public Response getCalendarMicroapp()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/calendar/microapp")
	public Response getCalendarMicroapp(@Context HttpServletRequest httpRequest) throws URISyntaxException{
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		Enumeration<String> headerParams = httpRequest.getParameterNames();
		while(headerParams != null && headerParams.hasMoreElements())
			logger.info("incoming headers"+headerParams.nextElement());
		String auth = httpRequest.getParameter("auth");
		logger.info("You called the micro app route! for "+auth);
		if(!Utility.isAuthenticated(auth)){
			return Response.status(200).entity(errorResponse).build();
		}
		
		String baseRequestUrl = configManager.getPropertyAsString("BASE_REQUEST_URL");
		logger.info("Redirecting to URL : "+baseRequestUrl+"/public/calendar/microapp.html");
		URI uri = new URI(baseRequestUrl+"/public/calendar/microapp.html");
        return responseBuilder.buildResponse(uri);
	}
	
	@OPTIONS
	@Path("/contextual")
	public Response getContextual()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/contextual")
	public Response getContextual(@Context HttpServletRequest httpRequest) throws URISyntaxException{
		
		logger.info("We are requesting the contextual data for Mail");
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		logger.info("query String "+httpRequest.getQueryString());
		
		String queryString = httpRequest.getQueryString();
		String auth = null;
		String emails = null;
		String contextStr = null;
		
		if(queryString.contains("auth=")){
			auth = queryString.substring(queryString.indexOf("auth=")+5,queryString.indexOf("auth=")+37);
		}
		
		if(queryString.contains("context=")){
			contextStr = contextStr.substring(contextStr.indexOf("context=")+8);
		}
		
		JSONObject jsonObject = new JSONObject(contextStr);
		if(jsonObject.has("emails")){
			JSONArray emailsArray = jsonObject.getJSONArray("emails");
			emails = emailsArray.getString(0);
			logger.info("emails : "+emails);
		}
		
		String context = httpRequest.getContextPath()+"/rest";
		logger.info("You called the contextual route with context for mail as : "+context);
		String baseRequestUrl = configManager.getPropertyAsString("BASE_REQUEST_URL");
		logger.info("Redirecting to URL : "+baseRequestUrl+"/public/contextualMailDisp.html?auth="+auth+"&emails="+emails);
		URI uri = new URI(baseRequestUrl+"/public/contextualMailDisp.html?auth="+auth+"&emails="+emails);
        return responseBuilder.buildResponse(uri);
	}
	
	@OPTIONS
	@Path("/calendar/contextual")
	public Response getCalendarContextual()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/calendar/contextual")
	public Response getCalendarContextual(@Context HttpServletRequest httpRequest){
		
		logger.info("We are requesting the contextual data");
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		String auth = httpRequest.getHeader("Authentication");
		if(!Utility.isAuthenticated(auth)){
			return Response.status(200).entity(errorResponse).build();
		}
		String context = httpRequest.getContextPath()+"/rest";
		logger.info("You called the contextual route with context : "+context);
		return responseBuilder.buildResponse(context);	
	}
	
	@OPTIONS
	@Path("/test")
	public Response test()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/test")
	public Response test(@Context HttpServletRequest httpRequest){
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		String successResponse = "{\"message\": \"hooray! welcome to our api!\"}";
		String auth = httpRequest.getHeader("Authentication");
		if(!Utility.isAuthenticated(auth)){
			return responseBuilder.buildResponse(errorResponse);
		}
		logger.info("You called the test route!");
		return responseBuilder.buildResponse(successResponse);	
	}
	
	@OPTIONS
	@Path("/calendar/test")
	public Response calendarTest()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/calendar/test")
	public Response calendarTest(@Context HttpServletRequest httpRequest){
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		String successResponse = "{\"message\": \"hooray! welcome to our api!\"}";
		String auth = httpRequest.getHeader("Authentication");
		if(!Utility.isAuthenticated(auth)){
			return responseBuilder.buildResponse(errorResponse);
		}
		logger.info("You called the test route!");
		return responseBuilder.buildResponse(successResponse);	
	}
	
	@OPTIONS
	@Path("/pushnotifications")
	public Response getNotifications()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/pushnotifications")
	public Response getNotifications(@Context HttpServletRequest httpRequest){
		
		logger.info("We are requesting the notifications count");
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		String successResponse = "{\"count\": \"99\"}";
		
		try {
			successResponse = hubManager.getNotification();
		} catch (Exception e) {
			logger.error("Exception Occured While getting notification");
		}
		 return responseBuilder.buildResponse(successResponse);	
	}
	
	@OPTIONS
	@Path("/calendar/pushnotifications")
	public Response getCalendarNotifications()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/calendar/pushnotifications")
	public Response getCalendarNotifications(@Context HttpServletRequest httpRequest){
		
		logger.info("We are requesting the notifications count");
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		String successResponse = "{\"count\": \"99\"}";
		
		try {
			successResponse = hubManager.getNotification();
		} catch (Exception e) {
			logger.error("Exception Occured While getting notification");
		}
		 return responseBuilder.buildResponse(successResponse);	
	}
	
	@OPTIONS
	@Path("/notifications")
	public Response notification()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/notifications")
	public Response notification(@Context HttpServletRequest httpRequest){
		
		logger.info("We are requesting the notifications count");
		String successResponse = "{\"count\": \"99\"}";
		
		logger.info("query String "+httpRequest.getQueryString());
		
		try {
			successResponse = hubManager.notification(httpRequest);
		} catch (Exception e) {
			logger.error("Exception Occured While getting notification");
		}
		 return responseBuilder.buildResponse(successResponse);	
	}
	
	@OPTIONS
	@Path("/calendar/notifications")
	public Response calendarNotification()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/calendar/notifications")
	public Response calendarNotification(@Context HttpServletRequest httpRequest){
		
		logger.info("We are requesting the notifications count");
		String errorResponse = "{\"message\": \"you are not authenticated\"}";
		String successResponse = "{\"count\": \"0\"}";
		
		logger.info("query String "+httpRequest.getQueryString());
		
		/*try {
			successResponse = hubManager.notification(httpRequest);
		} catch (Exception e) {
			logger.error("Exception Occured While getting notification");
		}*/
		 return responseBuilder.buildResponse(successResponse);	
	}
	
	
	@OPTIONS
	@Path("/authenticate")
	public Response authenticate()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/authenticate")
	public Response authenticate(@Context HttpServletRequest httpRequest) throws URISyntaxException{
		
		logger.info("authenticate query String : "+httpRequest.getQueryString());
		String callbackUrl = httpRequest.getQueryString();
		if(callbackUrl.contains("callback=")){
			int index = callbackUrl.indexOf("callback=")+9;
			logger.info("callbackurl = "+callbackUrl.substring(index));
			String callBackUrl= callbackUrl.substring(index);
			if(callBackUrl.indexOf("&ua") != -1)
			{
				callBackUrl= callBackUrl.substring(0, callBackUrl.indexOf("&ua"));
				userDataBean.setCallbackUrl(callBackUrl);
				logger.info("callbackurl = "+ callBackUrl);
			}
			else {
				userDataBean.setCallbackUrl(callBackUrl);
			}
			
		}
			
		String baseRequestUrl = configManager.getPropertyAsString("BASE_REQUEST_URL");
		logger.info("Redirecting to URL : "+baseRequestUrl+"/public/signup.html");
		URI uri = new URI(baseRequestUrl+"/public/signup.html");
        return responseBuilder.buildResponse(uri);
	}
	
	@OPTIONS
	@Path("/mail/logout")
	public Response mailLogout()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/mail/logout")
	public Response mailLogout(@QueryParam("auth") String auth , @Context HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		String response = null;
        try {
			logger.info("Request Type : "
			      + httpRequest.getRequestURL().toString()
			      + "\n auth for User Logout For Mail :"+auth );
			
				response = hubManager.mailLogout(auth);
			} catch (Exception e) {
				logger.error("Exception Occured while User Logout "+e.getMessage());
			}
        	logger.info("User Logout Response "+response);
			return Response.status(200).entity(response).build();
	}
	
	@OPTIONS
	@Path("/calendar/logout")
	public Response calendarLogout()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/calendar/logout")
	public Response calendarLogout(@QueryParam("auth") String auth , @Context HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		String response = null;
        try {
        	logger.info("Request Type : "
  			      + httpRequest.getRequestURL().toString()
  			      + "\n auth for User Logout For Calendar :"+auth );
				response = hubManager.calendarLogout(auth);
			} catch (Exception e) {
				logger.error("Exception Occured while User Logout "+e.getMessage());
			}
        	logger.info("User Logout Response "+response);
			return Response.status(200).entity(response).build();
	}
	
	@OPTIONS
	@Path("/drive/logout")
	public Response driveLogout()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/drive/logout")
	public Response driveLogout(@QueryParam("auth") String auth , @Context HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		String response = null;
        try {
        	logger.info("Request Type : "
  			      + httpRequest.getRequestURL().toString()
  			      + "\n auth for User Logout For Drive :"+auth );
				response = hubManager.driveLogout(auth);
			} catch (Exception e) {
				logger.error("Exception Occured while User Logout "+e.getMessage());
			}
        	logger.info("User Logout Response "+response);
			return Response.status(200).entity(response).build();
	}
	
	@OPTIONS
	@Path("/calendar/authenticate")
	public Response calendarAuthenticate()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/calendar/authenticate")
	public Response calendarAuthenticate(@Context HttpServletRequest httpRequest) throws URISyntaxException{
		
		logger.info("authenticate calendar query String : "+httpRequest.getQueryString());
		String calendarCallbackUrl = httpRequest.getQueryString();
		if(calendarCallbackUrl.contains("callback=")){
			int index = calendarCallbackUrl.indexOf("callback=")+9;
			logger.info("calendarCallbackUrl = "+calendarCallbackUrl.substring(index));
			String callBackUrl= calendarCallbackUrl.substring(index);
			if( callBackUrl.indexOf("&ua") != -1)
			{
				callBackUrl= callBackUrl.substring(0, callBackUrl.indexOf("&ua"));
				userDataBean.setCalendarCallbackUrl(callBackUrl);
				logger.info("callbackurl = "+ callBackUrl);
			}
			else
			userDataBean.setCalendarCallbackUrl(callBackUrl);
			
		
		}
			
		String baseRequestUrl = configManager.getPropertyAsString("BASE_REQUEST_URL");
		logger.info("Redirecting to URL : "+baseRequestUrl+"/public/signupCalendar.html");
		URI uri = new URI(baseRequestUrl+"/public/signupCalendar.html");
        return responseBuilder.buildResponse(uri);
	}
	
	@OPTIONS
	@Path("/signupUser")
	public Response signupUser()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Path("/signupUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response signupUser(@FormParam("name") String name,
		@FormParam("password") String password  , @Context HttpServletRequest httpRequest) throws URISyntaxException {
		LoginDetails loginDetails = new LoginDetails();
		loginDetails.setName(name);
		loginDetails.setPassword(password);
		ObjectMapper mapper = new ObjectMapper();
		String response = null;
		String urlString = null;
		URI uri = null;
		String hashId = null;
		logger.info("Requesting signupUser api "+httpRequest.getRequestURI());
        try {
			logger.info("signupUser params : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(loginDetails));
			userLoginData = loginManager.userLogin(loginDetails);
			logger.info("Got the response for user :"+loginDetails.getName());
			hashId = hashEncoderDecoder.encode(name+"mail");
			logger.info("Unique hashId generated for mail is "+hashId);
            
            	userLoginData.setUsername(loginDetails.getName());
            	logger.info("Setting callback url : "+userDataBean.getCallbackUrl());
            	userLoginData.setCallback(userDataBean.getCallbackUrl());
            	userDataBean.getUserDataMap().put(hashId, userLoginData);
            	logger.info("Map : "+userDataBean.getUserDataMap());
            	
				logger.info("userDatabean  map : "+userDataBean.userDataMap);
				logger.info("LoginDetails Name : "+loginDetails.getName());
				urlString = userLoginData.getCallback()+"&auth="+hashId+"&username="+userLoginData.getUsername();
				urlString = urlString.substring(urlString.indexOf("https"));
				logger.info(" urlString====== "+URLDecoder.decode(urlString,"UTF-8"));
				uri = new URI(URLDecoder.decode(urlString,"UTF-8"));
				/*logger.info(" urlString====== "+urlString);
				urlString = "https://stage.hub.broadsoftlabs.com/v1/BloreHubTest9/auth?key=coreStage-dt&auth="+hashId+"&username=demo.openxchange@ucc.teaming.fr";
				uri = new URI(urlString);*/
			}
		  catch (Exception e) {
				logger.error("url malformed "+urlString);
				logger.error("Error Occured wile url forming: "+e.getMessage());
		  }
		logger.info("Redirceting to the url : "+uri);
        return responseBuilder.buildResponse(uri);
	}
	
	@OPTIONS
	@Path("/calendar/signupUser")
	public Response calendarSignupUser()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Path("/calendar/signupUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response calendarSignupUser(@FormParam("name") String name,
		@FormParam("password") String password  , @Context HttpServletRequest httpRequest) throws URISyntaxException {
		LoginDetails loginDetails = new LoginDetails();
		loginDetails.setName(name);
		loginDetails.setPassword(password);
		ObjectMapper mapper = new ObjectMapper();
		String urlString = null;
		URI uri = null;
		String hashId = null;
		logger.info("Requesting signupUser api "+httpRequest.getRequestURI());
        try {
			logger.info("signupUser params : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(loginDetails));
			userLoginData = loginManager.userLogin(loginDetails);
			logger.info("Got the response for user :"+loginDetails.getName());
			hashId = hashEncoderDecoder.encode(name+"calendar");
			//hashId = "Q2pzXmOnqR4AjrLgB7z8Ydk3lbWVKway";
			logger.info("Unique hashId generated is "+hashId);
            
            	userLoginData.setUsername(loginDetails.getName());
            	logger.info("Setting callback url : "+userDataBean.getCalendarCallbackUrl());
            	userLoginData.setCallback(userDataBean.getCalendarCallbackUrl());
            	logger.info("userDataBean.getUserCalendarDataMap()==="+userDataBean.getUserCalendarDataMap());
            	userDataBean.getUserCalendarDataMap().put(hashId, userLoginData);
            	logger.info("Calendar Map : "+userDataBean.getUserCalendarDataMap());
				logger.info("userCalendarDataMap  map : "+userDataBean.userCalendarDataMap);
				logger.info("LoginDetails Name : "+loginDetails.getName());
				urlString = userLoginData.getCallback()+"&auth="+hashId+"&username="+userLoginData.getUsername();
				urlString = urlString.substring(urlString.indexOf("https"));
				logger.info(" urlString====== "+URLDecoder.decode(urlString,"UTF-8"));
				uri = new URI(URLDecoder.decode(urlString,"UTF-8"));
				/*logger.info(" urlString====== "+urlString);
				//urlString = "https://stage.hub.broadsoftlabs.com/v1/BloreHubTest7/auth?key=coreStage-dt&auth=Q2pzXmOnqR4AjrLgB7z8Ydk3lbWVKway&username=demo.openxchange@ucc.teaming.fr";
				uri = new URI(urlString);*/
			}
		  catch (Exception e) {
				logger.error("url malformed "+urlString);
				logger.error("Error Occured wile url forming: "+e.getMessage());
		  }
		logger.info("Redirceting to the url : "+uri);
        return responseBuilder.buildResponse(uri);
	}
	
	
	@OPTIONS
	@Path("/drive/authenticate")
	public Response driveAuthenticate()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@GET
	@Path("/drive/authenticate")
	public Response driveAuthenticate(@Context HttpServletRequest httpRequest) throws URISyntaxException{
		
		logger.info("authenticate drive query String : "+httpRequest.getQueryString());
		String driveCallbackUrl = httpRequest.getQueryString();
		if(driveCallbackUrl.contains("callback=")){
			int index = driveCallbackUrl.indexOf("callback=")+9;
			logger.info("driveCallbackUrl = "+driveCallbackUrl.substring(index));
			String callbackUrlDrive= driveCallbackUrl.substring(index);
			if(callbackUrlDrive.indexOf("&ua") != -1)
			{
				callbackUrlDrive = callbackUrlDrive.substring(0, callbackUrlDrive.indexOf("&ua"));
				logger.info("driveCallbackUrl = "+callbackUrlDrive);
				userDataBean.setDriveCallbackUrl(callbackUrlDrive);
			}
			else
			userDataBean.setDriveCallbackUrl(driveCallbackUrl.substring(index));
		}
			
		String baseRequestUrl = configManager.getPropertyAsString("BASE_REQUEST_URL");
		logger.info("Base Request Url For Drive Authenticate = "+baseRequestUrl);
		logger.info("Redirecting to URL : "+baseRequestUrl+"/public/signupDrive.html");
		URI uri = new URI(baseRequestUrl+"/public/signupDrive.html");
        return responseBuilder.buildResponse(uri);
	}
	
	@OPTIONS
	@Path("/drive/signupUser")
	public Response driveSignupUser()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Path("/drive/signupUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response driveSignupUser(@FormParam("name") String name,
		@FormParam("password") String password  , @Context HttpServletRequest httpRequest) throws URISyntaxException {
		LoginDetails loginDetails = new LoginDetails();
		loginDetails.setName(name);
		loginDetails.setPassword(password);
		ObjectMapper mapper = new ObjectMapper();
		String urlString = null;
		URI uri = null;
		String hashId = null;
		logger.info("Requesting signupUser api for drive"+httpRequest.getRequestURI());
        try {
			logger.info("signupUser params for drive : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(loginDetails));
			userLoginData = loginManager.userLogin(loginDetails);
			logger.info("Got the response for user :"+loginDetails.getName());
			hashId = hashEncoderDecoder.encode(name+"drive");
			//hashId = "Q2pzXmOnqR4AjrLgB7z8Ydk3lbWVKway";
			logger.info("Unique hashId generated is "+hashId);
            
            	userLoginData.setUsername(loginDetails.getName());
            	logger.info("Setting callback url : "+userDataBean.getCalendarCallbackUrl());
            	userLoginData.setCallback(userDataBean.getDriveCallbackUrl());
            	logger.info("userDataBean.getUserDriveDataMap()==="+userDataBean.getUserDriveDataMap());
            	userDataBean.getUserDriveDataMap().put(hashId, userLoginData);
            	logger.info("Drive Map : "+userDataBean.getUserDriveDataMap());
            	
				logger.info("userDriveDataMap  map : "+userDataBean.userDriveDataMap);
				logger.info("LoginDetails Name : "+loginDetails.getName());
				urlString = userLoginData.getCallback()+"&auth="+hashId+"&username="+userLoginData.getUsername();
				urlString = urlString.substring(urlString.indexOf("https"));
				logger.info(" urlString====== "+URLDecoder.decode(urlString,"UTF-8"));
				uri = new URI(URLDecoder.decode(urlString,"UTF-8"));
			}
		  catch (Exception e) {
				logger.error("url malformed : "+urlString);
				logger.error("Error Occured wile url forming: "+e.getMessage());
		  }
		logger.info("Redirceting to the url : "+uri);
        return responseBuilder.buildResponse(uri);
	}
	
	
	@POST
	@Path("/refreshsession")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshMailSession(@FormParam("auth") String auth,
									   @FormParam("name") String name,
								       @FormParam("password") String password,
								       @FormParam("type") String type,
									   @Context HttpServletRequest httpRequest) throws URISyntaxException {
		BaseResponse baseResponse = new BaseResponse();
		logger.info("Requesting refresh session For  "+type);
        try {
        	LoginDetails loginDetails = new LoginDetails();
        	loginDetails.setAuth(auth);
        	loginDetails.setName(name);
        	loginDetails.setPassword(password);
			userLoginData = loginManager.userLogin(loginDetails);
			logger.info("Got the login response for user :"+loginDetails.getName());
			userLoginData.setUsername(loginDetails.getName());
			if(type.equalsIgnoreCase("calendar")){
				logger.info("userDataBean.getUserCalendarDataMap()==="+userDataBean.getUserCalendarDataMap());
				userDataBean.getUserCalendarDataMap().put(loginDetails.getAuth(), userLoginData);
				logger.info("Session Refreshed For "+type+"  Session : "+userLoginData.getSession());
			}else if(type.equalsIgnoreCase("drive")){
				logger.info("userDataBean.getUserDriveDataMap()==="+userDataBean.getUserDriveDataMap());
				userDataBean.getUserDriveDataMap().put(loginDetails.getAuth(), userLoginData);
				logger.info("Session Refreshed For "+type+"  Session : "+userLoginData.getSession());
			}else if(type.equalsIgnoreCase("mail")){
				logger.info("userDataBean.getUserDataMap()==="+userDataBean.getUserDataMap());
				userDataBean.getUserDataMap().put(loginDetails.getAuth(), userLoginData);
				logger.info("Session Refreshed For "+type+"  Session : "+userLoginData.getSession());
			}
			
            baseResponse.setData(userLoginData);
		}
		catch (Exception e) {
				logger.error("Error Occured while session refresh for type : "+type+" : "+e.getMessage());
				baseResponse.setError(e.getMessage());
		}
		
        return responseBuilder.buildResponse(baseResponse);
	}
}
