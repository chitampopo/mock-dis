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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.Request;
import com.innovation.mock.tool.entity.ServerProfile;
import com.innovation.mock.tool.entity.ServerProfileCollection;
import com.innovation.mock.tool.util.Constants;
import com.innovation.mock.tool.util.ServerUtil;
import com.innovation.mock.tool.util.SftpHelper;
import com.jcraft.jsch.JSchException;

@Controller
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(WebUIController.class);
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
		InputStream inputStream = new ClassPathResource("./" + sourceFileName).getInputStream();
		logger.info("File for AH1: " + inputStream.available());
		if(metadata.getRequest().getAccountHolder().equals("1")) {
			inputStream = new ClassPathResource("./" + sourceFileNameForAH2).getInputStream();
			logger.info("File for AH2: " + inputStream.available());
		}
		
		File sourceFile = new File(sourceFileName);
		 
	    FileUtils.copyInputStreamToFile(inputStream, sourceFile);
		Optional<ServerProfile> serverProfile = ServerUtil.findCurrentServerProfile(metadata.getServer(), serverProfiles);
		logger.info("Server profile available: " + serverProfile.isPresent());
		if (serverProfile.isPresent()) {
			logger.info("Server profile: " + serverProfile.get().toString());
			if (!serverProfile.get().getName().contains("local")) {
				sendFileToExternalServer(metadata.getRequest(), serverProfile.get());
			} else {
				sendFileInteralServer(metadata.getRequest(), sourceFile, serverProfile.get());
			}
		}
		model.addAttribute(Constants.FILE_STATUS, "1");
		model.addAttribute(Constants.METADATA, metadata);
		return Constants.MAIN_PAGE;
	}

	@PostMapping("/checkFile")
	public String checkFileStatus(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws IOException, NumberFormatException, JSchException {
		String fileStatus = "0";// begin
		Optional<ServerProfile> serverProfile = ServerUtil.findCurrentServerProfile(metadata.getServer(), serverProfiles);
		logger.info("Server profile available: " + serverProfile.isPresent());
		if (serverProfile.isPresent()) {
			logger.info("Server profile: " + serverProfile.get().toString());
			if (!serverProfile.get().getName().contains("local")) {
				fileStatus = checkFileRemoteServer(metadata.getRequest(), serverProfile.get());
			} else {
				fileStatus = checkFileLocal();
			}
		}
		model.addAttribute(Constants.FILE_STATUS, fileStatus);
		model.addAttribute(Constants.METADATA, metadata);
		return Constants.MAIN_PAGE;
	}

	private String checkFileRemoteServer(Request request, ServerProfile serverProfile) throws JSchException, IOException {
		String fileStatus;
		boolean fileIsChecking = SftpHelper.checkFileExisted(buildNewFilePath(request, serverProfile.getDisFolder(), sourceFileName), serverProfile);
		boolean fileInDisFolder = SftpHelper.checkFileExisted(buildProcessByDISPath(request, serverProfile.getDisFolder(), sourceFileName), serverProfile);
		fileStatus = getStatusDisFile(fileIsChecking, fileInDisFolder);
		return fileStatus;
	}

	private String checkFileLocal() {
		boolean fileIsChecking = checkFileInDisFolder();
		boolean fileProcessed = checkFileInProcessedByDisFolder();
		return getStatusDisFile(fileIsChecking, fileProcessed);
	}

	private String getStatusDisFile(boolean fileIsChecking, boolean fileProcessed) {
		String fileStatus = "0";// begin
		if (fileIsChecking && !fileProcessed) {
			fileStatus = "1"; // in-process
		} else if (fileIsChecking && fileProcessed) {
			fileStatus = "3"; // error
		} else if (!fileIsChecking && fileProcessed) {
			fileStatus = "2"; // finished
		}
		return fileStatus;
	}

	private boolean checkFileInProcessedByDisFolder() {
		if(fileIsExisted(fileInProcessedByDIS)) {
			return true;
		}
		return false;
	}

	private void sendFileInteralServer(Request request, File sourceFile, ServerProfile serverProfile) throws IOException, FileNotFoundException {
		String newFilePath = buildNewFilePath(request, serverProfile.getDisFolder(), sourceFile.getName());
		logger.info("New file path: " + newFilePath);
		String inProcessedByDIS = buildProcessByDISPath(request, serverProfile.getDisFolder(), sourceFile.getName());
		logger.info("Path in processed by dIS: " + inProcessedByDIS);
		deleteIfExisted(newFilePath);
		deleteIfExisted(inProcessedByDIS);
		savedfilePath = newFilePath;
		fileInProcessedByDIS = inProcessedByDIS;
		InputStream source = new FileInputStream(sourceFile);
		File destFile = new File(newFilePath);
		FileUtils.copyInputStreamToFile(source, destFile);
		//sourceFile.delete();
	}

	private void deleteIfExisted(String path) {
		File f = new File(path);
		if (fileIsExisted(path)) {
			f.delete();
		}
	}

	private boolean fileIsExisted(String path) {
		File f = new File(path);
		if (f.exists() && !f.isDirectory()) {
			return true;
		}
		return false;
	}

	private void sendFileToExternalServer(Request request, ServerProfile serverProfile) throws IOException {
		savedfilePath = SftpHelper.uploadFile(request, sourceFileName, serverProfile);
	}

	private String buildNewFilePath(Request request, String destFolder, String fileName) {
		return destFolder + fileName.replaceAll(Constants.ORIGIN_TRANSACTION_ID, request.getCob_id());
	}

	private String buildProcessByDISPath(Request request, String disFolder, String fileName) {
		if (request != null) {
			if(disFolder.contains("opt")) {
				return disFolder + "processedByDIS/" + fileName.replaceAll(Constants.ORIGIN_TRANSACTION_ID, request.getCob_id());
			}
			return disFolder + "processedByDIS\\" + fileName.replaceAll(Constants.ORIGIN_TRANSACTION_ID, request.getCob_id());
		}
		return disFolder + "processedByDIS" + File.separator + fileName;
	}

	private boolean checkFileInDisFolder() {
		boolean fileIsChecking = true;
		File f = new File(savedfilePath);
		if(!f.exists() && !f.isDirectory()) { 
			fileIsChecking = false;
		}
		
		return fileIsChecking;
	}
}