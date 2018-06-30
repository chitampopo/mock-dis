package com.innovation.mock.tool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.util.Constants;
import com.innovation.mock.tool.util.RequestUtil;
import com.innovation.mock.tool.util.ServerUtil;

@Controller
@RequestMapping("/")
public class WebUIController {

	@Autowired
	private Metadata initialMetadata;
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String getMainPage(Model model) {
		model.addAttribute(Constants.METADATA, initialMetadata);
        return Constants.MAIN_PAGE;
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String sendRequest(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws JsonProcessingException {
		String url = metadata.getServer().buildURL();
		HttpEntity<MultiValueMap<String, String>> request = RequestUtil.buildRequestInformation(metadata, RequestUtil.buildHeaders());
		
		new RestTemplate().postForObject(url, request, String.class);
        model.addAttribute(Constants.METADATA, metadata);
        return Constants.MAIN_PAGE;
    }

	@RequestMapping(value="/updateRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) {
		model.addAttribute(Constants.METADATA, metadata);
        return Constants.MAIN_PAGE;
    }
	
	@RequestMapping(value="/updateServer", method = RequestMethod.POST)
	public String updateServer(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) {
		metadata.setServer(ServerUtil.updateServerInfo(metadata.getServer()));
		model.addAttribute(Constants.METADATA, metadata);
        return Constants.MAIN_PAGE;
    }
}
