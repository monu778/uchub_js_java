package com.broadsoft.demohub.api.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.broadsoft.demohub.api.beans.UserLoginData;
import com.broadsoft.demohub.api.beans.UserParticipant;
import com.broadsoft.demohub.api.config.ConfigManager;
import com.broadsoft.demohub.api.manager.MailManagerIF;
import com.broadsoft.demohub.api.response.manager.ResponseBuilder;
import com.broadsoft.demohub.api.response.manager.ResponseObjectManager;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Path("/mailservice")
public class MailService {

	private Logger logger = Logger.getLogger(LoginService.class);

	@Autowired
	MailManagerIF mailManager;
	@Autowired
	UserDataBean userDataBean;
	@Autowired
	ResponseBuilder responseBuilder;
	@Autowired
	ResponseObjectManager responseObjectManager;
	@Autowired
	ConfigManager configManager;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCount")
	public Response getCount(MailData mailData, @Context HttpServletRequest httpRequest) {
		BaseResponse baseResponse = new BaseResponse();
		MailData response = new MailData();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for get mail count :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.getCount(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}
			else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while getting mail count " + e.getMessage());
			if (null == userLoginData) {
		    	response.setError("User Not Logged In");
			}
			else{
			response.setError(e.getMessage());
			}
		}
		logger.info("mail count Response " + response);
		return responseBuilder.buildResponse(response);

	}
	
	@OPTIONS
	@Path("/allmails")
	public Response getAllMails() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/allmails")
	public Response getAllMails(MailData mailData, @Context HttpServletRequest httpRequest) {
		BaseResponse baseResponse = new BaseResponse();
		ArrayList<AllMailResponse> response = new ArrayList<AllMailResponse>();
		AllMailResponse allMailResponse = new AllMailResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for get all mails :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));
			logger.info("mailData.getAuth()==" + mailData.getAuth());
			logger.info("userDataBean.getUserDataMap()===" + userDataBean.getUserDataMap());
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.getAllMails(mailData);

			allMailResponse = response.get(0);
			if (allMailResponse.getError() != null && allMailResponse.getError().contains("Archive") 
					&& mailData.getFolder() != null && mailData.getFolder().equalsIgnoreCase("default0/Archive")){
				mailData.setFolder("default0/Archives");
				response = mailManager.getAllMails(mailData);
			}
			if(response.size() == 1){
				 allMailResponse = response.get(0);
				if (allMailResponse.getError() != null){
					logger.info("get allmails Error Response " + allMailResponse.getError());
					baseResponse.setError(allMailResponse.getError());
				}else{
					baseResponse.setData(response);
				}
			}
			else{
				baseResponse.setData(response);
			}

		} catch (Exception e) {
			logger.error("Exception Occured while getting all mails " + e.getMessage());
			if (null == userLoginData) {
				baseResponse.setError("User Not Logged In");
				logger.info("get allmails error Response " + response);
			
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("get allmails Response " + response);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getmail")
	public Response getMail(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling get mail with " + mailData.getAuth());
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		MailResponse response = new MailResponse();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for get mail :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.getMail(mailData);
			mapper.setSerializationInclusion(Include.NON_NULL);
			if(response.getError() != null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while getting all mails " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			
			}
			else{
				baseResponse.setError(e.getMessage());
			}
			
		}
		logger.info("get mail Response " + response);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/deletemail")
	public Response deleteMail(MailDelete mailData, @Context HttpServletRequest httpRequest) {
		BaseResponse response = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for get mail count :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());
			response = mailManager.deleteMail(mailData);
		} catch (Exception e) {
			logger.error("Exception Occured while deleting mail  " + e.getMessage());
			if (null == userLoginData){
				response.setError("User Not Logged In");
			}else{
				response.setError(e.getMessage());
			}
		}
		logger.info("mail count Response " + response);
		return responseBuilder.buildResponse(response);
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/getmailattachment")
	public Response getMailAttachment(MailDelete mailData, @Context HttpServletRequest httpRequest) {
		String response = null;
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for get mail attachment:"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());
			response = mailManager.getMailAttachment(mailData);
		} catch (Exception e) {
			logger.error("Exception Occured while getting mail attachment" + e.getMessage());
			if (null == userLoginData)
				response = "{\"error\":\"User Not Logged In\"}";
		}
		logger.info("mail count Response " + response);
		return responseBuilder.buildResponse(response);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchmails")
	public Response searchMail(MailData mailData, @Context HttpServletRequest httpRequest) {
		BaseResponse baseResponse = new BaseResponse();
		ArrayList<AllMailResponse> response = new ArrayList<AllMailResponse>();
		String responseData = null;
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for searchMail :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("session:" + mailData.getSession());
			response = mailManager.searchMail(mailData);
			if(response.size() == 1){
				AllMailResponse allMailResponse = response.get(0);
				if (allMailResponse.getError() != null){
					baseResponse.setError(allMailResponse.getError());
					logger.info("get allmails Error Response " + allMailResponse.getError());
					
				}
			}
			else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while getting Search Mail Response : " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
				logger.error("Error response : " + baseResponse.getError());
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("Search Mail Response " + responseData);
		return responseBuilder.buildResponse(baseResponse);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getreplymail")
	public Response getReplyMail(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling get reply mail with " + mailData.getAuth());
		BaseResponse baseResponse = new BaseResponse();
		MailResponse response = new MailResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for get reply mail :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.getReplyMail(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		
		} catch (Exception e) {
			logger.error("Exception Occured while getting reply mail " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("get reply mail Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/replymail")
	public Response replyMail(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling reply mail with " + mailData.getAuth());
		MailResponse response = new MailResponse();
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for reply mail :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.replyMail(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while replying mail " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");	
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("reply mail Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getreplyallmail")
	public Response getReplyAllMail(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling getreplyall mail with " + mailData.getAuth());
		MailResponse response = new MailResponse();
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for getreplyall mail :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.getReplyAllMail(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while calling getreplyallmail " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
				
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("replyall Response " + response);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/replyallmail")
	public Response replyAllMail(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling replyall mail with " + mailData.getAuth());
		MailResponse response = new MailResponse();
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for replyall mail :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.replyAllMail(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while calling replyall mail " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("replyall Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getforwardmail")
	public Response getForwardMail(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling get forward mail with " + mailData.getAuth());
		MailResponse response = new MailResponse();
		UserLoginData userLoginData = new UserLoginData();
		BaseResponse baseResponse = new BaseResponse();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for get forward mail :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.getForwardMail(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while getting get forward mails " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("get forward mail Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/forwardmail")
	public Response forwardMail(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling forward mail with " + mailData.getAuth());
		MailResponse response = new MailResponse();
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for forward mail :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.forwardMail(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while forwarding mail " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
				
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("forward mail Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/markReadUnread")
	public Response markReadUnread(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling markReadUnread mail with " + mailData.getAuth());
		MailResponse response = new MailResponse();
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for markReadUnread :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.markReadUnread(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while markReadUnread mails " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("markReadUnread Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/allfoldermails")
	public Response getAllFolderMails(FolderData mailData, @Context HttpServletRequest httpRequest) {
		ArrayList<AllMailResponse> response = new ArrayList<>();
		UserLoginData userLoginData = new UserLoginData();
		BaseResponse baseResponse = new BaseResponse();
		ObjectMapper mapper = new ObjectMapper();
		try {
			logger.info("Request Type : " + httpRequest.getRequestURL().toString()
					+ "\nPOST input for get all folders mails :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));
			logger.info("mailData.getAuth()==" + mailData.getAuth());
			logger.info("userDataBean.getUserDataMap()===" + userDataBean.getUserDataMap());
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			if (mailData.getTitle() != null && mailData.getTitle().equalsIgnoreCase("archive"))
				mailData.setTitle(mailData.getTitle().replaceAll("archive", "Archive"));
			    if(!mailData.getTitle().contains("default0")){
			    	mailData.setTitle("default0/"+mailData.getTitle());
			    }
				response = mailManager.getArchiveFolderMails(mailData); // call
																		// archive
																		// mails
																		// here

			/*
			 * else response = mailManager.getAllFolderMails(mailData);
			 */
			
			
			if((response.size()==1 && response.get(0).getError()!=null) && (response.get(0).getError().equalsIgnoreCase("Mail folder \"Archive\" could not be found on mail server 127.0.0.1") || response.get(0).getError().equalsIgnoreCase("Le dossier de courrier « Archive » n'a pas été trouvé sur le serveur de courrier 127.0.0.1.")  || response.get(0).getError().contains("127.0.0.1."))){
				//logger.info("Response is set to empty array when Archive folder is not found for user");
				//baseResponse.setData(new ArrayList<>());
				//logger.info("get all folder mails Response " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(baseResponse));
				mailData.setTitle(mailData.getTitle().replaceAll("Archive", "Archives"));
				response = mailManager.getArchiveFolderMails(mailData);
				if(response.size() == 1 && response.get(0).getError()!=null){
					baseResponse.setError(response.get(0).getError());
				}else{
					baseResponse.setData(response);
				}
			}else if(response.size()==1 && response.get(0).getError()!=null){
				baseResponse.setError(response.get(0).getError());
			}
			else{
				baseResponse.setData(response);
			}
			

		} catch (Exception e) {
			logger.error("Exception Occured while getting all folder mails " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
				}else{
					baseResponse.setError(e.getMessage());
				}
		
		}
		
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updatemail")
	public Response updateMail(UpdateMail mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling update mail with " + mailData.getAuth());
		MailResponse response = new MailResponse();
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for update mails :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("folder:" + mailData.getFolder() + " " + "session:" + mailData.getSession());

			response = mailManager.updateMail(mailData);
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while updating mails " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("Update Mails Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateinvite")
	public Response updateInvite(UserParticipant mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling update invite with " + mailData.getAuth());
		InviteResponse response = new InviteResponse();
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for update Invite :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("session:" + mailData.getSession());

			response = mailManager.updateInvite(mailData);
			
			if(response.getError()!=null){
				baseResponse.setError(response.getError());
			}else{
				baseResponse.setData(response);
			}
		} catch (Exception e) {
			logger.error("Exception Occured while updating Invite " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}
			else{
				baseResponse.setError(e.getMessage());
			}
				
		}
		logger.info("Update Invite Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@OPTIONS
	@Path("/contextual")
	public Response getContextual() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/contextual")
	public Response getContextual(MailData mailData, @Context HttpServletRequest httpRequest) {

		logger.info("calling getContextual with " + mailData.getAuth());
		ArrayList<AllMailResponse> response = new ArrayList<AllMailResponse>();
		BaseResponse baseResponse = new BaseResponse();
		JSONArray responseArray = null;
		UserLoginData userLoginData = new UserLoginData();
		try {
			logger.info("userDataBean auth contextual Map " + userDataBean.getUserDataMap());
			logger.info("userDataBean auth contextual " + userDataBean.getUserDataMap().get(mailData.getAuth()));
			logger.info("userDataBean session contextual"
					+ userDataBean.getUserDataMap().get(mailData.getAuth()).getSession());
			userLoginData = userDataBean.getUserDataMap().get(mailData.getAuth());
			logger.info("calling getContextual with session "
					+ userDataBean.getUserDataMap().get(mailData.getAuth()).getSession());
			mailData.setSession(userLoginData.getSession());
			ObjectMapper mapper = new ObjectMapper();
			logger.info("Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for getContextual :"
					+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mailData));

			logger.info("session:" + mailData.getSession());

			response = mailManager.getContextual(mailData);
			if(response.size() == 1 && response.get(0).getError() != null){
				AllMailResponse allMailResponse = response.get(0);
				logger.error("Error Occured : " + allMailResponse.getError());
				baseResponse.setError(allMailResponse.getError());
				
			}
			else{
				baseResponse.setData(response);
			}
			logger.info("Response Array : " + responseArray);

		} catch (Exception e) {
			logger.error("Exception Occured while calling getContextual " + e.getMessage());
			if (null == userLoginData) {
				baseResponse.setError("User Not Logged In");
				
			}
			else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("get Contextual Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}

	@OPTIONS
	@Path("/downloadattachment")
	public Response downloadAttachment() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/downloadattachment/{mailid}/{attachmentid}/{filename}")
	public Response downloadAttachment(@PathParam("mailid") String mailId,
			@PathParam("attachmentid") String attachmentId, @PathParam("filename") String fileName,
			@QueryParam("auth") String auth, @QueryParam("folderid") String folderId,
			@Context HttpServletRequest httpRequest) {

		logger.info("calling downloadattachment with : " + auth);
		Attachment attachment = null;
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(auth);
			logger.info("Request Type : " + httpRequest.getRequestURL().toString()
					+ "\nGET request for downloadattachment for mailID :" + mailId);

			Attachment attachmentRequest = new Attachment();
			attachmentRequest.setFolder(folderId);
			attachmentRequest.setMailId(mailId);
			attachmentRequest.setFileName(fileName);
			attachmentRequest.setId(attachmentId);
			attachmentRequest.setAuth(auth);
			attachment = mailManager.downloadAttachment(attachmentRequest);

		} catch (Exception e) {
			logger.error("Exception Occured while downloadattachment " + e.getMessage());
			if (null == userLoginData) {
				logger.info("downloadattachment  " + attachment.getFileContent().length);
				String errorResponse = "{\"error\":\"User Not Logged In\"}";
				return responseBuilder.buildResponse(errorResponse);
			}
		}
		logger.info("downloadattachment  " + attachment.getFileContent().length);
		return responseBuilder.buildResponse(attachment.getFileContent(), attachment.getFileContent().length,
				attachment.getFileName());

	}

	@POST
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/downloadattachment")
	public Response downloadAttachment(Attachment attachmentRequest, @Context HttpServletRequest httpRequest) {

		logger.info("calling downloadattachment for file  : " + attachmentRequest.getFileName());
		Attachment attachment = null;
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDataMap().get(attachmentRequest.getAuth());
			logger.info("Request Type : " + httpRequest.getRequestURL().toString()
					+ "\nGET request for downloadattachment for mailID :" + attachmentRequest.getMailId());

			attachment = mailManager.downloadAttachment(attachmentRequest);

		} catch (Exception e) {
			logger.error("Exception Occured while downloadattachment " + e.getMessage());
			if (null == userLoginData) {
				logger.info("downloadattachment  " + attachment.getFileContent().length);
				String errorResponse = "{\"error\":\"User Not Logged In\"}";
				return responseBuilder.buildResponse(errorResponse);
			}
		}
		logger.info("downloadattachment  " + attachment.getFileContent().length);
		return responseBuilder.buildResponse(attachment.getFileContent(), attachment.getFileContent().length,
				attachment.getFileName());

	}

	@OPTIONS
	@Path("/redirect")
	public Response redirect() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}

	@GET
	@Path("/redirect")
	public Response redirect(@Context HttpServletRequest httpRequest) throws URISyntaxException {
		String baseRequestUrl = configManager.getPropertyAsString("BASE_REQUEST_URL");
		logger.info("Redirecting to URL : " + baseRequestUrl + "/public/open.html");
		URI uri = new URI(baseRequestUrl + "/public/open.html");
		return responseBuilder.buildResponse(uri);
	}

}
