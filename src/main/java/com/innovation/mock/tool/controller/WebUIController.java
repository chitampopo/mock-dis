package com.innovation.mock.tool.controller;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.innovation.mock.tool.entity.RequestStatus;
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
	
	@Autowired
	private FileController fileController;
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String getMainPage(Model model) {
		model.addAttribute(Constants.METADATA, initialMetadata);
        return Constants.MAIN_PAGE;
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String sendRequest(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws JsonProcessingException {
		String url = metadata.getServer().buildURL();
		HttpEntity<MultiValueMap<String, String>> request = RequestUtil.buildRequestHeader(metadata);
		try {
			new RestTemplate().postForObject(url, request, String.class);
			metadata.setRequestStatus(RequestStatus.SUCCESSFUL.getValue());
		} catch (RestClientException e) {
			metadata.setRequestStatus(RequestStatus.ERROR.getValue());
			logger.error(Arrays.toString(e.getStackTrace()));
		}
        model.addAttribute(Constants.METADATA, metadata);
        return Constants.MAIN_PAGE;
    }

	@RequestMapping(value="/submitBoth", method = RequestMethod.POST)
	public String submitBoth(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws IOException {
		sendRequest(metadata, model);
		fileController.singleFileUpload(metadata, model);
        return Constants.MAIN_PAGE;
	}
	
	@RequestMapping(value="/updateRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) {
		try {
			metadata.setServer(ServerUtil.updateServerInfo(metadata.getServer(), serverProfiles));
		} catch (Exception e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		model.addAttribute(Constants.METADATA, metadata);
        return Constants.MAIN_PAGE;
    }
}
