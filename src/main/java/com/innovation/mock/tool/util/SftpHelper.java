package com.innovation.mock.tool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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

	public static void uploadFile(Request request, String sourceFilePath, ServerProfile serverProfile) throws IOException {
		JSch ssh = new JSch();
		ChannelSftp sftp = null;
		Session session = null;

		File fileNeedUploaded = new ClassPathResource(sourceFilePath).getFile();
		String newFilePath = buildNewFilePath(request, serverProfile.getDisFolder(), fileNeedUploaded);
		try {
			session = ssh.getSession(serverProfile.getSshUsername(), serverProfile.getHost(), Integer.valueOf(serverProfile.getSshPort()));
			session.setConfig(buildConfiguations());
			session.setPassword(serverProfile.getSshPassword());
			session.connect();
			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();
			sftp.put(new FileInputStream(fileNeedUploaded),	newFilePath, ChannelSftp.OVERWRITE);
		} catch (JSchException e) {
			System.out.println(e);
		} catch (FileNotFoundException e) {
			// Nothing
		} catch (SftpException e) {
			// Nothing
		} finally {
			sftp.disconnect();
			session.disconnect();
		}
	}

	public static boolean checkFileStatus(String sourceFilePath, ServerProfile serverProfile) throws NumberFormatException, JSchException, IOException {
		JSch ssh = new JSch();
		ChannelSftp sftp = null;
		Session session = null;

		File fileNeedUploaded = new ClassPathResource(sourceFilePath).getFile();
		String newFilePath = serverProfile.getDisFolder() + File.separator + fileNeedUploaded.getName();
		try {
			session = ssh.getSession(serverProfile.getSshUsername(), serverProfile.getHost(), Integer.valueOf(serverProfile.getSshPort()));
			session.setConfig(buildConfiguations());
			session.setPassword(serverProfile.getSshPassword());
			session.connect();
			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();
			sftp.lstat(newFilePath);
		} catch (SftpException e) {
			return false;
		}
		return true;
	}
	
	private static Properties buildConfiguations() {
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
		return config;
	}
	
	private static String buildNewFilePath(Request request, String destFolder, File file) {
		return destFolder + File.separator + file.getName().replaceAll(Constants.ORIGIN_TRANSACTION_ID, request.getCob_id() + "-" + request.getAccountHolder());
	}
}
