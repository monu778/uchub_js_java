package com.broadsoft.demohub.api.beans;

public class UserParticipant extends MailData {
private String displayName;
private String confirmation;
private String confirmationMessage;
private String objectId;
private String folderId;

public String getDisplayName() {
	return displayName;
}
public void setDisplayName(String displayName) {
	this.displayName = displayName;
}
public String getConfirmation() {
	return confirmation;
}
public void setConfirmation(String confirmation) {
	this.confirmation = confirmation;
}
public String getConfirmationMessage() {
	return confirmationMessage;
}
public void setConfirmationMessage(String confirmationMessage) {
	this.confirmationMessage = confirmationMessage;
}
public String getObjectId() {
	return objectId;
}
public void setObjectId(String objectId) {
	this.objectId = objectId;
}
public String getFolderId() {
	return folderId;
}
public void setFolderId(String folderId) {
	this.folderId = folderId;
}

}
