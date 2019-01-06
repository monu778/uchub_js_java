package com.broadsoft.demohub.api.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

public class MailResponse extends BaseResponse{
	
	@JsonProperty("folder_id")
	private String folderId ;
	@JsonProperty("id")
	private String id ;
	@JsonProperty("unread")
	private Integer unread ;
	@JsonProperty("attachment")
	private Boolean attachment ;
	@JsonProperty("content_type")
	private String contentType ;
	@JsonProperty("size")
	private Integer size ;
	@JsonProperty("accoun_name")
	private String accounName ;
	@JsonProperty("accoun_id")
	private Integer accounId ;
	@JsonProperty("malicious")
	private Boolean maicious ;
	@JsonProperty("security_info")
	private SecurityInfo securityInfo;
	@JsonProperty("from")
	private String[][] from = null;
	@JsonProperty("to")
	private String[][] to = null;
	@JsonProperty("cc")
	private String[][] cc = null;
	@JsonProperty("bcc")
	private String[][] bcc = null;
	@JsonProperty("subject")
	private String subject;
	@JsonProperty("received_date")
	private Long receivedDate;
	@JsonProperty("date")
	private Long date;
	@JsonProperty("flags")
	private Integer flags;
	@JsonProperty("user")
	private List<String> user ;
	@JsonProperty("color_label")
	private Integer colorLabel ;
	@JsonProperty("priority")
	private Integer priority ;
	@JsonProperty("header")
	private Header header;
	@JsonProperty("attachments")
	Attachment [] attachments = null;
	@JsonProperty("realAttachment")
	private Boolean realAttachment ;
	@JsonProperty("modified")
	private Integer modified;
	@JsonProperty("isInvite")
	private Boolean invite;
	private String sequenceId;
	private String msgref;
	private boolean flagged;

	
	public Boolean getInvite() {
		return invite;
	}
	public void setInvite(Boolean invite) {
		this.invite = invite;
	}
	
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getUnread() {
		return unread;
	}
	public void setUnread(Integer unread) {
		this.unread = unread;
	}
	public Boolean getAttachment() {
		return attachment;
	}
	public void setAttachment(Boolean attachment) {
		this.attachment = attachment;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public String getAccounName() {
		return accounName;
	}
	public void setAccounName(String accounName) {
		this.accounName = accounName;
	}
	public Integer getAccounId() {
		return accounId;
	}
	public void setAccounId(Integer accounId) {
		this.accounId = accounId;
	}
	public Boolean getMaicious() {
		return maicious;
	}
	public void setMaicious(Boolean maicious) {
		this.maicious = maicious;
	}
	public SecurityInfo getSecurityInfo() {
		return securityInfo;
	}
	public void setSecurityInfo(SecurityInfo securityInfo) {
		this.securityInfo = securityInfo;
	}
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Long getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Long receivedDate) {
		this.receivedDate = receivedDate;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public Integer getFlags() {
		return flags;
	}
	public void setFlags(Integer flags) {
		this.flags = flags;
	}
	public List<String> getUser() {
		return user;
	}
	public void setUser(List<String> user) {
		this.user = user;
	}
	public Integer getColorLabel() {
		return colorLabel;
	}
	public void setColorLabel(Integer colorLabel) {
		this.colorLabel = colorLabel;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public Boolean getRealAttachment() {
		return realAttachment;
	}
	public void setRealAttachment(Boolean realAttachment) {
		this.realAttachment = realAttachment;
	}
	public Integer getModified() {
		return modified;
	}
	public void setModified(Integer modified) {
		this.modified = modified;
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
	
	public Attachment[] getAttachments() {
		return attachments;
	}
	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;
	}
	public String getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}
	public String getMsgref() {
		return msgref;
	}
	public void setMsgref(String msgref) {
		this.msgref = msgref;
	}
	public boolean isFlagged() {
		return flagged;
	}
	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}
	

}
