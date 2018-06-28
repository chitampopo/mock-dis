package com.innovation.mock.tool.controller;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.net.util.Base64;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.mock.tool.entity.Constants;
import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.Server;
import com.innovation.mock.tool.entity.ServerProduct;
import com.innovation.mock.tool.entity.ServerProductConfig;
import com.innovation.mock.tool.entity.WebidResults;

@Controller
@RequestMapping("/")
public class WebUIController {

	@Autowired
	private Metadata originMetadata;
	
	@Autowired
	private ServerProductConfig serverProductConfig;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String index(Model model) throws JsonGenerationException, JsonMappingException, IOException {
		model.addAttribute(Constants.METADATA, originMetadata);
		model.addAttribute(Constants.REQUEST_HEADER, originMetadata.getServer().buildRequestHeader());
		model.addAttribute(Constants.REQUEST_BODY, originMetadata.getRequest().toRequestBody());
        return "index";
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String updateMetadata(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws JsonProcessingException {
		WebidResults results = new WebidResults();
		results.setWebid_result(metadata.getRequest());
		String json = mapper.writeValueAsString(results);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		//Authentication
		String plainCreds = "makube:1234";
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		headers.add("Authorization", "Basic " + base64Creds);

		//Build request
		MultiValueMap<String, String> parts = new LinkedMultiValueMap<String, String>();
        parts.add("webid_response", json);
        parts.add("your_transaction_id", metadata.getRequest().getTransaction_id());
        parts.add("webid_action_id", metadata.getRequest().getWebid_action_id());
        parts.add("webid_confirmed", metadata.getRequest().isConfirmed() ? "true" : "false");
        parts.add("webid_doc_signed", "");
        parts.add("webid_server_timestamp", "362054604");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String,String>>(parts, headers);
        new RestTemplate().postForObject(metadata.getServer().buildURL() , request, String.class);
		
        model.addAttribute(Constants.METADATA, metadata);
		model.addAttribute(Constants.REQUEST_HEADER, metadata.getServer().buildRequestHeader());
		model.addAttribute(Constants.REQUEST_BODY, metadata.getRequest().toRequestBody());
        return "index";
    }
	
	@RequestMapping(value="/updateRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws JsonProcessingException {
		model.addAttribute(Constants.METADATA, metadata);
		model.addAttribute(Constants.REQUEST_HEADER, metadata.getServer().buildRequestHeader());
		model.addAttribute(Constants.REQUEST_BODY, metadata.getRequest().toRequestBody());
        return "index";
    }
	
	@RequestMapping(value="/updateServer", method = RequestMethod.POST)
	public String updateServer(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws JsonProcessingException {
		Server currentServer = metadata.getServer();
		String serverName = currentServer.getProject() + "-" + metadata.getServer().getServerType();
		Optional<ServerProduct> serverProductOptional = serverProductConfig.getServerProducts().stream().filter(server -> server.getName().equals(serverName)).findFirst();
		
		if(serverProductOptional.isPresent()) {
			currentServer.setHost(serverProductOptional.get().getHost());
			currentServer.setPort(serverProductOptional.get().getPort());
		}
		
		metadata.setServer(currentServer);
		model.addAttribute(Constants.METADATA, metadata);
		model.addAttribute(Constants.REQUEST_HEADER, metadata.getServer().buildRequestHeader());
		model.addAttribute(Constants.REQUEST_BODY, metadata.getRequest().toRequestBody());
        return "index";
    }
}
