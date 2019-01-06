package com.broadsoft.demohub.api.adapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.DriveData;
import com.broadsoft.demohub.api.beans.FolderData;
import com.broadsoft.demohub.api.beans.PermissionsData;
import com.broadsoft.demohub.api.beans.UserDataBean;
import com.broadsoft.demohub.api.config.ConfigManager;

@Component
public class FolderAdapter {

	@Autowired
	ConfigManager configManager;

	@Autowired
	UserDataBean userDataBean;

	private Logger logger = Logger.getLogger(FolderAdapter.class);

	public String getSubFolders(FolderData folderData) throws Exception {
		String response = "";
		StringBuffer input = new StringBuffer("");
		String openExchangeMailUrl = configManager.getPropertyAsString("OX_FOLDER_URL");
		String columns = null;
		String unseen = "false";
		String session = "";

		String cookie = "";
		try {

			if (folderData.getModule() == null || folderData.getModule().equalsIgnoreCase("infostore")) {
				session = userDataBean.getUserDriveDataMap().get(folderData.getAuth()).getSession();
				cookie = userDataBean.getUserDriveDataMap().get(folderData.getAuth()).getCookie();
			} else {
				session = userDataBean.getUserDataMap().get(folderData.getAuth()).getSession();
				cookie = userDataBean.getUserDataMap().get(folderData.getAuth()).getCookie();
			}
			URL url = new URL(openExchangeMailUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Cookie", cookie);
			if (folderData.getColumns() == null) {
				columns = configManager.getPropertyAsString("FOLDER_COLUMNS");
			} else {
				columns = folderData.getColumns();
			}

			input.append("action=list");
			input.append("&columns=");
			input.append(columns);
			input.append("&session=");
			input.append(URLEncoder.encode(session, "UTF-8"));
			input.append("&parent=");
			input.append(folderData.getTitle());
			if (folderData.getModule() != null) {
				input.append("&allowed_modules=");
				input.append(folderData.getModule());
			}

			OutputStream os = conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();
			logger.info("Response Code from OpenXchange server : " + response);
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				logger.error("Error Response Code : " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			logger.info("Request to OpenXchange server : " + input);
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				response = response + output;
			}
			logger.info("Response Code from OpenXchange server : " + response);
			conn.disconnect();

		} catch (Exception e) {
			logger.error("Exception Occured while getting sub folders : " + e.getMessage());
			if (null == session)
				response = "{\"error\":\"User Not Logged In\"}";
		}
		return response;
	}

	List<FolderData> getAllVisibleFolder(DriveData driveData, String contentType) {

		String response = "";
		String input = null;
		String openExchangeFolderUrl = configManager.getPropertyAsString("OX_FOLDER_URL");
		String session = userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getSession();
		String columns = configManager.getPropertyAsString("FOLDER_COLUMNS");
		JSONObject dataValue;
		ArrayList<FolderData> visibleFolderDetails = new ArrayList();

		FolderData folderData = new FolderData();
		try {
			URL url = new URL(openExchangeFolderUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			conn.setRequestProperty("Cookie", userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());
			logger.info("userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie() :: "
					+ userDataBean.getUserDriveDataMap().get(driveData.getAuth()).getCookie());

			input = "action=" + URLEncoder.encode("allVisible", "UTF-8") + "&session="
					+ URLEncoder.encode(session, "UTF-8") + "&content_type=" + URLEncoder.encode(contentType, "UTF-8")
					+ "&columns=" + URLEncoder.encode(columns, "UTF-8");
			logger.info("Request to orange server : " + input);
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

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
			dataValue = new JSONObject(response);
			logger.info("Response from  OpenXchange server : " + dataValue.toString());
			
			if (dataValue.has("error_desc")) {
				folderData.setErrorMessage(dataValue.get("error_desc").toString());
				visibleFolderDetails.add(folderData);
			} else if (dataValue.has("data")) {
				if (dataValue.getJSONObject("data").has("public")) {
					JSONArray visiblePublicFoldersArray = dataValue.getJSONObject("data").getJSONArray("public");
					listVisibleFolder(visibleFolderDetails, visiblePublicFoldersArray);
				}
				if (dataValue.getJSONObject("data").has("private")) {
					JSONArray visiblePrivateFoldersArray = dataValue.getJSONObject("data").getJSONArray("private");
					listVisibleFolder(visibleFolderDetails, visiblePrivateFoldersArray);
				}
			}

		} catch (Exception e) {
			logger.info("inside of  the getAllVisibleFolder method exception");
			if (null == input) {
				folderData.setErrorMessage("User Not Logged In");
				visibleFolderDetails.add(folderData);
				return visibleFolderDetails;
			}
			else{
			folderData.setErrorMessage(e.getMessage());
			visibleFolderDetails.add(folderData);
			return visibleFolderDetails;
			}
		}
		logger.info("end of  the getAllVisibleFolder method");
		return visibleFolderDetails;

	}

	public void listVisibleFolder(List<FolderData> visibleFolderDetails, JSONArray visiblePublicFoldersArray) {
		logger.info("inside the listVisibleFolder method");
		List<String> listOfFolder = new ArrayList<>();
		listOfFolder.add("Documents");
		listOfFolder.add("Music");
		listOfFolder.add("Pictures");
		listOfFolder.add("Videos");

		if (visiblePublicFoldersArray != null) {
			for (int i = 0; i < visiblePublicFoldersArray.length(); i++) {

				FolderData folderData = new FolderData();
				if (!(listOfFolder.contains(visiblePublicFoldersArray.getJSONArray(i).get(0).toString()))) {
					folderData.setFolder_name(visiblePublicFoldersArray.getJSONArray(i).get(0).toString()); // adds
					// name
					// of
					// this
					// folder
					folderData.setModule(visiblePublicFoldersArray.getJSONArray(i).get(1).toString()); // Name
																										// of
																										// the
																										// module
																										// e.g
					// task,infostore
					folderData.setType(visiblePublicFoldersArray.getJSONArray(i).getInt(2)); // Type
																								// of
																								// folder
																								// private,public
																								// or
					// shared
					folderData.setSubfolders(visiblePublicFoldersArray.getJSONArray(i).getBoolean(3)); // true
																										// if
																										// folder
																										// has
					// subfolders
					folderData.setOwn_rights(visiblePublicFoldersArray.getJSONArray(i).get(4).toString());
					// folderData.setPermission(visiblePublicFoldersArray.getJSONArray(i).get(5));

					folderData.setSummary(visiblePublicFoldersArray.getJSONArray(i).get(6).toString());

					folderData.setStandard_folder(visiblePublicFoldersArray.getJSONArray(i).getBoolean(7));

					folderData.setTotal(visiblePublicFoldersArray.getJSONArray(i).getInt(8));

					String displayName = visiblePublicFoldersArray.getJSONArray(i).optString(9);
					if (!displayName.isEmpty()) {
						folderData.setDisplayName(displayName);
					}

					String folderId = visiblePublicFoldersArray.getJSONArray(i).optString(10);
					if (!folderId.isEmpty()) {
						folderData.setFolder_id(folderId);
					}

					String parentFolderId = visiblePublicFoldersArray.getJSONArray(i).optString(11);
					if (!parentFolderId.isEmpty()) {
						folderData.setParentFolderId(parentFolderId);
					}

					String createdUserId = visiblePublicFoldersArray.getJSONArray(i).optString(12);
					if (!createdUserId.isEmpty()) {
						folderData.setCreatedUserId(createdUserId);
					}

					String modifiedUserId = visiblePublicFoldersArray.getJSONArray(i).optString(13);
					if (!modifiedUserId.isEmpty()) {
						folderData.setModifiedUserId(modifiedUserId);
					}

					Long creationDate = visiblePublicFoldersArray.getJSONArray(i).optLong(14);
					if (creationDate != 0) {
						folderData.setCreationDate(creationDate);
					}

					Long lastModificationDate = visiblePublicFoldersArray.getJSONArray(i).optLong(14);
					if (lastModificationDate != 0) {
						folderData.setLastModified(lastModificationDate);
					}
					
					if(visiblePublicFoldersArray.getJSONArray(i).get(16)!=null){
						List<PermissionsData> listPermissionsData = new ArrayList<>();
						JSONArray extendedPermissionArray = visiblePublicFoldersArray.getJSONArray(i).getJSONArray(16);
						for (int j = 0; j < extendedPermissionArray.length(); j++) {
							PermissionsData permissionsData = new PermissionsData();
							JSONObject extendedPermission = extendedPermissionArray.getJSONObject(j);
						if (extendedPermission.has("entity")) {
							permissionsData.setEntity(extendedPermission.getInt("entity"));
						}
						if (extendedPermission.has("display_name")) {
							permissionsData.setDisplay_name(extendedPermission.getString("display_name"));
						}
						if (extendedPermission.has("bits")) {
							permissionsData.setBits(extendedPermission.getInt("bits"));
						}
						if (extendedPermission.has("type")) {
							permissionsData.setType(extendedPermission.getString("type"));
						}
						
						if(extendedPermission.has("contact")){
							JSONObject contactJosn = extendedPermission.getJSONObject("contact");
						if (contactJosn.has("email1")) {
							permissionsData.setEmail_address(contactJosn.getString("email1"));
						}
						if (contactJosn.has("last_name")) {
							permissionsData.setLastName(contactJosn.getString("last_name"));
						}
						
						if (contactJosn.has("first_name")) {
							permissionsData.setFirstName(contactJosn.getString("first_name"));
						}
						
						}
						listPermissionsData.add(permissionsData);
						
						}
						folderData.setExtendedObjectPermissions(listPermissionsData);
					}
					visibleFolderDetails.add(folderData);

				}
			}
		}
		logger.info("end of  the listVisibleFolder method");
	}

}
