package com.innovation.elca.web.tool.elcawebtool.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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

	public static String main(String fullPath, FtpInfo info) {
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
            
            File fileNeedUploaded = new ClassPathResource("3-15199762141581264789438.jpg").getFile();
            //File fileNeedUploaded = new File("C:\\Users\\Tam\\Pictures\\the bao hiem.PNG");
            //String secondRemoteFile = fileNeedUploaded.getName();
            InputStream inputStream = new FileInputStream(fileNeedUploaded);
 
            System.out.println("Start uploading second file");
            //OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
            
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileNeedUploaded.getPath()))) {
        		ftpClient.storeFile(fileNeedUploaded.getName(), in);
        	} catch (Exception e) {
        	} finally {
        		inputStream.close();
               // outputStream.close();
        	}
 
            System.out.println("The second file is uploaded successfully.");
 
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
		return "index";
	}
}
