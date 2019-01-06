/*
 *  Copyright © 2018 BroadSoft. All rights reserved.
 */
package com.broadsoft.demohub.api.beans;

public class SecurityInfo {
	private boolean encrypted;
	private boolean signed;
	
	public boolean isEncrypted() {
		return encrypted;
	}
	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}
	public boolean isSigned() {
		return signed;
	}
	public void setSigned(boolean signed) {
		this.signed = signed;
	}
}
