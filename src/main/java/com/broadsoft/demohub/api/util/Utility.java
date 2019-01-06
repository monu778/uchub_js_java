package com.broadsoft.demohub.api.util;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class Utility {
	
	private static Logger logger = Logger.getLogger(Utility.class);

	public static boolean isAuthenticated(String auth){
		return true;
	}
	
	public static boolean isAuthenticated2(HttpServletRequest httpRequest){
		boolean isAuthenticated = false;
		String authString = httpRequest.getHeader("Authorization");
		String uriString = null;
		if(authString == null){
			uriString = httpRequest.getRequestURI();
		    if(uriString != null && uriString.contains("?auth=")){
		    	int startIndex = uriString.indexOf("?auth="+6);
		    	int lastIndex  = uriString.indexOf("&",startIndex);
		    	if(startIndex < lastIndex){
					authString = uriString.substring(startIndex , lastIndex);
		    	}else {
					authString = uriString.substring(startIndex);
				}
		    }
		}
		
		
		return isAuthenticated;
	}
	
	public static String  randomNumber(){
		Random random = new Random();
		return String.valueOf(random.nextInt());
	}
	
	public static boolean isErrorResponse(String response){
		try{
			JSONObject dataObject= new JSONObject(response);
			String error = dataObject.optString("error");
			if(error.isEmpty()) return false;
			return true;
		}
		catch (Exception e) {
			logger.error("Exception Occured while Parsing Error Response "+e.getMessage());
		}
		return false;
	}

	public static String getError(String response) {
		JSONObject dataObject= new JSONObject(response);
		String error = dataObject.optString("error");
		return error;
	}
	
}
