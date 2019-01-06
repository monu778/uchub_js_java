package com.broadsoft.demohub.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.broadsoft.demohub.api.beans.Attachment;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.DriveData;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.beans.UserLoginData;
import com.broadsoft.demohub.api.manager.DriveManagerIF;
import com.broadsoft.demohub.api.response.manager.ResponseBuilder;
import com.broadsoft.demohub.api.response.manager.ResponseObjectManager;

@Service
@Path("/driveservice")
public class DriveService {
	private Logger logger = Logger.getLogger(DriveService.class);

	@Autowired
	DriveManagerIF driveManager;
	@Autowired
	UserDataBean userDataBean;
	@Autowired
	ResponseBuilder responseBuilder;
	@Autowired
	ResponseObjectManager responseObjectManager;
	
	
	@OPTIONS
	@Path("/getdrivedata")
	public Response allDriveFiles() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getdrivedata")
	public Response allDriveFiles(DriveData driveData, @Context HttpServletRequest httpRequest) {

		logger.info("calling all drive files with auth " + driveData.getAuth());
		BaseResponse baseResponse = new BaseResponse();
		Map<String,Object> driveResponse = new HashMap<>();
		UserLoginData userLoginData = new UserLoginData();
		ObjectMapper mapper = new ObjectMapper();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.getAuth());

		
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for all files in drive :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());

			driveResponse = driveManager.allDriveFiles(driveData);
			if(driveResponse.get("error")!=null){
			baseResponse.setError(driveResponse.get("error").toString());
			}
			else{
				baseResponse.setData(driveResponse);
			}
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(baseResponse));
			
		} catch (Exception e) {
			logger.error("Exception Occured while getting all files in drive in service class" + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}
			else{
				baseResponse.setError(e.getMessage());
			}
			
		}
		
		return responseBuilder.buildResponse(baseResponse);
	}	
	
	
	@OPTIONS
	@Path("/deletefile")
	public Response deleteDriveFile() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/deletefile")
	public Response deleteDriveFile(List<DriveData> driveData, @Context HttpServletRequest httpRequest) {

		logger.info("calling delete drive file with auth " + driveData.get(0).getAuth());

		BaseResponse baseResponse = new BaseResponse();
	
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.get(0).getAuth());

			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for delete file in drive :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());

			baseResponse = driveManager.deleteDriveFile(driveData);
			
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(baseResponse));

		} catch (Exception e) {
			logger.error("Exception Occured while deleting file in drive " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}else{
				baseResponse.setError(e.getMessage());
			}
		}
		logger.info("Delete drive file Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);

	}
	
	
	@OPTIONS
	@Path("/uploaddrivefile")
	public Response uploadDriveFile() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/uploaddrivefile")
	public Response uploadDriveFile(@Context HttpServletRequest httpRequest) {
		ObjectMapper mapper = new ObjectMapper();
		BaseResponse driveResponse = new BaseResponse();
		try {
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\n file to upload");
			driveResponse = driveManager.uploadDriveFile(httpRequest );
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveResponse));
		} catch (Exception e) {
			logger.error("Exception Occured while uploading file in drive " + e.getMessage());
				driveResponse.setError(e.getMessage());
		}
		
		return responseBuilder.buildResponse(driveResponse);
	}
	
	
	@OPTIONS
	@Path("/sharedwithme")
	public Response sharedFiles() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sharedwithme")
	public Response sharedFiles(DriveData driveData, @Context HttpServletRequest httpRequest) {

		logger.info("calling getting shared files with auth " + driveData.getAuth());
		BaseResponse baseResponse = new BaseResponse();
		Map<String,Object> driveSharedResponse = new HashMap<>();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.getAuth());

			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for shared files in drive :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());

			driveSharedResponse = driveManager.getSharedDriveFiles(driveData);
			if(driveSharedResponse.get("error")!=null){
				baseResponse.setError(driveSharedResponse.get("error").toString());
				}
				else{
					baseResponse.setData(driveSharedResponse);
				}
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(baseResponse));
		} catch (Exception e) {
			logger.error("Exception Occured while getting shared files in drive " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}else{
				baseResponse.setError(e.getMessage());
			}
				
		}
		logger.info("shared drive files Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);
	}	
	
	
	
	@OPTIONS
	@Path("/getdocumentlink")
	public Response getLink() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getdocumentlink")
	public Response getLink(DriveData driveData, @Context HttpServletRequest httpRequest) {
		logger.info("getting document link with auth:" + driveData.getAuth());
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.getAuth());

			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for get document link :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());
			baseResponse = driveManager.getDocumentLink(driveData);
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(baseResponse));
			
		}catch (Exception e) {
			logger.error("Exception Occured while getting document link" + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}else{
				baseResponse.setError(e.getMessage());
			}
				
		}
		logger.info("get document link Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);
		
	}
	
	
	@OPTIONS
	@Path("/sharedocument")
	public Response shareDocument() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sharedocument")
	public Response shareDocument(DriveData driveData, @Context HttpServletRequest httpRequest) {
		logger.info("getting document link with auth:" + driveData.getAuth());
		BaseResponse baseResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.getAuth());

			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for sharing document :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());
			baseResponse = driveManager.shareDocument(driveData);
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(baseResponse));
		}catch (Exception e) {
			logger.error("Exception Occured while sharing document" + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}else{
				baseResponse.setError(e.getMessage());
			}
				
		}
		logger.info("share doument response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);
		
	}
	
	
	
	@OPTIONS
	@Path("/recentfiles")
	public Response getRecentFiles() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/recentfiles")
	public Response getRecentFiles(DriveData driveData, @Context HttpServletRequest httpRequest) {

		logger.info("calling getting recent files with auth " + driveData.getAuth());
		BaseResponse baseResponse = new BaseResponse();
		Map<String,Object> driveSharedResponse = new HashMap<>();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.getAuth());

			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for recent files in drive :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());

			driveSharedResponse = driveManager.getRecentFiles(driveData);
			if(driveSharedResponse.get("error")!=null){
				baseResponse.setError(driveSharedResponse.get("error").toString());
				}
				else{
					baseResponse.setData(driveSharedResponse);
				}
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(baseResponse));
		} catch (Exception e) {
			logger.error("Exception Occured while getting recent files in drive " + e.getMessage());
			if (null == userLoginData){
				baseResponse.setError("User Not Logged In");
			}else{
				baseResponse.setError(e.getMessage());
			}
				
		}
		logger.info("Recent drive files Response " + baseResponse);
		return responseBuilder.buildResponse(baseResponse);
	}	
	
	
	
	@OPTIONS
	@Path("/searchdrivefiles")
	public Response searchdrivefiles() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/searchdrivefiles")
	public Response searchdrivefiles(DriveData driveData, @Context HttpServletRequest httpRequest) {

		logger.info("calling searchdrivefiles with auth " + driveData.getAuth());
		BaseResponse driveSharedResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.getAuth());

			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for searching files in drive :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());

			driveSharedResponse = driveManager.searchdrivefiles(driveData);
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveSharedResponse));

		} catch (Exception e) {
			logger.error("Exception Occured while searching files in drive " + e.getMessage());
			if (null == userLoginData){
				driveSharedResponse.setError("User Not Logged In");
			}else{
				driveSharedResponse.setError(e.getMessage());
			}
				
		}
		logger.info("Searchdrivefiles   Response " + driveSharedResponse);
		return responseBuilder.buildResponse(driveSharedResponse);
	}	
	
	
	
	@OPTIONS
	@Path("/downloadfile")
	public Response downloadDriveFile() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	
	@GET
	@Produces({MediaType.APPLICATION_OCTET_STREAM,MediaType.APPLICATION_JSON})
	@Path("/downloadfile/{fileName}/{version}/{createdUserId}/{timestamp}")
	public Response downloadDriveFile(@PathParam("fileName") String fileName,
			 @PathParam("version") String version,@PathParam("createdUserId") String createdUserId,@PathParam("timestamp") long timestamp,
			@QueryParam("auth") String auth,@QueryParam("fileObjectID") String fileObjectID, @QueryParam("folderid") String folderId, @Context HttpServletRequest httpRequest) {

		DriveData driveData = new DriveData();
		driveData.setAuth(auth);
		driveData.setFileName(fileName);
		driveData.setFileObjectID(fileObjectID);
		driveData.setVersion(version);
		driveData.setCreatedUserId(createdUserId);
		driveData.setTimestamp(timestamp);
		if(folderId !=null){
		driveData.setFolder(folderId);
		}else{
			String folder = fileObjectID.substring(0,fileObjectID.indexOf("/"));
			driveData.setFolder(folder);
		}
		
		logger.info("calling downloadDriveFile with auth " + driveData.getAuth());
		BaseResponse downloadResponse = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.getAuth());

			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for downloadDriveFile in drive :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());

			downloadResponse = driveManager.downloadDriveFile(driveData);
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(downloadResponse));
		} catch (Exception e) {
			logger.error("Exception Occured while downloadDriveFile  in drive " + e.getMessage());
			if (null == userLoginData){
				downloadResponse.setError("User Not Logged In");
			}else{
				downloadResponse.setError(e.getMessage());
			}
				
		}
		logger.info("Download DriveFile   Response " + downloadResponse);
		if(((Attachment)downloadResponse.getData())!=null){
		return responseBuilder.buildResponse(((Attachment)downloadResponse.getData()).getFileContent(), ((Attachment)downloadResponse.getData()).getFileContent().length,
				((Attachment)downloadResponse.getData()).getFileName());
		}
		else{
			return responseBuilder.buildResponse(downloadResponse);
		}
	}	
	
	
	@OPTIONS
	@Path("/contextualdata")
	public Response getContextualData() {
		return responseBuilder.buildResponse(responseObjectManager.getSuccessStatus());
	}
	
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/contextualdata")
	public Response getContextualData(DriveData driveData, @Context HttpServletRequest httpRequest) {

		logger.info("calling getContextualData with auth " + driveData.getAuth());
		BaseResponse contextualData = new BaseResponse();
		UserLoginData userLoginData = new UserLoginData();
		try {
			userLoginData = userDataBean.getUserDriveDataMap().get(driveData.getAuth());

			ObjectMapper mapper = new ObjectMapper();
			logger.info(
					"Request Type : " + httpRequest.getRequestURL().toString() + "\nPOST input for contextualdata in drive :"
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(driveData));

			logger.info("session:" + userLoginData.getSession());

			contextualData = driveManager.getContextualData(driveData);
			
			logger.info("Response to portal  : " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(contextualData));
		} catch (Exception e) {
			logger.error("Exception Occured while gettingContextualData   " + e.getMessage());
			if (null == userLoginData){
				contextualData.setError("User Not Logged In");
			}else{
				contextualData.setError(e.getMessage());
			}
				
		}
		 return responseBuilder.buildResponse(contextualData);
		
	}	

	
}
