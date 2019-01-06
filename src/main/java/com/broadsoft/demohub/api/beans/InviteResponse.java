package com.broadsoft.demohub.api.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class InviteResponse extends BaseResponse{

	private ArrayList<CalendarResponse> calendarResponse = new ArrayList<CalendarResponse>();
	@JsonProperty("timestamp")
	private long timeStamp;
	
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public ArrayList<CalendarResponse> getCalendarResponse() {
		return calendarResponse;
	}
	public void setCalendarResponse(ArrayList<CalendarResponse> calendarResponse) {
		this.calendarResponse = calendarResponse;
	}
	
}
