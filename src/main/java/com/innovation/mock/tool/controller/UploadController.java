package com.innovation.mock.tool.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.ServerProfile;
import com.innovation.mock.tool.util.Constants;
import com.innovation.mock.tool.util.ServerUtil;

@Controller
public class UploadController {

	@Value("${sourceFile}")
	private String sourceFilePath;
	
	@PostMapping("/uploadFile")
	public String singleFileUpload(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws IOException {
		File sourceFile = new ClassPathResource(sourceFilePath).getFile();
		Optional<ServerProfile> serverProfile = ServerUtil.findCurrentServerProfile(metadata.getServer());
		
		if (serverProfile.isPresent()) {
			if (!serverProfile.get().getName().contains("local")) {
				sendFileToExternalServer(serverProfile.get());
			} else {
				sendFileInteralServer(metadata, sourceFile, serverProfile.get());
			}
		}

		model.addAttribute(Constants.METADATA, metadata);
		return Constants.MAIN_PAGE;
	}

	@SuppressWarnings("resource")
	private void sendFileInteralServer(Metadata metadata, File sourceFile, ServerProfile serverProfile)	throws IOException, FileNotFoundException {
		String newFilePath = buildNewFilePath(metadata, serverProfile.getDisFolder(), sourceFile);
		try (FileChannel source = new FileInputStream(sourceFile).getChannel();
			 FileChannel dest = new FileOutputStream(new File(newFilePath)).getChannel();) 
		{
			dest.transferFrom(source, 0, source.size());
		}
	}

	private void sendFileToExternalServer(ServerProfile serverProfile) throws IOException {
		UploadSftpController.uploadFile(serverProfile);
	}
	
	private String buildNewFilePath(Metadata metadata, String destFolder, File file) {
		return destFolder + File.separator + file.getName().replaceAll(Constants.ORIGIN_TRANSACTION_ID, metadata.getRequest().getCob_id() + "-" + metadata.getRequest().getAccountHolder());
	}
	
	
}