package com.innovation.elca.web.tool.elcawebtool.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.core.io.ClassPathResource;

import com.innovation.elca.web.tool.elcawebtool.request.FtpInfo;

public class UploadFtpController {

	public static void main(String fullPath, FtpInfo info) {
		String serverName = info.getHost();
        int port = info.getPort();
        String user = info.getUsername();
        String pass = info.getPassword();
 
        FTPClient ftpClient = new FTPClient();
        
        try {
        	
            ftpClient.connect(serverName, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
 
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            File fileNeedUploaded = new ClassPathResource("DISfile.txt").getFile();
            String secondRemoteFile = fileNeedUploaded.getName();
            InputStream inputStream = new FileInputStream(fileNeedUploaded);
 
            System.out.println("Start uploading second file");
            OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
            byte[] bytesIn = new byte[4096];
            int read = 0;
 
            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
            }
            inputStream.close();
            outputStream.close();
 
            boolean completed = ftpClient.completePendingCommand();
            if (completed) {
                System.out.println("The second file is uploaded successfully.");
            }
 
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
}
