package com.innovation.mock.tool.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.mock.tool.entity.FtpInfo;
import com.innovation.mock.tool.entity.Metadata;

@Controller
public class UploadController {

	@Autowired
	private Metadata metadata;

	@Autowired
	private FtpInfo ftpInfoFromConfig;

	@Value("${disFolder}")
	private String UPLOADED_FOLDER;

	@Value("${SftpDisFolder}")
	private String SFTP_DIS_FOLDER;

	private ObjectMapper mapper = new ObjectMapper();

	@PostMapping("/uploadFile")
	public String singleFileUpload(@ModelAttribute("ftpInfo") FtpInfo ftpInfo, Model model) throws IOException {

		MultipartFile file = ftpInfo.getFile();

		if (ftpInfo.isEnable()) {
			UploadSftpController.uploadFile(SFTP_DIS_FOLDER, ftpInfo);
		} else {
			try {
				byte[] bytes = file.getBytes();
				Path path = Paths.get(UPLOADED_FOLDER + File.separator + file.getOriginalFilename());
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