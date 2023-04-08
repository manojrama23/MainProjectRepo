package com.smart.rct;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.smart.rct.constants.Constants;
import com.smart.rct.util.LoadPropertyFiles;

@Configuration
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	  StringBuilder custIconSavePath = new StringBuilder();
	  custIconSavePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
		.append(Constants.CUSTOMER).append(Constants.CUSTOMER_ICON_SAVE_PATH);
	 registry.addResourceHandler(Constants.CUSTOMER_ICON_GET_PATH+"**").addResourceLocations("file://" + custIconSavePath.toString());
  }
}