package com.broadsoft.demohub.api.manager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.DriveData;

@Component
public interface DriveManagerIF {

	public Map<String, Object> allDriveFiles(DriveData aDriveData);

	public BaseResponse deleteDriveFile(List<DriveData> driveData);
	
	public BaseResponse uploadDriveFile(HttpServletRequest httpRequest);

	public Map<String, Object> getSharedDriveFiles(DriveData driveData);
	
	public BaseResponse getDocumentLink(DriveData driveData);
	
	public BaseResponse shareDocument(DriveData driveData);

	public Map<String, Object> getRecentFiles(DriveData driveData);

	public BaseResponse searchdrivefiles(DriveData driveData);

	public BaseResponse downloadDriveFile(DriveData driveData) throws Exception;

	public BaseResponse getContextualData(DriveData driveData);

}
