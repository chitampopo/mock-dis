package com.innovation.mock.tool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.mock.tool.entity.Constants;
import com.innovation.mock.tool.entity.ElcaData;

@Controller
@RequestMapping("/elca")
public class ElcaController {
	
	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String index(Model model)  {
		model.addAttribute(Constants.ELCA_DATA, new ElcaData());
        return "elca";
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute(Constants.ELCA_DATA) ElcaData elcaData, Model model) throws JsonProcessingException {
		model.addAttribute(Constants.ELCA_DATA, elcaData);
		System.out.println("====="+elcaData.getDossierId());
        return "elca";
    }
}
