package com.innovation.mock.tool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.Server;
import com.innovation.mock.tool.entity.ServerProfileCollection;
import com.innovation.mock.tool.util.Constants;
import com.innovation.mock.tool.util.RequestUtil;
import com.innovation.mock.tool.util.ServerUtil;

@Controller
@RequestMapping("/")
public class WebUIController {

	private static final Logger logger = LoggerFactory.getLogger(WebUIController.class);
	
	@Autowired
	private Metadata initialMetadata;

	@Autowired
	private ServerProfileCollection serverProfiles;
	
	@Value("${metadata.server.username}")
	private static String userName;
	
	@Value("${metadata.server.password}")
	private static String password;
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String getMainPage(Model model) {
		model.addAttribute(Constants.METADATA, initialMetadata);
		model.addAttribute(Constants.FILE_STATUS, "0");
        return Constants.MAIN_PAGE;
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String sendRequest(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws JsonProcessingException {
		String url = metadata.getServer().buildURL();
		logger.info("URL: " + url);
		Server currentServer = metadata.getServer();
		HttpEntity<MultiValueMap<String, String>> request = RequestUtil.buildRequestInformation(metadata, RequestUtil.buildHeaders(currentServer.getUsername(), currentServer.getPassword()));
		try {
			new RestTemplate().postForObject(url, request, String.class);
		} catch (RestClientException e) {
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
		}
        model.addAttribute(Constants.METADATA, metadata);
        model.addAttribute(Constants.FILE_STATUS, "0");
        return Constants.MAIN_PAGE;
    }

	@RequestMapping(value="/updateRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) {
		model.addAttribute(Constants.METADATA, metadata);
		model.addAttribute(Constants.FILE_STATUS, "0");
        return Constants.MAIN_PAGE;
    }
	
	@RequestMapping(value="/updateServer", method = RequestMethod.POST)
	public String updateServer(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) {
		metadata.setServer(ServerUtil.updateServerInfo(metadata.getServer(), serverProfiles));
		model.addAttribute(Constants.METADATA, metadata);
		model.addAttribute(Constants.FILE_STATUS, "0");
        return Constants.MAIN_PAGE;
    }
}
