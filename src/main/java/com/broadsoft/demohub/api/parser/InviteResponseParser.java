package com.broadsoft.demohub.api.parser;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.broadsoft.demohub.api.beans.CalendarResponse;
import com.broadsoft.demohub.api.beans.Confirmation;
import com.broadsoft.demohub.api.beans.InviteResponse;
import com.broadsoft.demohub.api.beans.Participant;
import com.broadsoft.demohub.api.beans.UserInvite;

public class InviteResponseParser {

	private Logger logger = Logger.getLogger(InviteResponseParser.class);
	
	public InviteResponse parseInvite(String response){
		InviteResponse inviteResponse= new InviteResponse();
		ArrayList<CalendarResponse> calendarResponses= new ArrayList<>();
		CalendarResponse calendarResponse = new CalendarResponse();
		JSONObject dataObject= new JSONObject(response);
		
		try{
			if(dataObject.has("timestamp")){
				inviteResponse.setTimeStamp(dataObject.getLong("timestamp"));
			}
			
			JSONObject inviteObject= dataObject.getJSONObject("data");
			if(inviteObject.has("last_modified_utc")){
				calendarResponse.setLast_modified_utc(inviteObject.getLong("last_modified_utc"));
			}
			if(inviteObject.has("last_modified")){
				calendarResponse.setLast_modified(inviteObject.getLong("last_modified"));
			}
			if(inviteObject.has("creation_date")){
				calendarResponse.setCreation_date(inviteObject.getLong("creation_date"));
			}
			if(inviteObject.has("modified_by")){
				calendarResponse.setModified_by(inviteObject.getInt("modified_by"));
			}
			if(inviteObject.has("created_by")){
				calendarResponse.setCreated_by(inviteObject.getInt("created_by"));
			}
			if(inviteObject.has("id")){
				calendarResponse.setId(inviteObject.getInt("id"));
			}
			if(inviteObject.has("folder_id")){
				calendarResponse.setFolder_id(inviteObject.getInt("folder_id"));
			}
			if(inviteObject.has("number_of_attachments")){
				calendarResponse.setNumber_of_attachments(inviteObject.getInt("number_of_attachments"));
			}
			if(inviteObject.has("color_label")){
				calendarResponse.setColor_label(inviteObject.getInt("color_label"));
			}
			if(inviteObject.has("private_flag")){
				calendarResponse.setPrivate_flag(inviteObject.getBoolean("private_flag"));
			}
			if(inviteObject.has("full_time")){
				calendarResponse.setFull_time(inviteObject.getBoolean("full_time"));
			}
			if(inviteObject.has("principalId")){
				calendarResponse.setPrincipalId(inviteObject.getInt("principalId"));
			}
			if(inviteObject.has("organizerId")){
				calendarResponse.setOrganizerId(inviteObject.getInt("organizerId"));
			}
			if(inviteObject.has("confirmations")){
				Confirmation[] confirmations= null;
				Confirmation confirmation=null;
				JSONArray confirmationsArray = inviteObject.getJSONArray("confirmations");
				confirmations= new Confirmation[confirmationsArray.length()];
				for(int i=0;i<confirmationsArray.length();i++)
				{
					JSONObject confirmationJsonObject = (JSONObject) confirmationsArray.get(i);
					confirmation= new Confirmation();
					if(confirmationJsonObject.has("type")){
						confirmation.setType(confirmationJsonObject.getInt("type"));
					}
					if(confirmationJsonObject.has("mail")){
						confirmation.setMail(confirmationJsonObject.getString("mail"));
					}
					if(confirmationJsonObject.has("status")){
						confirmation.setStatus(confirmationJsonObject.getInt("status"));
					}
					confirmations[i]=confirmation;
				}
				calendarResponse.setConfirmations(confirmations);
			}
			if(inviteObject.has("sequence")){
				calendarResponse.setSequence(inviteObject.getInt("sequence"));
			}
			if(inviteObject.has("organizer")){
				calendarResponse.setOrganizer(inviteObject.getString("organizer"));
			}
			if(inviteObject.has("uid")){
				calendarResponse.setUid(inviteObject.getString("uid"));
			}
			if(inviteObject.has("title")){
				String title = inviteObject.getString("title").replaceAll("[^\\p{ASCII}]", "");
				calendarResponse.setTitle(title);
			}
			if(inviteObject.has("start_date")){
				calendarResponse.setStart_date(inviteObject.getLong("start_date"));
			}
			if(inviteObject.has("end_date")){
				calendarResponse.setEnd_date(inviteObject.getLong("end_date"));
			}
			if(inviteObject.has("shown_as")){
				calendarResponse.setShown_as(inviteObject.getInt("shown_as"));
			}
			if(inviteObject.has("note")){
				String note = inviteObject.getString("note").replaceAll("[^\\p{ASCII}]", "");
				calendarResponse.setNote(note);
			}
			if(inviteObject.has("recurrence_type")){
				calendarResponse.setRecurrence_type(inviteObject.getInt("recurrence_type"));
			}
			if(inviteObject.has("participants")){
				
				Participant[] participants= null;
				Participant participant=null;
				JSONArray participantArray = inviteObject.getJSONArray("participants");
				participants= new Participant[participantArray.length()];
				for(int i=0;i<participantArray.length();i++)
				{
					JSONObject participantJsonObject = (JSONObject) participantArray.get(i);
					participant= new Participant();
					if(participantJsonObject.has("type")){
						participant.setType(participantJsonObject.getInt("type"));
					}
					if(participantJsonObject.has("mail")){
						participant.setMail(participantJsonObject.getString("mail"));
					}
					if(participantJsonObject.has("id")){
						participant.setId(participantJsonObject.getLong("id"));
					}
					participants[i]=participant;
				}
				calendarResponse.setParticipants(participants);	
			}
			
			if(inviteObject.has("users")){
				
				UserInvite[] userInvites= null;
				UserInvite userInvite=null;
				JSONArray userInviteArray = inviteObject.getJSONArray("users");
				userInvites= new UserInvite[userInviteArray.length()];
				for(int i=0;i<userInviteArray.length();i++)
				{
					JSONObject userInviteJsonObject = (JSONObject) userInviteArray.get(i);
					userInvite= new UserInvite();
					if(userInviteJsonObject.has("id")){
						userInvite.setId(userInviteJsonObject.getInt("id"));
					}
					if(userInviteJsonObject.has("confirmation")){
						userInvite.setConfirmation(userInviteJsonObject.getInt("confirmation"));
					}
					if(userInviteJsonObject.has("confirmmessage")){
						userInvite.setConfirmationMessage(userInviteJsonObject.getString("confirmmessage"));
					}
					userInvites[i]=userInvite;
				}
				calendarResponse.setUsers(userInvites);	
			}
			if(inviteObject.has("timezone")){
				calendarResponse.setTimeZone(inviteObject.getString("timezone"));
			}
			
		}
		catch (Exception e) {
			logger.error("Error Occured while parsing invite : "+e.getMessage());
		}
		calendarResponses.add(calendarResponse);
		inviteResponse.setCalendarResponse(calendarResponses);
		return inviteResponse;
	}

	public ArrayList<CalendarResponse> parseAllCalendarResponse(String response) {
		ArrayList<CalendarResponse> allCalendarResponse = new ArrayList<CalendarResponse>();
		CalendarResponse calendarResponse = null;
		JSONObject dataObject = new JSONObject(response);
		JSONArray calendarArray = dataObject.getJSONArray("data");
		for(int inviteIndex = 0 ; inviteIndex < calendarArray.length() ; inviteIndex++){
			JSONArray calendar = calendarArray.getJSONArray(inviteIndex);
			calendarResponse = new CalendarResponse();
			//int checkFlag=0;
			
			JSONArray participantsArray = calendar.getJSONArray(21);
			Participant [] participants = new Participant[participantsArray.length()];
			for (int participantNo = 0 ; participantNo < participantsArray.length() ; participantNo++){
				JSONObject participantObject = participantsArray.getJSONObject(participantNo);
				Participant participant = new Participant();
				if(participantObject.has("id"))
				participant.setId(participantObject.getInt("id"));
				if(participantObject.has("type"))
				participant.setType(participantObject.getInt("type"));
				if(participantObject.has("mail")) {
				/*checkFlag++;*/	
				participant.setMail(participantObject.getString("mail"));
				}
				if(participantObject.has("display_name"))
				participant.setDisplayName(participantObject.getString("display_name"));
				if(participantObject.has("field"))
				participant.setField(participantObject.getString("display_name"));
				participants[participantNo] = participant;
			}
			
			calendarResponse.setParticipants(participants);
			/*else
				continue;*/

			calendarResponse.setId((Integer) calendar.get(0));
			calendarResponse.setCreated_by((Integer) calendar.get(1));
			calendarResponse.setModified_by((Integer) calendar.get(2));
			calendarResponse.setCreation_date((long) calendar.get(3));
			calendarResponse.setLast_modified((long) calendar.get(4));
			calendarResponse.setFolder_id((Integer) calendar.get(5));
			String categories = calendar.get(6).toString();
			if(!categories.equalsIgnoreCase("null")){
				calendarResponse.setCategories(categories);
			}
			String privateFlag = calendar.get(7).toString();
			if(!privateFlag.equalsIgnoreCase("null")){
				calendarResponse.setPrivate_flag(calendar.getBoolean(7));
			}
			String colorLabel = calendar.get(8).toString();
			if(!colorLabel.equalsIgnoreCase("null")){
				calendarResponse.setColor_label(calendar.getInt(8));
			}
			
			String title = calendar.get(9).toString();
			if(!title.equalsIgnoreCase("null")){
				title = title.replaceAll("[^\\p{ASCII}]", "");
				calendarResponse.setTitle(title);
			}
			
			String startDate = calendar.get(10).toString();
			if(!startDate.equalsIgnoreCase("null")){
				calendarResponse.setStart_date(calendar.getLong(10));
			}
			
			String endDate = calendar.get(11).toString();
			if(!endDate.equalsIgnoreCase("null")){
				calendarResponse.setEnd_date(calendar.getLong(11));
			}
			
			String note = calendar.get(12).toString();
			if(!note.equalsIgnoreCase("null")){
				if(note.contains("-::") && note.contains(":~::-\nPlease do not edit this section of")){
					note = note.substring(0, note.indexOf("-::"));
				}
				note = note.replaceAll("\n", "").replaceAll("\\*", "").replaceAll("~", "");
				calendarResponse.setNote(note.replaceAll("[^\\p{ASCII}]", ""));
			}
			
			String alarm = calendar.get(13).toString();
			if(!alarm.equalsIgnoreCase("null")){
				calendarResponse.setAlarm(calendar.getInt(13));
			}
			
			String recurrenceType = calendar.get(14).toString();
			if(!recurrenceType.equalsIgnoreCase("null")){
				calendarResponse.setRecurrence_type(calendar.getInt(14));
			}
			
			String days = calendar.get(15).toString();
			if(!days.equalsIgnoreCase("null")){
				calendarResponse.setDays(calendar.getInt(15));
			}
			
			String dayInMonth = calendar.get(16).toString();
			if(!dayInMonth.equalsIgnoreCase("null")){
				calendarResponse.setDay_in_month((calendar.getInt(16)));
			}
			
			String month = calendar.get(17).toString();
			if(!month.equalsIgnoreCase("null")){
				calendarResponse.setMonth(calendar.getInt(17));
			}
			
			String interval = calendar.get(18).toString();
			if(!interval.equalsIgnoreCase("null")){
				calendarResponse.setInterval(calendar.getInt(18));
			}
			
			String until = calendar.get(19).toString();
			if(!until.equalsIgnoreCase("null")){
				calendarResponse.setUntil(calendar.getLong(19));
			}
			
			String notification = calendar.get(20).toString();
			if(!notification.equalsIgnoreCase("null")){
				calendarResponse.setNotification(calendar.getBoolean(20));
			}
			
			/*JSONArray participantsArray = calendar.getJSONArray(21);
			Participant [] participants = new Participant[participantsArray.length()];
			for (int participantNo = 0 ; participantNo < participantsArray.length() ; participantNo++){
				JSONObject participantObject = participantsArray.getJSONObject(participantNo);
				Participant participant = new Participant();
				if(participantObject.has("id"))
				participant.setId(participantObject.getInt("id"));
				if(participantObject.has("type"))
				participant.setType(participantObject.getInt("type"));
				if(participantObject.has("mail"))
				participant.setMail(participantObject.getString("mail"));
				if(participantObject.has("display_name"))
				participant.setDisplayName(participantObject.getString("display_name"));
				if(participantObject.has("field"))
				participant.setField(participantObject.getString("display_name"));
				participants[participantNo] = participant;
			}
			calendarResponse.setParticipants(participants);*/
			
			JSONArray userInviteArray = calendar.getJSONArray(22);
			UserInvite [] usersInvite = new UserInvite[userInviteArray.length()];
			for (int userNo = 0 ; userNo < userInviteArray.length() ; userNo++){
				JSONObject userInvite = userInviteArray.getJSONObject(userNo);
				UserInvite userInviteObject = new UserInvite();
				if(userInvite.has("id"))
				userInviteObject.setId(userInvite.getInt("id"));
				if(userInvite.has("confirmation"))
				userInviteObject.setConfirmation(userInvite.getInt("confirmation"));
				if(userInvite.has("confirmmessage"))
				userInviteObject.setConfirmationMessage(userInvite.getString("confirmmessage"));
			    usersInvite[userNo] = userInviteObject;
			}
			calendarResponse.setUsers(usersInvite);
			
			allCalendarResponse.add(calendarResponse);
		}
		
		return allCalendarResponse;
	}
	
	public ArrayList<CalendarResponse> parseSearchCalendarResponse(String response) {
		ArrayList<CalendarResponse> allCalendarResponse = new ArrayList<CalendarResponse>();
		CalendarResponse calendarResponse = null;
		JSONObject dataObject = new JSONObject(response);
		JSONArray calendarArray = dataObject.getJSONArray("data");
		for(int inviteIndex = 0 ; inviteIndex < calendarArray.length() ; inviteIndex++){
			JSONObject calendar = calendarArray.getJSONObject(inviteIndex);
			calendarResponse = new CalendarResponse();
			int checkFlag=0;
			Participant [] participants=null;
			if(calendar.has("participants")) {
			JSONArray participantsArray = calendar.getJSONArray("participants");
			 participants = new Participant[participantsArray.length()];
			for (int participantNo = 0 ; participantNo < participantsArray.length() ; participantNo++){
				JSONObject participantObject = participantsArray.getJSONObject(participantNo);
				Participant participant = new Participant();
				if(participantObject.has("id"))
				participant.setId(participantObject.getInt("id"));
				if(participantObject.has("type"))
				participant.setType(participantObject.getInt("type"));
				if(participantObject.has("mail")) {
				checkFlag++;	
				participant.setMail(participantObject.getString("mail"));
				}
				if(participantObject.has("display_name"))
				participant.setDisplayName(participantObject.getString("display_name"));
				if(participantObject.has("field"))
				participant.setField(participantObject.getString("display_name"));
				participants[participantNo] = participant;
			}
		}
			if(checkFlag>0)
			calendarResponse.setParticipants(participants);
			else
				continue;

			if(calendar.has("id")) {
			calendarResponse.setId(calendar.getInt("id"));
			}
			if(calendar.has("created_by")) {
			calendarResponse.setCreated_by(calendar.getInt("created_by"));
			}
			if(calendar.has("modified_by")) {
			calendarResponse.setModified_by(calendar.getInt("modified_by"));
			}
			if(calendar.has("creation_date")) {
			calendarResponse.setCreation_date(calendar.getLong("creation_date"));
			}
			if(calendar.has("last_modified")) {
			calendarResponse.setLast_modified(calendar.getLong("last_modified"));
			}
			if(calendar.has("folder_id")) {
			calendarResponse.setFolder_id(calendar.getInt("folder_id"));
			}
			if(calendar.has("categories")) {
			String categories = calendar.getString("categories");
			calendarResponse.setCategories(categories);
			}
			if(calendar.has("private_flag")) {
			boolean privateFlag = calendar.getBoolean("private_flag");
			calendarResponse.setPrivate_flag(privateFlag);
			}
			if(calendar.has("color_label")) {
			int colorLabel = calendar.getInt("color_label");
			calendarResponse.setColor_label(colorLabel);
			}
			if(calendar.has("title")) {
				String title = calendar.getString("title").replaceAll("[^\\p{ASCII}]", "");	
				calendarResponse.setTitle(title);
			}
			
			if(calendar.has("start_date")) {
			Long startDate = calendar.getLong("start_date");
			calendarResponse.setStart_date(startDate);
			}
			
			if(calendar.has("end_date")) {
				Long end_date = calendar.getLong("end_date");
				calendarResponse.setEnd_date(end_date);
				}
			String note=null;
			if(calendar.has("note")) {
			 note = calendar.getString("note");
			}
			if(note != null && !note.equalsIgnoreCase("null")){
				if(note.contains("-::") && note.contains(":~::-\nPlease do not edit this section of")){
					note = note.substring(0, note.indexOf("-::"));
				}
				note = note.replaceAll("\n", "").replaceAll("\\*", "").replaceAll("~", "");
				calendarResponse.setNote(note.replaceAll("[^\\p{ASCII}]", ""));
			}
			
			if(calendar.has("alarm")) {
			int alarm = calendar.getInt("alarm");
			calendarResponse.setAlarm(alarm);
			}
			
			if(calendar.has("recurrence_type")) {
			int recurrence_type = calendar.getInt("recurrence_type");
				calendarResponse.setRecurrence_type(recurrence_type);
			}
			
			if(calendar.has("days")) {
			int days = calendar.getInt("days");
			calendarResponse.setDays(days);
			}
			
			if(calendar.has("day_in_month")) {
			int dayInMonth = calendar.getInt("day_in_month");
			calendarResponse.setDay_in_month(dayInMonth);
			}
			
			if(calendar.has("month")) {
			int month = calendar.getInt("month");
			calendarResponse.setMonth(month);
			}
			
			if(calendar.has("interval")) {
			int interval = calendar.getInt("interval");
			calendarResponse.setInterval(interval);
			}
			
			if(calendar.has("until")) {
			long until = calendar.getLong("until");
			calendarResponse.setUntil(until);
			}
			
			if(calendar.has("notification")) {
			boolean notification = calendar.getBoolean("notification");
				calendarResponse.setNotification(notification);
			}
			
			/*JSONArray participantsArray = calendar.getJSONArray(21);
			Participant [] participants = new Participant[participantsArray.length()];
			for (int participantNo = 0 ; participantNo < participantsArray.length() ; participantNo++){
				JSONObject participantObject = participantsArray.getJSONObject(participantNo);
				Participant participant = new Participant();
				if(participantObject.has("id"))
				participant.setId(participantObject.getInt("id"));
				if(participantObject.has("type"))
				participant.setType(participantObject.getInt("type"));
				if(participantObject.has("mail"))
				participant.setMail(participantObject.getString("mail"));
				if(participantObject.has("display_name"))
				participant.setDisplayName(participantObject.getString("display_name"));
				if(participantObject.has("field"))
				participant.setField(participantObject.getString("display_name"));
				participants[participantNo] = participant;
			}
			calendarResponse.setParticipants(participants);*/
			if(calendar.has("confirmations")) {
			JSONArray userInviteArray = calendar.getJSONArray("confirmations");
			UserInvite [] usersInvite = new UserInvite[userInviteArray.length()];
			for (int userNo = 0 ; userNo < userInviteArray.length() ; userNo++){
				JSONObject userInvite = userInviteArray.getJSONObject(userNo);
				UserInvite userInviteObject = new UserInvite();
				if(userInvite.has("id"))
				userInviteObject.setId(userInvite.getInt("id"));
				if(userInvite.has("confirmation"))
				userInviteObject.setConfirmation(userInvite.getInt("confirmation"));
				if(userInvite.has("confirmmessage"))
				userInviteObject.setConfirmationMessage(userInvite.getString("confirmmessage"));
			    usersInvite[userNo] = userInviteObject;
			}
			calendarResponse.setUsers(usersInvite);
		}
			
			allCalendarResponse.add(calendarResponse);
		}
		
		return allCalendarResponse;
	}
}
