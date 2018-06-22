package com.innovation.elca.web.tool.elcawebtool.controller;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.elca.web.tool.elcawebtool.request.Metadata;

@Controller
@RequestMapping("/")
public class WebUIController {

	ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String index(Model model) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Metadata medatada = new Metadata();
		model.addAttribute("metadata", medatada);
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(medatada.getRequest()));
        return "index";
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String updateMetadata(@ModelAttribute("metadata") Metadata metadata, Model model) throws JsonProcessingException {
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
        return "index";
    }
	
	@RequestMapping(value="/updateRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute("metadata") Metadata metadata, Model model) throws JsonProcessingException {
		model.addAttribute("metadata", metadata);
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
        return "index";
    }
	
}
