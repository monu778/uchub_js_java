/*
 *  Copyright © 2018 BroadSoft. All rights reserved.
 */
package com.broadsoft.demohub.api.response.manager;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;

import com.broadsoft.demohub.api.service.HubService;
public class ResponseBuilder
{
   private Logger logger = Logger.getLogger(ResponseBuilder.class);
   public Response buildResponse( Object obj )
   {
      return Response
            .status(Response.Status.OK)
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST , PUT , DELETE , HEAD , PATCH , OPTIONS")
            .header("Access-Control-Allow-Headers",
                  "Origin, X-Requested-With, Content-Type, Accept, Authorization,"
                  + "CHECKOUT,CONNECT,COPY,DELETE,GET,HEAD,LOCK,M-SEARCH,MERGE,MKACTIVITY,MKCALENDAR,MKCOL,"
                  + "MOVE,NOTIFY,PATCH,POST,PROPFIND,PROPPATCH,PURGE,PUT,REPORT,SEARCH,SUBSCRIBE,TRACE,UNLOCK,UNSUBSCRIBE")
            .header("Access-Control-Allow-Credentials", "true").entity(obj).build();
   }
   
   public Response buildResponse( Object obj , String key , String value )
   {
      return Response
            .status(Response.Status.OK)
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST , PUT , DELETE , HEAD , PATCH , OPTIONS")
            .header("Access-Control-Allow-Headers",
                  "Origin, X-Requested-With, Content-Type, Accept, Authorization,"
                  + "CHECKOUT,CONNECT,COPY,DELETE,GET,HEAD,LOCK,M-SEARCH,MERGE,MKACTIVITY,MKCALENDAR,MKCOL,"
                  + "MOVE,NOTIFY,PATCH,POST,PROPFIND,PROPPATCH,PURGE,PUT,REPORT,SEARCH,SUBSCRIBE,TRACE,UNLOCK,UNSUBSCRIBE")
            .header("Access-Control-Allow-Credentials", "true")
            .header(key, value).entity(obj).build();
   }
   
   public Response buildResponse( UriBuilder builder) 
   {
	   return Response.seeOther(builder.build())
			   .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Methods", "GET, POST , PUT , DELETE , HEAD , PATCH , OPTIONS")
	            .header("Access-Control-Allow-Headers",
	                  "Origin, X-Requested-With, Content-Type, Accept, Authorization,"
	                  + "CHECKOUT,CONNECT,COPY,DELETE,GET,HEAD,LOCK,M-SEARCH,MERGE,MKACTIVITY,MKCALENDAR,MKCOL,"
	                  + "MOVE,NOTIFY,PATCH,POST,PROPFIND,PROPPATCH,PURGE,PUT,REPORT,SEARCH,SUBSCRIBE,TRACE,UNLOCK,UNSUBSCRIBE")
	            .header("Access-Control-Allow-Credentials", "true").build();
   }
   
   public Response buildResponse( URI uri) 
   {
	   return Response.seeOther(uri)
			    .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Methods", "GET, POST , PUT , DELETE , HEAD , PATCH , OPTIONS")
	            .header("Access-Control-Allow-Headers",
	                  "Origin, X-Requested-With, Content-Type, Accept, Authorization,"
	                  + "CHECKOUT,CONNECT,COPY,DELETE,GET,HEAD,LOCK,M-SEARCH,MERGE,MKACTIVITY,MKCALENDAR,MKCOL,"
	                  + "MOVE,NOTIFY,PATCH,POST,PROPFIND,PROPPATCH,PURGE,PUT,REPORT,SEARCH,SUBSCRIBE,TRACE,UNLOCK,UNSUBSCRIBE")
	            .header("Access-Control-Allow-Credentials", "true").build();
	   
   }
   
   public Response buildResponse( byte[] buffer, long contentLength , String filename )
   {
      return Response
            .ok(buffer, MediaType.APPLICATION_OCTET_STREAM)
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST , PUT , DELETE , HEAD , PATCH , OPTIONS")
            .header("Accept-Ranges", "none")
            .header("Access-Control-Allow-Headers",
                  "Origin, X-Requested-With, Content-Type, Accept, Authorization")
            .header("Content-Disposition", "attachment; filename="+"\""+filename+"\"" )
            .header("Access-Control-Allow-Credentials", "true").build();
   }
   
   public Response buildNoContentAvailableResponse()
   {
      return Response.status(Response.Status.NO_CONTENT).build();
   }
}
