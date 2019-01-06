package com.broadsoft.demohub.api.adapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.AppointmentRequest;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.CalendarData;
import com.broadsoft.demohub.api.beans.CalendarResponse;
import com.broadsoft.demohub.api.beans.Facets;
import com.broadsoft.demohub.api.beans.Filter;
import com.broadsoft.demohub.api.beans.InviteResponse;
import com.broadsoft.demohub.api.beans.Participant;
import com.broadsoft.demohub.api.beans.SearchAppointment;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.beans.UserParticipant;
import com.broadsoft.demohub.api.config.ConfigManager;
import com.broadsoft.demohub.api.parser.InviteResponseParser;
import com.broadsoft.demohub.api.parser.MailResponseParser;
import com.broadsoft.demohub.api.util.Utility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CalendarAdapter {

	@Autowired
	UserDataBean userDataBean ;
	
	@Autowired
	ConfigManager configManager;
	
	@Autowired
	MailResponseParser mailResponseParser ;
	
	@Autowired
	InviteResponseParser inviteResponseParser ;
	
	HttpsURLConnection conn = null;
	private Logger logger = Logger.getLogger(LoginAdapter.class);
	
	
	// get all calendar API
	public ArrayList<CalendarResponse> allCalendar(CalendarData calendarData) throws Exception
	{
		ArrayList<CalendarResponse> allCalendarResponse = new ArrayList<CalendarResponse>();
		ArrayList<CalendarResponse> paginatedResponse = new ArrayList<CalendarResponse>();
		String response = "";
		String input = null;
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_CALENDAR_URL");
		String session = userDataBean.getUserCalendarDataMap().get(calendarData.getAuth()).getSession();
		String columns=new String();
		if(calendarData.getColumns() == null){
			columns = configManager.getPropertyAsString("CALENDAR_COLUMNS");
		}else{
			columns = calendarData.getColumns();
		}
		String start = calendarData.getStart_date_limit();
		String end = calendarData.getEnd_date_limit();
		logger.info("openExchangeMailUrl :: "+openExchangeMailUrl);
		try{
			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					
			conn.setRequestProperty("Cookie", userDataBean.getUserCalendarDataMap().get(calendarData.getAuth()).getCookie());
			logger.info("userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getCookie() :: "+userDataBean.getUserCalendarDataMap().get(calendarData.getAuth()).getCookie());
			
			input = "action="+URLEncoder.encode("all","UTF-8")
			+"&session="+URLEncoder.encode(session, "UTF-8")
			+"&columns="+URLEncoder.encode(columns,"UTF-8")
			+"&sort=201&order=asc";
			
			if(calendarData.getInviteFolderId()!=null )  //used in contextual user invite API
			{
				input=input+"&folder="+calendarData.getInviteFolderId()
				+"&showPrivate=true"
				+"&recurrence_master=false"
				+"&sort=201"
				+"&order=asc"
				+"&timezone=UTC";
				
			}
			
			logger.info("input 1=="+input);
			
			if(start != null && end != null)
			{
				input = input+"&start="+URLEncoder.encode(start,"UTF-8")
			+"&end="+URLEncoder.encode(end,"UTF-8");
				
			}

			logger.info("input 2=="+input);
			
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
			
			if(Utility.isErrorResponse(response)){
				CalendarResponse calendarResponse = new CalendarResponse();
				calendarResponse.setError(Utility.getError(response));
				allCalendarResponse.add(calendarResponse);
				logger.error("Error Occured while getting mail count" + Utility.getError(response));
			    return allCalendarResponse;
			}else{
				allCalendarResponse = inviteResponseParser.parseAllCalendarResponse(response);
			}

			conn.disconnect();
			
			//allCalendarResponse = filterResponse(allCalendarResponse , calendarData);
			
			if(calendarData.getLeftHandLimit()!=null &&  Integer.parseInt(calendarData.getLeftHandLimit()) >=0 && calendarData.getRightHandLimit() != null && Integer.parseInt(calendarData.getLeftHandLimit()) < Integer.parseInt(calendarData.getRightHandLimit()))
			{
				for(int i = Integer.parseInt(calendarData.getLeftHandLimit()) ; i < Integer.parseInt(calendarData.getRightHandLimit()) ; i++ ){
					if(i < allCalendarResponse.size())
					paginatedResponse.add(allCalendarResponse.get(i));
				}
			}else {
				paginatedResponse = allCalendarResponse;
			}
			
			
			
			
			
			/*removing the unecessary links in notes
			//removing the unecessary links in notes
			JSONObject calendarObject= new JSONObject(response);
			JSONArray CalendarDataArray= calendarObject.getJSONArray("data");
			JSONArray calendarArray = new JSONArray();
			for(int j=0;j<CalendarDataArray.length();j++)
			{
				// put code here to remove note
				calendarArray=CalendarDataArray.getJSONArray(j);
				if(calendarArray.get(12).toString().contains(":~::-\\nPlease do not edit this section of"))  { //contains(":~::-\\nPlease do not edit this section of")) {
					int index=calendarArray.getString(12).indexOf("-::~");
					String subString=calendarArray.getString(12).substring(0, index);
					calendarArray.put(12, subString);
				}
			}*/
			
			
				// String allInviteList="{\"data\":"+ calendarArray.toString()+ "}";
				 //response=allInviteList;
			
			
		}
		 catch (Exception e) {
			 logger.error("Exception Occured while getting all calendars : "+e.getMessage());
			  if(null == input)
			  response = "{\"error\":\"User Not Logged In\"}";
		 }
	return paginatedResponse;
		
	}
	
	private ArrayList<CalendarResponse> filterResponse(ArrayList<CalendarResponse> allCalendarResponse,CalendarData calendarData) {
		ArrayList<CalendarResponse> filteredResponse = new ArrayList<>();
		Iterator<CalendarResponse> itr = allCalendarResponse.iterator();
		while(itr.hasNext()){
			CalendarResponse calendarResponse = itr.next();
			if(calendarResponse.getStart_date() >= Long.parseLong(calendarData.getStart_date_limit())
				&&	calendarResponse.getEnd_date() <= Long.parseLong(calendarData.getEnd_date_limit())){
				filteredResponse.add(calendarResponse);
			}
		}
		return filteredResponse;
	}

	//get an invite API
	public InviteResponse getCalendarInvite(CalendarData mailData) throws Exception
	{
		String response = "";
		String input = null;
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_CALENDAR_URL");
		String session = userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getSession();
		String id=mailData.getObjectId();
		String folder=mailData.getInviteFolderId();
		InviteResponseParser inviteResponseParser= new InviteResponseParser();
		InviteResponse inviteResponse= new InviteResponse();
		
		try{
			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getCookie());
			
			input = "action="+URLEncoder.encode("get","UTF-8")
			+"&session="+URLEncoder.encode(session, "UTF-8")
			+"&id="+URLEncoder.encode(id,"UTF-8")
			+"&folder="+URLEncoder.encode(folder,"UTF-8");

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

			conn.disconnect();
			JSONObject inviteJsonResponse = new JSONObject(response);
			if(inviteJsonResponse.has("data")){
			inviteResponse=inviteResponseParser.parseInvite(response);
			
			//removing the unecessary links in note
			if(inviteResponse.getCalendarResponse().get(0).getNote()!=null)
			{
				int index=inviteResponse.getCalendarResponse().get(0).getNote().indexOf("-::~");
				inviteResponse.getCalendarResponse().get(0).setNote(inviteResponse.getCalendarResponse().get(0).getNote().substring(0, index));
				
			}
			}
			else if(inviteJsonResponse.has("error")){
				inviteResponse.setError(inviteJsonResponse.get("error").toString());
			}
			
		}
		 catch (Exception e) {
			 logger.error("Exception Occured while getting a invite : "+e.getMessage());
			  if(null == input){
				  inviteResponse.setError("User Not Logged In");
				  logger.error("{\"error\":\"User Not Logged In\"}");
			  }
			  else{
				  inviteResponse.setError(e.getMessage());
			  }
		 }
	return inviteResponse;
	}
	
	//update invite API
		public BaseResponse updateInvite(UserParticipant mailData) throws Exception
		{
			BaseResponse baseResponse = new BaseResponse();
			String response = "";
			String openExchangeMailUrl = configManager.getPropertyAsString("OX_CALENDAR_URL");
			String id=mailData.getObjectId();
			String folder = mailData.getFolderId();
			String session = userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getSession();
			String updateInviteUrl = openExchangeMailUrl+"?action=confirm&session="+session+"&folder="+folder+"&id="+id;
			
			try{
				
				 	URL url = new URL(updateInviteUrl);;
				   HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				   conn.setDoOutput(true);
				   conn.setRequestMethod("PUT");
				   conn.setRequestProperty("Content-Type", " application/json");
				   conn.setRequestProperty("Cookie", userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getCookie());
				   
				   StringBuilder jsonData= new StringBuilder();
				   jsonData.append("{");
				   jsonData.append("\"confirmation\":\""+Integer.parseInt(mailData.getConfirmation())+"\"");
				   jsonData.append(",");
				   if(mailData.getConfirmationMessage()!=null)
				   jsonData.append("\"confirmmessage\":\""+mailData.getConfirmationMessage()+"\"");
				   else
				   jsonData.append("\"confirmmessage\":\""+""+"\"");
				   jsonData.append("}");
				   
				   OutputStream os = conn.getOutputStream();
					os.write(jsonData.toString().getBytes());
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

					
					conn.disconnect();
					JSONObject updateInviteJson = new JSONObject(response);
					if(updateInviteJson.has("error")){
						baseResponse.setError(updateInviteJson.get("error").toString());
					}
					else{
						baseResponse.setData("Calendar invite updated successfully for objectID: " + id);
					}
				}
			catch (Exception e) {
				 logger.error("Exception Occured while updating Invites : "+e.getMessage());
				  if(null == userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getSession())
				  {
					  baseResponse.setError("User Not Logged In");
				  }
				  else{
					  baseResponse.setError(e.getMessage());
				  }
				 
			 }
			return baseResponse;
		}
		
		
		//search calendar
		public InviteResponse searchCalendar(SearchAppointment mailData) throws Exception{
			String response = "";
			int flag=0;
			Integer leftLimit = Integer.parseInt(mailData.getLeftHandLimit());
			Integer rightLimit = Integer.parseInt(mailData.getRightHandLimit());
			InviteResponse searchCalendarResponse = new InviteResponse();
			ArrayList<CalendarResponse> allCalendarResponse = new ArrayList<CalendarResponse>();
			ArrayList<CalendarResponse> allInvitesResponse = new ArrayList<CalendarResponse>();
			String session = userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getSession();
			String columns= configManager.getPropertyAsString("CALENDAR_COLUMNS");
			String openExchangeMailUrl = configManager.getPropertyAsString("OX_CALENDAR_URL"); 
			String calendarSearchUrl=openExchangeMailUrl  + "?action=search"+"&session="+session+"&columns="+columns;
			try{
				URL url = new URL(calendarSearchUrl);
				   HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				   logger.info("OX URL for Search Calendar : "+url.toString());
				   conn.setDoOutput(true);
				   conn.setRequestMethod("PUT");
				   conn.setRequestProperty("Content-Type", " application/json");
				   conn.setRequestProperty("Cookie", userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getCookie());
				   
				   StringBuilder jsonData= new StringBuilder();
				   jsonData.append("{");
				   if(null != mailData.getSearchString() && !mailData.getSearchString().isEmpty())
				   {
					   if(mailData.getSearchString().indexOf("from:") != -1) {
						   String startLetter=mailData.getSearchString().substring(mailData.getSearchString().indexOf(":")+1);
						   jsonData.append("\"startletter\":\""+startLetter+"\"");
						   flag++;
					   }
					   else
					   {
						   jsonData.append("\"pattern\":\""+mailData.getSearchString()+"\"");  //search based on content
					   }
				   }
				   jsonData.append("}");
				   
				   OutputStream os = conn.getOutputStream();
					os.write(jsonData.toString().getBytes());
					
					logger.info("OX Json Request for Search Calendar : "+jsonData.toString());
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
					logger.info("OX Response For Search Calendar : "+response);
					
					conn.disconnect();
					
					if(flag >0)
					{
						ArrayList<CalendarResponse> invites = inviteResponseParser.parseAllCalendarResponse(response);
						ArrayList<CalendarResponse> matchingInvitesList = new ArrayList<>();
						for(int i=0;i<invites.size();i++) {
							Participant[] participant= invites.get(i).getParticipants();
						inner:	for(int j=0;j<participant.length;j++) {
								if(null!=participant[j].getMail() && participant[j].getMail().contains(mailData.getSearchString().substring(mailData.getSearchString().indexOf(":")+1))) {
									matchingInvitesList.add(invites.get(i));
									break inner;
								}
							}
						}
						
						matchingInvitesList = addValidResults(matchingInvitesList, mailData.getStart_date_limit() , mailData.getEnd_date_limit());
						HashSet<CalendarResponse> responseSet = new HashSet<CalendarResponse>(matchingInvitesList);
						matchingInvitesList = new ArrayList<>(responseSet);
						Collections.sort(matchingInvitesList);
						if(leftLimit != null && rightLimit != null)
						matchingInvitesList = paginate(matchingInvitesList , leftLimit , rightLimit);

						
						Collections.sort(matchingInvitesList);
						searchCalendarResponse.setCalendarResponse(matchingInvitesList);
					}
					else
					{
						ArrayList<CalendarResponse> calendarResponses = inviteResponseParser.parseAllCalendarResponse(response);	
						calendarResponses = addValidResults(calendarResponses, mailData.getStart_date_limit() , mailData.getEnd_date_limit());
						HashSet<CalendarResponse> responseSet = new HashSet<CalendarResponse>(calendarResponses);
						calendarResponses = new ArrayList<>(responseSet);
						Collections.sort(calendarResponses);
						if(leftLimit != null && rightLimit != null)
						calendarResponses = paginate(calendarResponses ,leftLimit , rightLimit);	
						
						Collections.sort(calendarResponses);
							
						searchCalendarResponse.setCalendarResponse(calendarResponses);
					}
				
			}
					
			
			catch(Exception e)
			{
				logger.error("Exception Occured while searching invites  : "+e.getMessage());
				 
			}
			return searchCalendarResponse;
		}
		
		
		private ArrayList<CalendarResponse> paginate(ArrayList<CalendarResponse> matchingInvitesList, Integer leftLimit,Integer rightLimit) {

			ArrayList<CalendarResponse> paginatedList = new ArrayList<>();
			if(matchingInvitesList.size() > leftLimit){
				if(matchingInvitesList.size() >= rightLimit){
					for(int i = leftLimit ; i < rightLimit ; i++)
					paginatedList.add(matchingInvitesList.get(i));
				}else{
					for(int i = leftLimit ; i <matchingInvitesList.size() ; i++)
						paginatedList.add(matchingInvitesList.get(i));
					
				}
				
			}else{
				return paginatedList;
			}
			return paginatedList;
		
		}

		private ArrayList<CalendarResponse> addValidResults(ArrayList<CalendarResponse> calendarResponses,String start_date_limit, String end_date_limit) {

			ArrayList<CalendarResponse> searchList = new ArrayList<CalendarResponse>();
			for(int i = 0 ; i < calendarResponses.size() ; i++){
				if(calendarResponses.get(i).getStart_date() >= Long.parseLong(start_date_limit)
					&& calendarResponses.get(i).getStart_date() <= Long.parseLong(end_date_limit)){
					searchList.add(calendarResponses.get(i));
				}
			}
			for(int i = 0 ; i < calendarResponses.size() ; i++){
				Long startDateLimit = calendarResponses.get(i).getStart_date();
				Long endDateLimit = calendarResponses.get(i).getEnd_date();
				if(calendarResponses.get(i).getUntil() != null && calendarResponses.get(i).getUntil() > Long.parseLong(start_date_limit)
					&& calendarResponses.get(i).getRecurrence_type() != null && calendarResponses.get(i).getRecurrence_type() == 2){
					while(startDateLimit < calendarResponses.get(i).getUntil()){
						if(startDateLimit < Long.parseLong(start_date_limit)){
							startDateLimit = startDateLimit + (24*60*60*1000);
							endDateLimit = endDateLimit + (24*60*60*1000);
							continue;
						}
						
						if(startDateLimit > Long.parseLong(end_date_limit)){
							break;
						}
						CalendarResponse calendarResponse = new CalendarResponse();
						calendarResponse = copyContent(calendarResponses.get(i));
						calendarResponse.setStart_date(startDateLimit);
						calendarResponse.setEnd_date(endDateLimit);
						
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(startDateLimit);
						if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
								cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
							searchList.add(calendarResponse);	
						}
						startDateLimit = startDateLimit + (24*60*60*1000);
						endDateLimit = endDateLimit + (24*60*60*1000);
					}
				}
				}
				
					
			
			return searchList;
		}

		private CalendarResponse copyContent(CalendarResponse calendarResponse) {
			CalendarResponse response = new CalendarResponse();
			response.setLast_modified_utc(calendarResponse.getLast_modified_utc());
			response.setLast_modified(calendarResponse.getLast_modified());
			response.setCreation_date(calendarResponse.getCreation_date());
			response.setModified_by(calendarResponse.getModified_by());
			response.setCreated_by(calendarResponse.getCreated_by());
			response.setFolder_id(calendarResponse.getFolder_id());
			response.setNumber_of_attachments(calendarResponse.getNumber_of_attachments());
			response.setColor_label(calendarResponse.getColor_label());
			response.setCategories(calendarResponse.getCategories());
			response.setPrivate_flag(calendarResponse.getPrivate_flag());
			response.setFull_time(calendarResponse.getFull_time());
			response.setPrincipalId(calendarResponse.getPrincipalId());
			response.setOrganizerId(calendarResponse.getOrganizerId());
			response.setConfirmations(calendarResponse.getConfirmations());
			response.setSequence(calendarResponse.getSequence());
			response.setOrganizer(calendarResponse.getOrganizer());
			response.setUid(calendarResponse.getUid());
			response.setTitle(calendarResponse.getTitle());
			response.setStart_date(calendarResponse.getStart_date());
			response.setEnd_date(calendarResponse.getEnd_date());
			response.setShown_as(calendarResponse.getShown_as());
			response.setNote(calendarResponse.getNote());
			response.setRecurrence_type(calendarResponse.getRecurrence_type());
			response.setParticipants(calendarResponse.getParticipants());
			response.setUsers(calendarResponse.getUsers());
			response.setTimeZone(calendarResponse.getTimeZone());
			response.setAlarm(calendarResponse.getAlarm());
			response.setDays(calendarResponse.getDays());
			response.setDay_in_month(calendarResponse.getDay_in_month());
			response.setMonth(calendarResponse.getMonth());
			response.setInterval(calendarResponse.getInterval());
			response.setUntil(calendarResponse.getUntil());
			response.setNotification(calendarResponse.getNotification());
			response.setId(calendarResponse.getId());;
			return response;
		}

		/*
		 * calendar context API
		 */
		public ArrayList<CalendarResponse> searchCalendarContextual(CalendarData calendarData) throws Exception{
			
			logger.info("json resquest for Search Calendar : "+calendarData);
			String leftLimit = calendarData.getLeftHandLimit();
			String rightLimit = calendarData.getRightHandLimit();
			
			CalendarData calData= new CalendarData();
			calData= calendarData;
			calData.setLeftHandLimit(null);
			calData.setRightHandLimit(null);
			
			ArrayList<CalendarResponse> response = allCalendar(calData);
			if(response.size() == 1 && response.get(0).getError() != null) {
				return response;
			}
			ArrayList<CalendarResponse> invitesList = new ArrayList<>();
			ArrayList<CalendarResponse> matchingInvitesList = new ArrayList<>();
			ArrayList<CalendarResponse> paginatedResponse = new ArrayList<>();
			
			HashMap<String, String> userDetails= userDetailsMapping(calendarData);  //getting the user details map with id and emails of all users
			for(int k=0;k<response.size();k++) {
				Participant[] participant= response.get(k).getParticipants();
				for(int l=0; l<participant.length;l++)
				{
					long id = participant[l].getId();
					participant[l].setMail(userDetails.get(Long.toString(id)));
				}
			}
			
		for(int i=0;i<response.size();i++) {
			Participant[] participant= response.get(i).getParticipants();
		inner:	for(int j=0;j<participant.length;j++) {
				if(null!=participant[j].getMail() && participant[j].getMail().equalsIgnoreCase(calendarData.getEmailId())) {
					invitesList.add(response.get(i));
					break inner;
				}
			}
		}
		
		
		//once we get the invite List of particular emailId we search for the content match ; searching on paginated data
		if(calendarData.getContent() != null)
		{
		for(int j=0;j<invitesList.size();j++) {
			if((invitesList.get(j).getTitle() !=null && invitesList.get(j).getTitle().toLowerCase().contains(calendarData.getContent().toLowerCase()))|| (invitesList.get(j).getNote() != null && invitesList.get(j).getNote().toLowerCase().contains(calendarData.getContent().toLowerCase())))
				matchingInvitesList.add(invitesList.get(j));
			else
				continue;
		}
		invitesList.clear();
		invitesList=matchingInvitesList;
		}
		logger.info("search calendar contextual after searching on context basis : "+invitesList);	
		
		
		//paginating on the filtered list of invites
				calendarData.setLeftHandLimit(leftLimit);
				calendarData.setRightHandLimit(rightLimit);
				if(calendarData.getLeftHandLimit()!=null &&  Integer.parseInt(calendarData.getLeftHandLimit()) >=0 && calendarData.getRightHandLimit() != null && Integer.parseInt(calendarData.getLeftHandLimit()) < Integer.parseInt(calendarData.getRightHandLimit()))
				{
					for(int i = Integer.parseInt(calendarData.getLeftHandLimit()) ; i < Integer.parseInt(calendarData.getRightHandLimit()) ; i++ ){
						if(i < invitesList.size())
						paginatedResponse.add(invitesList.get(i));
					}
				}else {
					paginatedResponse = invitesList;
				}
		
		
		
		
		
		return paginatedResponse;
			
			
			
			
			
			/*String response="";// allCalendar(mailData);
			JSONArray selectedInvites= new JSONArray();
			JSONObject invitesObject= new JSONObject(response);
			JSONArray allInvitesArray= invitesObject.getJSONArray("data");
			
			 for(int i=0;i<allInvitesArray.length();i++){
				JSONArray array= allInvitesArray.getJSONArray(i);
				JSONArray participants=array.getJSONArray(21);
				inner :for(int j=0;j<participants.length();j++)
				{
					if(participants.getJSONObject(j).has("mail") && participants.getJSONObject(j).getString("mail").equalsIgnoreCase(mailData.getEmailId()))
					{
						selectedInvites.put(allInvitesArray.getJSONArray(i));
						break inner;
					}
				}
			}
			 String allinvitesList="{\"data\":"+ selectedInvites.toString()+ "}";
			 return allinvitesList;*/
		}
		

		//contextualUserInvite API
		public String contextualUserInvite(CalendarData mailData) throws Exception{
			String response = "";
			String invites="";
			mailData.setModule("calendar");
			StringBuffer input = new StringBuffer("");
			String openExchangeMailUrl = configManager.getPropertyAsString("OX_FOLDER_URL");
			String columns = null;
			String unseen = "false";
			try{
				
				URL url = new URL(openExchangeMailUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Cookie", userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getCookie());
				if(mailData.getColumns() == null){
					columns = configManager.getPropertyAsString("FOLDER_COLUMNS");
				}else{
					columns = mailData.getColumns();
				}
			
				//adding folder id in columns
				columns="1,"+columns;
				// getting folders list here	
				 
				input.append("action=allVisible");
				input.append("&altNames=true");  
				input.append("&timezone=UTC");
				input.append("&tree=1");
				input.append("&columns=");
				input.append(columns);
				input.append("&session=");
				input.append(URLEncoder.encode(userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getSession(), "UTF-8"));
				input.append("&content_type=");
				input.append(mailData.getModule());
				
				OutputStream os = conn.getOutputStream();
				os.write(input.toString().getBytes());
				os.flush();

				
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : "+conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
				}

				logger.error("Response Code : "+conn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

				String output;
				while ((output = br.readLine()) != null) {
					response = response + output;
				}

				conn.disconnect();
				
				//breaking the given emailID
				String[] emailPartsOne= mailData.getEmailId().split("[.]", 2);
				String[] emailPartsTwo=emailPartsOne[1].split("@",2);
				String filename=emailPartsTwo[0]+","+emailPartsOne[0];
				
				JSONObject dataObject= new JSONObject(response);
				JSONObject inviteObject= dataObject.getJSONObject("data");
				JSONArray invitesArray= inviteObject.getJSONArray("shared");
				String privateFolderId=null;
				
				for(int i=0;i<invitesArray.length();i++){
					JSONArray folderObject=invitesArray.getJSONArray(i);
					if(folderObject.getString(9).equalsIgnoreCase(filename))
					{
						privateFolderId=folderObject.getString(0);
						break;
					}
				}
				
				//fetching all calendars for a folder id
				if(privateFolderId!=null){
				mailData.setInviteFolderId(privateFolderId);
				invites= "" ;//allCalendar(mailData);
				}
	}
			catch (Exception e) {
				 logger.error("Exception Occured while getting all mails from specific folder : "+e.getMessage());
				  if(null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession())
				  response = "{\"error\":\"User Not Logged In\"}";
			 }
			return invites;
		}
		

		public String createAppointment(AppointmentRequest appointmentRequest) throws Exception{
			String response = "";
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			String session = userDataBean.getUserCalendarDataMap().get(appointmentRequest.getAuth()).getSession();
			String openExchangeCalendarUrl=configManager.getPropertyAsString("OX_CALENDAR_URL"); 
			String calendarSearchUrl = openExchangeCalendarUrl  + "?action=new&session="+session+"&timezone=UTC";
			try{
				
				URL url = new URL(calendarSearchUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Cookie", userDataBean.getUserCalendarDataMap().get(appointmentRequest.getAuth()).getCookie());
				String jsonRequest = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
				    		appointmentRequest);
							  
				OutputStream os = conn.getOutputStream();
				os.write(jsonRequest.getBytes());
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

				conn.disconnect();
				
			}
			catch(Exception e)
			{
				logger.error("Exception Occured while creating Appointment  : "+e.getMessage());
				  if(null == session)
				  response = "{\"error\":\"User Not Logged In\"}";
			}
			return response;
		}

		//populating the map for userids 
		HashMap<String , String > userDetailsMapping(CalendarData mailData){
			
			
			String response = "";
			String input = null;
			HashMap<String, String> userDetails= new HashMap<>();
			String session = userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getSession();
			String columns = configManager.getPropertyAsString("USER_COLUMNS");
			String openExchangeMailUrl = configManager.getPropertyAsString("OX_USER_URL")+"?action="+"all"+"&session="+session+"&columns="+columns;
					
			try {
		
				URL url = new URL(openExchangeMailUrl);	
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				conn.setRequestProperty("Cookie", userDataBean.getUserCalendarDataMap().get(mailData.getAuth()).getCookie());
					

				OutputStream os = conn.getOutputStream();
				//os.write(input.getBytes());
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

				conn.disconnect();
				
				JSONObject dataObject= new JSONObject(response);
				
				JSONArray inviteObject= dataObject.getJSONArray("data");
				for(int i=0; i< inviteObject.length();i++)
				{
					JSONArray object = inviteObject.getJSONArray(i);
					String userId = object.get(0).toString();
					String userEmail= object.get(7).toString();
					userDetails.put(userId, userEmail);
				}
				   
			}
			catch(Exception e)
			{
				logger.error("Exception Occured while populating user details  : "+e.getMessage());
				 
			}
			
			return userDetails;
		}
}
