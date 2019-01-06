package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentRequest extends BaseRequest{

	@JsonProperty("start_date")
	private Long startDate;
	@JsonProperty("end_date")
	private Long endDate;
	@JsonProperty("folder_id")
	private String folderId;
	private Participant [] participants ;
	private String alarm;
	@JsonProperty("recurrence_type")
	private String recurrenceType;
	@JsonProperty("notification")
	private Boolean notification;
	@JsonProperty("shown_as")
	private Integer shownAs;
	private String timezone;
	private Integer organizerId;
	private String title;
	private String location;
	private String note;
	private Integer recurrenceId;
	private Integer recurrencePosition;
	private Integer recurrenceDatePosition;
	private Integer[] changeExceptions;
	private Integer[] deleteExceptions;
	private Integer recurrenceStart;
	private Boolean ignoreConflicts;
	private Integer days;
	private Integer daysInMonth;
	private Integer month;
	private Integer interval;
	private Integer until;
	private Integer occurences;
	private String  uid;
	private String  organizer;
	
	public Long getStartDate() {
		return startDate;
	}
	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}
	public Long getEndDate() {
		return endDate;
	}
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public Participant[] getParticipants() {
		return participants;
	}
	public void setParticipants(Participant[] participants) {
		this.participants = participants;
	}
	public String getAlarm() {
		return alarm;
	}
	public void setAlarm(String alarm) {
		this.alarm = alarm;
	}
	public String getRecurrenceType() {
		return recurrenceType;
	}
	public void setRecurrenceType(String recurrenceType) {
		this.recurrenceType = recurrenceType;
	}
	public Boolean getNotifiation() {
		return notification;
	}
	public void setNotifiation(Boolean notifiation) {
		this.notification = notifiation;
	}
	public Integer getShownAs() {
		return shownAs;
	}
	public void setShownAs(Integer shownAs) {
		this.shownAs = shownAs;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public Integer getOrganizerId() {
		return organizerId;
	}
	public void setOrganizerId(Integer organizerId) {
		this.organizerId = organizerId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public Integer getRecurrenceId() {
		return recurrenceId;
	}
	public void setRecurrenceId(Integer recurrenceId) {
		this.recurrenceId = recurrenceId;
	}
	public Integer getRecurrencePosition() {
		return recurrencePosition;
	}
	public void setRecurrencePosition(Integer recurrencePosition) {
		this.recurrencePosition = recurrencePosition;
	}
	public Integer getRecurrenceDatePosition() {
		return recurrenceDatePosition;
	}
	public void setRecurrenceDatePosition(Integer recurrenceDatePosition) {
		this.recurrenceDatePosition = recurrenceDatePosition;
	}
	public Integer[] getChangeExceptions() {
		return changeExceptions;
	}
	public void setChangeExceptions(Integer[] changeExceptions) {
		this.changeExceptions = changeExceptions;
	}
	public Integer[] getDeleteExceptions() {
		return deleteExceptions;
	}
	public void setDeleteExceptions(Integer[] deleteExceptions) {
		this.deleteExceptions = deleteExceptions;
	}
	public Integer getRecurrenceStart() {
		return recurrenceStart;
	}
	public void setRecurrenceStart(Integer recurrenceStart) {
		this.recurrenceStart = recurrenceStart;
	}
	public Boolean getIgnoreConflicts() {
		return ignoreConflicts;
	}
	public void setIgnoreConflicts(Boolean ignoreConflicts) {
		this.ignoreConflicts = ignoreConflicts;
	}
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	public Integer getDaysInMonth() {
		return daysInMonth;
	}
	public void setDaysInMonth(Integer daysInMonth) {
		this.daysInMonth = daysInMonth;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getInterval() {
		return interval;
	}
	public void setInterval(Integer interval) {
		this.interval = interval;
	}
	public Integer getUntil() {
		return until;
	}
	public void setUntil(Integer until) {
		this.until = until;
	}
	public Integer getOccurences() {
		return occurences;
	}
	public void setOccurences(Integer occurences) {
		this.occurences = occurences;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getOrganizer() {
		return organizer;
	}
	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}
}
