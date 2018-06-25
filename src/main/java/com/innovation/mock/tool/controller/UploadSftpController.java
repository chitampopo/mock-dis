package com.innovation.mock.tool.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import com.innovation.mock.tool.entity.FtpInfo;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Controller
public class UploadSftpController {

	public static void uploadFile(String fullPath, FtpInfo info) throws IOException {
		JSch ssh = new JSch();
		ChannelSftp sftp = null;
		Session session = null;

		String serverName = info.getHost();
		int port = info.getPort();
		String user = info.getUsername();
		String pass = info.getPassword();

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");

		File fileNeedUploaded = new ClassPathResource("DIS_001023_177672700_COB000117-0_20171003171752524141.zip.gpg")
				.getFile();
		try {
			session = ssh.getSession(user, serverName, port);
			session.setConfig(config);
			session.setPassword(pass);
			session.connect();
			sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();

			sftp.put(new FileInputStream(fileNeedUploaded),	fullPath + fileNeedUploaded.getName(),
					ChannelSftp.OVERWRITE);

		} catch (JSchException e) {
			// Nothing
		} catch (FileNotFoundException e) {
			// Nothing
		} catch (SftpException e) {
			// Nothing
		} finally {
			sftp.disconnect();
			session.disconnect();
		}
	}
}
