package com.broadsoft.demohub.api.security;

import java.util.Date;
import org.hashids.Hashids;

public class HashEncoderDecoder {
	
	public String encode(String userName){
		Hashids hashids = new Hashids(userName,32);
		Date date = new Date();
		Long timestamp = date.getTime();
		String id = hashids.encodeHex(String.valueOf(timestamp));
		return id;
	}
	
	public String encode(int len){
		Hashids hashids = new Hashids("broadsofthubapplication",len);
		Date date = new Date();
		Long timestamp = date.getTime();
		String id = hashids.encodeHex(String.valueOf(timestamp));
		return id;
	}

}
