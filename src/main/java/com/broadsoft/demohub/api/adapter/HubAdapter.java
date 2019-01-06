package com.broadsoft.demohub.api.adapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.broadsoft.demohub.api.beans.AllMailResponse;
import com.broadsoft.demohub.api.beans.LoginDetails;
import com.broadsoft.demohub.api.beans.MailData;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.config.ConfigManager;

public class HubAdapter {

	private Logger logger = Logger.getLogger(HubAdapter.class);
	@Autowired
	ConfigManager configManager;
	@Autowired
	UserDataBean userDataBean;
	@Autowired
	MailAdapter mailAdapter;
	@Autowired
	MailData mailData;

	public String getNotification() {
		String response = "";
		String input = null;

		try {

			userDataBean.getUserDataMap();
			URL url = new URL(
					"https://stage.hub.broadsoftlabs.com/v1/BloreHubTest9/demo.openxchange@ucc.teaming.fr/push");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			input = "{\"type\": \"notification\", \"data\": {\"BloreHubTest9\": {\"count\":183}}}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.error("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}

			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while user logging out : " + e.getMessage());
			if (null == input)
				response = "{\"error\":\"User Not Logged In\"}";
		}

		return response;
	}

	public String notification(HttpServletRequest httpRequest) {
		String response = "";
		String queryString = null;
		String auth = null;
		try {
			queryString = httpRequest.getQueryString();
			auth = queryString.substring(queryString.indexOf("auth=") + 5, queryString.indexOf("auth=") + 37);
			mailData.setAuth(auth);
			mailData.setUnseen("true");
			ArrayList<AllMailResponse> unseenMailResponse = mailAdapter.getAllMails(mailData);
			int mailCount = unseenMailResponse.size();
			if(mailCount == 1 && unseenMailResponse.get(0).getError() != null){
				logger.error("Error Occured while calling notification So Setting mailcount to 0 : "+unseenMailResponse.get(0).getError());
				mailCount = 0;
			}
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			int refreshTime = 1000 * Integer.parseInt(configManager.getPropertyAsString("REFRESH_TIME"));
			long timestamp = gregorianCalendar.getTimeInMillis() + refreshTime;
			response = "{\"count\":" + mailCount + ",\"refreshAt\":" + timestamp + "}";

		} catch (Exception e) {
			logger.error("Exception Occured while user logging out : " + e.getMessage());
			response = "{\"error\":\"User Not Logged In\"}";
		}

		return response;
	}
	
	public String mailLogout(String auth) throws Exception{
		String response = "";
		String input = null;
		String openExchangeLoginUrl = configManager.getPropertyAsString("OX_LOGIN_URL");
		try {
			 URL url = new URL(openExchangeLoginUrl);
			 HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			 conn.setDoOutput(true);
			 conn.setRequestMethod("POST");
			 conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(auth).getCookie());
			 input = "session="+URLEncoder.encode(userDataBean.getUserDataMap().get(auth).getSession(), "UTF-8")
				                         +"&action="+URLEncoder.encode("logout","UTF-8");
			       
			 OutputStream os = conn.getOutputStream();
			 os.write(input.getBytes());
			 os.flush();

			 if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : "+conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			 }else{
				logger.info("Response Code : "+conn.getResponseCode());
				response = "User Logged out Successfully From Mail";
				userDataBean.getUserDataMap().remove(auth);
				logger.info("Removed Session Associated with mail auth "+auth);
				logger.info("User Logged out Successfully From Mail");
			 }
			
			 BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			 String output;
		 	 while ((output = br.readLine()) != null) {
				response = response + output;
			 }

			 conn.disconnect();

		  } catch (Exception e) {
			  logger.error("Exception Occured while mail logging out : "+e.getMessage());
			  if(null == input)
			  response = "User Not Logged In ";
		  } 
	return response;
	}
	
	public String calendarLogout(String auth) throws Exception{
		String response = "";
		String input = null;
		String openExchangeLoginUrl = configManager.getPropertyAsString("OX_LOGIN_URL");
		try {
			 URL url = new URL(openExchangeLoginUrl);
			 HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			 conn.setDoOutput(true);
			 conn.setRequestMethod("POST");
			 conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 conn.setRequestProperty("Cookie", userDataBean.getUserCalendarDataMap().get(auth).getCookie());
			 input = "session="+URLEncoder.encode(userDataBean.getUserCalendarDataMap().get(auth).getSession(), "UTF-8")
				                         +"&action="+URLEncoder.encode("logout","UTF-8");
			       
			 OutputStream os = conn.getOutputStream();
			 os.write(input.getBytes());
			 os.flush();

			 if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : "+conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			 }else{
				logger.info("Response Code : "+conn.getResponseCode());
				response = "User Logged out Successfully From Calendar";
				userDataBean.getUserCalendarDataMap().remove(auth);
				logger.info("Removed Session Associated with calendar auth "+auth);
				logger.info("User Logged out Successfully From Calendar");
			 }
			
			 BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			 String output;
		 	 while ((output = br.readLine()) != null) {
				response = response + output;
			 }

			 conn.disconnect();

		  } catch (Exception e) {
			  logger.error("Exception Occured while calendar logging out : "+e.getMessage());
			  if(null == input)
			  response = "User Not Logged In ";
		  } 
	return response;
	}
	
	public String driveLogout(String auth) throws Exception{
		String response = "";
		String input = null;
		String openExchangeLoginUrl = configManager.getPropertyAsString("OX_LOGIN_URL");
		try {
			 URL url = new URL(openExchangeLoginUrl);
			 HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			 conn.setDoOutput(true);
			 conn.setRequestMethod("POST");
			 conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			 conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(auth).getCookie());
			 input = "session="+URLEncoder.encode(userDataBean.getUserDriveDataMap().get(auth).getSession(), "UTF-8")
				                         +"&action="+URLEncoder.encode("logout","UTF-8");
			       
			 OutputStream os = conn.getOutputStream();
			 os.write(input.getBytes());
			 os.flush();

			 if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : "+conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			 }else{
				logger.info("Response Code : "+conn.getResponseCode());
				response = "User Logged out Successfully From Drive";
				userDataBean.getUserDriveDataMap().remove(auth);
				logger.info("Removed Session Associated with drive auth "+auth);
				logger.info("User Logged out Successfully From Drive");
			 }
			
			 BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			 String output;
		 	 while ((output = br.readLine()) != null) {
				response = response + output;
			 }

			 conn.disconnect();

		  } catch (Exception e) {
			  logger.error("Exception Occured while drive logging out : "+e.getMessage());
			  if(null == input)
			  response = "User Not Logged In ";
		  } 
	return response;
	}

}
