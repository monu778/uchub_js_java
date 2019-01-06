package com.broadsoft.demohub.api.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.broadsoft.demohub.api.beans.DriveResponse;

public class DriveSearchResponseParser {

	public List<DriveResponse> getParsedSearchDriveData(JSONObject searchJsonData, String folder) {
		List<DriveResponse> driveResponseList = new ArrayList<>();
		if (searchJsonData.has("results")) {
			JSONArray fileJsonArray = searchJsonData.getJSONArray("results");
			for (int i = 0; i < fileJsonArray.length(); i++) {
				JSONObject searchJson = fileJsonArray.getJSONObject(i);
				DriveResponse searchResponse = new DriveResponse();
				String fileObjectId = searchJson.get("id").toString().substring(searchJson.get("id").toString().indexOf("/"));
				if(searchJson.has("id") && searchJson.get("id").toString().equals(folder+fileObjectId)){
				if (searchJson.has("last_modified")) {
					searchResponse.setLastModified(searchJson.getLong("last_modified"));
				}

				if (searchJson.has("creation_date")) {
					searchResponse.setCreationDate(searchJson.getLong("creation_date"));
				}

				if (searchJson.has("modified_by")) {
					searchResponse.setModifiedUserId(searchJson.getInt("modified_by"));
				}

				if (searchJson.has("folder_id")) {
					searchResponse.setParentFolderId(searchJson.get("folder_id").toString());
				}

				if (searchJson.has("title")) {
					searchResponse.setFileTitle(searchJson.get("title").toString());
				}

				if (searchJson.has("version")) {
					searchResponse.setVersion(searchJson.getInt("version"));
				}

				if (searchJson.has("id")) {
					searchResponse.setFileObjectID(searchJson.get("id").toString());
				}

				if (searchJson.has("file_size")) {
					searchResponse.setFileSize(searchJson.getInt("file_size"));
				}

				if (searchJson.has("description")) {
					searchResponse.setDescription(searchJson.get("description").toString());
				}

				if (searchJson.has("filename")) {
					searchResponse.setFileName(searchJson.get("filename").toString());
				}

				if (searchJson.has("file_mimetype")) {
					searchResponse.setFileMIMEtype(searchJson.get("file_mimetype").toString());
				}

				if (searchJson.has("locked_until")) {
					searchResponse.setLockedUntil(searchJson.getInt("locked_until"));
				}

				if (searchJson.has("file_md5sum")) {
					searchResponse.setFileMd5sum(searchJson.get("file_md5sum").toString());
				}

				if (searchJson.has("current_version")) {
					searchResponse.setCurrentFileVersion(searchJson.get("current_version").toString());
				}

				if (searchJson.has("color_label")) {
					searchResponse.setColorLabel(searchJson.getInt("color_label"));
				}

				if (searchJson.has("last_modified_utc")) {
					searchResponse.setLockedUntil(searchJson.getLong("last_modified_utc"));
				}

				if (searchJson.has("number_of_versions")) {
					searchResponse.setNumberOfFileVersions(searchJson.get("number_of_versions").toString());
				}

				if (searchJson.has("com.openexchange.realtime.resourceID")) {
					searchResponse.setResourceID(searchJson.get("com.openexchange.realtime.resourceID").toString());
				}

				if (searchJson.has("com.openexchange.file.sanitizedFilename")) {
					searchResponse
							.setSanitizedFilename(searchJson.get("com.openexchange.file.sanitizedFilename").toString());
				}

				if (searchJson.has("shareable")) {
					searchResponse.setShareable(searchJson.getBoolean("shareable"));
				}
				driveResponseList.add(searchResponse);
				}
			}
		}
		return driveResponseList;

	}
}
