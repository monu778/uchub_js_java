package com.broadsoft.demohub.api.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FolderData extends MailData implements Comparable<FolderData>{

	private String title;
	private String module;
	private Number type;
	private Boolean subfolders;
	private String own_rights;
	private PermissionsData[] permission;
	private String summary;
	private Boolean standard_folder;
	private Number total;
	private String displayName;
	private FolderData[] folderDataArray;
	private Number newObject;
	private Number unreadObjects;
	private Number deletedObjects;
	private Number capabilities;
	private Boolean subscribed;
	private Boolean subscr_subflds;
	private String accountId;
	private String folder_name;
	private String folderId;
	private String createdUserId;
	private String modifiedUserId;
	private Long creationDate;
	private Long lastModified;
	private String parentFolderId;
	List<PermissionsData> extendedObjectPermissions;
	public List<PermissionsData> getExtendedObjectPermissions() {
		return extendedObjectPermissions;
	}

	public void setExtendedObjectPermissions(List<PermissionsData> extendedObjectPermissions) {
		this.extendedObjectPermissions = extendedObjectPermissions;
	}

	private String errorMessage;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Number getType() {
		return type;
	}

	public void setType(Number type) {
		this.type = type;
	}

	public Boolean getSubfolders() {
		return subfolders;
	}

	public void setSubfolders(Boolean subfolders) {
		this.subfolders = subfolders;
	}

	public String getOwn_rights() {
		return own_rights;
	}

	public void setOwn_rights(String own_rights) {
		this.own_rights = own_rights;
	}

	public PermissionsData[] getPermission() {
		return permission;
	}

	public void setPermission(PermissionsData[] permission) {
		this.permission = permission;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Boolean getStandard_folder() {
		return standard_folder;
	}

	public void setStandard_folder(Boolean standard_folder) {
		this.standard_folder = standard_folder;
	}

	public Number getTotal() {
		return total;
	}

	public void setTotal(Number total) {
		this.total = total;
	}

	public FolderData[] getFolderDataArray() {
		return folderDataArray;
	}

	public void setFolderDataArray(FolderData[] folderDataArray) {
		this.folderDataArray = folderDataArray;
	}

	public Number getNewObject() {
		return newObject;
	}

	public void setNewObject(Number newObject) {
		this.newObject = newObject;
	}

	public Number getUnreadObjects() {
		return unreadObjects;
	}

	public void setUnreadObjects(Number unreadObjects) {
		this.unreadObjects = unreadObjects;
	}

	public Number getDeletedObjects() {
		return deletedObjects;
	}

	public void setDeletedObjects(Number deletedObjects) {
		this.deletedObjects = deletedObjects;
	}

	public Number getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Number capabilities) {
		this.capabilities = capabilities;
	}

	public Boolean getSubscribed() {
		return subscribed;
	}

	public void setSubscribed(Boolean subscribed) {
		this.subscribed = subscribed;
	}

	public Boolean getSubscr_subflds() {
		return subscr_subflds;
	}

	public void setSubscr_subflds(Boolean subscr_subflds) {
		this.subscr_subflds = subscr_subflds;
	}

	public String getFolder_name() {
		return folder_name;
	}

	public void setFolder_name(String folder_name) {
		this.folder_name = folder_name;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	public String getModifiedUserId() {
		return modifiedUserId;
	}

	public void setModifiedUserId(String modifiedUserId) {
		this.modifiedUserId = modifiedUserId;
	}

	public String getParentFolderId() {
		return parentFolderId;
	}

	public void setParentFolderId(String parentFolderId) {
		this.parentFolderId = parentFolderId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public int compareTo(FolderData folderData) {
		return this.folder_name.compareTo(folderData.getFolder_name());
	}

}
