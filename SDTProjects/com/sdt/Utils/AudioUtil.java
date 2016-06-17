package com.sdt.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioUtil {

	public static int getTotalAudioTime(File file, TimeUnit unit) {
		AudioInputStream audioInputStream;
		double durationInSeconds = 0.0;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
			AudioFormat format = audioInputStream.getFormat();
			//System.out.println(format.toString());
			long frames = audioInputStream.getFrameLength();
			durationInSeconds = (frames+0.0) / format.getFrameRate();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return convertFromSec(unit, durationInSeconds);
	}
	
	@SuppressWarnings("incomplete-switch")
	public static int convertFromSec(TimeUnit to, double num) {
		int inUnitTime = 0;
		switch (to) {
		case SECONDS:
			inUnitTime = (int)num * 1;
			break;
		case MILLISECONDS:
			inUnitTime = new Double(num * 1000).intValue();
			break;
		}
		return inUnitTime;
	}
}
