package com.innovation.elca.web.tool.elcawebtool.controller;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.elca.web.tool.elcawebtool.request.FtpInfo;
import com.innovation.elca.web.tool.elcawebtool.request.Metadata;
import com.innovation.elca.web.tool.elcawebtool.request.Server;

@Controller
@RequestMapping("/")
public class WebUIController {

	@Autowired
	private Metadata metadata;
	
	@Autowired
	private FtpInfo ftpInfo;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String index(Model model) throws JsonGenerationException, JsonMappingException, IOException {
		model.addAttribute("metadata", metadata);
		model.addAttribute("ftpInfo", ftpInfo);
		model.addAttribute("requestHeader", metadata.getServer().buildRequestHeader());
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
        return "index";
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String updateMetadata(@ModelAttribute("metadata") Metadata metadata, Model model) throws JsonProcessingException {
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
		
		//Send signal to Axon
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(buildURL(metadata.getServer()), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()), String.class);
		
		//Upload file to DIS folder
		
		//Waiting until file handled
		
		
        return "index";
    }
	
	@RequestMapping(value="/updateRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute("metadata") Metadata metadata, Model model) throws JsonProcessingException {
		model.addAttribute("metadata", metadata);
		model.addAttribute("ftpInfo", ftpInfo);
		model.addAttribute("requestHeader", metadata.getServer().buildRequestHeader());
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
        return "index";
    }
	
	@RequestMapping(value="/updateServer", method = RequestMethod.POST)
	public String updateServer(@ModelAttribute("metadata") Metadata metadata, Model model) throws JsonProcessingException {
		model.addAttribute("metadata", metadata);
		model.addAttribute("ftpInfo", ftpInfo);
		model.addAttribute("requestHeader", metadata.getServer().buildRequestHeader());
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
        return "index";
    }
	
	public String buildURL(Server server) {
		return "http://" + server.getHost() + ":" + server.getPort() + "/" +  server.getApplication() + "/" + server.getContext() + "/" + server.getApplication();
	}
}
