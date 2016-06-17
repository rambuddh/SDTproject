package com.sdt.Utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class FileUtils {
	
	public static final String NAME_SEPARATOR = "-";
	public static final String DOT = ".";

	public static String getFileName(File file) {
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}
	
	public static String getFileExtension(File file) {
		return file.getName().substring(file.getName().lastIndexOf(".") + 1);
	}
	
	public static String getStrFile(String path, String fileName, String counter, String ext) {
		return (counter != null) ? path + fileName + NAME_SEPARATOR + counter + DOT + ext
				: path + fileName + DOT + ext;
	}
	
	public static List<String> readFile(File file) {
		List<String> fileData = new LinkedList<>();
		try (Scanner scanner = new Scanner(file)){
			while(scanner.hasNext()) {
				fileData.add(scanner.nextLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fileData;
	}
}
