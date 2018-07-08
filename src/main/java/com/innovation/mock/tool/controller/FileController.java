package com.innovation.mock.tool.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.innovation.mock.tool.entity.DocumentServerStatus;
import com.innovation.mock.tool.entity.FileSentStatus;
import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.Request;
import com.innovation.mock.tool.entity.ServerProfile;
import com.innovation.mock.tool.entity.ServerProfileCollection;
import com.innovation.mock.tool.util.Constants;
import com.innovation.mock.tool.util.FileHelper;
import com.innovation.mock.tool.util.ServerUtil;
import com.innovation.mock.tool.util.SftpHelper;

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
		Request request = metadata.getRequest();
		try {
			File sourceFile = FileHelper.getSourceFileByAccountHolder(request.getAccountHolder(), sourceFileName, sourceFileNameForAH2);
			ServerProfile serverProfile = ServerUtil.findCurrentServerProfile(metadata.getServer(), serverProfiles);
			handleSendingFile(request, sourceFile, serverProfile);
		} catch (Exception e) {
			logger.error(Arrays.toString(e.getStackTrace()));
			metadata.setSendFileStatus(FileSentStatus.ERROR.getValue());
		} 
		metadata.setSendFileStatus(FileSentStatus.SUCCESSFUL.getValue());
		model.addAttribute(Constants.METADATA, metadata);
		return Constants.MAIN_PAGE;
	}

	private void handleSendingFile(Request request, File sourceFile, ServerProfile serverProfile) throws IOException {
		if (!serverProfile.getName().contains("local")) {
			sendFileToExternalServer(request, serverProfile);
		} else {
			sendFileInteralServer(request.getCob_id(), sourceFile, serverProfile.getDisFolder());
		}
	}

	@PostMapping("/checkFile")
	public String checkFileStatus(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) {
		String fileStatus = DocumentServerStatus.WAITING_UPLOAD.getValue();
		try {
			ServerProfile serverProfile = ServerUtil.findCurrentServerProfile(metadata.getServer(), serverProfiles);
			if (!serverProfile.getName().contains("local")) {
				fileStatus = FileHelper.checkFileRemoteServer(metadata.getRequest().getCob_id(), serverProfile, sourceFileName);
			} else {
				fileStatus = FileHelper.checkFileLocal(savedfilePath, fileInProcessedByDIS);
			}
		} catch (Exception e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		}
		
		metadata.setServerFileStatus(fileStatus);
		model.addAttribute(Constants.METADATA, metadata);
		return Constants.MAIN_PAGE;
	}

	private void sendFileInteralServer(String cobId, File sourceFile, String disFolder) throws IOException {
		String filePathInDisFolder = FileHelper.buildFilePathInDisFolder(cobId, disFolder, sourceFile.getName());
		String filePathInProcessedByDisFolder = FileHelper.buildFilePathInProcessByDISFolder(cobId, disFolder, sourceFile.getName());
		
		FileHelper.deleteIfExisted(filePathInDisFolder);
		FileHelper.deleteIfExisted(filePathInProcessedByDisFolder);
		
		savedfilePath = filePathInDisFolder;
		fileInProcessedByDIS = filePathInProcessedByDisFolder;
		
		FileUtils.copyInputStreamToFile(new FileInputStream(sourceFile), new File(filePathInDisFolder));
	}

	private void sendFileToExternalServer(Request request, ServerProfile serverProfile) throws IOException {
		savedfilePath = SftpHelper.uploadFile(request, sourceFileName, serverProfile);
	}
}