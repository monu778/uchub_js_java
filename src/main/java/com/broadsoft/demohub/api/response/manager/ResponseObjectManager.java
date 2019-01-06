package com.broadsoft.demohub.api.response.manager;

import org.springframework.beans.factory.annotation.Autowired;

import com.broadsoft.demohub.api.beans.Status;


public class ResponseObjectManager {
	
	@Autowired
	Status status;
	
	public Status getSuccessStatus()
	   {
	      status.setStatusCode("S0001");
	      status.setStatusMsg("Success");
	      return status;
	   }

}
