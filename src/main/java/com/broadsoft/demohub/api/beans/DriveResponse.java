package com.broadsoft.demohub.api.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriveResponse implements Comparable<DriveResponse>{
	private String fileObjectID;
	private Integer createdUserId;
	private Integer modifiedUserId;
	private long creationDate;
	private long lastModified;
	private String parentFolderId;
	private String categories;
	private Integer colorLabel;
	List<PermissionsData> objectPermissions;
	private Boolean shareable;
	private String fileTitle;
	private String url;
	private String fileName; // Displayed filename of the document.
	private String fileMIMEtype;
	private Integer fileSize;
	private Integer version;
	private String description;
	private long lockedUntil;
	private String fileMd5sum;
	private String versionComment;
	private String currentFileVersion;
	private String numberOfFileVersions;
	List<PermissionsData> extendedObjectPermissions;
	private String resourceID;
	private String sanitizedFilename;
	private long timestamp;

	public String getFileObjectID() {
		return fileObjectID;
	}

	public void setFileObjectID(String fileObjectID) {
		this.fileObjectID = fileObjectID;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public Integer getColorLabel() {
		return colorLabel;
	}

	public void setColorLabel(Integer colorLabel) {
		this.colorLabel = colorLabel;
	}

	public Boolean getShareable() {
		return shareable;
	}

	public void setShareable(Boolean shareable) {
		this.shareable = shareable;
	}

	public String getFileTitle() {
		return fileTitle;
	}

	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileMIMEtype() {
		return fileMIMEtype;
	}

	public void setFileMIMEtype(String fileMIMEtype) {
		this.fileMIMEtype = fileMIMEtype;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getLockedUntil() {
		return lockedUntil;
	}

	public void setLockedUntil(long lockedUntil) {
		this.lockedUntil = lockedUntil;
	}

	public String getFileMd5sum() {
		return fileMd5sum;
	}

	public void setFileMd5sum(String fileMd5sum) {
		this.fileMd5sum = fileMd5sum;
	}

	public List<PermissionsData> getObjectPermissions() {
		return objectPermissions;
	}

	public void setObjectPermissions(List<PermissionsData> objectPermissions) {
		this.objectPermissions = objectPermissions;
	}

	public List<PermissionsData> getExtendedObjectPermissions() {
		return extendedObjectPermissions;
	}

	public void setExtendedObjectPermissions(List<PermissionsData> extendedObjectPermissions) {
		this.extendedObjectPermissions = extendedObjectPermissions;
	}

	public Integer getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(Integer createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Integer getModifiedUserId() {
		return modifiedUserId;
	}

	public void setModifiedUserId(Integer modifiedUserId) {
		this.modifiedUserId = modifiedUserId;
	}

	
	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersionComment() {
		return versionComment;
	}

	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}

	public String getCurrentFileVersion() {
		return currentFileVersion;
	}

	public void setCurrentFileVersion(String currentFileVersion) {
		this.currentFileVersion = currentFileVersion;
	}

	public String getNumberOfFileVersions() {
		return numberOfFileVersions;
	}

	public void setNumberOfFileVersions(String numberOfFileVersions) {
		this.numberOfFileVersions = numberOfFileVersions;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	public String getSanitizedFilename() {
		return sanitizedFilename;
	}

	public void setSanitizedFilename(String sanitizedFilename) {
		this.sanitizedFilename = sanitizedFilename;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo(DriveResponse driveResponse) {
		if(this.lastModified < driveResponse.getLastModified())
			return 1;
		else if(this.lastModified > driveResponse.getLastModified())
			return -1;
		return 0;
	}

	
}
