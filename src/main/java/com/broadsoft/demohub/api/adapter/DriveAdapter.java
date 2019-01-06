package com.broadsoft.demohub.api.adapter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.Attachment;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.DriveData;
import com.broadsoft.demohub.api.beans.DriveResponse;
import com.broadsoft.demohub.api.beans.Facets;
import com.broadsoft.demohub.api.beans.Filter;
import com.broadsoft.demohub.api.beans.FolderData;
import com.broadsoft.demohub.api.beans.PermissionsData;
import com.broadsoft.demohub.api.beans.UploadDriveResponse;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.config.ConfigManager;
import com.broadsoft.demohub.api.parser.DriveResponseParser;
import com.broadsoft.demohub.api.parser.DriveSearchResponseParser;
import com.broadsoft.demohub.api.security.HashEncoderDecoder;

@Component
public class DriveAdapter {
	

	@Autowired
	ConfigManager configManager;

	@Autowired
	UserDataBean userDataBean;

	@Autowired
	FolderAdapter folderAdapter;

	@Autowired
	DriveResponseParser driveResponseParser;

	@Autowired
	HashEncoderDecoder hashEncoderDecoder;

	@Autowired
	DriveSearchResponseParser driveSearchResponseParser;

	private Logger logger = Logger.getLogger(DriveAdapter.class);

	public Map<String, Object> allDriveFiles(DriveData driveData) {

		Map<String, Object> driveResponse = new HashMap<>();
		List<FolderData> listOfFolder = new ArrayList<>();
		List<FolderData> folderDetails = new ArrayList<>();
		String folder = "";
		ObjectMapper mapper = new ObjectMapper();

		if (driveData.getFolder() == null) {
			folderDetails = folderAdapter.getAllVisibleFolder(driveData, "infostore");// gets
																						// all
			try {
				logger.info("Got the visible folder details : "
						+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(folderDetails));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // drive
				// folders
			if (folderDetails.size() == 1 && folderDetails.get(0).getErrorMessage() != null) {
				Object errorMessage = folderDetails.get(0).getErrorMessage();
				driveResponse.put("error", errorMessage);
				return driveResponse;
			} else {
				for (FolderData folderData : folderDetails) {
					if (folderData.getParentFolderId() != null && folderData.getStandard_folder() != null
							&& folderData.getCreatedUserId() != null && userDataBean.getUserDriveDataMap()
									.get(driveData.getAuth()).getUserId().toString() != null) {
						if (folderData.getParentFolderId().equals("10") && folderData.getStandard_folder()
								&& folderData.getCreatedUserId().equalsIgnoreCase(userDataBean.getUserDriveDataMap()
										.get(driveData.getAuth()).getUserId().toString())) {
							folder = folderData.getFolder_id(); // gets default
																// openxchange
																// drive
																// folder
						}
					}
				}
				for (FolderData folderData : folderDetails) {
					if (folderData.getParentFolderId() != null) {
						if (folderData.getParentFolderId().equals(folder)) {
							listOfFolder.add(folderData); // adds all subfolders
															// of
															// default
															// openxchange
															// drive folder
															// using
															// folder as
															// folderId of
															// parent folder

						}
					}
				}
			}
			Collections.sort(listOfFolder);

		}

		else {
			folder = driveData.getFolder();

			FolderData getSubFolder = new FolderData();
			getSubFolder.setAuth(driveData.getAuth());
			if (driveData.getModule() != null) {
				getSubFolder.setModule(driveData.getModule());
			}
			getSubFolder.setTitle(folder);
			try {
				String responseValue = folderAdapter.getSubFolders(getSubFolder);
				if (responseValue.contains("error")) {
					driveResponse.put("error", responseValue);
					return driveResponse;

				}
				if (!(responseValue.isEmpty())) {
					JSONObject dataValue = new JSONObject(responseValue);
					if (dataValue.has("data")) {
						JSONArray visiblePublicFoldersArray = dataValue.getJSONArray("data");
						folderAdapter.listVisibleFolder(folderDetails, visiblePublicFoldersArray);
						listOfFolder.addAll(folderDetails); // adds all
															// subfolders
					}
				}

			} catch (Exception e) {
				logger.error(
						"Exception Occured in DriveAdapter  while getting subfolders of drive when folderId mentioned: "
								+ e.getMessage());
				driveResponse.put("error", e.getMessage());
				return driveResponse;

			}
		}

		return getDriveFiles(driveData, listOfFolder, folder);
	}

	private Map<String, Object> getDriveFiles(DriveData driveData, List<FolderData> listOfFolder, String folder) {
		String input = "";
		Map<String, Object> driveResponse = new HashMap<>();
		List<DriveResponse> response = new ArrayList<>();
		String stringResponse = "";
		Integer leftHandLimit = null;
		Integer rightHandLimit = null;
		if ((driveData.getLeftHandLimit() != null) && (driveData.getRightHandLimit() != null)) {
			leftHandLimit = Integer.parseInt(driveData.getLeftHandLimit());
			rightHandLimit = Integer.parseInt(driveData.getRightHandLimit());
		}
		String openExchangeDriveUrl = configManager.getPropertyAsString("OX_DRIVE_URL");
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();
		String columns = "";

		

		if (driveData.getColumns() == null) {
			columns = configManager.getPropertyAsString("DRIVE_COLUMNS");
		} else {
			columns = driveData.getColumns();
		}
		try {
			URL url = new URL(openExchangeDriveUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());
			logger.info("userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie() :: "
					+ userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());

			input = "action=" + URLEncoder.encode("all", "UTF-8") + "&session=" + URLEncoder.encode(session, "UTF-8")
					+ "&columns=" + URLEncoder.encode(columns, "UTF-8") + "&folder="
					+ URLEncoder.encode(folder, "UTF-8");
			if (driveData.getOrder() != null) {
				input = input + "&sort=" + URLEncoder.encode("5", "UTF-8") + "&order="
						+ URLEncoder.encode(driveData.getOrder(), "UTF-8");
			}

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			logger.info("Request to OpenXchange server : " + input);
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code from OpenXchange server : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				stringResponse = stringResponse + output;
			}

			conn.disconnect();
			JSONObject driveFileJson = new JSONObject(stringResponse);
			logger.info("Response from OpenXchange server : " + driveFileJson.toString());
			
			if (driveFileJson.has("error")) {
				driveResponse.put("error", driveFileJson.get("error"));
			} else {

				int folderSize = listOfFolder.size();
				int filesSize = 0;

				if (leftHandLimit != null && rightHandLimit != null && leftHandLimit < rightHandLimit) {

					if (rightHandLimit <= folderSize) {
						listOfFolder = listOfFolder.subList(leftHandLimit, rightHandLimit);
						driveResponse.put("listOfFolder", listOfFolder);
						driveResponse.put("listOfFiles", new ArrayList<>());
					}

					if (rightHandLimit > folderSize && leftHandLimit < folderSize) {
						listOfFolder = listOfFolder.subList(leftHandLimit, listOfFolder.size());
						driveResponse.put("listOfFolder", listOfFolder);
						driveResponse.put("listOfFiles", new ArrayList<>());
					}

					if (rightHandLimit > folderSize) {
						response = driveResponseParser.getParsedDriveResponse(stringResponse);
						filesSize = response.size();
						if (leftHandLimit <= folderSize && (rightHandLimit - folderSize) > 0
								&& (rightHandLimit - folderSize) < filesSize && filesSize > 0) {
							response = response.subList(0, (rightHandLimit - folderSize));
							driveResponse.put("listOfFiles", response);
							if (!(response.isEmpty())) {
								driveResponse.put("timestamp", response.get(0).getTimestamp());
							}

						}
						if (leftHandLimit <= folderSize && (rightHandLimit - folderSize) > 0
								&& (rightHandLimit - folderSize) >= filesSize && filesSize > 0) {
							driveResponse.put("listOfFiles", response);
							if (!(response.isEmpty())) {
								driveResponse.put("timestamp", response.get(0).getTimestamp());
							}

						}
						if (leftHandLimit > folderSize && (rightHandLimit - folderSize) < filesSize && filesSize > 0) {
							response = response.subList(leftHandLimit - folderSize, (rightHandLimit - folderSize));
							driveResponse.put("listOfFiles", response);
							if (!(response.isEmpty())) {
								driveResponse.put("timestamp", response.get(0).getTimestamp());
							}

						}

						if (leftHandLimit > folderSize && (rightHandLimit - folderSize) > filesSize
								&& leftHandLimit < (folderSize + filesSize) && filesSize > 0) {
							response = response.subList(leftHandLimit - folderSize, filesSize);
							driveResponse.put("listOfFiles", response);
							if (!(response.isEmpty())) {
								driveResponse.put("timestamp", response.get(0).getTimestamp());
							}

						}
						if(filesSize == 0) {
							driveResponse.put("listOfFiles", new ArrayList<>());
						}
					}
				} else {
					response = driveResponseParser.getParsedDriveResponse(stringResponse);
					JSONObject parsedDriveResponse = new JSONObject(response);
					if(parsedDriveResponse.has("error")){
						driveResponse.put("error", parsedDriveResponse.get("error").toString());
					}
					
					driveResponse.put("listOfFiles", response);
					driveResponse.put("listOfFolder", listOfFolder);
					if (!(response.isEmpty())) {
						driveResponse.put("timestamp", response.get(0).getTimestamp());
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception inside getDriveFiles method Occured while getting all files of drive : "
					+ e.getMessage());

			if (null == userDataBean.getUserDriveDataMap().get(driveData.getAuth())) {
				driveResponse.put("error", "User Not Logged In");
			} else {
				driveResponse.put("error", e.getMessage());
			}
		}
		return driveResponse;
	}

	public BaseResponse deleteDriveFile(List<DriveData> driveData) {
		BaseResponse baseResponse = new BaseResponse();
		String deleteResponse = "";
		String openExchangeDriveUrl = configManager.getPropertyAsString("OX_DRIVE_URL");
		String session = userDataBean.getUserDriveDataMap().get(driveData.get(0).getAuth()).getSession();
		long timestamp = 0l;
		String cookie = "";
		StringBuilder idsDeleted = new StringBuilder();
		String deleteUrl = "";

		try {
			if (driveData.get(0) != null) {
				timestamp = driveData.get(0).getTimestamp();
				cookie = userDataBean.getUserDriveDataMap().get(driveData.get(0).getAuth()).getCookie();
				StringBuilder listJsonRequest = new StringBuilder();
				listJsonRequest.append("[");
				if (driveData.get(0).getFileObjectID() == null) {
					deleteUrl = "https://mail.teaming.orange-business.com/appsuite/api/folders?action=delete&extendedResponse=true&failOnError=true&session="
							+ session + "&tree=1";
					for (int i = 0; i < driveData.size(); i++) {
						listJsonRequest = listJsonRequest.append(driveData.get(i).getFolder() + ",");
						idsDeleted = idsDeleted.append(driveData.get(i).getFolder() + " ,");
					}
				} else {
					deleteUrl = openExchangeDriveUrl + "?action=delete&session=" + session + "&timestamp=" + timestamp
							+ "&hardDelete=" + false;
					for (int i = 0; i < driveData.size(); i++) {
						StringBuilder jsonData = new StringBuilder();
						jsonData.append("{");
						jsonData.append("\"id\":\"" + driveData.get(i).getFileObjectID() + "\"");
						jsonData.append(",");
						jsonData.append("\"folder\":\"" + driveData.get(i).getFolder() + "\"");
						jsonData.append("},");
						listJsonRequest.append(jsonData);
						idsDeleted = idsDeleted.append(driveData.get(i).getFileObjectID() + " ,");
					}
				}
				listJsonRequest.deleteCharAt(listJsonRequest.length() - 1);
				listJsonRequest.append("]");
				logger.info("Request to OpenXchange server url : " + deleteUrl +" JsonRequest : "+ listJsonRequest.toString());
				URL url = new URL(deleteUrl);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("PUT");
				conn.setRequestProperty("Content-Type", " application/json");
				conn.setRequestProperty("Cookie", cookie);
				idsDeleted = idsDeleted.deleteCharAt(idsDeleted.length() - 1);
				OutputStream os = conn.getOutputStream();
				os.write(listJsonRequest.toString().getBytes());
				os.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error("Error Response Code : " + conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}

				logger.info("Response Code from OpenXchange server : " + conn.getResponseCode());
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output = "";
				while ((output = br.readLine()) != null) {
					deleteResponse = deleteResponse + output;

				}
				conn.disconnect();
				JSONObject deleteJsonResponse = new JSONObject(deleteResponse);
				if (deleteJsonResponse.has("data") && deleteJsonResponse.getJSONArray("data").length() == 0) {
					baseResponse.setData("files deleted successfully for fileObject Ids : " + idsDeleted);
				} else if (deleteJsonResponse.has("error")) {
					baseResponse.setError(deleteJsonResponse.get("error").toString());
				}

			} else {
				baseResponse.setError("Please send list of files to be deleted");
			}
		} catch (Exception e) {
			baseResponse.setError(e.getMessage());
		}

		return baseResponse;
	}

	public BaseResponse uploadDriveFile(HttpServletRequest httpRequest) {

		DriveData driveData = new DriveData();
		InputStream ip = null;

		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(httpRequest);
			if (isMultipart) {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(httpRequest);
				Iterator<FileItem> iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = iter.next();
					if (!item.isFormField()) {
						String uploadFileName = item.getName();
						logger.info("upload file name :" + uploadFileName);
						ip = new ByteArrayInputStream(item.get());
						driveData.setFileContent(IOUtils.toByteArray(ip));
					} else {
						String fieldName = item.getFieldName();
						if (fieldName.equalsIgnoreCase("auth")) {
							String auth = item.getString();
							userDataBean.getUserDriveDataMap().get(auth);
							driveData.setAuth(auth);
						} else if (fieldName.equalsIgnoreCase("folder")) {
							String folder = item.getString();
							driveData.setFolder(folder);
						} else if (fieldName.equalsIgnoreCase("filename")) {
							String filename = item.getString();
							driveData.setFileName(filename);
						}

					}
				}
			}
		} catch (Exception ex) {
			logger.error("Error Occured " + ex.getMessage());
		}

		BaseResponse baseResponse = new BaseResponse();
		UploadDriveResponse driveResponse = new UploadDriveResponse();
		String key = hashEncoderDecoder.encode(16);
		final String boundary = "------WebKitFormBoundary" + key;
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();
		String cookie = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie();
		if (cookie == null) {
			baseResponse.setError("User Not Logged In");
			return baseResponse;
		}
		String url = "https://mail.teaming.orange-business.com/appsuite/api/files?action=new&extendedResponse=true&force_json_response=true&session="
				+ session + "&try_add_version=true";
		String response = "";
		String folder = "";

		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary.substring(2));
			conn.setRequestProperty("Content-Disposition", "form-data; name=\"json\"");
			conn.setRequestProperty("Host", "mail.teaming.orange-business.com");
			conn.setRequestProperty("Cookie", cookie);
			OutputStream os = conn.getOutputStream();

			String LINE_FEED = "\r\n";

			StringBuffer textJosn = new StringBuffer(boundary);
			textJosn.append(LINE_FEED);
			textJosn.append(
					"Content-Disposition: form-data; name=\"file\"; filename=\"" + driveData.getFileName() + "\"");
			textJosn.append(LINE_FEED);
			textJosn.append("Content-Type: application/octet-stream");
			textJosn.append(LINE_FEED);
			textJosn.append(LINE_FEED);
			logger.info("Request url for OpenXchange server : " + url + " JsonRequest : " + textJosn.toString());
			os.write(textJosn.toString().getBytes());
			os.flush();
			os.write(driveData.getFileContent());
			os.flush();
			
			List<FolderData> folderDetails = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			if (driveData.getFolder() == null) {
				folderDetails = folderAdapter.getAllVisibleFolder(driveData, "infostore");// gets
																							// all
				try {
					logger.info("Got the visible folder details : "
							+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(folderDetails));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // drive
					// folders
				if (folderDetails.size() == 1 && folderDetails.get(0).getErrorMessage() != null) {
					baseResponse.setError(folderDetails.get(0).getErrorMessage());
					return baseResponse;
				} else {
					for (FolderData folderData : folderDetails) {
						if (folderData.getParentFolderId() != null && folderData.getStandard_folder() != null
								&& folderData.getCreatedUserId() != null && userDataBean.getUserDriveDataMap()
										.get(driveData.getAuth()).getUserId().toString() != null) {
							if (folderData.getParentFolderId().equals("10") && folderData.getStandard_folder()
									&& folderData.getCreatedUserId().equalsIgnoreCase(userDataBean.getUserDriveDataMap()
											.get(driveData.getAuth()).getUserId().toString())) {
								folder = folderData.getFolder_id(); // gets
																	// default
																	// openxchange
																	// drive
																	// folder
							}
						}
					}
				}
			} else {
				folder = driveData.getFolder();
			}

			StringBuffer textjson2 = new StringBuffer();
			textjson2.append(LINE_FEED);
			textjson2.append(LINE_FEED);
			textjson2.append(boundary);
			textjson2.append(LINE_FEED);
			textjson2.append("Content-Disposition: form-data; name=\"json\"");
			textjson2.append(LINE_FEED);
			textjson2.append(LINE_FEED);
			textjson2.append("{\"folder_id\":\"" + folder + "\",\"description\":\"\"}");
			textjson2.append(LINE_FEED);
			textjson2.append(boundary + "--");

			os.write(textjson2.toString().getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("HTTP Error Code  From OpenXchange : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.error("HTTP Code  From OpenXchange server : " + conn.getResponseCode());
			BufferedReader br2 = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;

			while ((output = br2.readLine()) != null) {
				response = response + output;
			}
			logger.info("Response From OpenXchange server : " + response);
			conn.disconnect();

			StringBuilder value = new StringBuilder(
					response.substring(response.indexOf("({\""), response.indexOf("\"}}})")));

			value = value.append("\"}}}");

			value = value.deleteCharAt(0);
			JSONObject uploadJsonResponse = new JSONObject(value.toString());
			if (uploadJsonResponse.has("data")) {
				driveResponse = driveResponseParser.parseUploadFileResponse(uploadJsonResponse.getJSONObject("data"));
				baseResponse.setData(driveResponse);
			} else if (uploadJsonResponse.has("error")) {
				baseResponse.setError(uploadJsonResponse.get("error").toString());
			}
		} catch (Exception e) {
			baseResponse.setError(e.getMessage());
			logger.error("Exception Occured While Drive File Upload " + e.getMessage());
		}
		return baseResponse;
	}

	public Map<String, Object> getSharedDriveFiles(DriveData driveData) {

		Map<String, Object> sharedDriveResponse = new HashMap<>();
		List<FolderData> listOfFolder = new ArrayList<>();
		List<FolderData> folderDetails = new ArrayList<>();
		List<FolderData> listOfSubFolder = new ArrayList<>();
		String folder = "10";

		FolderData getSubFolder = new FolderData();
		getSubFolder.setAuth(driveData.getAuth());
		if (driveData.getFolder() != null) {
			folder = driveData.getFolder();
			getSubFolder.setTitle(folder);
		} else {
			getSubFolder.setTitle(folder);
		}
		try {
			String subFolderDetails = folderAdapter.getSubFolders(getSubFolder);
			JSONObject dataValue = new JSONObject(subFolderDetails);
			if (subFolderDetails.contains("error")) {
				sharedDriveResponse.put("error", dataValue.get("error"));
				return sharedDriveResponse;

			}
			if (dataValue.has("data")) {
				JSONArray subFolderArray = dataValue.getJSONArray("data");
				folderAdapter.listVisibleFolder(folderDetails, subFolderArray);
				for (FolderData folderData : folderDetails) {
					if (!(folderData.getCreatedUserId().equalsIgnoreCase(
							userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getUserId().toString()))) {
						listOfFolder.add(folderData); // adds all
														// subfolders
					}
				}

			}
			/*
			 * if (driveData.getCreatedUserId() != null &&
			 * !(driveData.getCreatedUserId().equalsIgnoreCase(
			 * userDataBean.getUserDriveDataMap().get(driveData.getAuth()).
			 * getUserId().toString()))) {
			 * sharedDriveResponse.put("listOfFiles", new ArrayList<>());
			 * sharedDriveResponse.put("listOfFolder", listOfFolder); return
			 * sharedDriveResponse; }
			 */

			for (FolderData subFolder : listOfFolder) {
				FolderData getMainSubFolder = new FolderData();
				getMainSubFolder.setAuth(driveData.getAuth());
				getMainSubFolder.setTitle(subFolder.getFolder_id());
				getMainSubFolder.setCreatedUserId(subFolder.getCreatedUserId());
				String mainSubFolderDetails = folderAdapter.getSubFolders(getMainSubFolder);
				JSONObject mainSubFolderJson = new JSONObject(mainSubFolderDetails);
				if (mainSubFolderJson.has("error")) {
					sharedDriveResponse.put("error", mainSubFolderJson.get("error"));
					return sharedDriveResponse;

				} else {
					JSONArray mainSubFolderArray = mainSubFolderJson.getJSONArray("data");
					folderAdapter.listVisibleFolder(listOfSubFolder, mainSubFolderArray);
				}
			}

			/*
			 * folderDetails.removeAll(folderDetails);
			 * 
			 * for (FolderData subFolderData : listOfFolder) {
			 * 
			 * FolderData getSubFolders = new FolderData();
			 * getSubFolders.setAuth(driveData.getAuth());
			 * getSubFolders.setTitle(subFolderData.getFolder_id()); String
			 * subFolderResponseValue =
			 * folderAdapter.getSubFolders(getSubFolders); if
			 * (subFolderResponseValue.contains("error")) {
			 * sharedDriveResponse.put("error", subFolderResponseValue); return
			 * sharedDriveResponse; }
			 * 
			 * if (!(subFolderResponseValue.isEmpty())) { JSONObject dataValue1
			 * = new JSONObject(subFolderResponseValue); if
			 * (dataValue1.has("data")) { JSONArray visiblePublicFoldersArray =
			 * dataValue1.getJSONArray("data");
			 * folderAdapter.listVisibleFolder(folderDetails,
			 * visiblePublicFoldersArray);
			 * listOfSubFolder.addAll(folderDetails); // adds all // //
			 * subfolders } } }
			 */

		} catch (Exception e) {
			logger.error(
					"Exception Occured while getting subfolders of drive when folderId mentioned: " + e.getMessage());
			sharedDriveResponse.put("error", e.getMessage());
			return sharedDriveResponse;
		}
		Map<String, Object> sharedWithMeFiles = getDriveFiles(driveData, listOfSubFolder, folder);
		if(sharedWithMeFiles.containsKey("listOfFolder")) {
			sharedWithMeFiles.remove("listOfFolder");
		}
		return sharedWithMeFiles;

	}

	public BaseResponse getDocumentLink(DriveData driveData) {
		String response = "";
		BaseResponse baseResponse = new BaseResponse();
		DriveResponse driveResponse = new DriveResponse();
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();
		String openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/share/management?action=getLink&session="
				+ session + "&timezone=UTC";

		try {
			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", " application/json");
			conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());

			StringBuilder json = new StringBuilder();
			json.append("{");
			json.append("\"module\":" + "\"" + "infostore" + "\"");
			json.append(",");
			json.append("\"folder\":" + "\"" + driveData.getFolder() + "\"");
			if (driveData.getFileObjectID() != null) {
				json.append(",");
				json.append("\"item\":" + "\"" + driveData.getFileObjectID() + "\"");
			}
			json.append("}");
            logger.info("Request to OpenXchange server : " + openExchangeMailUrl + " JsonRequest : "+ json);
			OutputStream op = conn.getOutputStream();
			op.write(json.toString().getBytes());
			op.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code from OpenXhange server : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}

			conn.disconnect();
            logger.info("Response from OpenXhange server : "+ response);
			// parsing the response
			JSONObject dataValue = new JSONObject(response);
			if (dataValue.has("data")) {

				JSONObject data = dataValue.getJSONObject("data");
				if (data.has("url")) {
					driveResponse.setUrl(data.getString("url"));
					baseResponse.setData(driveResponse);
				}
			} else if (dataValue.has("error")) {
				baseResponse.setError(dataValue.get("error").toString());
			}

		} catch (Exception e) {
			logger.error("Exception Occured while getting document link: " + e.getMessage());
			baseResponse.setError("error while fetching document link :" + e.getMessage());
		}
		return baseResponse;
	}

	public BaseResponse shareDocument(DriveData driveData) {
		String response = "";
		BaseResponse baseResponse = new BaseResponse();
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();

		String openExchangeMailUrl = "https://mail.teaming.orange-business.com/appsuite/api/share/management?action=sendLink&session="
				+ session;

		try {
			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", " application/json");
			conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());

			// {"module":"infostore","folder":"3952","recipients":[["snayak@broadsoft.com","snayak@broadsoft.com"],["brijeshbiet88@gmail.com","brijeshbiet88@gmail.com"]]}
			StringBuilder json = new StringBuilder();
			json.append("{");
			json.append("\"module\":" + "\"" + "infostore" + "\"");
			json.append(",");
			json.append("\"folder\":" + "\"" + driveData.getFolder() + "\"");
			if (driveData.getFileObjectID() != null) {
				json.append(",");
				json.append("\"item\":" + "\"" + driveData.getFileObjectID() + "\"");
			}
			json.append(",");
			json.append("\"recipients\" :[");
			for (int i = 0; i < driveData.getRecipients().length; i++) {
				if (i != 0)
					json.append(",");

				json.append("[\"");
				json.append(driveData.getRecipients()[i] + "\"");
				json.append(",");
				json.append("\"" + driveData.getRecipients()[i] + "\"]");
			}
			json.append("]");
			json.append("}");

			OutputStream op = conn.getOutputStream();
			op.write(json.toString().getBytes());
			op.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code from OpenXchange server : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}

			conn.disconnect();

			JSONObject dataValue = new JSONObject(response);
			if (dataValue.has("data")) {

				baseResponse.setData(dataValue.get("data").toString());
			} else if (dataValue.has("error")) {
				baseResponse.setError(dataValue.get("error").toString());
			}
		} catch (Exception e) {
			logger.error("Exception Occured while sharing document : " + e.getMessage());
			baseResponse.setError("error while shari document :" + e.getMessage());
		}
		return baseResponse;
	}

	public Map<String, Object> getRecentFiles(DriveData driveData) {
		Map<String, Object> driveResponse = new HashMap<>();
		List<FolderData> folderDetails = new ArrayList<>();
		String folder = "";
		ObjectMapper mapper = new ObjectMapper();

		if (driveData.getFolder() == null) {
			folderDetails = folderAdapter.getAllVisibleFolder(driveData, "infostore");// gets
																						// all
			try {
				logger.info("Got the visible folder details : "
						+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(folderDetails));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // drive
				// folders
			if (folderDetails.size() == 1 && folderDetails.get(0).getErrorMessage() != null) {
				Object errorMessage = folderDetails.get(0).getErrorMessage();
				driveResponse.put("error", errorMessage);
				return driveResponse;
			} else {
				for (FolderData folderData : folderDetails) {
					if (folderData.getParentFolderId() != null && folderData.getStandard_folder() != null
							&& folderData.getCreatedUserId() != null && userDataBean.getUserDriveDataMap()
									.get(driveData.getAuth()).getUserId().toString() != null) {
						if (folderData.getParentFolderId().equals("10") && folderData.getStandard_folder()
								&& folderData.getCreatedUserId().equalsIgnoreCase(userDataBean.getUserDriveDataMap()
										.get(driveData.getAuth()).getUserId().toString())) {
							folder = folderData.getFolder_id(); // gets default
																// openxchange
																// drive
																// folder
						}
					}
				}
			}
		}

		Map<String, Object> recentFiles = getDriveFiles(driveData, new ArrayList<>(), folder);
		if(recentFiles.containsKey("listOfFolder")) {
			recentFiles.remove("listOfFolder");
		}
		return recentFiles;
		
	}

	public BaseResponse searchdrivefiles(DriveData driveData) {
		BaseResponse baseResponse = new BaseResponse();
		Map<String, Object> driveResponse = new HashMap<>();
		List<FolderData> folderDetails = new ArrayList<>();
		List<DriveResponse> listDriveResponse = new ArrayList<>();
		String folder = "";
		ObjectMapper mapper = new ObjectMapper();
		String response = "";
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();
		String columns = configManager.getPropertyAsString("DRIVE_COLUMNS");
		String queryUrl = "https://mail.teaming.orange-business.com/appsuite/api/find?action=query&";
		String driveSearchUrl = queryUrl + "session=" + session + "&columns=" + columns + "&module=files";
		if (driveData.getFolder() == null) {
			folderDetails = folderAdapter.getAllVisibleFolder(driveData, "infostore");// gets
																						// all
			try {
				logger.info("Got the visible folder details : "
						+ mapper.writerWithDefaultPrettyPrinter().writeValueAsString(folderDetails));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // drive
				// folders
			if (folderDetails.size() == 1 && folderDetails.get(0).getErrorMessage() != null) {
				Object errorMessage = folderDetails.get(0).getErrorMessage();
				if (errorMessage != null) {
					baseResponse.setError(errorMessage.toString());
				}

			} else {
				for (FolderData folderData : folderDetails) {
					if (folderData.getParentFolderId() != null && folderData.getStandard_folder() != null
							&& folderData.getCreatedUserId() != null && userDataBean.getUserDriveDataMap()
									.get(driveData.getAuth()).getUserId().toString() != null) {
						if (folderData.getParentFolderId().equals("10") && folderData.getStandard_folder()
								&& folderData.getCreatedUserId().equalsIgnoreCase(userDataBean.getUserDriveDataMap()
										.get(driveData.getAuth()).getUserId().toString())) {
							folder = folderData.getFolder_id(); // gets default
																// openxchange
																// drive
																// folder
						}
					}
				}
			}
		} else {
			folder = driveData.getFolder();
		}
		try {

			URL url = new URL(driveSearchUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());

			Facets[] facets = new Facets[2];
			ArrayList<Facets> facetsList = new ArrayList<>();
			Facets facet = new Facets();
			Filter filter = new Filter();
			String[] fields = new String[1];
			String[] queries = new String[1];
			fields[0] = null;
			queries[0] = null;
			facet.setFacet("folder");
			facet.setValue(folder);
			filter.setFields(fields);
			filter.setQueries(queries);
			facet.setFilter(filter);
			facets[0] = facet;

			Facets facetValue = new Facets();
			Filter filterValue = new Filter();
			String[] fieldValue = new String[1];
			String[] querieValue = new String[1];
			fieldValue[0] = "filename";
			querieValue[0] = driveData.getFileName();
			facetValue.setFacet("file_name");
			facetValue.setValue("file_name:" + driveData.getFileName());
			filterValue.setFields(fieldValue);
			filterValue.setQueries(querieValue);
			facetValue.setFilter(filterValue);
			facets[1] = facetValue;

			for (int i = 0; i < 2; i++) {
				if (facets[i] != null)
					facetsList.add(facets[i]);
			}

			StringBuilder requestJson = new StringBuilder();
			String jsonData = mapper.writeValueAsString(facetsList.toArray(new Facets[facetsList.size()]));

			requestJson.append("{");
			requestJson.append("\"facets\":" + jsonData);
			/*
			 * if(driveData.getLeftHandLimit() != null &&
			 * driveData.getRightHandLimit() != null) { requestJson.append(",");
			 * requestJson.append("\"start\":" + "\""+
			 * driveData.getLeftHandLimit() +"\""); requestJson.append(","); int
			 * size =
			 * Integer.valueOf(driveData.getRightHandLimit())-Integer.valueOf(
			 * driveData.getLeftHandLimit()); requestJson.append("\"size\":" +
			 * "\""+ size+"\""); }
			 */
			requestJson.append("}");

			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
           logger.info("Request to OpenXhange server : "+ driveSearchUrl+ " json request : "+ requestJson.toString());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code from OpenXhange server : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			conn.disconnect();
			JSONObject searchJson = new JSONObject(response);
			if (searchJson.has("error")) {
				baseResponse.setError(searchJson.get("error").toString());
			} else if (searchJson.has("data")
					&& searchJson.getJSONObject("data").getJSONArray("results").length() == 0) {
				driveResponse.put("listOfFiles", new ArrayList<>());
				baseResponse.setData(driveResponse);
			} else if (searchJson.has("data")) {
				listDriveResponse = driveSearchResponseParser.getParsedSearchDriveData(searchJson.getJSONObject("data"),
						folder);
				if (listDriveResponse.isEmpty()) {
					driveResponse.put("listOfFiles", new ArrayList<>());
					baseResponse.setData(driveResponse);
				} else {
					Collections.sort(listDriveResponse);
					List<DriveResponse> returnDriveResponse = new ArrayList<>();
					if (driveData.getRightHandLimit() != null && driveData.getLeftHandLimit() != null) {
						int startIndex = Integer.valueOf(driveData.getLeftHandLimit());
						int endIndex = Integer.valueOf(driveData.getRightHandLimit());
						int driveResponseSize = listDriveResponse.size();
						// pagination for search drive files
						if (driveResponseSize >= endIndex) {
							for (int i = startIndex; i < endIndex; i++) {
								returnDriveResponse.add(listDriveResponse.get(i));
							}
						} else {
							for (int i = startIndex; i < listDriveResponse.size(); i++) {
								returnDriveResponse.add(listDriveResponse.get(i));
							}
						}
						driveResponse.put("listOfFiles", returnDriveResponse);
						baseResponse.setData(driveResponse);
					} else {
						driveResponse.put("listOfFiles",listDriveResponse);
						baseResponse.setData(driveResponse);
					}
				}
			}

		} catch (Exception e) {
			baseResponse.setError(e.getMessage());
		}

		return baseResponse;
	}

	public BaseResponse downloadDriveFile(DriveData driveData) throws Exception{
		Attachment attachment = new Attachment();
		BaseResponse baseResponse = new BaseResponse();
		String context = String.valueOf(userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getContextId());
		String downloadUrl = "";
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();

		String folder = driveData.getFolder();
		String userId = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getUserId().toString();
		try {

			if (driveData.getFileName() == null) {

				downloadUrl = "https://mail.teaming.orange-business.com/appsuite/api/files?action=zipfolder&folder="
						+ folder + "&recursive=true&session=" + session + "&callback=yell";
			} else {
				downloadUrl = "https://mail.teaming.orange-business.com/appsuite/api/files/"
						+ URLEncoder.encode(driveData.getFileName(),"UTF-8") + "?action=document&folder=" + folder
						+ "&id=" + URLEncoder.encode(driveData.getFileObjectID(), "UTF-8") + "&version="
						+ driveData.getVersion() + "&user=" + userId + "&context=" + context + "&sequence="
						+ driveData.getTimestamp() + "&" + userId + "." + context + "." + driveData.getTimestamp()
						+ "&delivery=download&callback=yell";
			}
			URL url = new URL(downloadUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());
			OutputStream os = conn.getOutputStream();
			os.flush();
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				if(conn.getResponseCode() == 400) {
					baseResponse.setError("Your session expired. Please login again.");
					return baseResponse;
				}
				else {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
			}
			logger.info("Request to OpenXhange Server : "+ downloadUrl);
		
//			logger.info("Response Code : " + conn.getResponseCode());
//			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//
//			String output;
//			String response = "";
//			while ((output = br.readLine()) != null) {
//				response = response + output;
//			}
			
			InputStream ip = conn.getInputStream();
			byte[] buffer = IOUtils.toByteArray(ip);
			logger.info("Response Code from OpenXhange server : " + conn.getResponseCode());
			attachment.setFileName(driveData.getFileName());
			attachment.setFileContent(buffer);
			baseResponse.setData(attachment);

			conn.disconnect();
		} catch (Exception e) {
			logger.error("Exception Occured while user logging out : " + e.getMessage());
			baseResponse.setError(e.getMessage());
		}

		return baseResponse;

	}

	public BaseResponse getContextualData(DriveData driveData) {
		BaseResponse baseResponse = new BaseResponse();
		Map<String, Object> contextualData = new HashMap<>();
		Map<String, Object> responseFromGetAllDriveData = new HashMap<>();
		Map<String, Object> responseFromSharedWithMe = new HashMap<>();
		List<DriveResponse> driveFileResponse = new ArrayList<>();
		List<FolderData> driveFolderResponse = new ArrayList<>();
		int start=0;
		int end=15;
		int responseSize=0;
		int requestedSize=0;
		if(driveData.getLeftHandLimit() != null && driveData.getRightHandLimit() !=null)
		{
			 start = Integer.valueOf(driveData.getLeftHandLimit());
			 driveData.setLeftHandLimit(null);
			 end = Integer.valueOf(driveData.getRightHandLimit());
			 driveData.setRightHandLimit(null);
			 requestedSize = Integer.valueOf(end)-Integer.valueOf(start) ;
			
		}
		responseFromGetAllDriveData = allDriveFiles(driveData);
		responseFromSharedWithMe = getSharedDriveFiles(driveData);
		
		if (responseFromGetAllDriveData.get("error") != null || responseFromSharedWithMe.get("error") != null) {
			if (responseFromGetAllDriveData.get("error") != null) {
				baseResponse.setError(responseFromGetAllDriveData.get("error").toString());
			} else {
				baseResponse.setError(responseFromSharedWithMe.get("error").toString());
			}
		} else {

			if (responseFromGetAllDriveData.get("listOfFiles") != null) {
				List<DriveResponse> getAllfileResponse = (List<DriveResponse>) responseFromGetAllDriveData
						.get("listOfFiles");
				
				if (!(getAllfileResponse.isEmpty())) {
					List<DriveResponse> getFilteredAllFileResponse = driveResponseParser
							.getAllFilteredData(getAllfileResponse, "all", driveData.getToUserName(),driveData.getFileName());
					Collections.sort(getFilteredAllFileResponse);
					responseSize = getFilteredAllFileResponse.size();
					if(responseSize >= end && end!=0){
						for(int i = start ; i < end ; i++){
							driveFileResponse.add(getFilteredAllFileResponse.get(i));
						}
						
						contextualData.put("listOfFiles",driveFileResponse );
						contextualData.put("listOfFolder", new ArrayList<>());
					}
					else if(responseSize < end && start < responseSize) {
						for(int i = start ; i < responseSize ; i++){
							driveFileResponse.add(getFilteredAllFileResponse.get(i));
						}
						contextualData.put("listOfFiles", driveFileResponse);
						contextualData.put("listOfFolder", new ArrayList<>());
					}
					
					
				}
			}
				List<FolderData> getAllFolderResponse = (List<FolderData>) responseFromGetAllDriveData
						.get("listOfFolder");
				if(getAllFolderResponse!=null){
				if (!(getAllFolderResponse.isEmpty())) {
					List<FolderData> getFilteredAllFolderResponse = driveResponseParser
							.getFilteredFolderData(getAllFolderResponse, "all", driveData.getToUserName(),driveData.getFileName());
					
					
					if(requestedSize > responseSize && requestedSize!=0){
						int remaingSize = requestedSize -responseSize;
						int folderResponseSize = getFilteredAllFolderResponse.size();
						if(folderResponseSize > remaingSize  ){
							for(int j=0;j<remaingSize;j++){
								driveFolderResponse.add(getFilteredAllFolderResponse.get(j));
							}
							contextualData.put("listOfFolder", driveFolderResponse);
						}
						else{
							contextualData.put("listOfFolder", getFilteredAllFolderResponse);
						}
						if(contextualData.get("listOfFiles") == null) {
							contextualData.put("listOfFiles", new ArrayList<>());
						}
					}
					
				}
				}
				baseResponse.setData(contextualData);
			}

		return baseResponse;
	}

	
	
	
	
	
	
	public BaseResponse shareDriveFiles(DriveData driveData) {
		
	
		BaseResponse baseResponse = new BaseResponse();
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();
		String shareDriveFileUrl="";
		String userId = "";
		String response="";
		List<PermissionsData> listPermissionData = null;
		//String fileObject = driveData.getFolder()+"/"+driveData.getFileObjectID();
		try {
			shareDriveFileUrl = "https://mail.teaming.orange-business.com/appsuite/api/files?action=update&extendedResponse=true&id="+URLEncoder.encode(driveData.getFileObjectID(), "UTF-8")+"&ignoreWarnings&session="+session+"&timestamp="+driveData.getTimestamp();
			URL url = new URL(shareDriveFileUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", " application/json");
			conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());
		    userId = getUserId(driveData);
		    if(userId == null) {
		    	baseResponse.setError("UserId is not found for sharing the file");
		    	return baseResponse;
		    }
		    StringBuilder requestBody = new StringBuilder();
            StringBuilder subRequestBody = new StringBuilder();	
            String mailRequest = "{\"transport\":\"mail\"}";
		
            listPermissionData = driveData.getObjectPermissions();
            
			StringBuilder jsonData = new StringBuilder();
			jsonData.append("[");
			jsonData.append("{");
			jsonData.append("\"bits\":" + 1 );
			jsonData.append(",");
			jsonData.append("\"entity\":" + userId );
			jsonData.append(",");
			jsonData.append("\"group\":" + false);
			jsonData.append("}");
			if(listPermissionData != null) {
				for(int i=0;i<listPermissionData.size();i++) {
				jsonData.append(",");
				jsonData.append("{");
				jsonData.append("\"bits\":" + listPermissionData.get(i).getBits() );
				jsonData.append(",");
				jsonData.append("\"entity\":" + listPermissionData.get(i).getEntity() );
				jsonData.append(",");
				jsonData.append("\"group\":" + listPermissionData.get(i).getGroup());
				jsonData.append("}");
				
				}
				
			}
			jsonData.append("]");
			subRequestBody.append("{");
			subRequestBody.append("\"object_permissions\":"+ jsonData );
			subRequestBody.append("}");
			subRequestBody.append(",");
			subRequestBody.append("\"notification\":"+mailRequest);
			requestBody.append("{");
			requestBody.append("\"file\":"+ subRequestBody);
			requestBody.append("}");
		
			OutputStream op = conn.getOutputStream();
			op.write(requestBody.toString().getBytes());
			op.flush();
			logger.info("Request to OpenXchange Server : " + shareDriveFileUrl + " JsonRequest : "+ requestBody.toString());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Response Code from OpenXchange server : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}

			conn.disconnect();

			JSONObject dataValue = new JSONObject(response);
			if (dataValue.has("data")) {
				baseResponse.setData("{}");
			} else if (dataValue.has("error")) {
				baseResponse.setError(dataValue.get("error").toString());
			}
		
		}
		catch(Exception e) {
			logger.info(" Exception occured while sharing the drive files : " + e.getMessage());
			baseResponse.setError(e.getMessage());
		}
		
	
		
		return baseResponse;
	}
	
	
	public String getUserId(DriveData driveData) {
		String userId ="";
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();
		String response="";
		String userListUrl = "https://mail.teaming.orange-business.com/appsuite/api/user?action=all&session="+session+"&columns=1,555";
		try {
		URL url = new URL(userListUrl);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());
		OutputStream os = conn.getOutputStream();
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
		JSONObject userListResponse = new JSONObject(response);
		if(userListResponse.has("data")) {
			JSONArray userList = userListResponse.getJSONArray("data");
			for(int i=0; i<userList.length();i++) {
				JSONArray userInfo = userList.getJSONArray(i);
				if(userInfo.get(1).toString().equalsIgnoreCase(driveData.getRecipients()[0])) {
					userId = userInfo.get(0).toString();
					return userId;
				}
			}
			
		}else {
			userId = null;
		}
		
		
		}
		catch(Exception e) {
			logger.error("Exception Occured while getting user information : " + e.getMessage());
			userId = null;
		}
		

		return userId;
	}
}
