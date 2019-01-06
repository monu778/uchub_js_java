package com.broadsoft.demohub.api.beans;

import java.util.ArrayList;

public class MailDelete extends MailData{

	ArrayList<MailData> deleteList= new ArrayList<MailData>();

	public ArrayList<MailData> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(ArrayList<MailData> deleteList) {
		this.deleteList = deleteList;
	}
	
}
