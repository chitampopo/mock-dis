package com.innovation.mock.tool.controller;

import java.io.File;
import java.io.FileInputStream;
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

import com.innovation.mock.tool.entity.Constants;
import com.innovation.mock.tool.entity.FtpInfo;
import com.innovation.mock.tool.entity.Metadata;
import com.innovation.mock.tool.entity.Server;
import com.innovation.mock.tool.entity.ServerProduct;
import com.innovation.mock.tool.entity.ServerProductConfig;

@Controller
public class UploadController {

	@Value("${disFolder}")
	private String UPLOADED_FOLDER;

	@Value("${SftpDisFolder}")
	private String SFTP_DIS_FOLDER;

	@Value(value = "sourceFile")
	private String sourceFilePath;
	
	@Autowired
	private ServerProductConfig profiles;
	
	@SuppressWarnings("resource")
	@PostMapping("/uploadFile")
	public String singleFileUpload(@ModelAttribute(Constants.METADATA) Metadata metadata, Model model) throws IOException {
		File file = new ClassPathResource(sourceFilePath).getFile();

		Server currentServer = metadata.getServer();
		String serverName = currentServer.getProject() + "-" + currentServer.getServerType();
		Optional<ServerProduct> serverProductOptional = profiles.getServerProducts().stream().filter(server -> server.getName().equals(serverName)).findFirst();

		if (serverProductOptional.isPresent()) {
			if (!serverProductOptional.get().getName().contains("local")) {
				FtpInfo ftpInfo = new FtpInfo(serverProductOptional.get().getHost(), Integer.valueOf(serverProductOptional.get().getSshPort()));
				UploadSftpController.uploadFile(SFTP_DIS_FOLDER, ftpInfo);
			} else {
				String newFilePath = buildNewFilePath(metadata, file);
				try (FileChannel sourceChannel = new FileInputStream(file).getChannel();
						FileChannel destChannel = new FileOutputStream(new File(newFilePath)).getChannel();) {
					destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
				}
			}
		}

		model.addAttribute(Constants.METADATA, metadata);
		model.addAttribute(Constants.REQUEST_HEADER, metadata.getServer().buildRequestHeader());
		model.addAttribute(Constants.REQUEST_BODY, metadata.getRequest().toRequestBody());
		return "index";
	}

	private String buildNewFilePath(Metadata metadata, File file) {
		return UPLOADED_FOLDER + File.separator + file.getName().replaceAll(Constants.ORIGIN_TRANSACTION_ID, metadata.getRequest().getCob_id() + "-" + metadata.getRequest().getAccountHolder());
	}
}