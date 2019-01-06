package com.broadsoft.demohub.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class BaseResponse {
		private Object data;
		private String error;
		private String success;

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

		public String getSuccess() {
			return success;
		}

		public void setSuccess(String success) {
			this.success = success;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

		
	}
