package com.sdt.filesplitter;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.sdt.Utils.AudioUtil;
import com.sdt.Utils.FileUtils;
import com.sdt.common.file.ReadDirectory;

public class FileSplitter {

	private int spiltTime = 400;
	private TimeUnit unit = TimeUnit.MILLISECONDS;
	private String extension = "wav";
	private ReadDirectory rd = new ReadDirectory();
	
	public FileSplitter() {
		super();
	}

	public FileSplitter(String inputDir, String outputDir, int spiltTime, TimeUnit unit, String extension) {
		super();
		this.rd = new ReadDirectory(inputDir, outputDir);
		this.spiltTime = spiltTime;
		this.unit = unit;
		this.extension = extension;
	}

	public static void main(String[] args) {
		FileSplitter fileSplitter = new FileSplitter();
		if(args != null && args.length == 5) {
			try {
				fileSplitter = new FileSplitter(args[0], args[1], Integer.parseInt(args[2]), TimeUnit.valueOf(args[3]), args[4]);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		fileSplitter.startSpiltting();
	}
	
	public void startSpiltting() {
		File[] files = this.rd.getFiles();
		if (files != null) {
			for (File child : files) {
				String fileName = FileUtils.getFileName(child);
				String ext = FileUtils.getFileExtension(child);
				if(this.extension.equals(ext)) {
					this.split(fileName, ext, spiltTime, unit);
				}
			}
		}
	}
	
	public void split(String strFile, String ext, int unitTimeToSplit, TimeUnit unit) {
		File file = new File(FileUtils.getStrFile(rd.getInputDir(), strFile, null, ext));
		int count = 0;
		int startTime = 0;
		int endTime = 0;
		int totalAudioTime = AudioUtil.getTotalAudioTime(file, unit);
		
		while (endTime != totalAudioTime) {
			count++;
			startTime = endTime;
			endTime = (endTime + unitTimeToSplit < totalAudioTime) ? endTime + unitTimeToSplit : totalAudioTime;
			String destFile = FileUtils.getStrFile(this.rd.getOutputDir(), strFile, "" + count, ext);
			this.splitAudio(file, destFile, startTime, endTime, unit);
		}
	}
	
	public void splitAudio(File file, String destFile, int startUnitTime, int endUnitTime, TimeUnit unit) {
		AudioInputStream inputStream = null;
		AudioInputStream shortenedStream = null;
		try {
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
			AudioFormat format = fileFormat.getFormat();
			inputStream = AudioSystem.getAudioInputStream(file);
			int bytesPerUnitTime = format.getFrameSize() * this.getFrameRatePerUnitTime(format, unit);
			inputStream.skip(startUnitTime * bytesPerUnitTime);
			long framesOfAudioToCopy = (endUnitTime - startUnitTime) * this.getFrameRatePerUnitTime(format, unit);
			shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
			AudioSystem.write(shortenedStream, fileFormat.getType(), new File(destFile));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (shortenedStream != null) try { shortenedStream.close(); shortenedStream = null;} catch (Exception e) { e.printStackTrace(); }
			if (inputStream != null) try { inputStream.close(); inputStream = null; } catch (Exception e) { e.printStackTrace(); }
		 }
	}
	
	@SuppressWarnings("incomplete-switch")
	public int getFrameRatePerUnitTime (AudioFormat format, TimeUnit unit) {
		int inUnitTime = 0;
		double rateInSec = format.getFrameRate();
		switch (unit) {
		case SECONDS:
			inUnitTime = (int)rateInSec * 1;
			break;
		case MILLISECONDS:
			inUnitTime = new Double(rateInSec * 0.001).intValue();
			break;
		}
		return inUnitTime;
	}
}
