/*
 *  Copyright © 2018 BroadSoft. All rights reserved.
 */
package com.broadsoft.demohub.api.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attachment extends BaseRequest{
	
	@JsonProperty("id")
	private String id;
	@JsonProperty("filename")
	private String fileName;
	@JsonProperty("size")
	private Integer size;
	@JsonProperty("disp")
	private String disp;
	@JsonProperty("content_type")
	private String contentType;
	@JsonProperty("content")
	private String content;
	@JsonProperty("truncated")
	private Boolean truncated;
	@JsonProperty("sanitized")
	private Boolean sanitized;
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	private byte[] fileContent;
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public String getDisp() {
		return disp;
	}
	public void setDisp(String disp) {
		this.disp = disp;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Boolean getTruncated() {
		return truncated;
	}
	public void setTruncated(Boolean truncated) {
		this.truncated = truncated;
	}
	public Boolean getSanitized() {
		return sanitized;
	}
	public void setSanitized(Boolean sanitized) {
		this.sanitized = sanitized;
	}
	public byte[] getFileContent() {
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
}
