package com.innovation.mock.tool.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Optional;

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

	private static String savedfilePath = "";
	private static String fileInProcessedByDIS = "";

	@Value("${sourceFile}")
	private String sourceFilePath;

	@Autowired
	private ServerProfileCollection serverProfiles;

	@PostMapping("/uploadFile")
	public String singleFileUpload(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws IOException {
		File sourceFile = new ClassPathResource(sourceFilePath).getFile();
		Optional<ServerProfile> serverProfile = ServerUtil.findCurrentServerProfile(metadata.getServer(), serverProfiles);

		if (serverProfile.isPresent()) {
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
		boolean fileIsChecking = checkFileInDisFolder();
		boolean fileProcessed = checkFileInProcessedByDisFolder();

		String fileStatus = "0";// begin
		if (fileIsChecking && !fileProcessed) {
			fileStatus = "1"; // in-process
		} else if (fileIsChecking && fileProcessed) {
			fileStatus = "3"; // error
		} else if (!fileIsChecking && fileProcessed) {
			fileStatus = "2"; // finished
		}

		model.addAttribute(Constants.FILE_STATUS, fileStatus);
		model.addAttribute(Constants.METADATA, metadata);
		return Constants.MAIN_PAGE;
	}

	private boolean checkFileInProcessedByDisFolder() {
		if(fileIsExisted(fileInProcessedByDIS)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("resource")
	private void sendFileInteralServer(Request request, File sourceFile, ServerProfile serverProfile) throws IOException, FileNotFoundException {
		String newFilePath = buildNewFilePath(request, serverProfile.getDisFolder(), sourceFile);
		String inProcessedByDIS = buildProcessByDISPath(request, serverProfile.getDisFolder(), sourceFile);

		deleteIfExisted(newFilePath);
		deleteIfExisted(inProcessedByDIS);
		savedfilePath = newFilePath;
		fileInProcessedByDIS = inProcessedByDIS;
		try (FileChannel source = new FileInputStream(sourceFile).getChannel();
				FileChannel dest = new FileOutputStream(new File(newFilePath)).getChannel();) {
			dest.transferFrom(source, 0, source.size());
		}
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
		SftpHelper.uploadFile(request, sourceFilePath, serverProfile);
	}

	private String buildNewFilePath(Request request, String destFolder, File file) {
		return destFolder + File.separator + file.getName().replaceAll(Constants.ORIGIN_TRANSACTION_ID,
				request.getCob_id() + "-" + request.getAccountHolder());
	}

	private String buildProcessByDISPath(Request request, String disFolder, File file) {
		if (request != null) {
			return disFolder + File.separator + "processedByDIS" + File.separator + file.getName().replaceAll(
					Constants.ORIGIN_TRANSACTION_ID, request.getCob_id() + "-" + request.getAccountHolder());
		}
		return disFolder + File.separator + "processedByDIS" + File.separator + file.getName();
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