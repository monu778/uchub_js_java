package com.broadsoft.demohub.api.beans;

public class UpdateMail extends MailData{
private String destinationFolder;
private String color_label;
private String flags;
private String value; 
private String set_flags;
private String clear_flags;
private boolean flagged;

public String getColor_label() {
	return color_label;
}
public void setColor_label(String color_label) {
	this.color_label = color_label;
}
public String getFlags() {
	return flags;
}
public void setFlags(String flags) {
	this.flags = flags;
}

public String getSet_flags() {
	return set_flags;
}
public void setSet_flags(String set_flags) {
	this.set_flags = set_flags;
}
public String getClear_flags() {
	return clear_flags;
}
public void setClear_flags(String clear_flags) {
	this.clear_flags = clear_flags;
}
public String getDestinationFolder() {
	return destinationFolder;
}
public void setDestinationFolder(String destinationFolder) {
	this.destinationFolder = destinationFolder;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
public boolean isFlagged() {
	return flagged;
}
public void setFlagged(boolean flagged) {
	this.flagged = flagged;
}


}
