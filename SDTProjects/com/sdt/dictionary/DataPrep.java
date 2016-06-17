package com.sdt.dictionary;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.sdt.Utils.FileUtils;
import com.sdt.common.file.ReadDirectory;


public class DataPrep {
	
	public static final String SEPARATOR = "\t";
	public static final String PHONE_SEPARATOR = "(";
	
	private ReadDirectory rd = null;
	
	public DataPrep(ReadDirectory rd) {
		super();
		this.rd = rd;
	}
	
	public List<WordPhones> loadDictionaries() {
		List<WordPhones> wordPhonesList = new LinkedList<>();
		File[] files = this.rd.getFiles();
		for(File file : files) {
			List<String> fileData = FileUtils.readFile(file);
			wordPhonesList.addAll(this.parseLine(fileData));
		}
		
		return wordPhonesList;
	}

	private List<WordPhones> parseLine(List<String> fileData) {
		List<WordPhones> wordPhonesList = new LinkedList<>();
		if(fileData != null && !fileData.isEmpty()) {
			for(String line : fileData) {
				WordPhones wordPhones = WordPhones.getWordPhones(line);
				if(!wordPhonesList.isEmpty()) {
					WordPhones lastWordPhones = wordPhonesList.get(wordPhonesList.size() - 1);
					if(lastWordPhones.getWord().equalsIgnoreCase(wordPhones.getWord())) {
						lastWordPhones.getPhones().addAll(wordPhones.getPhones());
					} else {
						wordPhonesList.add(wordPhones);
					}
				} else {
					wordPhonesList.add(wordPhones);
				}
			}
		}
		
		return wordPhonesList;
	}
	
	public static class WordPhones {
		String word;
		List<String> phones;
		
		private WordPhones() { }
		
		public static WordPhones getWordPhones(String line) {
			if(line == null || "".equals(line) || line.split(SEPARATOR).length < 2) {
				throw new RuntimeException("Dictionary line is invalid");
			}
			String[] split = line.split(SEPARATOR);
			WordPhones wordPhones = new WordPhones();
			wordPhones.word = split[0].contains(PHONE_SEPARATOR) ? split[0].substring(0, split[0].lastIndexOf(PHONE_SEPARATOR)) : split[0];
			wordPhones.phones = new LinkedList<>();
			wordPhones.phones.add(split[1]);
			return wordPhones;
		}

		public String getWord() {
			return word;
		}

		public List<String> getPhones() {
			return phones;
		}

		@Override
		public String toString() {
			return "WordPhones [word=" + word + ", phones=" + phones + "]";
		}
		
	}
}
