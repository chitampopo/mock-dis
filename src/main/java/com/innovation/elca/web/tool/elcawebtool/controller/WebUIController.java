package com.innovation.elca.web.tool.elcawebtool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.innovation.elca.web.tool.elcawebtool.request.Request;

@Controller
@RequestMapping("/")
public class WebUIController {

	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String index(Model model) {
		model.addAttribute("request", new Request());
        return "index";
    }
	
	@RequestMapping(value="/sendRequest", method = RequestMethod.POST)
	public String requestHandler(Model model) {
		model.addAttribute("request", new Request());
        return "index";
    }
	
}
