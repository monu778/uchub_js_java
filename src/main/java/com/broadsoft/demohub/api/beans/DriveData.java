package com.broadsoft.demohub.api.beans;

import java.util.List;

public class DriveData extends BaseRequest {
	private String folder;
	private String fileName;
	byte [] fileContent;
		private String columns;
	private String sort;
	private String order;
	private long timestamp;
	private String fileObjectID;
	private String module;
	private String createdUserId;
	private String[] recipients;
	private String leftHandLimit;
	private String rightHandLimit;
	private String start;
	private String size;
	private String version;
	private String toUserName;
	List<PermissionsData> objectPermissions;

	

	public List<PermissionsData> getObjectPermissions() {
		return objectPermissions;
	}

	public void setObjectPermissions(List<PermissionsData> objectPermissions) {
		this.objectPermissions = objectPermissions;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String[] getRecipients() {
		return recipients;
	}

	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getFileObjectID() {
		return fileObjectID;
	}

	public void setFileObjectID(String fileObjectID) {
		this.fileObjectID = fileObjectID;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}
	
	public String getLeftHandLimit() {
		return leftHandLimit;
	}

	public void setLeftHandLimit(String leftHandLimit) {
		this.leftHandLimit = leftHandLimit;
	}

	public String getRightHandLimit() {
		return rightHandLimit;
	}

	public void setRightHandLimit(String rightHandLimit) {
		this.rightHandLimit = rightHandLimit;
	}

}
