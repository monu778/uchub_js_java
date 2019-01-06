/*
 *  Copyright © 2018 BroadSoft. All rights reserved.
 */
package com.broadsoft.demohub.api.parser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.broadsoft.demohub.api.beans.AllMailResponse;
import com.broadsoft.demohub.api.beans.Attachment;
import com.broadsoft.demohub.api.beans.ContextualResponseBean;
import com.broadsoft.demohub.api.beans.Header;
import com.broadsoft.demohub.api.beans.MailData;
import com.broadsoft.demohub.api.beans.MailResponse;
import com.broadsoft.demohub.api.beans.SecurityInfo;

public class MailResponseParser {
	
	private Logger logger = Logger.getLogger(MailResponseParser.class);
	
	public MailResponse parseGetMail(String response){
		SecurityInfo securityInfo = new SecurityInfo();
		JSONObject securityInfoJsonObject = null; 
		MailResponse mailResponse = new MailResponse();
		JSONObject dataObject= new JSONObject(response);
		
	try{
		JSONObject mailsObject= dataObject.getJSONObject("data");
		if(mailsObject.has("folder_id")){
			String folder = mailsObject.getString("folder_id");
			mailResponse.setFolderId(folder);
		}
		
		if(mailsObject.has("id")){
			String id = mailsObject.getString("id");
			mailResponse.setId(id);
		}
		
		if(mailsObject.has("unread")){
			Integer unread = mailsObject.getInt("unread");
			mailResponse.setUnread(unread);
		}
		
		if(mailsObject.has("attachment")){
			Boolean attachment = mailsObject.getBoolean("attachment");
			mailResponse.setAttachment(attachment);
		}
		
		if(mailsObject.has("content_type")){
			String contentType = mailsObject.getString("content_type");
			mailResponse.setContentType(contentType);
		}
		
		if(mailsObject.has("size")){
			Integer size = mailsObject.getInt("size");
			mailResponse.setSize(size);
		}
		
		if(mailsObject.has("account_name")){
			String accountName = mailsObject.getString("account_name");
			mailResponse.setAccounName(accountName);
		}
		
		if(mailsObject.has("account_id")){
			Integer accountId = mailsObject.getInt("account_id");
			mailResponse.setAccounId(accountId);
		}
		
		if(mailsObject.has("malicious")){
			Boolean malicious = mailsObject.getBoolean("malicious");
			mailResponse.setMaicious(malicious);
		}
		
		if(mailsObject.has("security_info")){
			securityInfoJsonObject = mailsObject.getJSONObject("security_info");
			if(securityInfoJsonObject.has("encrypted")){
				Boolean encrypted = securityInfoJsonObject.getBoolean("encrypted");
				securityInfo.setEncrypted(encrypted);
			}
			
			if(securityInfoJsonObject.has("signed")){
			Boolean signed = securityInfoJsonObject.getBoolean("signed");	
			securityInfo.setEncrypted(signed);
			}
			mailResponse.setSecurityInfo(securityInfo);
		}
		
		
		JSONArray from = mailsObject.getJSONArray("from");
		JSONArray fromArray = null; 
		if(null != from){
			String[][] fromList = new String[from.length()][2];
			for(int i = 0 ; i < from.length() ; i++){
				fromArray = from.getJSONArray(i);
				fromList[i][0] = fromArray.get(0).toString();
				fromList[i][1] = fromArray.get(1).toString();							
			}
			mailResponse.setFrom(fromList);
		}
		
		JSONArray to = mailsObject.getJSONArray("to");
		JSONArray toArray = null; 
		if(null != to){
			String[][] toList = new String[to.length()][2];
			for(int i = 0 ; i < to.length() ; i++){
				toArray = to.getJSONArray(i);
				toList[i][0] = toArray.get(0).toString();
				toList[i][1] = toArray.get(1).toString();							
			}
			mailResponse.setTo(toList);
		}
		
		JSONArray cc = mailsObject.getJSONArray("cc");
		JSONArray ccArray = null; 
		if(null != cc){
			String[][] ccList = new String[cc.length()][2];
			for(int i = 0 ; i < cc.length() ; i++){
				ccArray = cc.getJSONArray(i);
				ccList[i][0] = ccArray.get(0).toString();
				ccList[i][1] = ccArray.get(1).toString();							
			}
			mailResponse.setCc(ccList);
		}
		
		JSONArray bcc = mailsObject.getJSONArray("bcc");
		JSONArray bccArray = null; 
		if(null != bcc){
			String[][] bccList = new String[bcc.length()][2];
			for(int i = 0 ; i < bcc.length() ; i++){
				bccArray = bcc.getJSONArray(i);
				bccList[i][0] = bccArray.get(0).toString();
				bccList[i][1] = bccArray.get(1).toString();							
			}
			mailResponse.setBcc(bccList);
		}
		
		if(mailsObject.has("subject")){
			String subject = mailsObject.getString("subject");
			mailResponse.setSubject(subject.replaceAll("[^\\p{ASCII}]", ""));
		}
		
		if(mailsObject.has("received_date")){
			Long receivedDate = mailsObject.getLong("received_date");
			mailResponse.setReceivedDate(receivedDate);
		}
		
		if(mailsObject.has("date")){
			Long date = mailsObject.getLong("date");
			mailResponse.setDate(date);
		}
		
		if(mailsObject.has("flags")){
			Integer flags = mailsObject.getInt("flags");
			mailResponse.setFlags(flags);
		}
		
		if(mailsObject.has("user")){
		JSONArray users = mailsObject.getJSONArray("user");
		List<String> user = new ArrayList<String>();
			for(int i = 0 ; i < users.length() ; i++ ){
				user.add(users.getString(i)); 
			}
			mailResponse.setUser(user);
		}
	
		if(mailsObject.has("color_label")){
			Integer colorLabel = mailsObject.getInt("color_label");
			mailResponse.setColorLabel(colorLabel);
			if(colorLabel>0)
				mailResponse.setFlagged(true);
		}
		
		if(mailsObject.has("priority")){
			Integer priority = mailsObject.getInt("priority");
			mailResponse.setPriority(priority);
		}
		
		if(mailsObject.has("headers")){
			JSONObject jSONHeaderObject = mailsObject.getJSONObject("headers");
			Header header = null;
			header = new  Header();
			
			if(jSONHeaderObject.has("MIME-Version") ){
				String mimeVersion = jSONHeaderObject.getString("MIME-Version");
				header.setMimeVersion(mimeVersion);
			}
			
			if(jSONHeaderObject.has("References") ){
				String references = jSONHeaderObject.getString("References");
				header.setReferences(references);
			}
			
			if(jSONHeaderObject.has("Return-Path") ){
				String returnPath = jSONHeaderObject.getString("Return-Path");
				header.setReturnPath(returnPath);
			}
			
			if(jSONHeaderObject.has("X-Google-DKIM-Signature") ){
				if(!jSONHeaderObject.optString("X-Google-DKIM-Signature").isEmpty())
				header.setxGoogleDKIMSignature(jSONHeaderObject.optString("X-Google-DKIM-Signature"));
			}
			
			if(jSONHeaderObject.has("X-Proofpoint-Virus-Version") ){
				if(!jSONHeaderObject.optString("X-Proofpoint-Virus-Version").isEmpty())
				header.setxProofpointVirusVersion(jSONHeaderObject.optString("X-Proofpoint-Virus-Version"));
			}
			
			if(jSONHeaderObject.has("X-Proofpoint-Spam-Reason") ){
				if(!jSONHeaderObject.optString("X-Proofpoint-Spam-Reason").isEmpty())
				header.setxProofpointSpamReason(jSONHeaderObject.optString("X-Proofpoint-Spam-Reason"));
			}
			
			if(jSONHeaderObject.has("In-Reply-To") ){
				if(!jSONHeaderObject.optString("In-Reply-To").isEmpty())
				header.setInReplyTo(jSONHeaderObject.optString("In-Reply-To"));
			}
			
			if(jSONHeaderObject.has("X-Received") ){
				if(!jSONHeaderObject.optString("X-Received").isEmpty())
				header.setxReceived(jSONHeaderObject.optString("X-Received"));
			}
			
			if(jSONHeaderObject.has("Message-ID") ){
				if(!jSONHeaderObject.optString("Message-ID").isEmpty())
				header.setMessageID(jSONHeaderObject.optString("Message-ID"));
			}
			
			if(jSONHeaderObject.has("Reply-To") ){
				if(!jSONHeaderObject.optString("Reply-To").isEmpty())
				header.setReplyTo(jSONHeaderObject.optString("Reply-To"));
			}
			
			if(jSONHeaderObject.has("DKIM-Signature") ){
				String[] dkimSignatureArray = null;
				try{
				JSONArray dkimSignatureJsonArray = jSONHeaderObject.optJSONArray("DKIM-Signature");
			    if(dkimSignatureJsonArray != null)
				dkimSignatureArray = new String[dkimSignatureJsonArray.length()];
			    else
			    dkimSignatureArray = new String[1];
			    for (int i = 0 ; i < dkimSignatureJsonArray.length() ; i++){
			    	dkimSignatureArray[i] = dkimSignatureJsonArray.getString(i);
			    }
			    header.setDkimSignature(dkimSignatureArray);
				}
				catch (Exception e) {
					logger.error("DKIM-Signature Error "+e.getMessage());
					if(!jSONHeaderObject.optString("DKIM-Signature").isEmpty()){
						   dkimSignatureArray[0] = jSONHeaderObject.optString("DKIM-Signature");
						   header.setDkimSignature(dkimSignatureArray); 
					}
				}
				
			}
			
			if(jSONHeaderObject.has("Content-Transfer-Encoding") ){
				String contentTransferEncoding = jSONHeaderObject.getString("Content-Transfer-Encoding");
				header.setContentTransferEncoding(contentTransferEncoding);
			}
			
			if(jSONHeaderObject.has("X-VADE-STATUS") ){
				JSONArray xVadeJsonStatusArray = null;
				String[] xVadeStatusArray = null;
				try{
				 xVadeJsonStatusArray = jSONHeaderObject.optJSONArray("X-VADE-STATUS");
				 if(xVadeJsonStatusArray != null)
					 xVadeStatusArray = new String[xVadeJsonStatusArray.length()];
				 else
					 xVadeStatusArray = new String[1];
			     for (int i = 0 ; i < xVadeJsonStatusArray.length() ; i++){
			    	xVadeStatusArray[i] = xVadeJsonStatusArray.getString(i);
			     }
			     header.setxVadeStatus(xVadeStatusArray);
				 }
			    catch (Exception e) {
					logger.error("X-VADE-STATUS Error "+e.getMessage());
					if(!jSONHeaderObject.optString("X-VADE-STATUS").isEmpty()){
					   xVadeStatusArray[0] = jSONHeaderObject.optString("X-VADE-STATUS");
					   header.setxVadeStatus(xVadeStatusArray); 
					}
				}
			}
			if(jSONHeaderObject.has("Received") ){
				JSONArray receivedJsonArray = jSONHeaderObject.getJSONArray("Received");
			    String[] receivedArray = new String[receivedJsonArray.length()];
			    for (int i = 0 ; i < receivedJsonArray.length() ; i++){
			    	receivedArray[i] = receivedJsonArray.getString(i);
			    }
			    header.setReceived(receivedArray);
				
			}
			
			if(jSONHeaderObject.has("X-Google-Smtp-Source") ){
				if(!jSONHeaderObject.optString("X-Google-Smtp-Source").isEmpty())
				header.setxGoogleSmtpSource(jSONHeaderObject.optString("X-Google-Smtp-Source"));
			}
			
			if(jSONHeaderObject.has("Sender") ){
				if(!jSONHeaderObject.optString("Sender").isEmpty())
				header.setSender(jSONHeaderObject.optString("Sender"));
			}
			
			if(jSONHeaderObject.has("Delivered-To") ){
				String deliveredTo = jSONHeaderObject.getString("Delivered-To");
				header.setDeliveredTo(deliveredTo);
			}
			if(jSONHeaderObject.has("X-Gm-Message-State") ){
				if(!jSONHeaderObject.optString("X-Gm-Message-State").isEmpty())
				header.setxGmMessageState(jSONHeaderObject.optString("X-Gm-Message-State"));
			}
			if(jSONHeaderObject.has("X-Open-Xchange-Module")) {
				if(!jSONHeaderObject.optString("X-Open-Xchange-Module").isEmpty())
					header.setxOpenXchangeModule(jSONHeaderObject.optString("X-Open-Xchange-Module"));
			}
			
			mailResponse.setHeader(header);
		}
		
		if(mailsObject.has("attachments")){
			Attachment[] attachments = null;
			Attachment attachment = null;
			JSONArray attachmentsArray = mailsObject.getJSONArray("attachments");
			
			int len = 0;
			
			for(int i = 0 ; i < attachmentsArray.length() ; i++ ){
				JSONObject attachmentJsonObject = (JSONObject) attachmentsArray.get(i);
				if(attachmentJsonObject.has("filename") && attachmentJsonObject.getString("filename").startsWith("Part_") && (attachmentJsonObject.getString("filename").endsWith("html") 
					|| attachmentJsonObject.getString("filename").endsWith("txt"))){
					continue;
				}
				else 
					len++;
			}
			attachments = new Attachment[len];
			len = 0;
			for(int i = 0 ; i < attachmentsArray.length() ; i++ ){
				JSONObject attachmentJsonObject = (JSONObject) attachmentsArray.get(i);
				if(attachmentJsonObject.has("filename") && attachmentJsonObject.getString("filename").startsWith("Part_") && (attachmentJsonObject.getString("filename").endsWith("html") 
					|| attachmentJsonObject.getString("filename").endsWith("txt"))){
					continue;
				}
				attachment = new Attachment();
				if(attachmentJsonObject.has("id")){
					attachment.setId(attachmentJsonObject.getString("id"));
				}
				if(attachmentJsonObject.has("filename")){
					attachment.setFileName(attachmentJsonObject.getString("filename"));
				}
				if(attachmentJsonObject.has("size")){
					attachment.setSize(attachmentJsonObject.getInt("size"));
				}
				if(attachmentJsonObject.has("disp")){
					attachment.setDisp(attachmentJsonObject.getString("disp"));
				}
				if(attachmentJsonObject.has("content_type")){
					attachment.setContentType(attachmentJsonObject.getString("content_type"));
				}
				if(attachmentJsonObject.has("content")){
					if(!attachmentJsonObject.optString("content").isEmpty()){
						String content = attachmentJsonObject.optString("content").replaceAll("[^\\p{ASCII}]", "");
						attachment.setContent(content);
					}
				}
				if(attachmentJsonObject.has("truncated")){
					attachment.setTruncated(attachmentJsonObject.getBoolean("truncated"));
				}
				if(attachmentJsonObject.has("sanitized")){
					attachment.setSanitized(attachmentJsonObject.getBoolean("sanitized"));
				}
				
				attachments[len] = attachment;
				len++;
			}
			
			mailResponse.setAttachments(attachments);
		}
		
		
	   if(mailsObject.has("real_attachment")){
			Boolean realAttachment = mailsObject.getBoolean("real_attachment");
			mailResponse.setRealAttachment(realAttachment);
	   }
		
	   if(mailsObject.has("modified")){
			Integer modified = mailsObject.getInt("modified");
			mailResponse.setModified(modified);
	   }
	   if(mailsObject.has("msgref")){
		   mailResponse.setMsgref(mailsObject.get("msgref").toString());
	   }
			
	}catch (Exception e) {
		logger.error("Error Occured while parsing : "+e.getMessage());
	}
		
		return mailResponse;
	}

	public ArrayList<ContextualResponseBean> parseContextual(String response) {
		
		ArrayList<ContextualResponseBean> responseBeanList = new ArrayList<ContextualResponseBean>();
		
		ContextualResponseBean responseBean = null;
		try{
		JSONObject dataObject= new JSONObject(response);
		JSONArray contextualArray = dataObject.getJSONArray("data");
		String [][] from = null;
		String [][] to   = null;
		String [][] cc   = null;
		String [][] bcc  = null;
		
		for(int i = 0 ; i < contextualArray.length() ; i++){
			JSONArray mailArray = contextualArray.getJSONArray(i);
			responseBean = new ContextualResponseBean();
			responseBean.setId(mailArray.getString(0));
			responseBean.setFolderId(mailArray.getString(1));
			responseBean.setIsAttachment(mailArray.getBoolean(2));
	         
			JSONArray fromArray = mailArray.getJSONArray(3);
			if(fromArray.length() == 0){
				from = new String[0][0];
			}else{	
			from = new String[fromArray.length()][2];
			for(int j = 0 ; j < fromArray.length() ; j++){
				JSONArray fromMailArray = fromArray.getJSONArray(j);
				from[j][0] = fromMailArray.optString(0);
				if(from[j][0].isEmpty())
					from[j][0] = null;
				from[j][1] = fromMailArray.optString(1);
			}
			}
			responseBean.setFrom(from);
			
			JSONArray toArray = mailArray.getJSONArray(4);
			if(toArray.length() == 0){
				to = new String[0][0];
			}else{	
			to = new String[toArray.length()][2];
			for(int j = 0 ; j < toArray.length() ; j++){
				JSONArray toMailArray = toArray.getJSONArray(j);
				to[j][0] = toMailArray.optString(0);
				if(to[j][0].isEmpty())
					to[j][0] = null;
				to[j][1] = toMailArray.optString(1);
			}
			}
			responseBean.setTo(to);
			
			JSONArray ccArray = mailArray.getJSONArray(5);
			if(ccArray.length() == 0){
				cc = new String[0][0];
			}else{	
			cc = new String[ccArray.length()][2];
			for(int j = 0 ; j < ccArray.length() ; j++){
				JSONArray ccMailArray = ccArray.getJSONArray(j);
				cc[j][0] = ccMailArray.optString(0);
				if(cc[j][0].isEmpty())
					cc[j][0] = null;
				cc[j][1] = ccMailArray.optString(1);
			}
			}
			responseBean.setCc(cc);
			
			JSONArray bccArray = mailArray.getJSONArray(6);
			if(bccArray.length() == 0){
				bcc = new String[0][0];
			}else{	
			bcc = new String[bccArray.length()][2];
			for(int j = 0 ; j < bccArray.length() ; j++){
				JSONArray bccMailArray = bccArray.getJSONArray(j);
				bcc[j][0] = bccMailArray.optString(0);
				if(bcc[j][0].isEmpty())
					bcc[j][0] = null;
				bcc[j][1] = bccMailArray.optString(1);
			}
			}
			responseBean.setBcc(bcc);
			
			responseBean.setSubject(mailArray.getString(7));
			responseBean.setSize(mailArray.getInt(8));
			responseBean.setSentDate(mailArray.getLong(9));
			responseBean.setReceivedDate(mailArray.getLong(10));
			responseBean.setFlags(mailArray.getInt(11));
			responseBean.setLevel(mailArray.getInt(12));
			responseBean.setPriority(mailArray.getInt(13));
			responseBean.setAccountId(mailArray.getInt(14));
			responseBean.setMailType(mailArray.getString(15));
			
			responseBeanList.add(responseBean);
			
		}
		
		}
		catch (Exception e) {
			logger.error("Exception Occured while parsing Contextual Mail response : "+e.getMessage());
		}
		
		Collections.sort(responseBeanList);
		
		return responseBeanList;
	}
	
	
public ArrayList<AllMailResponse> parseAllMail(String response , MailData mailData) {
		
		ArrayList<AllMailResponse> responseBeanList = new ArrayList<AllMailResponse>();
		
		AllMailResponse responseBean = null;
		try{
		JSONObject dataObject= new JSONObject(response);
		JSONArray contextualArray = dataObject.getJSONArray("data");
		String [][] from = null;
		String [][] to   = null;
		String [][] cc   = null;
		String [][] bcc  = null;
		
		for(int i = 0 ; i < contextualArray.length() ; i++){
			JSONArray mailArray = contextualArray.getJSONArray(i);
			responseBean = new AllMailResponse();
			
			Integer flag = mailArray.getInt(11);
			if(mailData.getUnseen() != null && mailData.getUnseen().equalsIgnoreCase("true") && flag >= 32){
				continue ;
			}
			responseBean.setId(mailArray.getString(0));
			responseBean.setFolderId(mailArray.getString(1));
			responseBean.setIsAttachment(mailArray.getBoolean(2));
	         
			JSONArray fromArray = mailArray.getJSONArray(3);
			if(fromArray.length() == 0){
				from = new String[0][0];
			}else{	
			from = new String[fromArray.length()][2];
			for(int j = 0 ; j < fromArray.length() ; j++){
				JSONArray fromMailArray = fromArray.getJSONArray(j);
				from[j][0] = fromMailArray.optString(0);
				if(from[j][0].isEmpty())
					from[j][0] = null;
				from[j][1] = fromMailArray.optString(1);
			}
			}
			responseBean.setFrom(from);
			
			JSONArray toArray = mailArray.getJSONArray(4);
			if(toArray.length() == 0){
				to = new String[0][0];
			}else{	
			to = new String[toArray.length()][2];
			for(int j = 0 ; j < toArray.length() ; j++){
				JSONArray toMailArray = toArray.getJSONArray(j);
				to[j][0] = toMailArray.optString(0);
				if(to[j][0].isEmpty())
					to[j][0] = null;
				to[j][1] = toMailArray.optString(1);
			}
			}
			responseBean.setTo(to);
			
			JSONArray ccArray = mailArray.getJSONArray(5);
			if(ccArray.length() == 0){
				cc = new String[0][0];
			}else{	
			cc = new String[ccArray.length()][2];
			for(int j = 0 ; j < ccArray.length() ; j++){
				JSONArray ccMailArray = ccArray.getJSONArray(j);
				cc[j][0] = ccMailArray.optString(0);
				if(cc[j][0].isEmpty())
					cc[j][0] = null;
				cc[j][1] = ccMailArray.optString(1);
			}
			}
			responseBean.setCc(cc);
			
			JSONArray bccArray = mailArray.getJSONArray(6);
			if(bccArray.length() == 0){
				bcc = new String[0][0];
			}else{	
			bcc = new String[bccArray.length()][2];
			for(int j = 0 ; j < bccArray.length() ; j++){
				JSONArray bccMailArray = bccArray.getJSONArray(j);
				bcc[j][0] = bccMailArray.optString(0);
				if(bcc[j][0].isEmpty())
					bcc[j][0] = null;
				bcc[j][1] = bccMailArray.optString(1);
			}
			}
			responseBean.setBcc(bcc);
			
			String subject = mailArray.getString(7);
			responseBean.setSubject(subject.replaceAll("[^\\p{ASCII}]", ""));
			responseBean.setSize(mailArray.getInt(8));
			Long sentDate = mailArray.optLong(9);
			if(sentDate != 0)
			responseBean.setSentDate(mailArray.getLong(9));
			responseBean.setReceivedDate(mailArray.getLong(10));
			responseBean.setFlags(mailArray.getInt(11));
			responseBean.setLevel(mailArray.getInt(12));
			responseBean.setPriority(mailArray.getInt(13));
			responseBean.setAccountId(mailArray.getInt(14));
			responseBeanList.add(responseBean);
			
		}
		
		
		}
		catch (Exception e) {
			logger.error("Exception Occured while parsing AllMails response : "+e.getMessage());
		}
		
		return responseBeanList;
	}
	
	public String errorResponse(String responseString){
		JSONObject jsonObject = new JSONObject(responseString);
		String response = jsonObject.optString("error");
		return response;
	}

	public MailData parseCountResponse(String response) {
		MailData countResponse = new MailData();
		JSONObject jsonObject = new JSONObject(response);
		countResponse.setData(Integer.parseInt(jsonObject.optString("data")));
		return countResponse;
	}

}
