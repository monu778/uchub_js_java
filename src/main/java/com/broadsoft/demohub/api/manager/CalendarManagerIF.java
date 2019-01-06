package com.broadsoft.demohub.api.manager;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.AppointmentRequest;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.CalendarData;
import com.broadsoft.demohub.api.beans.CalendarResponse;
import com.broadsoft.demohub.api.beans.InviteResponse;
import com.broadsoft.demohub.api.beans.SearchAppointment;
import com.broadsoft.demohub.api.beans.UserParticipant;

@Component
public interface CalendarManagerIF {
	public ArrayList<CalendarResponse> allCalendar(CalendarData mailData) throws Exception;
	public InviteResponse getCalendarInvite(CalendarData mailData) throws Exception;

	public BaseResponse updateInvite(UserParticipant mailData) throws Exception;
	public InviteResponse searchCalendar(SearchAppointment mailData) throws Exception;
	
	public ArrayList<CalendarResponse> searchCalendarContextual(CalendarData mailData) throws Exception;
	public String contextualUserInvite(CalendarData mailData) throws Exception;

	public String createAppointment(AppointmentRequest appointmentRequest) throws Exception;

}
