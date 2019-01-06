/*
 *  Copyright © 2018 BroadSoft. All rights reserved.
 */
package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {

		@JsonProperty("MIME-Version")
		private String mimeVersion;
		@JsonProperty("References")
		private String references;
		@JsonProperty("Return-Path")
		private String returnPath;
		@JsonProperty("X-Google-DKIM-Signature")
		private String xGoogleDKIMSignature;
		@JsonProperty("X-Proofpoint-Virus-Version")
		private String xProofpointVirusVersion;
		@JsonProperty("X-Received")
		private String xReceived;
		@JsonProperty("Message-ID")
		private String messageID;
		@JsonProperty("Reply-To")
		private String replyTo;
		@JsonProperty("Content-Transfer-Encoding")
		private String contentTransferEncoding;
		@JsonProperty("X-Proofpoint-Spam-Reason")
		private String xProofpointSpamReason;
		@JsonProperty("DKIM-Signature")
		private String [] dkimSignature;
		@JsonProperty("In-Reply-To")
		private String inReplyTo;		
		@JsonProperty("X-VADE-STATUS")
		private String[] xVadeStatus = null;
		@JsonProperty("Received")
		private String[] received = null;
		@JsonProperty("X-Google-Smtp-Source")
		private String xGoogleSmtpSource;
		@JsonProperty("Sender")
		private String sender;
		@JsonProperty("Delivered-To")
		private String deliveredTo;
		@JsonProperty("X-Gm-Message-State")
		private String xGmMessageState;
		@JsonProperty("X-Open-Xchange-Module")
		private String xOpenXchangeModule;
		
		
		public String getxOpenXchangeModule() {
			return xOpenXchangeModule;
		}
		public void setxOpenXchangeModule(String xOpenXchangeModule) {
			this.xOpenXchangeModule = xOpenXchangeModule;
		}
		public String getReferences() {
			return references;
		}
		public void setReferences(String references) {
			this.references = references;
		}
		public String getxProofpointVirusVersion() {
			return xProofpointVirusVersion;
		}
		public void setxProofpointVirusVersion(String xProofpointVirusVersion) {
			this.xProofpointVirusVersion = xProofpointVirusVersion;
		}
		public String getxProofpointSpamReason() {
			return xProofpointSpamReason;
		}
		public void setxProofpointSpamReason(String xProofpointSpamReason) {
			this.xProofpointSpamReason = xProofpointSpamReason;
		}
		public String getInReplyTo() {
			return inReplyTo;
		}
		public void setInReplyTo(String inReplyTo) {
			this.inReplyTo = inReplyTo;
		}
		
		public String getxGoogleDKIMSignature() {
			return xGoogleDKIMSignature;
		}
		public void setxGoogleDKIMSignature(String xGoogleDKIMSignature) {
			this.xGoogleDKIMSignature = xGoogleDKIMSignature;
		}
		public String getxReceived() {
			return xReceived;
		}
		public void setxReceived(String xReceived) {
			this.xReceived = xReceived;
		}
		public String getMessageID() {
			return messageID;
		}
		public void setMessageID(String messageID) {
			this.messageID = messageID;
		}
		public String getReplyTo() {
			return replyTo;
		}
		public void setReplyTo(String replyTo) {
			this.replyTo = replyTo;
		}

		public String[] getDkimSignature() {
			return dkimSignature;
		}
		public void setDkimSignature(String[] dkimSignature) {
			this.dkimSignature = dkimSignature;
		}
		public String getxGoogleSmtpSource() {
			return xGoogleSmtpSource;
		}
		public void setxGoogleSmtpSource(String xGoogleSmtpSource) {
			this.xGoogleSmtpSource = xGoogleSmtpSource;
		}
		public String getSender() {
			return sender;
		}
		public void setSender(String sender) {
			this.sender = sender;
		}
		public String getxGmMessageState() {
			return xGmMessageState;
		}
		public void setxGmMessageState(String xGmMessageState) {
			this.xGmMessageState = xGmMessageState;
		}
		public String getMimeVersion() {
			return mimeVersion;
		}
		public void setMimeVersion(String mimeVersion) {
			this.mimeVersion = mimeVersion;
		}
		public String getReturnPath() {
			return returnPath;
		}
		public void setReturnPath(String returnPath) {
			this.returnPath = returnPath;
		}
		public String getContentTransferEncoding() {
			return contentTransferEncoding;
		}
		public void setContentTransferEncoding(String contentTransferEncoding) {
			this.contentTransferEncoding = contentTransferEncoding;
		}
		public String[] getxVadeStatus() {
			return xVadeStatus;
		}
		public void setxVadeStatus(String[] xVadeStatus) {
			this.xVadeStatus = xVadeStatus;
		}
		public String[] getReceived() {
			return received;
		}
		public void setReceived(String[] received) {
			this.received = received;
		}
		public String getDeliveredTo() {
			return deliveredTo;
		}
		public void setDeliveredTo(String deliveredTo) {
			this.deliveredTo = deliveredTo;
		}
	}
