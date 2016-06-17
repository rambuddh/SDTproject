package com.sdt.audio.stereo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.sdt.Utils.FileUtils;
import com.sdt.common.file.ReadDirectory;

public class MonoConverter {

	final int HEADER_IN_BYTES = 44;
	
	private String extension = "wav";
	private ReadDirectory rd = new ReadDirectory();
	
	public MonoConverter() {
		super();
	}

	public MonoConverter(String inputDir, String outputDir, String extension) {
		super();
		this.rd = new ReadDirectory(inputDir, outputDir);
		this.extension = extension;
	}

	public static void main(String[] args) {
		MonoConverter monoConv = new MonoConverter();
		if(args != null && args.length == 5) {
			try {
				monoConv = new MonoConverter(args[0], args[1], args[2]);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		monoConv.startConvertion();
	}
	
	public void startConvertion() {
		File[] files = this.rd.getFiles();
		if (files != null) {
			for (File child : files) {
				String fileName = FileUtils.getFileName(child);
				String ext = FileUtils.getFileExtension(child);
				System.out.println("File Name: " + fileName);
				if(this.extension.equals(ext)) {
					this.convert(child);
				}
			}
		}
	}
	
	public void convert(File file) {
		try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(file)){
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
			AudioFormat format = fileFormat.getFormat();

			/*System.out.println(fileFormat.toString());
			System.out.println(format.toString());*/
			
			byte[] fileData = readFile(inputStream);
			
			List<ChannelData> channelsData = extractByteForEachChannel(format, fileData);
			
			for(int i = 0; i < channelsData.size(); i++) {
				this.writeFile(channelsData.get(i), format, fileFormat, file, "" + i);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<ChannelData> extractByteForEachChannel(AudioFormat format, byte[] fileData) {
		
		int channels = format.getChannels();
		int noOfBytesEachChannel = format.getFrameSize() / channels;
		List<ChannelData> channelsData = new ArrayList<>();
		
		for(int i = 0; i < channels; i++) {
			channelsData.add(new ChannelData(new byte[HEADER_IN_BYTES + ((fileData.length - HEADER_IN_BYTES) / channels)]));
		}
		
		int channelNo = -1;
		ChannelData channelData = null;
		for(int i = 0; i < fileData.length; i++) {
			if(i % noOfBytesEachChannel == 0) {
				channelNo++;
				if(channelNo == channelsData.size())
					channelNo = 0;
				channelData = channelsData.get(channelNo);
			}
			
			channelData.addData(fileData[i]);
		}
		
		return channelsData;
	}
	
	public void writeFile(ChannelData channelData, AudioFormat format, AudioFileFormat fileFormat, File inputFile, String counter ) {
		byte[] byteData = channelData.data;
		File file = new File(FileUtils.getStrFile(this.rd.getOutputDir(), FileUtils.getFileName(inputFile), counter, this.extension));
		try {
			AudioFormat audioFormat = new AudioFormat(format.getSampleRate(), 
					format.getSampleSizeInBits(), 1, true, format.isBigEndian());
			AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(byteData), 
			        audioFormat, byteData.length);
			AudioSystem.write(stream, fileFormat.getType(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] readFile(InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			}
			
			buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toByteArray();
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
	
	public static class ChannelData {
		byte[] data;
		int curPos;
		
		public ChannelData(byte[] data) {
			super();
			this.data = data;
			this.curPos = 0;
		}
		
		public ChannelData(byte[] data, int curPos) {
			super();
			this.data = data;
			this.curPos = curPos;
		}
		
		public void addData(byte b) {
			this.data[curPos++] = b;
		}
	}

}
