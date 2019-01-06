package com.broadsoft.demohub.api.beans;

public class SearchAppointment extends MailData{
private String searchString;  //startletter
private String content;   //pattern
private String start_date_limit;
private String end_date_limit;

public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getSearchString() {
	return searchString;
}
public void setSearchString(String searchString) {
	this.searchString = searchString;
}
public String getStart_date_limit() {
	return start_date_limit;
}
public void setStart_date_limit(String start_date_limit) {
	this.start_date_limit = start_date_limit;
}
public String getEnd_date_limit() {
	return end_date_limit;
}
public void setEnd_date_limit(String end_date_limit) {
	this.end_date_limit = end_date_limit;
}

}
