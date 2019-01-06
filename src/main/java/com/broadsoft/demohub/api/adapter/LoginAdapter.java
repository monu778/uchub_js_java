package com.broadsoft.demohub.api.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.LoginDetails;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.beans.UserLoginData;
import com.broadsoft.demohub.api.config.ConfigManager;

@Component
public class LoginAdapter {
	@Autowired
	UserDataBean userDataBean ;
	
	@Autowired
	ConfigManager configManager;
	
	HttpsURLConnection conn = null;
	private Logger logger = Logger.getLogger(LoginAdapter.class);
	public UserLoginData userLogin(LoginDetails loginDetails) throws Exception{
		String response = "";
		UserLoginData userLoginData = new UserLoginData();
		
		String openExchangeLoginUrl = configManager.getPropertyAsString("OX_LOGIN_URL");
		String cookie = configManager.getPropertyAsString("LOGIN_COOKIE");
		 try {
				URL url = new URL(openExchangeLoginUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				
				logger.info("OX Request for login: "+url);
				//conn.setRequestProperty("Cookie", cookie);
				
				String input = "name="+URLEncoder.encode(loginDetails.getName(), "UTF-8")
				       +"&password="+URLEncoder.encode(loginDetails.getPassword(),"UTF-8")
				       +"&action="+URLEncoder.encode("login","UTF-8");
				
				logger.info("OX Input Request for login: "+input);
				
				logger.info("Login Service Input : " +response);

				OutputStream os = conn.getOutputStream();
				os.write(input.getBytes());
				os.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : "+conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
				}

				logger.info("Response Code : "+conn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

				String output;
				while ((output = br.readLine()) != null) {
					response = response + output;
				}
				
				logger.info("Login Service Response : " +response);
				
				JSONObject jsonObject = new JSONObject(response);
				userLoginData.setSession(jsonObject.getString("session"));
				userLoginData.setUserId(jsonObject.getInt("user_id"));
				userLoginData.setContextId(jsonObject.getLong("context_id"));
				userLoginData.setUsername("user");
				
				for (Map.Entry<String, List<String>> entries : conn.getHeaderFields().entrySet()) {
				    String values = "";
				    for (String value : entries.getValue()) {
				        values += value + ",";
				    }
				    if(entries.getKey() != null && entries.getKey().contains("Set-Cookie")){
				    	logger.info(entries.getKey() + " - " +  values );
				    	String publicSession = null;
				    	String sessionSecret = null;
				    	String jsessionId = null;
				    	String cookies = "";
				    	if(values.indexOf("open-xchange-public-session") != -1){
				    		publicSession = values.substring(0, 1+values.indexOf(";"));
				    		cookies = cookies+publicSession;
				    	}
				    	if(values.indexOf("open-xchange-secret") != -1){
				    		sessionSecret = values.substring(values.indexOf("open-xchange-secret"), 1+values.indexOf(";",values.indexOf("open-xchange-secret")));
				    		cookies = cookies+sessionSecret;
				    		
				    	}
				    	if(values.indexOf("JSESSIONID") != -1){
				    		jsessionId = values.substring(values.indexOf("JSESSIONID"), values.indexOf(";",values.indexOf("JSESSIONID")));
				    		cookies = cookies+jsessionId;
				    		
				    	}logger.info("Cookies : " +cookies);
				    	userLoginData.setCookie(cookies);
				    }
				    
				   
				}

				conn.disconnect();

			  } catch (MalformedURLException e) {
				  logger.error("Exception Occured while user login");

			  } catch (IOException e) {
				  logger.error("Exception Occured while user login");
			 }
		return userLoginData;
	}
	
	
	public String userLogout(LoginDetails loginDetails) throws Exception{
		String response = "";
		String input = null;
		String openExchangeLoginUrl = configManager.getPropertyAsString("OX_LOGIN_URL");
		try {
			 URL url = new URL(openExchangeLoginUrl);
			 HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			 conn.setDoOutput(true);
			 conn.setRequestMethod("POST");
			 conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(loginDetails.getAuth()).getCookie());
			 input = "session="+URLEncoder.encode(userDataBean.getUserDataMap().get(loginDetails.getAuth()).getSession(), "UTF-8")
				                         +"&action="+URLEncoder.encode("logout","UTF-8");
			       
			 OutputStream os = conn.getOutputStream();
			 os.write(input.getBytes());
			 os.flush();

			 if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : "+conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			 }else{
				logger.info("Error Response Code : "+conn.getResponseCode());
				response = "User Logged out Successfully";
				userDataBean.getUserDataMap().remove(loginDetails.getAuth());
			 }
			
			 BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			 String output;
		 	 while ((output = br.readLine()) != null) {
				response = response + output;
			 }

			 conn.disconnect();

		  } catch (Exception e) {
			  logger.error("Exception Occured while user logging out : "+e.getMessage());
			  if(null == input)
			  response = "User Not Logged In ";
		  } 
	return response;
	}
	
	}

