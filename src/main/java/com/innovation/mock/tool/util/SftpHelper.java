package com.innovation.mock.tool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import com.innovation.mock.tool.entity.Request;
import com.innovation.mock.tool.entity.ServerProfile;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Controller
public class SftpHelper {
	private static final Logger logger = LoggerFactory.getLogger(SftpHelper.class);
	
	private SftpHelper() {}

	public static String uploadFile(Request request, String sourceFilePath, ServerProfile serverProfile) throws IOException {
		InputStream inputStream = new ClassPathResource("./" + sourceFilePath).getInputStream();
		File fileNeedUploaded = new File(sourceFilePath);
	    FileUtils.copyInputStreamToFile(inputStream, fileNeedUploaded);
		String newFilePath = buildNewFilePath(request, serverProfile.getDisFolder(), fileNeedUploaded);
		
		try {
			ChannelSftp sftp = getSFTP(serverProfile);
			sftp.put(new FileInputStream(fileNeedUploaded),	newFilePath, ChannelSftp.OVERWRITE);
		} catch (JSchException |FileNotFoundException | SftpException e) {
			logger.error(Arrays.toString(e.getStackTrace()));
		} 
		return newFilePath;
	}

	/**
	 * existed or not
	 * 
	 * @param serverProfile
	 * @return
	 * @throws NumberFormatException
	 * @throws JSchException
	 * @throws IOException
	 */
	public static boolean checkFileExisted(String sourceFilePath, ServerProfile serverProfile) throws JSchException {
		try {
			ChannelSftp sftp = getSFTP(serverProfile);
			sftp.lstat(sourceFilePath);
		} catch (SftpException e) {
			return false;
		}
		return true;
	}

	private static ChannelSftp getSFTP(ServerProfile serverProfile) throws JSchException {
		Session session = new JSch().getSession(serverProfile.getSshUsername(), serverProfile.getHost(), Integer.valueOf(serverProfile.getSshPort()));
		session.setConfig(buildConfiguations());
		session.setPassword(serverProfile.getSshPassword());
		session.connect();
		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect();
		return sftp;
	}
	
	private static Properties buildConfiguations() {
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
		return config;
	}
	
	private static String buildNewFilePath(Request request, String destFolder, File file) {
		return destFolder + file.getName().replaceAll(Constants.ORIGIN_TRANSACTION_ID, request.getCob_id());
	}
}
