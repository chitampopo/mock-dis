package com.innovation.mock.tool.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.innovation.mock.tool.entity.DocumentStatus;
import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.Request;
import com.innovation.mock.tool.entity.ServerProfile;
import com.innovation.mock.tool.entity.ServerProfileCollection;
import com.innovation.mock.tool.util.Constants;
import com.innovation.mock.tool.util.FileHelper;
import com.innovation.mock.tool.util.ServerUtil;
import com.innovation.mock.tool.util.SftpHelper;
import com.jcraft.jsch.JSchException;

@Controller
public class FileController {

	public static final Logger logger = LoggerFactory.getLogger(WebUIController.class);
	private static String savedfilePath = "";
	private static String fileInProcessedByDIS = "";

	@Value("${sourceFile}")
	private String sourceFileName;
	
	@Value("${sourceFile2}")
	private String sourceFileNameForAH2;

	@Autowired
	private ServerProfileCollection serverProfiles;

	@PostMapping("/uploadFile")
	public String singleFileUpload(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws IOException {
		logger.info("Begin upload file");
		File sourceFile = FileHelper.getSourceFileByAccountHolder(metadata.getRequest().getAccountHolder(), sourceFileName, sourceFileNameForAH2);
	    
		Optional<ServerProfile> serverProfile = ServerUtil.findCurrentServerProfile(metadata.getServer(), serverProfiles);
		if (serverProfile.isPresent()) {
			logger.info("Server profile: " + serverProfile.get().toString());
			if (!serverProfile.get().getName().contains("local")) {
				sendFileToExternalServer(metadata.getRequest(), serverProfile.get());
			} else {
				sendFileInteralServer(metadata.getRequest().getCob_id(), sourceFile, serverProfile.get().getDisFolder());
			}
		}
		model.addAttribute(Constants.FILE_STATUS, DocumentStatus.FILE_IS_CHECKING.getValue());
		model.addAttribute(Constants.METADATA, metadata);
		return Constants.MAIN_PAGE;
	}

	@PostMapping("/checkFile")
	public String checkFileStatus(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws IOException, NumberFormatException, JSchException {
		String fileStatus = DocumentStatus.WAITING_UPLOAD.getValue();
		Optional<ServerProfile> serverProfile = ServerUtil.findCurrentServerProfile(metadata.getServer(), serverProfiles);
		logger.info("Server profile available: " + serverProfile.isPresent());
		if (serverProfile.isPresent()) {
			logger.info("Server profile: " + serverProfile.get().toString());
			if (!serverProfile.get().getName().contains("local")) {
				fileStatus = FileHelper.checkFileRemoteServer(metadata.getRequest().getCob_id(), serverProfile.get(), sourceFileName);
			} else {
				fileStatus = FileHelper.checkFileLocal(savedfilePath, fileInProcessedByDIS);
			}
		}
		model.addAttribute(Constants.FILE_STATUS, fileStatus);
		model.addAttribute(Constants.METADATA, metadata);
		return Constants.MAIN_PAGE;
	}

	private void sendFileInteralServer(String cobId, File sourceFile, String disFolder) throws IOException, FileNotFoundException {
		String filePathInDisFolder = FileHelper.buildFilePathInDisFolder(cobId, disFolder, sourceFile.getName());
		logger.info("File path in dis folder: " + filePathInDisFolder);
		String filePathInProcessedByDisFolder = FileHelper.buildFilePathInProcessByDISFolder(cobId, disFolder, sourceFile.getName());
		logger.info("File path in processed by dis folder: " + filePathInProcessedByDisFolder);
		FileHelper.deleteIfExisted(filePathInDisFolder);
		FileHelper.deleteIfExisted(filePathInProcessedByDisFolder);
		savedfilePath = filePathInDisFolder;
		fileInProcessedByDIS = filePathInProcessedByDisFolder;
		InputStream source = new FileInputStream(sourceFile);
		File destFile = new File(filePathInDisFolder);
		FileUtils.copyInputStreamToFile(source, destFile);
	}

	private void sendFileToExternalServer(Request request, ServerProfile serverProfile) throws IOException {
		savedfilePath = SftpHelper.uploadFile(request, sourceFileName, serverProfile);
	}
}