package com.broadsoft.demohub.api.service;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.broadsoft.demohub.api.beans.AppointmentRequest;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.CalendarData;
import com.broadsoft.demohub.api.beans.CalendarResponse;
import com.broadsoft.demohub.api.beans.InviteResponse;
import com.broadsoft.demohub.api.beans.SearchAppointment;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.beans.UserLoginData;
import com.broadsoft.demohub.api.beans.UserParticipant;
import com.broadsoft.demohub.api.manager.CalendarManagerIF;
import com.broadsoft.demohub.api.response.manager.ResponseBuilder;
import com.broadsoft.demohub.api.response.manager.ResponseObjectManager;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
@Path("/calendarservice")
public class CalendarService {

	private Logger logger = Logger.getLogger(LoginService.class);
	
	@Autowired
	CalendarManagerIF calendarManager;
	@Autowired
	UserDataBean userDataBean;
	@Autowired
	ResponseBuilder responseBuilder;
	
	@Autowired
	ResponseObjectManager responseObjectManager;
	
	@OPTIONS
	@Path("/allcalendar")
	public Response allCalendars()
	{
	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
		 
	    
	    	@POST
	  		@Produces(MediaType.APPLICATION_JSON)
	  		@Path("/allcalendar")
	  		public Response allCalendars(CalendarData calendarData , @Context HttpServletRequest httpRequest){
	  			
	  			logger.info("calling all calendars invites with auth "+calendarData.getAuth());
	  			ArrayList<CalendarResponse> response = new ArrayList<CalendarResponse>();
	  			String allCalendarResponse = null;
	  			UserLoginData userLoginData = new UserLoginData();
	  			try {
	  			userLoginData = userDataBean.getUserCalendarDataMap().get(calendarData.getAuth());
	  			calendarData.setSession(userLoginData.getSession());
	  			ObjectMapper mapper = new ObjectMapper();
	  	        logger.info("Request Type : "
	  	        		+ httpRequest.getRequestURL().toString()
	  				    + "\nPOST input for all calendars :"
	  				    + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
	  				    		calendarData));
	  			 
	  				logger.info("session:"+calendarData.getSession());
	  				
	  				response = calendarManager.allCalendar(calendarData);
	  				if(response.size() == 1 && response.get(0).getError() != null){
	  					CalendarResponse calendarResponse = response.get(0);
	  					logger.error("Error Occured While getting allcalendar Response : "+calendarResponse.getError());
	  					return responseBuilder.buildResponse(calendarResponse);
	  				}
	  				mapper.setSerializationInclusion(Include.NON_NULL);
	  				allCalendarResponse = "{ \"data\": "+mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response)+"}";
	  				
	  			} catch (Exception e) {
	  					logger.error("Exception Occured while getting all Invites "+e.getMessage());
	  					if(null == userLoginData){
	  					 CalendarResponse errorResponse = new CalendarResponse();
	  					errorResponse.setError("User Not Logged In");
	  					return responseBuilder.buildResponse(errorResponse);
	  					}
	  				}
	  	        	logger.info("all Invite Response "+allCalendarResponse);
	  	        	return responseBuilder.buildResponse(allCalendarResponse,"user_id",String.valueOf(userLoginData.getUserId()));
	  			
	  		}
	    	
	    	@POST
	  		@Produces(MediaType.APPLICATION_JSON)
	  		@Path("/getcalendarinvite")
	  		public Response getCalendarInvite(CalendarData mailData , @Context HttpServletRequest httpRequest){
	  			
	  			logger.info("calling get calendar invite with "+mailData.getAuth());
	  			BaseResponse baseResponse = new BaseResponse();
	  	
	  			InviteResponse inviteResponse= new InviteResponse();
	  			UserLoginData userLoginData = new UserLoginData();
	  			try {
	  			userLoginData = userDataBean.getUserCalendarDataMap().get(mailData.getAuth());
	  			mailData.setSession(userLoginData.getSession());
	  			ObjectMapper mapper = new ObjectMapper();
	  	        logger.info("Request Type : "
	  	        		+ httpRequest.getRequestURL().toString()
	  				    + "\nPOST input for getting a calendar invite :"
	  				    + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
	  				      mailData));
	  			 
	  				logger.info("session:"+mailData.getSession());
	  				
	  				inviteResponse = calendarManager.getCalendarInvite(mailData);
	  				if(inviteResponse.getError()!=null){
	  					baseResponse.setError(inviteResponse.getError());
	  				}
	  				else{
	  					baseResponse.setData(inviteResponse);
	  				}
	  				} catch (Exception e) {
	  					logger.error("Exception Occured while getting a Invite "+e.getMessage());
	  					if(null == userLoginData){
	  						baseResponse.setError("User Not Logged In");
	  					}
	  					else{
	  						baseResponse.setError(e.getMessage());
	  					}
	  				}
	  	        	logger.info("get Invite Response "+baseResponse);
	  	        	return responseBuilder.buildResponse(baseResponse);
	  			
	  		}
	    	
	    	@POST
	  		@Produces(MediaType.APPLICATION_JSON)
	  		@Path("/updateinvite")
	  		public Response updateInvite(UserParticipant mailData , @Context HttpServletRequest httpRequest){
	  			
	  			logger.info("calling update invite with "+mailData.getAuth());
	  			BaseResponse baseResponse = new BaseResponse();
	  			UserLoginData userLoginData = new UserLoginData();
	  			try {
	  			userLoginData = userDataBean.getUserCalendarDataMap().get(mailData.getAuth());
	  			mailData.setSession(userLoginData.getSession());
	  			ObjectMapper mapper = new ObjectMapper();
	  	        logger.info("Request Type : "
	  	        		+ httpRequest.getRequestURL().toString()
	  				    + "\nPOST input for update Invite :"
	  				    + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
	  				      mailData));
	  			 
	  				logger.info("session:"+mailData.getSession());
	  				
	  				baseResponse = calendarManager.updateInvite(mailData);
	  				
	  				} catch (Exception e) {
	  					logger.error("Exception Occured while updating Invite "+e.getMessage());
	  					if(null == userLoginData){
	  						baseResponse.setError("User Not Logged In");
	  					}
	  					else{
	  						baseResponse.setError(e.getMessage());
	  					}
	  				}
	  	        	logger.info("Update Invite Response "+baseResponse);
	  	        	return responseBuilder.buildResponse(baseResponse);
	  			
	  		}
	    	
	    	@OPTIONS
	    	@Path("/searchcalendar")
	    	public Response searchCalendar()
	    	{
	    	   return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	    	}
	    	
	    	
	    	@POST
	  		@Produces(MediaType.APPLICATION_JSON)
	  		@Path("/searchcalendar")
	  		public Response searchCalendar(SearchAppointment mailData , @Context HttpServletRequest httpRequest){
	  			
	  			logger.info("calling search calendar with "+mailData.getAuth());
	  			
	  			InviteResponse response = new InviteResponse();
	  			String searchCalendarResponse = null;
	  			BaseResponse baseResponse = new BaseResponse();
	  			JSONArray inviteArray = null;
	  			UserLoginData userLoginData = new UserLoginData();
	  			try {
	  			userLoginData = userDataBean.getUserCalendarDataMap().get(mailData.getAuth());
	  			mailData.setSession(userLoginData.getSession());
	  			ObjectMapper mapper = new ObjectMapper();
	  	        logger.info("Request Type : "
	  	        		+ httpRequest.getRequestURL().toString()
	  				    + "\nPOST input for search calendar :"
	  				    + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
	  				      mailData));
	  			 
	  				logger.info("session:"+mailData.getSession());
	  				response = calendarManager.searchCalendar(mailData);
	  				if(response.getError()!=null){
	  					baseResponse.setError(response.getError());
	  					return responseBuilder.buildResponse(baseResponse);
	  				}
	  				JSONObject dataObject= new JSONObject(response);
	  				inviteArray = dataObject.getJSONArray("calendarResponse");
	  				searchCalendarResponse = "{ \"data\":" + inviteArray.toString() + "}";
	  				
	  				} catch (Exception e) {
	  					logger.error("Exception Occured while Search Calendar "+e.getMessage());
	  					if(null == userLoginData){
	  						baseResponse.setError("User Not Logged In");
	  					}
	  					else{
	  						baseResponse.setError(e.getMessage());
	  					}
	  					return responseBuilder.buildResponse(baseResponse);
	  				}
	  			logger.info("Search calendar Response "+searchCalendarResponse);
	  			return responseBuilder.buildResponse(searchCalendarResponse,"user_id",String.valueOf(userLoginData.getUserId()));
	  			
	  		}
	    	
	    	@POST
	  		@Produces(MediaType.APPLICATION_JSON)
	  		@Path("/searchcalendarcontextual")
	  		public Response searchCalendarContextual(CalendarData mailData , @Context HttpServletRequest httpRequest){
	  			
	  			logger.info("calling search calendar contextual with "+mailData.getAuth());
	  			ArrayList<CalendarResponse> response = null;
	  			CalendarResponse calendarResponse = null;
	  			String contextualResponse = null;
	  			UserLoginData userLoginData = new UserLoginData();
	  			try {
	  			userLoginData = userDataBean.getUserCalendarDataMap().get(mailData.getAuth());
	  			mailData.setSession(userLoginData.getSession());
	  			ObjectMapper mapper = new ObjectMapper();
	  	        logger.info("Request Type : "
	  	        		+ httpRequest.getRequestURL().toString()
	  				    + "\nPOST input for search calendar contextual :"
	  				    + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
	  				      mailData));
	  			 
	  				logger.info("session:"+mailData.getSession());
	  				
	  				response = calendarManager.searchCalendarContextual(mailData);
	  				if(response.size() == 1 && response.get(0).getError() != null) {
	  					calendarResponse = response.get(0);
	  					return responseBuilder.buildResponse(calendarResponse);
	  				}
	  				final ObjectWriter writer = mapper.writer().withRootName("data");
	  				contextualResponse = writer.writeValueAsString(response);
	  				} catch (Exception e) {
	  					logger.error("Exception Occured while searching calendar contextual "+e.getMessage());
	  					if(null == userLoginData) {
	  						calendarResponse = new CalendarResponse();
	  						calendarResponse.setError( "User Not Logged In");
	  						return responseBuilder.buildResponse(calendarResponse);
	  					}
	  				}
	  	        	logger.info("search calendar contextual Response "+contextualResponse);
	  	        	return responseBuilder.buildResponse(contextualResponse,"user_id",String.valueOf(userLoginData.getUserId()));
	  			
	  		}
	    	
	    	@POST
	  		@Produces(MediaType.APPLICATION_JSON)
	  		@Path("/createappointment")
	  		public Response createAppointment(AppointmentRequest appointmentRequest , @Context HttpServletRequest httpRequest){
	  			
	  			logger.info("creating new appointment  ");
	  			String response = null;
	  			UserLoginData userLoginData = new UserLoginData();
	  			try {
	  			userLoginData = userDataBean.getUserCalendarDataMap().get(appointmentRequest.getAuth());
	  			ObjectMapper mapper = new ObjectMapper();
	  	        logger.info("Request Type : "
	  	        		+ httpRequest.getRequestURL().toString()
	  				    + "\nPOST input for creating new appointment :"
	  				    + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
	  				    		appointmentRequest));
	  			 

	  				
	  				response = calendarManager.createAppointment(appointmentRequest);
	  				} catch (Exception e) {
	  					logger.error("Exception Occured while creating new appointment "+e.getMessage());
	  					if(null == userLoginData)
	  					  response = "{\"error\":\"User Not Logged In\"}";
	  				}
	  	        	logger.info("create new appointment Response "+response);
	  	        	return responseBuilder.buildResponse(response);
	  			
	  		}
	    	
	    	@POST
	  		@Produces(MediaType.APPLICATION_JSON)
	  		@Path("/contextualuserinvite")
	  		public Response contextualUserInvite(CalendarData mailData , @Context HttpServletRequest httpRequest){
	  			
	  			logger.info("calling search calendar contextual for user invite with "+mailData.getAuth());
	  			String response = null;
	  			UserLoginData userLoginData = new UserLoginData();
	  			try {
	  			userLoginData = userDataBean.getUserCalendarDataMap().get(mailData.getAuth());
	  			mailData.setSession(userLoginData.getSession());
	  			ObjectMapper mapper = new ObjectMapper();
	  	        logger.info("Request Type : "
	  	        		+ httpRequest.getRequestURL().toString()
	  				    + "\nPOST input for search calendar contextual for user invite :"
	  				    + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
	  				      mailData));
	  			 
	  				logger.info("session:"+mailData.getSession());
	  				
	  				response = calendarManager.contextualUserInvite(mailData);
	  				} catch (Exception e) {
	  					logger.error("Exception Occured while searching calendar contextual for user invite "+e.getMessage());
	  					if(null == userLoginData)
	  					  response = "{\"error\":\"User Not Logged In\"}";
	  				}
	  	        	logger.info("search calendar contextual for user invite Response "+response);
	  	        	return responseBuilder.buildResponse(response);
	  			
	  		}
}
