package com.broadsoft.demohub.api.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigManager
{
   public final static String PROPSFILE = "api.properties";
   private static Properties props;
   private static final Logger logger = Logger.getLogger(ConfigManager.class);

   
   protected Properties getProperties() throws Exception
   {
      if (props == null)
      {
         props = new Properties();
         InputStream input = null;
         try
         {
            input = ConfigManager.class.getClassLoader().getResourceAsStream(PROPSFILE);
            props.load(input);  
            
            input = ConfigManager.class.getClassLoader().getResourceAsStream(props.getProperty("HUB_PROPERTIES"));
            props.load(input);
         }
         catch (FileNotFoundException e)
         {
        	 System.out.println("File Not Found Exception ");
            throw new FileNotFoundException("property file '" + PROPSFILE + "' not found in the classpath");
            
         }
         catch (IOException e)
         {
            throw e;
         }
         finally
         {
            if (input != null)
            {
               try
               {
                  input.close();
               }
               catch (IOException e)
               {
                  e.printStackTrace();
               }
            }
         }
      }
      return props;
   }
   public String getPropertyAsString( String propertyKey )
   {
      String propertyValue = "";
      try
      {
         propertyValue = getProperties().getProperty(propertyKey);
      }
      catch (Exception ex)
      {
         logger.error("Exception while getting property value for key " + propertyKey);
      }
      return propertyValue;
   }
}
