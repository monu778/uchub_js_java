package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllMailResponse extends BaseResponse implements Comparable<AllMailResponse>{
		
	@JsonProperty("id")
	private String id;
	@JsonProperty("folder_id")
	private String folderId;
	@JsonProperty("attachment")
	private Boolean isAttachment ;
	@JsonProperty("from")
	private String[][] from = null;
	@JsonProperty("to")
	private String[][] to = null;
	@JsonProperty("cc")
	private String[][] cc = null;
	@JsonProperty("bcc")
	private String[][] bcc = null;
	@JsonProperty("subject")
	private String subject = null;
	@JsonProperty("size")
	private Integer size;
	@JsonProperty("received_date")
	private Long receivedDate;
	@JsonProperty("sent_date")
	private Long sentDate;
	@JsonProperty("flags")
	private Integer flags;
	@JsonProperty("level")
	private Integer level;
	@JsonProperty("priority")
	private Integer priority;
	@JsonProperty("account_id")
	private Integer accountId;	
	@JsonProperty("mailType")
	private String mailType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public Boolean getIsAttachment() {
		return isAttachment;
	}
	public void setIsAttachment(Boolean isAttachment) {
		this.isAttachment = isAttachment;
	}
	public String[][] getFrom() {
		return from;
	}
	public void setFrom(String[][] from) {
		this.from = from;
	}
	public String[][] getTo() {
		return to;
	}
	public void setTo(String[][] to) {
		this.to = to;
	}
	public String[][] getCc() {
		return cc;
	}
	public void setCc(String[][] cc) {
		this.cc = cc;
	}
	public String[][] getBcc() {
		return bcc;
	}
	public void setBcc(String[][] bcc) {
		this.bcc = bcc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Long getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Long receivedDate) {
		this.receivedDate = receivedDate;
	}
	public Long getSentDate() {
		return sentDate;
	}
	public void setSentDate(Long sentDate) {
		this.sentDate = sentDate;
	}
	public Integer getFlags() {
		return flags;
	}
	public void setFlags(Integer flags) {
		this.flags = flags;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getMailType() {
		return mailType;
	}
	public void setMailType(String mailType) {
		this.mailType = mailType;
	}
	@Override
	public int compareTo(AllMailResponse allMailResponse) {
		if(this.receivedDate < allMailResponse.receivedDate)
			return 1;
		if(this.receivedDate > allMailResponse.receivedDate)
			return -1;
		return 0;
	}

}
