package com.broadsoft.demohub.api.beans;

import org.codehaus.jackson.annotate.JsonProperty;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarResponse extends BaseResponse implements Comparable<CalendarResponse>{
	
	@JsonProperty("last_modified_utc")
	private Long last_modified_utc;
	@JsonProperty("last_modified")
	private Long last_modified;
	@JsonProperty("creation_date")
	private Long creation_date;
	@JsonProperty("modified_by")
	private Integer modified_by;
	@JsonProperty("created_by")
	private Integer created_by;
	@JsonProperty("folder_id")
	private Integer folder_id;
	@JsonProperty("number_of_attachments")
	private Integer number_of_attachments;
	@JsonProperty("color_label")
	private Integer color_label;
	@JsonProperty("categories")
	private String categories;
	@JsonProperty("private_flag")
	private Boolean private_flag;
	@JsonProperty("full_time")
	private Boolean full_time;
	@JsonProperty("principalId")
	private Integer principalId;
	@JsonProperty("organizerId")
	private Integer organizerId;
	@JsonProperty("confirmations")
	private Confirmation[] confirmations=null; 
	@JsonProperty("sequence")
	private Integer sequence;
	@JsonProperty("organizer")
	private String organizer;
	@JsonProperty("uid")
	private String uid;
	@JsonProperty("title")
	private String title;
	@JsonProperty("start_date")
	private Long start_date;
	@JsonProperty("end_date")
	private Long end_date;
	@JsonProperty("shown_as")
	private Integer shown_as;
	@JsonProperty("note")
	private String note;
	@JsonProperty("recurrence_type")
	private Integer recurrence_type;
	@JsonProperty("participants")
	private Participant[] participants;
	@JsonProperty("users")
	private UserInvite[] users;
	@JsonProperty("timezone")
	private String timeZone;
	@JsonProperty("alarm")
	private Integer alarm;
	@JsonProperty("days")
	private Integer days;
	@JsonProperty("day_in_month")
	private Integer day_in_month;
	@JsonProperty("month")
	private Integer month;
	@JsonProperty("interval")
	private Integer interval;
	@JsonProperty("until")
	private Long until;
	@JsonProperty("notification")
	private Boolean notification;
	@JsonProperty("id")
	private Integer id;
	public Long getLast_modified_utc() {
		return last_modified_utc;
	}
	public void setLast_modified_utc(Long last_modified_utc) {
		this.last_modified_utc = last_modified_utc;
	}
	public Long getLast_modified() {
		return last_modified;
	}
	public void setLast_modified(Long last_modified) {
		this.last_modified = last_modified;
	}
	public Long getCreation_date() {
		return creation_date;
	}
	public void setCreation_date(Long creation_date) {
		this.creation_date = creation_date;
	}
	public Integer getModified_by() {
		return modified_by;
	}
	public void setModified_by(Integer modified_by) {
		this.modified_by = modified_by;
	}
	public Integer getCreated_by() {
		return created_by;
	}
	public void setCreated_by(Integer created_by) {
		this.created_by = created_by;
	}
	
	public Integer getFolder_id() {
		return folder_id;
	}
	public void setFolder_id(Integer folder_id) {
		this.folder_id = folder_id;
	}
	public Integer getNumber_of_attachments() {
		return number_of_attachments;
	}
	public void setNumber_of_attachments(Integer number_of_attachments) {
		this.number_of_attachments = number_of_attachments;
	}
	public Integer getColor_label() {
		return color_label;
	}
	public void setColor_label(Integer color_label) {
		this.color_label = color_label;
	}
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
	}
	public Boolean getPrivate_flag() {
		return private_flag;
	}
	public void setPrivate_flag(Boolean private_flag) {
		this.private_flag = private_flag;
	}
	public Boolean getFull_time() {
		return full_time;
	}
	public void setFull_time(Boolean full_time) {
		this.full_time = full_time;
	}
	public Integer getPrincipalId() {
		return principalId;
	}
	public void setPrincipalId(Integer principalId) {
		this.principalId = principalId;
	}
	public Integer getOrganizerId() {
		return organizerId;
	}
	public void setOrganizerId(Integer organizerId) {
		this.organizerId = organizerId;
	}
	public Confirmation[] getConfirmations() {
		return confirmations;
	}
	public void setConfirmations(Confirmation[] confirmations) {
		this.confirmations = confirmations;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public String getOrganizer() {
		return organizer;
	}
	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getStart_date() {
		return start_date;
	}
	public void setStart_date(Long start_date) {
		this.start_date = start_date;
	}
	public Long getEnd_date() {
		return end_date;
	}
	public void setEnd_date(Long end_date) {
		this.end_date = end_date;
	}
	public Integer getShown_as() {
		return shown_as;
	}
	public void setShown_as(Integer shown_as) {
		this.shown_as = shown_as;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Integer getRecurrence_type() {
		return recurrence_type;
	}
	public void setRecurrence_type(Integer recurrence_type) {
		this.recurrence_type = recurrence_type;
	}
	public Participant[] getParticipants() {
		return participants;
	}
	public void setParticipants(Participant[] participants) {
		this.participants = participants;
	}
	public UserInvite[] getUsers() {
		return users;
	}
	public void setUsers(UserInvite[] users) {
		this.users = users;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public Integer getAlarm() {
		return alarm;
	}
	public void setAlarm(Integer alarm) {
		this.alarm = alarm;
	}
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	public Integer getDay_in_month() {
		return day_in_month;
	}
	public void setDay_in_month(Integer day_in_month) {
		this.day_in_month = day_in_month;
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
	public Long getUntil() {
		return until;
	}
	public void setUntil(Long until) {
		this.until = until;
	}
	public Boolean getNotification() {
		return notification;
	}
	public void setNotification(Boolean notification) {
		this.notification = notification;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	public int compareTo(CalendarResponse calendarResponse) {
		if(this.start_date < calendarResponse.start_date)
			return -1;
		if(this.start_date > calendarResponse.start_date)
			return 1;
		return 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end_date == null) ? 0 : end_date.hashCode());
		result = prime * result + ((start_date == null) ? 0 : start_date.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalendarResponse other = (CalendarResponse) obj;
		if (end_date == null) {
			if (other.end_date != null)
				return false;
		} else if (!end_date.equals(other.end_date))
			return false;
		if (start_date == null) {
			if (other.start_date != null)
				return false;
		} else if (!start_date.equals(other.start_date))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	
}
