package com.broadsoft.demohub.api.beans;


public class ContextualResponseBean implements Comparable<ContextualResponseBean> {
		private String id;
		private String folderId;
		private Boolean isAttachment;
		private String [][] from;
		private String [][] to;
		private String [][] cc;
		private String [][] bcc;
		private String subject;
		private Integer size;
		private Long sentDate;
		private Long receivedDate;
		private Integer flags;
		private Integer level;
		private Integer priority;
		private Integer accountId;
		private String mailType;
		
		public Integer getLevel() {
			return level;
		}
		public void setLevel(Integer level) {
			this.level = level;
		}
		
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
		
		public String[][] getTo() {
			return to;
		}
		
		public String[][] getFrom() {
			return from;
		}
		public void setFrom(String[][] from) {
			this.from = from;
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
		public void setTo(String[][] to) {
			this.to = to;
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
		public Long getSentDate() {
			return sentDate;
		}
		public void setSentDate(Long sentDate) {
			this.sentDate = sentDate;
		}
		public Long getReceivedDate() {
			return receivedDate;
		}
		public void setReceivedDate(Long receivedDate) {
			this.receivedDate = receivedDate;
		}
		public Integer getFlags() {
			return flags;
		}
		public void setFlags(Integer flags) {
			this.flags = flags;
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
		public int compareTo(ContextualResponseBean bean) {
			 Long receivedDate = ((ContextualResponseBean)bean).getReceivedDate();
			 if(receivedDate > this.receivedDate)
				 return 1;
			 else if(receivedDate < this.receivedDate)
				 return -1;
			 else return 0;
		}
	}
