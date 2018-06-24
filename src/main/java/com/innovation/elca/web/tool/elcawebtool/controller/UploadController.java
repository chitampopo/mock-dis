package com.innovation.elca.web.tool.elcawebtool.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.elca.web.tool.elcawebtool.request.FtpInfo;
import com.innovation.elca.web.tool.elcawebtool.request.Metadata;

@Controller
public class UploadController {

	@Autowired
	private Metadata metadata;
	
	@Autowired
	private FtpInfo ftpInfoFromConfig;
	
	@Value("${disFolder}")
	private String UPLOADED_FOLDER;
	
	private ObjectMapper mapper = new ObjectMapper();

	@PostMapping("/uploadFile")
	public String singleFileUpload(@ModelAttribute("ftpInfo") FtpInfo ftpInfo, Model model, HttpServletRequest request)
			throws JsonGenerationException, JsonMappingException, IOException {
		
		MultipartFile file = ftpInfo.getFile();
		String filePath = request.getServletContext().getRealPath(File.separator);
		File f1 = new File(filePath + File.separator + file.getOriginalFilename());

		if (ftpInfo.isEnable()) {
			UploadFtpController.main(f1.getAbsolutePath(), ftpInfo);
		} else {
			try {
				byte[] bytes = file.getBytes();
				Path path = Paths.get(UPLOADED_FOLDER +  File.separator + file.getOriginalFilename());
				Files.write(path, bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		model.addAttribute("metadata", metadata);
		model.addAttribute("ftpInfo", ftpInfoFromConfig);
		model.addAttribute("requestHeader", metadata.getServer().buildRequestHeader());
		model.addAttribute("requestJson", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata.getRequest()));
		return "index";
	}
}