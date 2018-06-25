package com.innovation.mock.tool.controller;

import java.io.IOException;

import org.apache.commons.net.util.Base64;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.mock.tool.entity.FtpInfo;
import com.innovation.mock.tool.entity.Metadata;

@Controller
@RequestMapping("/")
public class WebUIController {

	@Autowired
	private Metadata originMetadata;
	
	@Autowired
	private FtpInfo originFtpInfo;
		
	private ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String index(Model model) throws JsonGenerationException, JsonMappingException, IOException {
		model.addAttribute("metadata", originMetadata);
		model.addAttribute("ftpInfo", originFtpInfo);
		model.addAttribute("requestHeader", originMetadata.getServer().buildRequestHeader());
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(originMetadata.getRequest()));
        return "index";
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String updateMetadata(@ModelAttribute("metadata") Metadata metadata, Model model) throws JsonProcessingException {
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest());
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		String url = metadata.getServer().buildURL();

		String plainCreds = "makube:1234";
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		headers.add("Authorization", "Basic " + base64Creds);
		HttpEntity <String> httpEntity = new HttpEntity <String> (json.toString(), headers);

		restTemplate.postForObject(url, httpEntity, String.class);
		
		model.addAttribute("metadata", originMetadata);
		model.addAttribute("ftpInfo", originFtpInfo);
		model.addAttribute("requestHeader", originMetadata.getServer().buildRequestHeader());
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(originMetadata.getRequest()));
        return "index";
    }
	
	@RequestMapping(value="/updateRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute("metadata") Metadata metadata, Model model) throws JsonProcessingException {
		model.addAttribute("metadata", metadata);
		model.addAttribute("ftpInfo", originFtpInfo);
		model.addAttribute("requestHeader", metadata.getServer().buildRequestHeader());
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
        return "index";
    }
	
	@RequestMapping(value="/updateServer", method = RequestMethod.POST)
	public String updateServer(@ModelAttribute("metadata") Metadata metadata, Model model) throws JsonProcessingException {
		model.addAttribute("metadata", metadata);
		model.addAttribute("ftpInfo", originFtpInfo);
		model.addAttribute("requestHeader", metadata.getServer().buildRequestHeader());
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
        return "index";
    }
}
