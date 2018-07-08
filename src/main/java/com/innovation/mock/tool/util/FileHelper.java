package com.innovation.mock.tool.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import com.innovation.mock.tool.controller.FileController;
import com.innovation.mock.tool.entity.DocumentServerStatus;
import com.innovation.mock.tool.entity.ServerProfile;
import com.jcraft.jsch.JSchException;

public class FileHelper {

	public static void deleteIfExisted(String filePath) {
		File file = new File(filePath);
		if (FileHelper.fileIsExisted(filePath)) {
			FileController.logger.info("Delete file: " + file.getAbsolutePath());
			file.delete();
		}
	}

	public static boolean fileIsExisted(String filePath) {
		File file = new File(filePath);
		if (file.exists() && !file.isDirectory()) {
			return true;
		}
		return false;
	}

	public static String getStatusDisFile(boolean fileIsChecking, boolean fileProcessed) {
		String fileStatus = DocumentServerStatus.WAITING_UPLOAD.getValue();
		if (fileIsChecking && !fileProcessed) {
			fileStatus = DocumentServerStatus.FILE_IS_CHECKING.getValue();
		} else if (fileIsChecking && fileProcessed) {
			fileStatus = DocumentServerStatus.ERROR.getValue();
		} else if (!fileIsChecking && fileProcessed) {
			fileStatus = DocumentServerStatus.FILE_CHECKED.getValue();
		}
		return fileStatus;
	}

	/**
	 * Cuz this app can run on Windows machine and destination is possible Unix platform
	 * 
	 * @param cobdId
	 * @param disFolder
	 * @param originFileName
	 * @return
	 */
	public static String buildFilePathInProcessByDISFolder(String cobdId, String disFolder, String originFileName) {
		boolean isFromUnixPlatform = disFolder.contains(Constants.FILE_SEPARATOR_UNIX);
		String processedByDisForlder = disFolder + Constants.PROCESSED_BY_DIS;
		String fileName = originFileName.replaceAll(Constants.ORIGIN_TRANSACTION_ID, cobdId);
		if(isFromUnixPlatform) {
			return processedByDisForlder + Constants.FILE_SEPARATOR_UNIX + fileName;
		}
		return processedByDisForlder + Constants.FILE_SERPATOR_WINDOWS + fileName;
	}

	public static String buildFilePathInDisFolder(String cobId, String destFolder, String fileName) {
		return destFolder + fileName.replaceAll(Constants.ORIGIN_TRANSACTION_ID, cobId);
	}

	public static String checkFileRemoteServer(String cobId, ServerProfile serverProfile, String sourceFileName) throws JSchException, IOException {
		boolean fileIsChecking = SftpHelper.checkFileExisted(buildFilePathInDisFolder(cobId, serverProfile.getDisFolder(), sourceFileName), serverProfile);
		boolean fileInDisFolder = SftpHelper.checkFileExisted(buildFilePathInProcessByDISFolder(cobId, serverProfile.getDisFolder(), sourceFileName), serverProfile);
		return getStatusDisFile(fileIsChecking, fileInDisFolder);
	}

	public static String checkFileLocal(String savedfilePath, String fileInProcessedByDIS) {
		boolean fileIsChecking = fileIsExisted(savedfilePath);
		boolean fileProcessed = fileIsExisted(fileInProcessedByDIS);
		return getStatusDisFile(fileIsChecking, fileProcessed);
	}

	public static File getSourceFileByAccountHolder(String accountHolder, String sourceFileName, String sourceFileNameForAH2) throws IOException {
		InputStream inputStream = FileHelper.getInputStream(accountHolder, sourceFileName, sourceFileNameForAH2);
		File sourceFile = new File(sourceFileName);
	    FileUtils.copyInputStreamToFile(inputStream, sourceFile);
		return sourceFile;
	}

	public static InputStream getInputStream(String accountHolder, String sourceFileName, String sourceFileNameForAH2) throws IOException {
		InputStream inputStream = new ClassPathResource("./" + sourceFileName).getInputStream();
		if (accountHolder.equals(Constants.ACCOUNT_HOLDER2)) {
			inputStream = new ClassPathResource("./" + sourceFileNameForAH2).getInputStream();
		}
		return inputStream;
	}

}
