package com.broadsoft.demohub.api.manager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.broadsoft.demohub.api.adapter.DriveAdapter;
import com.broadsoft.demohub.api.beans.BaseResponse;
import com.broadsoft.demohub.api.beans.DriveData;

@Component
public class DriveManager implements DriveManagerIF {

	@Autowired
	DriveAdapter driveAdapter;

	@Override
	public Map<String, Object> allDriveFiles(DriveData driveData) {
		return driveAdapter.allDriveFiles(driveData);
	}

	@Override
	public BaseResponse deleteDriveFile(List<DriveData> driveData) {
		return driveAdapter.deleteDriveFile(driveData);
	}

	@Override
	public BaseResponse uploadDriveFile(HttpServletRequest  httpRequest) {
		return driveAdapter.uploadDriveFile(httpRequest);
	}

	@Override
	public Map<String, Object> getSharedDriveFiles(DriveData driveData) {
		return driveAdapter.getSharedDriveFiles(driveData);
	}
	
	@Override
	public BaseResponse getDocumentLink(DriveData driveData) {
		return driveAdapter.getDocumentLink(driveData);
	}

	@Override
	public BaseResponse shareDocument(DriveData driveData) {
		return driveAdapter.shareDriveFiles(driveData);
	}
	

	
	public Map<String, Object> getRecentFiles(DriveData driveData){
		return driveAdapter.getRecentFiles(driveData);
	}
	
	
	public BaseResponse searchdrivefiles(DriveData driveData){
		return driveAdapter.searchdrivefiles(driveData);
	}
	
	public BaseResponse downloadDriveFile(DriveData driveData) throws Exception{
		return driveAdapter.downloadDriveFile(driveData);
	}
	public BaseResponse getContextualData(DriveData driveData){
		return driveAdapter.getContextualData(driveData);
	}
}
