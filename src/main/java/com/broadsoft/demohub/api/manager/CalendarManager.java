package com.broadsoft.demohub.api.manager;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.adapter.CalendarAdapter;
import com.broadsoft.demohub.api.beans.AppointmentRequest;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.CalendarData;
import com.broadsoft.demohub.api.beans.CalendarResponse;
import com.broadsoft.demohub.api.beans.InviteResponse;
import com.broadsoft.demohub.api.beans.SearchAppointment;
import com.broadsoft.demohub.api.beans.UserParticipant;


@Component
public class CalendarManager implements CalendarManagerIF {

	@Autowired
	CalendarAdapter calendarAdapter;
	
	
	@Override
	public ArrayList<CalendarResponse> allCalendar(CalendarData calendarData) throws Exception{
		return calendarAdapter.allCalendar(calendarData);
		
	}

	@Override
	public InviteResponse getCalendarInvite(CalendarData mailData) throws Exception {
		return calendarAdapter.getCalendarInvite(mailData);
	}
	
	@Override
	public BaseResponse updateInvite(UserParticipant mailData) throws Exception{
		return calendarAdapter.updateInvite(mailData);
		
	}
	
	@Override
	public InviteResponse searchCalendar(SearchAppointment mailData) throws Exception{
		return calendarAdapter.searchCalendar(mailData);
	}

	@Override
	public ArrayList<CalendarResponse> searchCalendarContextual(CalendarData mailData) throws Exception {
		return calendarAdapter.searchCalendarContextual(mailData);
	}


	@Override
	public String contextualUserInvite(CalendarData mailData) throws Exception {
		return calendarAdapter.contextualUserInvite(mailData);
	}
	
	@Override
	public String createAppointment(AppointmentRequest appointmentRequest) throws Exception {
		return calendarAdapter.createAppointment(appointmentRequest);

	}

}
