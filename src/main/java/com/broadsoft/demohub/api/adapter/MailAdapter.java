package com.broadsoft.demohub.api.adapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.AllMailResponse;
import com.broadsoft.demohub.api.beans.Attachment;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.FolderData;
import com.broadsoft.demohub.api.beans.InviteResponse;
import com.broadsoft.demohub.api.beans.MailData;
import com.broadsoft.demohub.api.beans.MailDelete;
import com.broadsoft.demohub.api.beans.MailResponse;
import com.broadsoft.demohub.api.beans.UpdateMail;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.beans.UserParticipant;
import com.broadsoft.demohub.api.config.ConfigManager;
import com.broadsoft.demohub.api.parser.InviteResponseParser;
import com.broadsoft.demohub.api.parser.MailResponseParser;
import com.broadsoft.demohub.api.security.HashEncoderDecoder;
import com.broadsoft.demohub.api.util.Utility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MailAdapter {

	@Autowired
	UserDataBean userDataBean;

	@Autowired
	ConfigManager configManager;

	@Autowired
	MailResponseParser mailResponseParser;

	@Autowired
	HashEncoderDecoder hashEncoderDecoder;

	@Autowired
	FolderAdapter folderAdapter;

	HttpsURLConnection conn = null;
	private Logger logger = Logger.getLogger(LoginAdapter.class);

	public MailData getCount(MailData mailData) throws Exception {
		MailData countResponse = new MailData();
		String response = "";
		String input = null;
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		try {
			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			input = "session="
					+ URLEncoder.encode(userDataBean.getUserDataMap().get(mailData.getAuth()).getSession(), "UTF-8")
					+ "&folder=" + URLEncoder
							.encode(((mailData.getFolder() == null) ? "default0/INBOX" : mailData.getFolder()), "UTF-8")
					+ "&action=" + URLEncoder.encode("count", "UTF-8");

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			
			if(Utility.isErrorResponse(response)){
				countResponse.setError(Utility.getError(response));
				logger.error("Error Occured while getting mail count" + Utility.getError(response));
			    return countResponse;
			}else{
				countResponse = mailResponseParser.parseCountResponse(response);
			}

			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while user logging out : " + e.getMessage());
			if (null == input)
				response = "{\"error\":\"User Not Logged In\"}";
		}
		return countResponse;
	}

	public ArrayList<AllMailResponse> getAllMails(MailData mailData) throws Exception {
		logger.info("The json input:" + mailData);
		ArrayList<AllMailResponse> responseList = new ArrayList<AllMailResponse>();
		AllMailResponse allMailResponse = new AllMailResponse();
		String response = "";
		StringBuffer input = new StringBuffer("");
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		String columns = null;
		String unseen = "false";
		try {
			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
			
			logger.info("OX Request for allmail: "+url);
			logger.info("Request Cookies : "+userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
			if (mailData.getColumns() == null) {
				columns = configManager.getPropertyAsString("MAIL_COLUMNS");
			} else {
				columns = mailData.getColumns();
			}
			if (mailData.getUnseen() != null && (mailData.getUnseen().equalsIgnoreCase("true")
					|| mailData.getUnseen().equalsIgnoreCase("false"))) {
				unseen = mailData.getUnseen();
			}

			input.append("session=");
			input.append(
					URLEncoder.encode(userDataBean.getUserDataMap().get(mailData.getAuth()).getSession(), "UTF-8"));
			input.append("&folder=");
			input.append((mailData.getFolder() == null) ? "default0/INBOX" : mailData.getFolder());
			input.append("&action=all");
			input.append("&columns=");
			input.append(URLEncoder.encode(columns, "UTF-8"));

			if (mailData.getLeftHandLimit() != null) {
				input.append("&left_hand_limit=" + mailData.getLeftHandLimit());
			}
			if (mailData.getRightHandLimit() != null) {
				input.append("&right_hand_limit=" + mailData.getRightHandLimit());
			}
			if (mailData.getUnseen() != null && mailData.getUnseen().equals("true")) {
				input.append("&sort=651");
				input.append("&order=asc");
			}
			else{
				if(mailData.getSort() != null && mailData.getOrder() != null){
					input.append("&sort="+mailData.getSort());
					input.append("&order="+mailData.getOrder());
				}else{
					input.append("&sort=610");
					input.append("&order=desc");
				}
				
			}
			
			logger.info("OX Input Request for all Mails : "+input.toString());

			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			
			logger.info("OX Response for GetAllMail : "+response);
			
			if(Utility.isErrorResponse(response)){
				allMailResponse.setError(Utility.getError(response));
				logger.error("Error Occured while getting allmails response : " + Utility.getError(response));
				responseList.add(allMailResponse);
			    return responseList;
			}
			
			responseList = mailResponseParser.parseAllMail(response, mailData);

			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while getting all mails : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession())
				response = "{\"error\":\"User Not Logged In\"}";
		}
		return responseList;
	}

	public MailResponse getMail(MailData mailData) throws Exception {
		String response = "";
		MailResponse mailResponse = new MailResponse();
		String input = null;
		String unseenFlag = null;

		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		try {
			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
			logger.info("OX Request URL for getmail: "+url);
			
			logger.info("Request Cookies : "+userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			// checking useen field
			if (mailData.getUnseen() != null)
				unseenFlag = mailData.getUnseen();
			else
				unseenFlag = "true";

			input = "session="
					+ URLEncoder.encode(userDataBean.getUserDataMap().get(mailData.getAuth()).getSession(), "UTF-8")
					+ "&folder="
					+ URLEncoder.encode(((mailData.getFolder() == null) ? "default0/INBOX" : mailData.getFolder()),
							"UTF-8")
					+ "&action=" + URLEncoder.encode("get", "UTF-8") + "&id="
					+ URLEncoder.encode(mailData.getId(), "UTF-8") 
					+"&view="+URLEncoder.encode(((mailData.getView()==null)?"text":mailData.getView()),"UTF-8")
					+"&unseen="+ URLEncoder.encode(unseenFlag, "UTF-8");
			
			logger.info("OX Input Request for getmail: "+input);

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}

			logger.info("OX Response For GetMail: "+response);
			conn.disconnect();
			
			if(Utility.isErrorResponse(response)){
				mailResponse = new MailResponse();
				mailResponse.setError(Utility.getError(response));
				logger.error("Error Occured while getting mail count" + Utility.getError(response));
			    return mailResponse;
			}

			mailResponse = mailResponseParser.parseGetMail(response);
			mailResponse.setInvite(false);
			
			// checking if mail is an invite
			if(null!= mailResponse.getHeader() && null != mailResponse.getHeader().getxOpenXchangeModule()) {
				if(mailResponse.getHeader().getxOpenXchangeModule().equalsIgnoreCase("Appointments"))
					mailResponse.setInvite(true);
					
			}
			else
			{
			Attachment[] attachments = mailResponse.getAttachments();
			for (int i = 0; i < attachments.length; i++) {
				String fileName = attachments[i].getFileName();
				if (null != fileName && fileName.endsWith("ics") && mailResponse.getInvite() == false) {
					mailResponse.setInvite(true);
				}
				if (null != fileName && fileName.startsWith("Part_") && fileName.endsWith("ics")) {
					mailResponse.setSequenceId(attachments[i].getId());
				}
			}
			}

			// removing inline attachments that display footer message
			Attachment[] attachmentArray = mailResponse.getAttachments();
			ArrayList<Attachment> duplicateAttachmentArray = new ArrayList<Attachment>();
			for (int i = 0; i < attachmentArray.length; i++) {
				
					int index = attachmentArray[i].getContent().lastIndexOf("<img src");
					String content = null;
					if (index != -1) {
						content = attachmentArray[i].getContent().substring(0, index);
						attachmentArray[i].setContent(content);
						duplicateAttachmentArray.add(attachmentArray[i]);
					}else{
						duplicateAttachmentArray.add(attachmentArray[i]);
					}

				
			}
			Attachment[] attach = new Attachment[duplicateAttachmentArray.size()];
			attach = duplicateAttachmentArray.toArray(attach);
			mailResponse.setAttachments(attach);

		} catch (Exception e) {
			logger.error("Exception Occured while getting all mails : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
				mailResponse.setError("User Not Logged In");
			}
			else{
				mailResponse.setError(e.getMessage());
			}
		}
		return mailResponse;
	}

	public BaseResponse deleteMail(MailDelete mailData) throws Exception {
		BaseResponse baseResponse = new BaseResponse();
		
		String response="";
		String input = null;
		StringBuilder idsDeleted =new StringBuilder();
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL") + "?action=" + "delete"
				+ "&session="
				+ URLEncoder.encode(userDataBean.getUserDataMap().get(mailData.getAuth()).getSession(), "UTF-8");
		try {

			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			StringBuilder deleteList = new StringBuilder("[");
			int deleteListSize = mailData.getDeleteList().size();

			for (int i = 0; deleteListSize > 0; i++) {
				if (i > 0)
					deleteList.append(",");

				deleteList.append("{\"folder\":\"");
				deleteList.append((mailData.getDeleteList().get(i).getFolder() == null) ? "default0/INBOX"
						: mailData.getDeleteList().get(i).getFolder());
				deleteList.append("\",\"id\":\"");
				deleteList.append(mailData.getDeleteList().get(i).getId());
				deleteList.append("\"}");
				deleteListSize--;
				idsDeleted = idsDeleted.append(mailData.getDeleteList().get(i).getId()+" ,");
			}

			deleteList.append("]");
			idsDeleted = idsDeleted.deleteCharAt(idsDeleted.length() - 1);
			input = deleteList.toString();

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			conn.disconnect();
			JSONObject dataValue = new JSONObject(response);
			if(dataValue.has("data") && dataValue.getJSONArray("data").length()==0){
				baseResponse.setData("mails deleted successfully for mail Ids : " + idsDeleted);
			}
			else if(dataValue.has("error")) {
				baseResponse.setError(dataValue.get("error").toString());
			}
		} catch (Exception e) {
			logger.error("Exception Occured while deleting mail : " + e.getMessage());
			if (null == input)
				baseResponse.setError("User Not Logged In");
		}
		
		return baseResponse;
	}

	public String getMailAttachment(MailData mailData) throws Exception {
		String response = "";
		StringBuffer input = new StringBuffer();
		String folder = null;
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		try {

			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();

			input.append("session=");
			input.append(userDataBean.getUserDataMap().get(mailData.getAuth()).getSession());
			input.append("&folder=" + URLEncoder.encode(folder, "UTF-8"));
			input.append("&id=" + mailData.getId());
			input.append("&attachment=" + mailData.getAttachment_id());
			input.append("&save=" + mailData.getSave());
			input.append("&action=attachment");

			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}

			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while getting mail attachment : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()))
				response = "{\"error\":\"User Not Logged In\"}";
		}
		return response;
	}

	/*public String searchMailsArchived(MailData mailData) throws Exception {
		String response = null;
		String leftLimit = null;
		String rightLimit = null;

		// getting all subfolders of Archive and their paths
		TreeSet<String> subFoldersList = new TreeSet<String>();
		FolderData folderData = new FolderData();
		folderData.setAuth(mailData.getAuth());
		if (mailData.getFolder().contains("default0"))
			folderData.setTitle(mailData.getFolder());
		else
			folderData.setTitle("default0/" + mailData.getFolder());
		folderData.setModule("mail");

		try {
			// getting Archive folder Subfolders list here

			String responseValue = folderAdapter.getSubFolders(folderData);
			JSONObject dataValue = new JSONObject(responseValue);
			JSONArray subFoldersArray = dataValue.getJSONArray("data");
			int subFoldersLength = subFoldersArray.length();

			for (int k = 0; k < subFoldersLength; k++) {
				JSONArray subFolderObject = subFoldersArray.getJSONArray(k);
				String x = subFolderObject.get(8).toString();
				if (!(subFolderObject.get(8).toString()).equals("\"0\""))
					subFoldersList.add(folderData.getTitle() + "/" + subFolderObject.get(0).toString());
			}

			TreeSet<String> temporaryList = new TreeSet<String>();

			temporaryList = getAllFolderPaths(folderData, subFoldersList);
			subFoldersList.addAll(temporaryList);

			// adding Archive folder
			subFoldersList.add("default0/Archive"); // even if the root Archive
													// is empty it is handled

			JSONArray data = new JSONArray();
			JSONArray paginatedData = new JSONArray();

			// manual pagination for Archive , so we copy the left and right
			// limits into local variable and set bean limits to null , because
			// we dont want pagination again by the main API
			if (mailData.getLeftHandLimit() != null && mailData.getRightHandLimit() != null) {
				leftLimit = mailData.getLeftHandLimit();
				rightLimit = mailData.getRightHandLimit();
				mailData.setLeftHandLimit(null);
				mailData.setRightHandLimit(null);
			}

			for (String name : subFoldersList) {
				mailData.setFolder(name);
				String mails = "";//searchMail(mailData);
				if (mails.contains("error") || mails.contains("\"data\":[]"))
					continue;
				JSONObject dataArray = new JSONObject(mails);
				JSONArray allMailsArray = dataArray.getJSONArray("data");
				for (int j = 0; j < allMailsArray.length(); j++) {
					data.put(allMailsArray.get(j));
				}
			}

			String allMailsList = "{\"data\":" + data.toString() + "}";
			// pagination done here
			if (leftLimit != null && Integer.parseInt(leftLimit) >= 0 && rightLimit != null && !rightLimit.equals("0")
					&& Integer.parseInt(leftLimit) <= Integer.parseInt(rightLimit)) {

				JSONObject paginatedObject = new JSONObject(allMailsList);
				JSONArray paginatedMailsArray = paginatedObject.getJSONArray("data");
				for (int i = Integer.parseInt(leftLimit); i <= Integer.parseInt(rightLimit); i++) {
					if (i < paginatedMailsArray.length())
						paginatedData.put(paginatedMailsArray.get(i));
					else
						break;

				}
				allMailsList = "{\"data\":" + paginatedData.toString() + "}";
			}

			response = allMailsList;
		} catch (Exception e) {
			logger.error("Exception Occured while searching Archive folders mails: " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession())
				response = "{\"error\":\"User Not Logged In\"}";
		}

		return response;

	}*/

	public ArrayList<AllMailResponse> searchMail(MailData mailData) throws Exception {
		
		ArrayList<AllMailResponse> searchMailResponse = new ArrayList<AllMailResponse>();
		AllMailResponse searchMail = new AllMailResponse();
		String response = "";
		StringBuffer input = new StringBuffer();
		String leftLimit = null;
		String rightLimit = null;
		String openExchangeMailUrl;
		String folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
		String columns = configManager.getPropertyAsString("MAIL_COLUMNS");
		String session = userDataBean.getUserDataMap().get(mailData.getAuth()).getSession();
		logger.info("Session : "+session);
		if (mailData.getLeftHandLimit() != null)
			leftLimit = mailData.getLeftHandLimit();
		if (mailData.getRightHandLimit() != null)
			rightLimit = mailData.getRightHandLimit();
		if (leftLimit != null && rightLimit != null)
			openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL") + "?action=" + "search" + "&session="
					+ session + "&folder=" + folder + "&columns=" + columns + "&left_hand_limit=" + leftLimit
					+ "&right_hand_limit=" + rightLimit + "&sort=610" + "&order=desc";
		else
			openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL") + "?action=" + "search" + "&session="
					+ session + "&folder=" + folder + "&columns=" + columns + "&sort=610" + "&order=desc";
		try {

			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
			logger.info("OX Request for searchmail: "+url);
			logger.info("OX Request Cookie : "+userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
			String searchString = mailData.getSearchString();
			String field = null;
			String value = null;
			if (searchString != null) {
				if (searchString.contains(":")) {
					field = searchString.split(":", 2)[0].toLowerCase();
					value = searchString.split(":", 2)[1];
				} else {
					field = "content";
					value = searchString;
				}

				if (field.equalsIgnoreCase("content")) {
					input.append("{");
					input.append("\"filter\":");
					input.append("[\"or\",");
					input.append("[\"=\",");
					input.append("{\"field\":" + "\"" + field + "\"},");
					input.append("\"" + value + "\"");
					input.append("],");
					input.append("[\"=\",");
					input.append("{\"field\":" + "\"subject\"},");
					input.append("\"" + value + "\"");
					input.append("]");
					input.append("]");
					input.append("}");

				} else {
					input.append("{");
					input.append("\"filter\":");
					input.append("[\"or\",");
					input.append("[\"=\",");
					input.append("{\"field\":" + "\"" + field + "\"},");
					input.append("\"" + value + "\"");
					input.append("]");
					input.append("]");
					input.append("}");
				}
			} else if (mailData.getField() != null && mailData.getOperator() != null && mailData.getValue() != null && mailData.getSearch() == null) {
				input.append("{");
				input.append("\"filter\":");
				input.append("[\"and\",");
				input.append("[\"" + mailData.getOperator() + "\",");
				input.append("{\"field\":" + "\"" + mailData.getField() + "\"},");
				input.append("\"" + mailData.getValue() + "\"");
				input.append("]");
				input.append("]");
				input.append("}");

			}
			else if(mailData.getField() != null && mailData.getOperator() != null && mailData.getValue() != null && mailData.getSearch() != null){
				String [] searchCriteria = new String[2];
				if(mailData.getSearch().contains(":")){
					searchCriteria = mailData.getSearch().split(":",2);
				}else{
					searchCriteria[0] = "content";
					searchCriteria[1] = mailData.getSearch();
				}
				
				input.append("{");
				input.append("\"filter\":");
				input.append("[\"and\",");
				input.append("[\"" + mailData.getOperator() + "\",");
				input.append("{\"field\":" + "\"" + mailData.getField() + "\"},");
				input.append("\"" + mailData.getValue() + "\"");
				input.append("],");
				input.append("[\"=\",");
				input.append("{");
				input.append("\"field\":");
				input.append("\""+searchCriteria[0]+"\"");
				input.append("},");
				input.append("\""+searchCriteria[1]+"\"");
				input.append("]");
				input.append("]");
				input.append("}");
			}
			
			logger.info("OX Request Input for search mail "+input);

			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			
			logger.info("OX Response For Search Mail : "+response);

			if(Utility.isErrorResponse(response)){
				searchMail.setError(Utility.getError(response));
				logger.error("Error Occured while getting allmails response : " + Utility.getError(response));
				searchMailResponse.add(searchMail);
			    return searchMailResponse;
			}
			
			searchMailResponse = mailResponseParser.parseAllMail(response , mailData);
			
			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while deleting mail : " + e.getMessage());
			if (null == session)
				response = "{\"error\":\"User Not Logged In\"}";
		}
		return searchMailResponse;
	}

	public MailResponse getReplyMail(MailData mailData) throws Exception {
		String response = "";
		MailResponse mailResponse = new MailResponse();
		StringBuffer input = new StringBuffer();
		String folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		try {

			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
			input.append("action=reply");
			input.append("&session=");
			input.append(userDataBean.getUserDataMap().get(mailData.getAuth()).getSession());
			input.append("&folder=" + URLEncoder.encode(folder, "UTF-8"));
			input.append("&id=" + mailData.getId());

			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			JSONObject dataObject= new JSONObject(response);
			if(dataObject.has("data")){
				mailResponse = mailResponseParser.parseGetMail(response);
			}
			else if (dataObject.has("error")){
				mailResponse.setError(dataObject.get("error").toString());
			}
			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while deleting mail : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
				mailResponse.setError("User Not Logged In");
			}
			else{
				mailResponse.setError(e.getMessage());
			}
		}
		return mailResponse;

	}

	public MailResponse getReplyAllMail(MailData mailData) throws Exception {
		String response = "";
		MailResponse mailResponse = new MailResponse();
		StringBuffer input = new StringBuffer();
		String folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		try {

			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
			input.append("action=replyall");
			input.append("&session=");
			input.append(userDataBean.getUserDataMap().get(mailData.getAuth()).getSession());
			input.append("&folder=" + URLEncoder.encode(folder, "UTF-8"));
			input.append("&id=" + mailData.getId());

			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			

			conn.disconnect();
			
			JSONObject dataObject= new JSONObject(response);
			if(dataObject.has("data")){
				mailResponse = mailResponseParser.parseGetMail(response);
			}
			else if (dataObject.has("error")){
				mailResponse.setError(dataObject.get("error").toString());
			}
		} catch (Exception e) {
			logger.error("Exception Occured while deleting mail : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
				mailResponse.setError("User Not Logged In");
			}
			else{
				mailResponse.setError(e.getMessage());
			}
		}
		return mailResponse;

	}

	public MailResponse replyAllMail(MailData mailData) throws Exception {
		String key = hashEncoderDecoder.encode(16);
		MailResponse mailResponse = new MailResponse();
		String response = "";
		final String LINE_FEED = "\r\n";
		final String boundary = "------WebKitFormBoundary"+key;
		logger.info("Boundary : "+boundary);
		StringBuffer inputJson = new StringBuffer(boundary);
		inputJson.append(LINE_FEED);
		inputJson.append("Content-Disposition: form-data; name=\"json_0\"");
		inputJson.append(LINE_FEED);
		inputJson.append(LINE_FEED);
		ObjectMapper mapper = new ObjectMapper();
		logger.info("Calling getreplyallmail to get response of the mail to be replied");
		MailResponse getReplyAllMailResponse = getReplyAllMail(mailData);
		if(getReplyAllMailResponse.getError()!=null){
			 mailResponse.setError(getReplyAllMailResponse.getError());
			return mailResponse;
		}
		String jsonInString = mapper.writeValueAsString(getReplyAllMailResponse);
		logger.info("Got reply mail response of the mail to be replied");
		JSONObject responseObject = new JSONObject(jsonInString);	
		logger.info("Got getReplyallmail response of the mail to be replied");


		String jsonRequest = null;
		
		logger.info("Forming json request to replyallmail ");
		
	    
		
	    JSONArray attachments = null;
	    if(responseObject.has("attachments"))
	    attachments = responseObject.getJSONArray("attachments");
	    
	    JSONObject attachment = null;
	    int index = 0;
	    for(int attachmentIndex = 0 ; attachmentIndex < attachments.length() ; attachmentIndex++){
	    	attachment = (JSONObject) attachments.get(attachmentIndex);
	    	if(attachment.has("id") && attachment.get("id").equals("1")){
	    		index = attachmentIndex;
	    		break;
	    	}
	    }
	    StringBuffer contentBuffer = new StringBuffer();
	    String content = attachment.optString("content");
	    if(content.isEmpty()){
	    	content = "<p>"+ mailData.getMessage() +"</p>";
	    	logger.info("Writing Replyall Mail Content");
	    }
	    else{
	    	contentBuffer.append("<p> ");
	    	contentBuffer.append(mailData.getMessage());
	    	contentBuffer.append("</p>");
	    	logger.info("Writing Replyall Mail Content ");
	    }
	    contentBuffer.append(content);
	    
	    attachment.put("content", contentBuffer.toString());
	    attachments.put(index,attachment);
	    responseObject.put("attachments", attachments);
	    if(mailData.getTo() != null ){
		    JSONArray toArray = responseObject.getJSONArray("to");
		    JSONArray addToArray = new JSONArray();
		    for(int i = 0 ; i < toArray.length() ; i++){
		    	addToArray.put(i, toArray.get(i));
		    }
		    String [][] toStringArray = new String[mailData.getTo().length][2];
		    for(int i = toArray.length(); i < toArray.length() + mailData.getTo().length ; i++){
		    	toStringArray[i - toArray.length()][0] = mailData.getTo()[i - toArray.length()];
		    	toStringArray[i - toArray.length()][1] = mailData.getTo()[i - toArray.length()];
		    	addToArray.put(i, toStringArray[i - toArray.length()]);
		    }
		    
		    responseObject.put("to", addToArray);
		    }
	    
	    if(mailData.getCc() != null ){
	    JSONArray ccArray = responseObject.getJSONArray("cc");
	    JSONArray addCcArray = new JSONArray();
	    for(int i = 0 ; i < ccArray.length() ; i++){
	    	addCcArray.put(i, ccArray.get(i));
	    }
	    String [][] ccStringArray = new String[mailData.getCc().length][2];
	    for(int i = ccArray.length(); i < ccArray.length() + mailData.getCc().length ; i++){
	    	ccStringArray[i - ccArray.length()][0] = mailData.getCc()[i - ccArray.length()];
	    	ccStringArray[i - ccArray.length()][1] = mailData.getCc()[i - ccArray.length()];
	    	addCcArray.put(i, ccStringArray[i - ccArray.length()]);
	    }
	    
	    responseObject.put("cc", addCcArray);
	    }
	    jsonRequest = responseObject.toString();
	    
	    StringBuffer inputJsonBuffer = new StringBuffer(jsonRequest.toString());
	    inputJsonBuffer.append(LINE_FEED);
	    
	    inputJson.append(inputJsonBuffer);
	    inputJson.append(LINE_FEED);
	    inputJson.append(boundary);
	    inputJson.append("--");
	    
	    try{
	    String session = userDataBean.getUserDataMap().get(mailData.getAuth()).getSession();
	    String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL")+"?action=new&lineWrapAfter=0&session="+session;
	    HttpURLConnection conn = (HttpURLConnection) new URL(openExchangeMailUrl).openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary.substring(2));
		conn.setRequestProperty("Content-Disposition","form-data; name=\"json_0\"");
		conn.setRequestProperty("Host", "mail.teaming.orange-business.com");
		conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
		OutputStream os = conn.getOutputStream();
		os.write(inputJson.toString().getBytes());
		os.flush();

		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			logger.error("Response Code : "+conn.getResponseCode());
			throw new RuntimeException("Failed : HTTP error code : "
				+ conn.getResponseCode());
			
		}

		logger.info("Response Code : "+conn.getResponseCode());
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String output;
		
		while ((output = br.readLine()) != null) {
			response = response + output;
		}
	    
		conn.disconnect();
		
		String value = response.substring(response.indexOf("({\""),response.indexOf("\"})"));
		if(value.contains("data")){
			mailResponse.setSuccess(value.substring(10));
		}
		else if(value.contains("error")){
			mailResponse.setError(value.substring(11));
		}
	    }
	    
	    catch (Exception e) {
	    	 logger.error("Exception Occured while calling replyall mail : "+e.getMessage());
			  if(null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
			  mailResponse.setError("User Not Logged In");
			  }
			  else{
				  mailResponse.setError(e.getMessage());
			  }
		}
	
		return mailResponse;
	}

	public MailResponse getForwardMail(MailData mailData) throws Exception {
		String response = "";
		StringBuffer input = new StringBuffer();
		MailResponse mailResponse = new MailResponse();
		String folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		try {

			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
			input.append("action=forward");
			input.append("&session=");
			input.append(userDataBean.getUserDataMap().get(mailData.getAuth()).getSession());
			input.append("&folder=" + URLEncoder.encode(folder, "UTF-8"));
			input.append("&id=" + mailData.getId());

			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}

			conn.disconnect();
			
			JSONObject dataObject= new JSONObject(response);
			if(dataObject.has("data")){
				mailResponse = mailResponseParser.parseGetMail(response);
			}
			else if (dataObject.has("error")){
				mailResponse.setError(dataObject.get("error").toString());
			}
		} catch (Exception e) {
			logger.error("Exception Occured while deleting mail : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
				mailResponse.setError("User Not Logged In");
			}
			else{
				mailResponse.setError(e.getMessage());
			}
		}
		return mailResponse;
	}

	public MailResponse forwardMail(MailData mailData) throws Exception {
		String key = hashEncoderDecoder.encode(16);
		String response="";
		MailResponse mailResponse = new MailResponse();
		final String LINE_FEED = "\r\n";
		final String boundary = "------WebKitFormBoundary" + key;
		logger.info("Boundary : " + boundary);
		StringBuffer inputJson = new StringBuffer(boundary);
		inputJson.append(LINE_FEED);
		inputJson.append("Content-Disposition: form-data; name=\"json_0\"");
		inputJson.append(LINE_FEED);
		inputJson.append(LINE_FEED);

		logger.info("Calling get forward mail to get response of the mail to be forwarded");
		ObjectMapper mapper = new ObjectMapper();
		MailResponse getForwardMailResponse = getForwardMail(mailData);
		if(getForwardMailResponse.getError()!=null){
			 mailResponse.setError(getForwardMailResponse.getError());
			return mailResponse;
		}
		String jsonInString = mapper.writeValueAsString(getForwardMailResponse);
		JSONObject responseObject = new JSONObject(jsonInString);
		logger.info("Got forward mail response of the mail that has to be forwarded");

		String jsonRequest = null;
		int toLength = mailData.getTo().length;
		logger.info("Number of to mails " + toString());

		logger.info("Forming json request to forward mail ");
		String[][] toMails = new String[toLength][2];
		logger.info("Number of to mails " + toLength);
		for (int toIndex = 0; toIndex < toLength; toIndex++) {
			toMails[toIndex][0] = mailData.getTo()[toIndex];
			toMails[toIndex][1] = mailData.getTo()[toIndex];
			;
		}
		responseObject.put("to", toMails);
		int ccLength = 0;
		if (mailData.getCc() != null) {
			ccLength = mailData.getCc().length;
			logger.info("Number of cc mails " + ccLength);
		}

		String[][] ccMails = new String[ccLength][2];
		for (int ccIndex = 0; ccIndex < ccLength; ccIndex++) {
			ccMails[ccIndex][0] = mailData.getCc()[ccIndex];
			ccMails[ccIndex][1] = mailData.getCc()[ccIndex];
			;
		}

		responseObject.put("cc", ccMails);

		JSONArray attachments = null;
		if (responseObject.has("attachments"))
			attachments = responseObject.getJSONArray("attachments");

		JSONObject attachment = null;
		int index = 0;
		for (int attachmentIndex = 0; attachmentIndex < attachments.length(); attachmentIndex++) {
			attachment = (JSONObject) attachments.get(attachmentIndex);
			if (attachment.has("id") && attachment.get("id").equals("1")) {
				index = attachmentIndex;
				break;
			}
		}
		StringBuffer contentBuffer = new StringBuffer();
		String content = attachment.optString("content");
		if (content.isEmpty()) {
			content = "<p>" + mailData.getMessage() + "</p>";
			logger.info("Writing Mail Content ");
		} else {
			contentBuffer.append("<p> ");
			contentBuffer.append(mailData.getMessage());
			contentBuffer.append("</p>");
			logger.info("Writing Mail Content ");
		}
		contentBuffer.append(content);

		attachment.put("content", contentBuffer.toString());
		attachments.put(index, attachment);
		responseObject.put("attachments", attachments);
		jsonRequest = responseObject.toString();

		StringBuffer inputJsonBuffer = new StringBuffer(jsonRequest.toString());
		inputJsonBuffer.append(LINE_FEED);

		inputJson.append(inputJsonBuffer);
		inputJson.append(LINE_FEED);
		inputJson.append(boundary);
		inputJson.append("--");

		try {
			String session = userDataBean.getUserDataMap().get(mailData.getAuth()).getSession();
			String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL")
					+ "?action=new&lineWrapAfter=0&session=" + session;
			HttpURLConnection conn = (HttpURLConnection) new URL(openExchangeMailUrl).openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary.substring(2));
			conn.setRequestProperty("Content-Disposition", "form-data; name=\"json_0\"");
			conn.setRequestProperty("Host", "mail.teaming.orange-business.com");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
			OutputStream os = conn.getOutputStream();
			os.write(inputJson.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());

			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				response = response + output;
			}

			conn.disconnect();
			String value = response.substring(response.indexOf("({\""),response.indexOf("\"})"));
			if(value.contains("data")){
				mailResponse.setSuccess(value.substring(10));
			}
			else if(value.contains("error")){
				mailResponse.setError(value.substring(11));
			}
		}

		catch (Exception e) {
			logger.error("Exception Occured while forwarding mail : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
				response = "{\"error\":\"User Not Logged In\"}";
				mailResponse.setError("User Not Logged In");
			}
			else{
				mailResponse.setError(e.getMessage());
			}
		}

		return mailResponse;
	}

	public MailResponse replyMail(MailData mailData) throws Exception {
		MailResponse mailResponse = new MailResponse();
		String key = hashEncoderDecoder.encode(16);
		String response = "";
		final String LINE_FEED = "\r\n";
		final String boundary = "------WebKitFormBoundary" + key;
		logger.info("Boundary : " + boundary);
		StringBuffer inputJson = new StringBuffer(boundary);
		inputJson.append(LINE_FEED);
		inputJson.append("Content-Disposition: form-data; name=\"json_0\"");
		inputJson.append(LINE_FEED);
		inputJson.append(LINE_FEED);
		ObjectMapper mapper = new ObjectMapper();
	
		logger.info("Calling get reply mail to get response of the mail to be replied");
		
		MailResponse getReplyMailResponse = getReplyMail(mailData);
		if(getReplyMailResponse.getError()!=null){
			 mailResponse.setError(getReplyMailResponse.getError());
			 return mailResponse;
		}
		String jsonInString = mapper.writeValueAsString(getReplyMailResponse);
		logger.info("Got reply mail response of the mail to be replied");
		JSONObject responseObject = new JSONObject(jsonInString);
	
		String jsonRequest = null;

		logger.info("Forming json request to reply mail ");
		

		int ccLength = 0;
		if (mailData.getCc() != null) {
			ccLength = mailData.getCc().length;
			logger.info("Number of cc mails " + ccLength);
		}

		
		String[][] ccMails = new String[ccLength][2];
		for (int ccIndex = 0; ccIndex < ccLength; ccIndex++) {
			ccMails[ccIndex][0] = mailData.getCc()[ccIndex];
			ccMails[ccIndex][1] = mailData.getCc()[ccIndex];
			;
		}

		responseObject.put("cc", ccMails);

		JSONArray attachments = null;
		if (responseObject.has("attachments"))
			attachments = responseObject.getJSONArray("attachments");

		JSONObject attachment = null;
		int index = 0;
		for (int attachmentIndex = 0; attachmentIndex < attachments.length(); attachmentIndex++) {
			attachment = (JSONObject) attachments.get(attachmentIndex);
			if (attachment.has("id") && attachment.get("id").equals("1")) {
				index = attachmentIndex;
				break;
			}
		}
		StringBuffer contentBuffer = new StringBuffer();
		String content = attachment.optString("content");
		if (content.isEmpty()) {
			content = "<p>" + mailData.getMessage() + "</p>";
			logger.info("Writing Reply Mail Content");
		} else {
			contentBuffer.append("<p> ");
			contentBuffer.append(mailData.getMessage());
			contentBuffer.append("</p>");
			logger.info("Writing Reply Mail Content ");
		}
		contentBuffer.append(content);

		attachment.put("content", contentBuffer.toString());
		attachments.put(index, attachment);
		responseObject.put("attachments", attachments);
		jsonRequest = responseObject.toString();

		StringBuffer inputJsonBuffer = new StringBuffer(jsonRequest.toString());
		inputJsonBuffer.append(LINE_FEED);

		inputJson.append(inputJsonBuffer);
		inputJson.append(LINE_FEED);
		inputJson.append(boundary);
		inputJson.append("--");

		try {
			String session = userDataBean.getUserDataMap().get(mailData.getAuth()).getSession();
			String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL")
					+ "?action=new&lineWrapAfter=0&session=" + session;
			HttpURLConnection conn = (HttpURLConnection) new URL(openExchangeMailUrl).openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary.substring(2));
			conn.setRequestProperty("Content-Disposition", "form-data; name=\"json_0\"");
			conn.setRequestProperty("Host", "mail.teaming.orange-business.com");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
			OutputStream os = conn.getOutputStream();
			os.write(inputJson.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());

			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			String value = response.substring(response.indexOf("({\""),response.indexOf("\"})"));
			if(value.contains("data")){
				mailResponse.setSuccess(value.substring(10));
			}
			else if(value.contains("error")){
				mailResponse.setError(value.substring(11));
			}
			conn.disconnect();
		}

		catch (Exception e) {
			logger.error("Exception Occured while replying mail : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
				mailResponse.setError("User Not Logged In");
			}
			else{
				mailResponse.setError(e.getMessage());
			}
		}

		return mailResponse;
	}

	public MailResponse markReadUnread(MailData mailData) throws Exception {
		MailResponse mailResponse = new MailResponse();
		ObjectMapper mapper = new ObjectMapper();
		String response = "";
		StringBuffer input = new StringBuffer();
		String folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		String session = userDataBean.getUserDataMap().get(mailData.getAuth()).getSession();
		String markReadUnreadUrl = openExchangeMailUrl + "?action=update&session=" + session + "&folder=" + folder
				+ "&id=" + mailData.getId();
		try {

			URL url = new URL(markReadUnreadUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			String jsonData = null;
			if (mailData.getSeen().equalsIgnoreCase("true")) {
				jsonData = "\"set_flags\":\"32\"";
			} else {
				jsonData = "\"clear_flags\":\"32\"";
			}
			input.append("{");
			input.append(jsonData);
			input.append("}");
			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			
			if(Utility.isErrorResponse(response)){
				mailResponse.setError(Utility.getError(response));
				mapper.setSerializationInclusion(Include.NON_NULL);
				response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailResponse);
				logger.error("Error Occured while marking read unread : " + Utility.getError(response));
			    return mailResponse;
			}
			JSONObject markReadUnreadResponse = new JSONObject(response);
			if(markReadUnreadResponse.has("data")){
			mailResponse.setFolderId(markReadUnreadResponse.getJSONObject("data").get("folder_id").toString());
			mailResponse.setId(markReadUnreadResponse.getJSONObject("data").get("id").toString());
			}

			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while deleting mail : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
				mailResponse.setError("User Not Logged In");
			}
			else{
				mailResponse.setError(e.getMessage());
			}
		}
		return mailResponse;
	}

	/*
	 * public String getAllFolderMails(FolderData mailData) throws Exception {
	 * String response = ""; mailData.setModule("mail"); StringBuffer input =
	 * new StringBuffer(""); String openExchangeMailUrl =
	 * configManager.getPropertyAsString("OX_FOLDER_URL"); String columns =
	 * null; String unseen = "false"; try{
	 * 
	 * URL url = new URL(openExchangeMailUrl); HttpsURLConnection conn =
	 * (HttpsURLConnection) url.openConnection(); conn.setDoOutput(true);
	 * conn.setRequestMethod("GET"); conn.setRequestProperty("Content-Type",
	 * "application/x-www-form-urlencoded"); conn.setRequestProperty("Cookie",
	 * userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
	 * if(mailData.getColumns() == null){ columns =
	 * configManager.getPropertyAsString("FOLDER_COLUMNS"); }else{ columns =
	 * mailData.getColumns(); }
	 * 
	 * 
	 * getting folders list here
	 * 
	 * input.append("action=allVisible"); input.append("&columns=");
	 * input.append(columns); input.append("&session=");
	 * input.append(URLEncoder.encode(userDataBean.getUserDataMap().get(mailData
	 * .getAuth()).getSession(), "UTF-8")); input.append("&content_type=");
	 * input.append(mailData.getModule());
	 * 
	 * OutputStream os = conn.getOutputStream();
	 * os.write(input.toString().getBytes()); os.flush();
	 * 
	 * 
	 * if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	 * logger.error("Error Response Code : "+conn.getResponseCode()); throw new
	 * RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
	 * }
	 * 
	 * logger.error("Response Code : "+conn.getResponseCode()); BufferedReader
	 * br = new BufferedReader(new InputStreamReader( (conn.getInputStream())));
	 * 
	 * String output; while ((output = br.readLine()) != null) { response =
	 * response + output; }
	 * 
	 * conn.disconnect();
	 * 
	 * //response has all the list of folders , he we get the list of folder
	 * names
	 * 
	 * JSONObject dataObject= new JSONObject(response); JSONObject
	 * foldersObject= dataObject.getJSONObject("data"); JSONArray foldersArray=
	 * foldersObject.getJSONArray("private"); int
	 * foldersArrayLength=foldersArray.length();
	 * 
	 * 
	 * TreeSet<String> temporaryList= new TreeSet<String>(); TreeSet<String>
	 * finalFoldersList= new TreeSet<String>();
	 * 
	 * 
	 * for(int i=0;i<foldersArrayLength;i++) { JSONArray
	 * folderObject=foldersArray.getJSONArray(i); String
	 * val=folderObject.getString(1);
	 * if(!folderObject.getString(1).equals("system") &&
	 * !folderObject.getString(0).equals("Spam") &&
	 * !folderObject.getString(0).equals("Trash")&&
	 * folderObject.get(1).equals("mail")) {
	 * 
	 * if(Integer.parseInt(folderObject.get(8).toString())>0)
	 * finalFoldersList.add(folderObject.get(0).toString()); // add the folder
	 * name to final list only if it has mails
	 * 
	 * }
	 * 
	 * }
	 * 
	 * temporaryList=getAllFolderPaths(mailData, finalFoldersList);
	 * finalFoldersList.addAll(temporaryList);
	 * 
	 * JSONArray data= new JSONArray(); JSONArray paginatedData=new JSONArray();
	 * 
	 * for(String name:finalFoldersList) { MailData allMails= new MailData();
	 * allMails.setAuth(mailData.getAuth()); if(name.equalsIgnoreCase("inbox"))
	 * name="INBOX"; allMails.setFolder(name); allMails.setUnseen("false");
	 * String mails =getAllMails(allMails); if(mails.contains("error"))
	 * continue; JSONObject dataArray= new JSONObject(mails); JSONArray
	 * allMailsArray= dataArray.getJSONArray("data"); for(int
	 * j=0;j<allMailsArray.length();j++) { data.put(allMailsArray.get(j)); } }
	 * 
	 * 
	 * 
	 * String allMailsList="{\"data\":"+ data.toString()+ "}";
	 * 
	 * //pagination done here if(mailData.getLeftHandLimit()!=null &&
	 * mailData.getLeftHandLimit()!="0" && mailData.getRightHandLimit()!=null &&
	 * mailData.getRightHandLimit()!="0" &&
	 * Integer.parseInt(mailData.getLeftHandLimit())<=Integer.parseInt(mailData.
	 * getRightHandLimit())) {
	 * 
	 * JSONObject paginatedObject= new JSONObject(allMailsList); JSONArray
	 * paginatedMailsArray= paginatedObject.getJSONArray("data"); for(int
	 * i=Integer.parseInt(mailData.getLeftHandLimit());i<=Integer.parseInt(
	 * mailData.getRightHandLimit());i++) { if(i<=paginatedMailsArray.length())
	 * paginatedData.put(paginatedMailsArray.get(i)); else break;
	 * 
	 * } if(paginatedData.length()>0) allMailsList="{\"data\":" +
	 * paginatedData.toString() +"}"; }
	 * 
	 * response=allMailsList; } catch (Exception e) { logger.
	 * error("Exception Occured while getting all mails from all folders : "+e.
	 * getMessage()); if(null ==
	 * userDataBean.getUserDataMap().get(mailData.getAuth()).getSession())
	 * response = "{\"error\":\"User Not Logged In\"}"; }
	 * 
	 * return response; }
	 */

	/**
	 * @param mailData
	 * @param finalFoldersList
	 * @throws Exception
	 */
	private TreeSet<String> getAllFolderPaths(FolderData mailData, TreeSet<String> finalFoldersList) throws Exception {
		TreeSet<String> temporaryList;
		temporaryList = finalFoldersList;

		TreeSet<String> subFoldersList = new TreeSet<String>();
		TreeSet<String> equalityCheckList = new TreeSet<String>();
		String path = null;
		// get paths here
		do {

			for (String subFolder : temporaryList) {

				if (subFolder.equalsIgnoreCase("inbox"))
					subFolder = "INBOX";

				if (subFolder.contains("default0"))
					path = subFolder;
				else
					path = "default0/" + subFolder; // we have to specify the
													// root folder default0 here
													// to fetch , else it doesnt
													// work

				FolderData getMailData = new FolderData();
				getMailData.setAuth(mailData.getAuth());
				getMailData.setTitle(path); // we have to specify the root
											// folder default0 here to fetch ,
											// else it doesnt work
				getMailData.setModule(mailData.getModule());
				String responseValue = folderAdapter.getSubFolders(getMailData);
				if (responseValue.contains("error")) {

					continue;
				}

				JSONObject dataValue = new JSONObject(responseValue);
				JSONArray subFoldersArray = dataValue.getJSONArray("data");
				int subFoldersLength = subFoldersArray.length();

				for (int k = 0; k < subFoldersLength; k++) {
					JSONArray subFolderObject = subFoldersArray.getJSONArray(k);
					String x = subFolderObject.get(8).toString();
					if (!(subFolderObject.get(8).toString()).equals("\"0\"")
							|| Integer.parseInt(subFolderObject.get(8).toString()) != 0) // 0
																							// case
						subFoldersList.add(path + "/" + subFolderObject.get(0).toString());

				}

			}

			equalityCheckList.addAll(temporaryList); // old temporary list
			if (subFoldersList.size() > 0)
				temporaryList.addAll(subFoldersList);

		} while (!compareTwoList(temporaryList, equalityCheckList));
		return temporaryList;
	}

	boolean compareTwoList(TreeSet<String> listOne, TreeSet<String> listTwo) {
		boolean flag = false;
		Iterator<String> itr = listOne.iterator();
		while (itr.hasNext()) {
			if (listTwo.contains(itr.next())) {
				flag = true;
				continue;
			} else {
				flag = false;
				break;
			}
		}
		return flag;
	}

	public ArrayList<AllMailResponse> getArchiveMails(FolderData mailData) throws Exception
	{
		String response = "";
		mailData.setModule("mail");	
		TreeSet<String>subFoldersList= new TreeSet<String>();
		ArrayList<AllMailResponse> listOfResponse = new ArrayList<>();
		try{
			 // getting  Archive folder Subfolders list here	
			/*mailData.setTitle("default0/"+ mailData.getTitle());
			
			String responseValue=folderAdapter.getSubFolders(mailData);
			JSONObject dataValue= new JSONObject(responseValue);
			JSONArray subFoldersArray= dataValue.getJSONArray("data");
			int subFoldersLength=subFoldersArray.length();
			
			for(int k=0;k<subFoldersLength;k++)
			{
				JSONArray subFolderObject=subFoldersArray.getJSONArray(k);
				String x=subFolderObject.get(8).toString();
				if(!(subFolderObject.get(8).toString()).equals("\"0\""))
					subFoldersList.add(mailData.getTitle()+"/"+subFolderObject.get(0).toString());		
				
			}
			
			TreeSet<String>temporaryList= new TreeSet<String>();
			
			if(subFoldersList.size() > 0){
			temporaryList=getAllFolderPaths(mailData, subFoldersList);
			subFoldersList.addAll(temporaryList);
			}*/
			
			
			//adding Archive folder
			String archiveFolder = mailData.getTitle();
			subFoldersList.add(archiveFolder); //even if the root Archive is empty it is handled
			
		
			
			ArrayList<AllMailResponse> paginatedResponse = new ArrayList<>();
			
			for(String name:subFoldersList)
			{
				MailData allMails= new MailData();
				allMails.setAuth(mailData.getAuth());
				if(name.equalsIgnoreCase("inbox")) name="INBOX";
				allMails.setFolder(name);
				allMails.setUnseen("false");
				ArrayList<AllMailResponse> responseFromGetMails = getAllMails(allMails);
				if(responseFromGetMails.size() == 1){
					AllMailResponse allMailResponse = responseFromGetMails.get(0);
					if (allMailResponse.getError() != null){
					return responseFromGetMails;
					}
				}
				if(!(responseFromGetMails.isEmpty())){
					listOfResponse.addAll(responseFromGetMails);
				}
			}
			 //pagination done here 
			if(mailData.getLeftHandLimit()!=null &&  Integer.parseInt(mailData.getLeftHandLimit())>=0 && mailData.getRightHandLimit()!=null && !mailData.getRightHandLimit().equals("0") && Integer.parseInt(mailData.getLeftHandLimit())<Integer.parseInt(mailData.getRightHandLimit()))
			 {		
					for(int i=Integer.parseInt(mailData.getLeftHandLimit());i<Integer.parseInt(mailData.getRightHandLimit()) ;i++)
					{
						if(i<listOfResponse.size())
							paginatedResponse.add(listOfResponse.get(i));	
						else 
							break;	
					} 
					listOfResponse = paginatedResponse;
			 }
			
			
			
			
		}
		catch (Exception e) {
			 logger.error("Exception Occured while getting Archive folders mails: "+e.getMessage());
			  if(null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession())
			  response = "{\"error\":\"User Not Logged In\"}";
		 }
		return listOfResponse;
	}

	// update API
	public MailResponse updateMail(UpdateMail mailData) throws Exception {
		String response = "";
		MailResponse mailResponse = new MailResponse();
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		String folder = mailData.getFolder() == null ? "default0/INBOX" : mailData.getFolder();
		String session = userDataBean.getUserDataMap().get(mailData.getAuth()).getSession();
		String updateUrl = openExchangeMailUrl + "?action=update&session=" + session + "&folder=" + folder + "&id="
				+ mailData.getId();
		try {

			URL url = new URL(updateUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", " application/json");
			conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

			StringBuilder jsonData = new StringBuilder();
			jsonData.append("{");
			if(!mailData.isFlagged())
			{
				jsonData.append("\"color_label\":\"" + Integer.valueOf("0") + "\"");
			}
			else {
				if (mailData.getColor_label() != null && Integer.valueOf(mailData.getColor_label())>0) {
				mailResponse.setFlagged(true);
				jsonData.append("\"color_label\":\"" + mailData.getColor_label() + "\"");
			}
				else {
					mailResponse.setFlagged(true);
					jsonData.append("\"color_label\":\"" + Integer.valueOf("1") + "\"");   //red color
				}
			}
			if (mailData.getDestinationFolder() != null) {
				jsonData.append(",");
				jsonData.append("\"folder_id\":\"" + mailData.getDestinationFolder() + "\"");
			}
			if (mailData.getFlags() != null) {
				jsonData.append(",");
				jsonData.append("\"flags\":\"" + mailData.getFlags() + "\"");
			}
			if (mailData.getValue() != null) {
				jsonData.append(",");
				jsonData.append("\"value\":\"" + mailData.getValue() + "\"");
			}
			if (mailData.getClear_flags() != null) {
				jsonData.append(",");
				jsonData.append("\"clear_flags\":\"" + mailData.getClear_flags() + "\"");
			}
			if (mailData.getSet_flags() != null) {
				jsonData.append(",");
				jsonData.append("\"set_flags\":\"" + mailData.getSet_flags() + "\"");
			}
			jsonData.append("}");

			OutputStream os = conn.getOutputStream();
			os.write(jsonData.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			JSONObject updateMailJsonObject = new JSONObject(response);
			if(updateMailJsonObject.has("data")){
				JSONObject updateResponseJsonObject = new JSONObject(updateMailJsonObject.get("data").toString());
				mailResponse.setFolderId(updateResponseJsonObject.get("folder_id").toString());
				mailResponse.setId(updateResponseJsonObject.get("id").toString());
			}
			else if(updateMailJsonObject.has("error")) {
				mailResponse.setError(updateMailJsonObject.get("error").toString());
			}

			conn.disconnect();

		} catch (Exception e) {
			logger.error("Exception Occured while updating mails : " + e.getMessage());
			if (null == userDataBean.getUserDataMap().get(mailData.getAuth()).getSession()){
				mailResponse.setError("User Not Logged In");
			}
			else{
				mailResponse.setError(e.getMessage());
			}
				
				
		}
		return mailResponse;
	}

	// update invite API from mail
	public InviteResponse updateInvite(UserParticipant mailData) throws Exception {
		String response = "";
		InviteResponseParser inviteResponseParser= new InviteResponseParser();
		InviteResponse inviteResponse= new InviteResponse();
		String session = userDataBean.getUserDataMap().get(mailData.getAuth()).getSession();
		String openExchangeMailUrl = new String();
		
		try {
			
			if((mailData.getSequenceId() == null || mailData.getSequenceId().isEmpty()) || null == mailData.getSequenceId())  //OX to OX invites
			{
				logger.info("Sequence id is : "+mailData.getSequenceId());
				String folderId=mailData.getFolderId();
				String objectId=mailData.getId();
				if(null != mailData.getTimestamp())
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/calendar?action=confirm&folder=26995&id="+objectId+"&session="+session+"&timestamp="+mailData.getTimestamp()+"&timezone=UTC";
				else
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/calendar?action=confirm&folder=26995&id="+objectId+"&session="+session+"&timezone=UTC";	
				logger.info("URL : "+openExchangeMailUrl);
				
				URL url = new URL(openExchangeMailUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Content-Type", " application/json");
				conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
				StringBuilder jsonData = new StringBuilder();
				jsonData.append("{");
				if (mailData.getConfirmation() != null) {
					jsonData.append("\"confirmation\":\"" + mailData.getConfirmation() + "\"");
				}
				if (mailData.getConfirmationMessage()!= null) {
					jsonData.append(",");
					jsonData.append("\"confirmmessage\":\"" + mailData.getConfirmationMessage() + "\"");
				}
				
				jsonData.append("}");
				OutputStream os = conn.getOutputStream();
				os.write(jsonData.toString().getBytes());
				os.flush();
				logger.info("Json date : "+jsonData.toString());
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				logger.info("Response Code : " + conn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				while ((output = br.readLine()) != null) {
					response = response + output;
				}

				conn.disconnect();
				
				//deleting from mailbox
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/mail?action=delete&harddelete=false&returnAffectedFolders=true&session="
						+ session;
				URL deleteUrl = new URL(openExchangeMailUrl);
				HttpsURLConnection connection = (HttpsURLConnection) deleteUrl.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", " application/json");
				connection.setRequestProperty("Cookie",
						userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
				StringBuilder json = new StringBuilder();
				json.append("[");
				json.append("{");
				if (mailData.getObjectId() != null) {
					json.append("\"id\":\"" + mailData.getObjectId() + "\"");
				}
				if (mailData.getFolderId() != null) {
					json.append(",");
					json.append("\"folder\":\"26995"+"\"");
				}
				json.append("}");
				json.append("]");

				logger.info("Json data to delete mail : "+jsonData.toString());
				OutputStream op = connection.getOutputStream();
				op.write(json.toString().getBytes());
				op.flush();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + connection.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
				}

				logger.info("Response Code : " + connection.getResponseCode());
					
			}
			
			else {
			if (Integer.parseInt(mailData.getConfirmation()) == 1) // accept
																	// case
			{
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/calendar/itip?action=accept&dataSource=com.openexchange.mail.ical&descriptionFormat=html&message=&session="
						+ session;
				URL url = new URL(openExchangeMailUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Content-Type", " application/json");
				conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

				StringBuilder jsonData = new StringBuilder();
				jsonData.append("{");
				if (mailData.getFolderId() != null) {
					jsonData.append("\"com.openexchange.mail.conversion.fullname\":\"" + "26995" + "\"");
				}
				if (mailData.getObjectId() != null) {
					jsonData.append(",");
					jsonData.append("\"com.openexchange.mail.conversion.mailid\":\"" + mailData.getObjectId() + "\"");
					jsonData.append(",");
				}
				jsonData.append("\"com.openexchange.mail.conversion.sequenceid\":\"" + mailData.getSequenceId() + "\"");
				jsonData.append("}");

				OutputStream os = conn.getOutputStream();
				os.write(jsonData.toString().getBytes());
				os.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				logger.info("Response Code : " + conn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				while ((output = br.readLine()) != null) {
					response = response + output;
				}

				conn.disconnect();

				// deleting it from mailbox
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/mail?action=delete&harddelete=false&returnAffectedFolders=true&session="
						+ session;
				URL deleteUrl = new URL(openExchangeMailUrl);
				HttpsURLConnection connection = (HttpsURLConnection) deleteUrl.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", " application/json");
				connection.setRequestProperty("Cookie",
						userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
				StringBuilder json = new StringBuilder();
				json.append("[");
				json.append("{");
				if (mailData.getObjectId() != null) {
					json.append("\"id\":\"" + mailData.getObjectId() + "\"");
				}
				if (mailData.getFolderId() != null) {
					json.append(",");
					json.append("\"folder\":\"" + "26995" + "\"");
				}
				json.append("}");
				json.append("]");

				OutputStream op = connection.getOutputStream();
				op.write(json.toString().getBytes());
				op.flush();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + connection.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
				}

				logger.info("Response Code : " + connection.getResponseCode());

			} else if (Integer.parseInt(mailData.getConfirmation()) == 2) // decline
																			// case
			{
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/calendar/itip?action=decline&dataSource=com.openexchange.mail.ical&descriptionFormat=html&message=&session="
						+ session;
				URL url = new URL(openExchangeMailUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Content-Type", " application/json");
				conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

				StringBuilder jsonData = new StringBuilder();
				jsonData.append("{");
				if (mailData.getFolderId() != null) {
					jsonData.append("\"com.openexchange.mail.conversion.fullname\":\"" + "26995" + "\"");
				}
				if (mailData.getObjectId() != null) {
					jsonData.append(",");
					jsonData.append("\"com.openexchange.mail.conversion.mailid\":\"" + mailData.getObjectId() + "\"");
					jsonData.append(",");
				}
				jsonData.append("\"com.openexchange.mail.conversion.sequenceid\":\"" + mailData.getSequenceId() + "\"");
				jsonData.append("}");

				OutputStream os = conn.getOutputStream();
				os.write(jsonData.toString().getBytes());
				os.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				logger.info("Response Code : " + conn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				while ((output = br.readLine()) != null) {
					response = response + output;
				}

				conn.disconnect();

				// deleting it from mailbox
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/mail?action=delete&harddelete=false&returnAffectedFolders=true&session="
						+ session;
				URL deleteUrl = new URL(openExchangeMailUrl);
				HttpsURLConnection connection = (HttpsURLConnection) deleteUrl.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", " application/json");
				connection.setRequestProperty("Cookie",
						userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
				StringBuilder json = new StringBuilder();
				json.append("[");
				json.append("{");
				if (mailData.getObjectId() != null) {
					json.append("\"id\":\"" + mailData.getObjectId() + "\"");
				}
				if (mailData.getFolderId() != null) {
					json.append(",");
					json.append("\"folder\":\"" + "26995" + "\"");
				}
				json.append("}");
				json.append("]");

				OutputStream op = connection.getOutputStream();
				op.write(json.toString().getBytes());
				op.flush();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + connection.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
				}

				logger.info("Response Code : " + connection.getResponseCode());

			} else if (Integer.parseInt(mailData.getConfirmation()) == 3) // tentative
																			// case
			{
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/calendar/itip?action=tentative&dataSource=com.openexchange.mail.ical&descriptionFormat=html&message=&session="
						+ session;
				URL url = new URL(openExchangeMailUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Content-Type", " application/json");
				conn.setRequestProperty("Cookie", userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());

				StringBuilder jsonData = new StringBuilder();
				jsonData.append("{");
				if (mailData.getFolderId() != null) {
					jsonData.append("\"com.openexchange.mail.conversion.fullname\":\"" + "26995" + "\"");
				}
				if (mailData.getObjectId() != null) {
					jsonData.append(",");
					jsonData.append("\"com.openexchange.mail.conversion.mailid\":\"" + mailData.getObjectId() + "\"");
					jsonData.append(",");
				}
				jsonData.append("\"com.openexchange.mail.conversion.sequenceid\":\"" + mailData.getSequenceId() + "\"");
				jsonData.append("}");

				OutputStream os = conn.getOutputStream();
				os.write(jsonData.toString().getBytes());
				os.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				logger.info("Response Code : " + conn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output;
				while ((output = br.readLine()) != null) {
					response = response + output;
				}

				conn.disconnect();

				// deleting it from mailbox
				openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/mail?action=delete&harddelete=false&returnAffectedFolders=true&session="
						+ session;
				URL deleteUrl = new URL(openExchangeMailUrl);
				HttpsURLConnection connection = (HttpsURLConnection) deleteUrl.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", " application/json");
				connection.setRequestProperty("Cookie",
						userDataBean.getUserDataMap().get(mailData.getAuth()).getCookie());
				StringBuilder json = new StringBuilder();
				json.append("[");
				json.append("{");
				if (mailData.getObjectId() != null) {
					json.append("\"id\":\"" + mailData.getObjectId() + "\"");
				}
				if (mailData.getFolderId() != null) {
					json.append(",");
					json.append("\"folder\":\"" + "26995" + "\"");
				}
				json.append("}");
				json.append("]");

				OutputStream op = connection.getOutputStream();
				op.write(json.toString().getBytes());
				op.flush();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + connection.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
				}

				logger.info("Response Code : " + connection.getResponseCode());

			}
			JSONObject updateResponseJson = new JSONObject(response);
			if(updateResponseJson.has("data")){
				inviteResponse = inviteResponseParser.parseInvite(response);
			}
			else if(updateResponseJson.has("error")){
				inviteResponse.setError(updateResponseJson.get("error").toString());
			}
		} }catch (Exception e) {
			logger.error("Exception Occured while updating Invites : " + e.getMessage());
		}
		return inviteResponse;
	}

	public ArrayList<AllMailResponse> getContextual(MailData mailData) throws Exception {

		ArrayList<AllMailResponse> responseList = new ArrayList<AllMailResponse>();
		MailData mailDataTo = new MailData();
		mailDataTo.setAuth(mailData.getAuth());
		mailDataTo.setField("to");
		mailDataTo.setValue(mailData.getValue());
		mailDataTo.setFolder("default0/Sent");
		mailDataTo.setOperator("=");
		mailDataTo.setSearch(mailData.getSearch());

		int mailListSize = 0;
		ArrayList<AllMailResponse> toList = searchMail(mailDataTo);
		if(toList.size() == 1 && toList.get(0).getError() != null ){
			return toList;
		}
		responseList.addAll(toList);
		for(int mailIndex = mailListSize ; mailIndex < responseList.size() ; mailIndex++){
			responseList.get(mailIndex).setMailType("Sent in to");
		}
		mailListSize = responseList.size();
		
		MailData mailDataToIncc = new MailData();
		mailDataToIncc.setAuth(mailData.getAuth());
		mailDataToIncc.setField("cc");
		mailDataToIncc.setValue(mailData.getValue());
		mailDataToIncc.setFolder("default0/Sent");
		mailDataToIncc.setOperator("=");
		mailDataToIncc.setSearch(mailData.getSearch());

		ArrayList<AllMailResponse> ccList = searchMail(mailDataToIncc);
		if(ccList.size() == 1 && ccList.get(0).getError() != null ){
			return ccList;
		}
		responseList.addAll(ccList);
		for(int mailIndex = mailListSize ; mailIndex < responseList.size() ; mailIndex++){
			responseList.get(mailIndex).setMailType("Sent in cc");
		}
		mailListSize = responseList.size();
		
		String user = userDataBean.getUserDataMap().get(mailData.getAuth()).getUsername();
		MailData mailDataFrom = new MailData();
		mailDataFrom.setAuth(mailData.getAuth());
		mailDataFrom.setField("from");
		mailDataFrom.setValue(mailData.getValue());
		mailDataFrom.setOperator("=");
		mailDataFrom.setSearch(mailData.getSearch());
		
		ArrayList<AllMailResponse> fromList = searchMail(mailDataFrom);
		if(fromList.size() == 1 && fromList.get(0).getError() != null ){
			return fromList;
		}
		responseList.addAll(fromList);
		for(int mailIndex = mailListSize ; mailIndex < responseList.size() ; mailIndex++){
			if(responseList.get(mailIndex).getTo() != null && responseList.get(mailIndex).getTo().length > 0){
				String[][] to = responseList.get(mailIndex).getTo();
				for(int toUserIndex = 0 ; toUserIndex < to.length ; toUserIndex++)
				if (to[toUserIndex][1].contains(user) ) {	
					responseList.get(mailIndex).setMailType("Received in to");
					break;
				}
				
			}if(responseList.get(mailIndex).getCc() != null && responseList.get(mailIndex).getCc().length > 0){
				String[][] cc = responseList.get(mailIndex).getCc();
				for(int ccUserIndex = 0 ; ccUserIndex < cc.length ; ccUserIndex++)
					if (cc[ccUserIndex][1].contains(user) ) {	
						responseList.get(mailIndex).setMailType("Received in cc");
						break;
					}
				}
			
			}
		
		Collections.sort(responseList);
		
		if (mailData.getOnlyAttachments() != null && mailData.getOnlyAttachments() == true) {
			ArrayList<AllMailResponse> temporaryList = new ArrayList<AllMailResponse>();
			for (int i = 0; i < responseList.size(); i++) {
				AllMailResponse responseBean = responseList.get(i);
				if (responseBean.getIsAttachment() == true)
					temporaryList.add(responseList.get(i));
			}
			responseList = temporaryList;
		}
		
		if (mailData.getLeftHandLimit() != null && Integer.parseInt(mailData.getLeftHandLimit()) >= 0
				&& mailData.getRightHandLimit() != null
				&& Integer.parseInt(mailData.getRightHandLimit()) > Integer.parseInt(mailData.getLeftHandLimit())
				&& Integer.parseInt(mailData.getRightHandLimit()) <= responseList.size()
				&& Integer.parseInt(mailData.getLeftHandLimit()) <= responseList.size()) {
			
			ArrayList<AllMailResponse> temporaryList = new ArrayList<AllMailResponse>();
			for (int i = Integer.parseInt(mailData.getLeftHandLimit()); i < Integer
					.parseInt(mailData.getRightHandLimit()); i++) {
				temporaryList.add(responseList.get(i));
			}
			responseList = temporaryList;
		} else if (mailData.getLeftHandLimit() != null && Integer.parseInt(mailData.getLeftHandLimit()) >= 0
				&& mailData.getRightHandLimit() != null
				&& Integer.parseInt(mailData.getRightHandLimit()) > Integer.parseInt(mailData.getLeftHandLimit())
				&& Integer.parseInt(mailData.getRightHandLimit()) > responseList.size()
				&& Integer.parseInt(mailData.getLeftHandLimit()) <= responseList.size()) {
			ArrayList<AllMailResponse> temporaryList = new ArrayList<AllMailResponse>();
			for (int i = Integer.parseInt(mailData.getLeftHandLimit()); i < responseList.size(); i++) {
				temporaryList.add(responseList.get(i));
			}
			responseList = temporaryList;
		}

		else if (mailData.getLeftHandLimit() != null && Integer.parseInt(mailData.getLeftHandLimit()) >= 0
				&& mailData.getRightHandLimit() != null
				&& Integer.parseInt(mailData.getRightHandLimit()) > Integer.parseInt(mailData.getLeftHandLimit())
				&& Integer.parseInt(mailData.getRightHandLimit()) > responseList.size()
				&& Integer.parseInt(mailData.getLeftHandLimit()) > responseList.size()) {

			responseList.clear();
		}
		
		
		
		
		return responseList;

	}

	public Attachment downloadAttachment(Attachment attachmentRequest) throws Exception {
		Attachment attachment = new Attachment();
		String input = null;
		String user = String.valueOf(userDataBean.getUserDataMap().get(attachmentRequest.getAuth()).getUserId());
		String context = String.valueOf(userDataBean.getUserDataMap().get(attachmentRequest.getAuth()).getContextId());
		;
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_MAIL_URL");
		String attachmentUrl = openExchangeMailUrl + "/" + attachmentRequest.getFileName()
				+ "?action=attachment&folder=" + attachmentRequest.getFolder() + "&id=" + attachmentRequest.getMailId()
				+ "&attachment=" + attachmentRequest.getId() + "&user=" + user + "&context=" + context
				+ "&delivery=download&callback=yell";

		try {
			URL url = new URL(attachmentUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Cookie",
					userDataBean.getUserDataMap().get(attachmentRequest.getAuth()).getCookie());
			OutputStream os = conn.getOutputStream();
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			InputStream ip = conn.getInputStream();
			byte[] buffer = IOUtils.toByteArray(ip);
			logger.info("Response Code : " + conn.getResponseCode());
			attachment.setFileName(attachmentRequest.getFileName());
			attachment.setFileContent(buffer);

			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while user logging out : " + e.getMessage());
			if (null == input) {
				logger.error("{\"error\":\"User Not Logged In\"}");
			}
		}
		return attachment;
	}
	
	
}
