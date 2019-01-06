package com.broadsoft.demohub.api.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.broadsoft.demohub.api.beans.DriveResponse;
import com.broadsoft.demohub.api.beans.FolderData;
import com.broadsoft.demohub.api.beans.PermissionsData;
import com.broadsoft.demohub.api.beans.UploadDriveResponse;

public class DriveResponseParser {
	private Logger logger = Logger.getLogger(DriveResponseParser.class);

	public List<DriveResponse> getParsedDriveResponse(String response) {
		logger.info("Parsing the DriveResponse");
		JSONObject dataValue = new JSONObject(response);
		List<DriveResponse> listDriveResponse = new ArrayList<DriveResponse>();
		if (dataValue.has("data")) {
			JSONArray driveResponseList = dataValue.getJSONArray("data");
			logger.info("Parsing the DriveResponse length" + driveResponseList.length());
			for (int i = 0; i < driveResponseList.length(); i++) {
				DriveResponse driveResponse = new DriveResponse();
				JSONArray driveResponseArray = driveResponseList.getJSONArray(i);
				if (driveResponseArray.getString(0) != null) {
					driveResponse.setFileObjectID(driveResponseArray.getString(0));
				}
				if (driveResponseArray.get(1) != null) {
					driveResponse.setCreatedUserId(driveResponseArray.getInt(1));
				}

				if (driveResponseArray.get(2) != null) {
					driveResponse.setModifiedUserId(driveResponseArray.getInt(2));
				}
				if (driveResponseArray.get(3) != null) {
					driveResponse.setCreationDate(driveResponseArray.getLong(3));
				}
				if (driveResponseArray.get(4) != null) {
					driveResponse.setLastModified(driveResponseArray.getLong(4));
				}
				if (driveResponseArray.getString(5) != null) {
					driveResponse.setParentFolderId(driveResponseArray.getString(5));
				}
				if (driveResponseArray.get(6) != null) {
					driveResponse.setCategories(driveResponseArray.get(6).toString());
				}
				if (driveResponseArray.get(7) != null) {
					driveResponse.setColorLabel(driveResponseArray.getInt(7));
				}
				if (driveResponseArray.getJSONArray(8) != null) {
					driveResponse.setObjectPermissions(getObjectPermissionArray(driveResponseArray.getJSONArray(8)));
				}
				if (driveResponseArray.get(9) != null) {
					driveResponse.setShareable(driveResponseArray.getBoolean(9));
				}
				if (driveResponseArray.get(10) != null) {
					driveResponse.setFileTitle(driveResponseArray.get(10).toString());
				}
				if (driveResponseArray.get(11) != null) {
					driveResponse.setUrl(driveResponseArray.get(11).toString());
				}
				if (driveResponseArray.get(12) != null) {
					driveResponse.setFileName(driveResponseArray.get(12).toString());
				}
				if (driveResponseArray.get(13) != null) {
					driveResponse.setFileMIMEtype(driveResponseArray.get(13).toString());
				}
				if (driveResponseArray.get(14) != null) {
					driveResponse.setFileSize(driveResponseArray.getInt(14));
				}
				if (driveResponseArray.get(15) != null) {
					driveResponse.setVersion(driveResponseArray.getInt(15));
				}
				if (driveResponseArray.get(16) != null) {
					driveResponse.setDescription(driveResponseArray.get(16).toString());
				}
				if (driveResponseArray.get(17) != null) {
					driveResponse.setLockedUntil(driveResponseArray.getLong(17));
				}
				if (driveResponseArray.get(18) != null) {
					driveResponse.setFileMd5sum(driveResponseArray.get(18).toString());
				}

				if (driveResponseArray.get(19) != null) {
					driveResponse.setVersionComment(driveResponseArray.get(19).toString());
				}

				if (driveResponseArray.get(20) != null) {
					driveResponse.setCurrentFileVersion(driveResponseArray.get(20).toString());
				}

				if (driveResponseArray.get(21) != null) {
					driveResponse.setNumberOfFileVersions(driveResponseArray.get(21).toString());
				}

				if (getObjectPermissionArray(driveResponseArray.getJSONArray(22)) != null) {
					driveResponse.setExtendedObjectPermissions(
							getObjectPermissionArray(driveResponseArray.getJSONArray(22)));
				}

				if (driveResponseArray.get(23) != null) {
					driveResponse.setResourceID(driveResponseArray.get(23).toString());
				}

				if (driveResponseArray.get(24) != null) {
					driveResponse.setSanitizedFilename(driveResponseArray.get(24).toString());
				}
				driveResponse.setTimestamp(dataValue.getLong("timestamp"));
				listDriveResponse.add(driveResponse);
			}
		}
		logger.info("End of Parsing the DriveResponse");
		return listDriveResponse;

	}

	private List<PermissionsData> getObjectPermissionArray(JSONArray arrayOfDriveResponse) {
		List<PermissionsData> listPermissionsData = new ArrayList<>();
		for (int i = 0; i < arrayOfDriveResponse.length(); i++) {
			PermissionsData permissionsData = new PermissionsData();
			JSONObject jsonFileObject = arrayOfDriveResponse.getJSONObject(i);
			if (jsonFileObject.has("entity")) {
				permissionsData.setEntity(jsonFileObject.getInt("entity"));
			}
			if (jsonFileObject.has("group")) {
				permissionsData.setGroup(jsonFileObject.getBoolean("group"));
			}
			if (jsonFileObject.has("bits")) {
				permissionsData.setBits(jsonFileObject.getInt("bits"));
			}

			if (jsonFileObject.has("rights")) {
				permissionsData.setRights(jsonFileObject.getString("rights"));
			}
			if (jsonFileObject.has("type")) {
				permissionsData.setType(jsonFileObject.getString("type"));
			}
			if (jsonFileObject.has("password")) {
				permissionsData.setPassword(jsonFileObject.getString("password"));
			}
			if(jsonFileObject.has("contact")){
				JSONObject contactJosn = jsonFileObject.getJSONObject("contact");
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
			if (jsonFileObject.has("display_name")) {
				permissionsData.setDisplay_name(jsonFileObject.getString("display_name"));
			}
			if (jsonFileObject.has("contact_id")) {
				permissionsData.setContact_id(jsonFileObject.getString("contact_id"));
			}
			if (jsonFileObject.has("contact_folder")) {
				permissionsData.setContact_folder(jsonFileObject.getString("contact_folder"));
			}
			if (jsonFileObject.has("expiry_date")) {
				permissionsData.setExpiry_date(jsonFileObject.getString("expiry_date"));
			}
			if (jsonFileObject.has("share_url")) {
				permissionsData.setShare_url(jsonFileObject.getString("share_url"));
			}
			listPermissionsData.add(permissionsData);

		}
		return listPermissionsData;
	}

	public UploadDriveResponse parseUploadFileResponse(JSONObject uploadedFileJsonResponse) {
		UploadDriveResponse uploadDriveResponse = new UploadDriveResponse();

		if (uploadedFileJsonResponse.has("save_action")) {
			uploadDriveResponse.setSaveAction(uploadedFileJsonResponse.get("save_action").toString());
		}
		if (uploadedFileJsonResponse.has("file")) {
			JSONObject fileJson = uploadedFileJsonResponse.getJSONObject("file");
			if (fileJson.has("last_modified")) {
				uploadDriveResponse.setLastModified(fileJson.getLong("last_modified"));
			}

			if (fileJson.has("creation_date")) {
				uploadDriveResponse.setCreationDate(fileJson.getLong("creation_date"));
			}

			if (fileJson.has("modified_by")) {
				uploadDriveResponse.setModifiedUserId(fileJson.getInt("modified_by"));
			}

			if (fileJson.has("folder_id")) {
				uploadDriveResponse.setParentFolderId(fileJson.get("folder_id").toString());
			}

			if (fileJson.has("title")) {
				uploadDriveResponse.setFileTitle(fileJson.get("title").toString());
			}

			if (fileJson.has("version")) {
				uploadDriveResponse.setVersion(fileJson.getInt("version"));
			}

			if (fileJson.has("id")) {
				uploadDriveResponse.setFileObjectID(fileJson.get("id").toString());
			}

			if (fileJson.has("file_size")) {
				uploadDriveResponse.setFileSize(fileJson.getInt("file_size"));
			}

			if (fileJson.has("description")) {
				uploadDriveResponse.setDescription(fileJson.get("description").toString());
			}

			if (fileJson.has("filename")) {
				uploadDriveResponse.setFileName(fileJson.get("filename").toString());
			}

			if (fileJson.has("file_mimetype")) {
				uploadDriveResponse.setFileMIMEtype(fileJson.get("file_mimetype").toString());
			}

			if (fileJson.has("locked_until")) {
				uploadDriveResponse.setLockedUntil(fileJson.getInt("locked_until"));
			}

			if (fileJson.has("file_md5sum")) {
				uploadDriveResponse.setFileMd5sum(fileJson.get("file_md5sum").toString());
			}

			if (fileJson.has("current_version")) {
				uploadDriveResponse.setCurrentFileVersion(fileJson.get("current_version").toString());
			}

			if (fileJson.has("color_label")) {
				uploadDriveResponse.setColorLabel(fileJson.getInt("color_label"));
			}

			if (fileJson.has("last_modified_utc")) {
				uploadDriveResponse.setLockedUntil(fileJson.getLong("last_modified_utc"));
			}

			if (fileJson.has("number_of_versions")) {
				uploadDriveResponse
						.setNumberOfFileVersions(fileJson.get("number_of_versions").toString());
			}

			if (fileJson.has("com.openexchange.realtime.resourceID")) {
				uploadDriveResponse
						.setResourceID(fileJson.get("com.openexchange.realtime.resourceID").toString());
			}

			if (fileJson.has("com.openexchange.file.sanitizedFilename")) {
				uploadDriveResponse.setSanitizedFilename(
						fileJson.get("com.openexchange.file.sanitizedFilename").toString());
			}

			if (fileJson.has("shareable")) {
				uploadDriveResponse.setShareable(fileJson.getBoolean("shareable"));
			}

		}

		return uploadDriveResponse;
	}

	public List<DriveResponse> getAllFilteredData(List<DriveResponse> getAllfileResponse, String category, String toUserName, String searchName) {
		List<DriveResponse> listOfFilteredData = new ArrayList<DriveResponse>(); 
		
		for(int i=0;i<getAllfileResponse.size();i++){
			DriveResponse driveResponse = getAllfileResponse.get(i);
			
			if(driveResponse.getExtendedObjectPermissions() != null){
			List<PermissionsData> listExtendedObjects = driveResponse.getExtendedObjectPermissions();
			for(int j=0; j<listExtendedObjects.size(); j++){
				PermissionsData extendedpermissionsData = listExtendedObjects.get(j);
				if(extendedpermissionsData.getEmail_address() != null && toUserName != null && toUserName.equalsIgnoreCase(extendedpermissionsData.getEmail_address())){
					if(searchName != null) {
						if(driveResponse.getFileName().toUpperCase().contains(searchName.toUpperCase()))
						{
							listOfFilteredData.add(getAllfileResponse.get(i));
			            }
					}
					else {
					listOfFilteredData.add(getAllfileResponse.get(i));
					}
				}
			}
			
			}
		}
		
		return listOfFilteredData;
	}

	public List<FolderData> getFilteredFolderData(List<FolderData> getAllFolderResponse, String category,
			String toUserName,String searchString) {
       List<FolderData> listOfFilteredData = new ArrayList<FolderData>(); 
		for(int i=0;i<getAllFolderResponse.size();i++){
			FolderData folderData = getAllFolderResponse.get(i);
		
			if(folderData.getExtendedObjectPermissions() != null){
			List<PermissionsData> listExtendedObjects = folderData.getExtendedObjectPermissions();
			for(int j=0; j<listExtendedObjects.size(); j++){
				PermissionsData extendedpermissionsData = listExtendedObjects.get(j);
				if(extendedpermissionsData.getEmail_address() != null && toUserName != null && toUserName.equalsIgnoreCase(extendedpermissionsData.getEmail_address())){
					if(searchString != null) {
						if(folderData.getFolder_name().contains(searchString.toLowerCase()) || (folderData.getFolder_name().contains(searchString.toUpperCase()))){
							listOfFilteredData.add(getAllFolderResponse.get(i));
						}
					}
					else {
					listOfFilteredData.add(getAllFolderResponse.get(i));
					}
			}
			}
			}
		}
		
		return listOfFilteredData;
	}

}
