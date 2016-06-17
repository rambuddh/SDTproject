package com.sdt.dictionary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sdt.common.file.ReadDirectory;
import com.sdt.dictionary.DataPrep.WordPhones;
import com.sdt.dictionary.alphabet.Alphabets;

public class UpgradeDictionary {

	public static final String STARTING_PHONES_PARAMS = "startingPhonesParams";
	public static final String ENDING_PHONES_PARAMS = "endingPhonesParams";
	
	private ReadDirectory rd = new ReadDirectory();
	
	private Parameter startingPhonesVowelsParams = new Parameter(Alphabets.VOWEL, true, true, true, true, true);
	private Parameter startingPhonesNonVowelsParams = new Parameter(Alphabets.NON_VOWEL, true, true, true, true, true);
	
	private Parameter endingPhonesVowelsParams = new Parameter(Alphabets.VOWEL, true, true, true, true, true);
	private Parameter endingPhonesNonVowelsParams = new Parameter(Alphabets.NON_VOWEL, true, true, true, true, true);
	
	private Map<String, List<Parameter>> paramMap = new HashMap<>();
	
	public UpgradeDictionary() {
		rd = new ReadDirectory();
		this.paramMap.put(STARTING_PHONES_PARAMS, Arrays.asList(startingPhonesVowelsParams, startingPhonesNonVowelsParams));
		this.paramMap.put(ENDING_PHONES_PARAMS, Arrays.asList(endingPhonesVowelsParams, endingPhonesNonVowelsParams));
	}
	
	public UpgradeDictionary(String inputDir, String outputDir) {
		super();
		this.rd = new ReadDirectory(inputDir, outputDir);
		this.paramMap.put(STARTING_PHONES_PARAMS, Arrays.asList(startingPhonesVowelsParams, startingPhonesNonVowelsParams));
		this.paramMap.put(ENDING_PHONES_PARAMS, Arrays.asList(endingPhonesVowelsParams, endingPhonesNonVowelsParams));
	}
	
	public static void main(String[] args) {
		UpgradeDictionary ud = new UpgradeDictionary();
		ud.upgradeDictionary();
	}
	
	public void upgradeDictionary() {
		DataPrep dp = new DataPrep(this.rd);
		List<WordPhones> dictionaries = dp.loadDictionaries();
		
		for(WordPhones wp : dictionaries) {
			System.out.println(wp);
		}
		
		if(dictionaries == null || dictionaries.isEmpty())
			throw new RuntimeException("Empty Dictionaries !!!!!!!!!!!");
		
		for(WordPhones wordPhones : dictionaries) {
			this.generateMorePhones(wordPhones);
		}

		writeDict(dictionaries, rd);
	}

	public static void writeDict(List<WordPhones> dictionaries, ReadDirectory rd) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(rd.getOutputDir() + "Dict.txt"))) {
			int count = 1;
			for(WordPhones wp : dictionaries) {
				String word = wp.getWord() + "\t";
				Set<String> uniqPhones = new HashSet<>();
				for(String phone : wp.phones) {
					if(uniqPhones.contains(phone))
						continue;
					else
						uniqPhones.add(phone);
					
					if (count > 1)
						word = wp.getWord() + "(" + count + ")\t";
					bw.write(word + phone);
					bw.newLine();
					count++;
				}
				count = 1;
			}
		} catch (Exception e) {
			throw new RuntimeException("Error in program..........");
		}
	}
	
	public void generateMorePhones(WordPhones wordPhones) {
		List<String> morePhones = new LinkedList<>();
		for(String phone : wordPhones.getPhones()) {
			morePhones.addAll(this.getAllPhones(phone, STARTING_PHONES_PARAMS, this.paramMap.get(STARTING_PHONES_PARAMS)));
			morePhones.addAll(this.getAllPhones(phone, ENDING_PHONES_PARAMS, this.paramMap.get(ENDING_PHONES_PARAMS)));
		}
		wordPhones.getPhones().addAll(morePhones);
	}
	
	public List<String> getAllPhones(String phone, String startEndParam, List<Parameter> params) {
		List<String> morePhones = new LinkedList<>();
		
		for (int i = 0 ; i < params.size(); i++) {
			Parameter param = params.get(i);
			if(param != null && param.isRequiredFlag()) {
				String[] split = phone.split(" ");
				String phonePart = (startEndParam.equalsIgnoreCase(STARTING_PHONES_PARAMS)) ? split[0] : split[split.length - 1];
				
				Set<String> posPhoneParts = this.possiblePhoneParts(phonePart, param);
				morePhones.addAll(getPhones(phone, startEndParam, posPhoneParts));
			}
		}
		
		return morePhones;
	}
	
	private Set<String> getPhones(String phone, String startEndParam, Set<String> posPhoneParts) {
		Set<String> extraPhone = new HashSet<>();
		String remaingPart = "";
		boolean front = false;
		
		if(phone.contains(" ")) {
			if(startEndParam.equalsIgnoreCase(STARTING_PHONES_PARAMS)) {
				remaingPart = phone.substring(phone.indexOf(" "));
				front = true;
			} else {
				remaingPart = phone.substring(0, phone.lastIndexOf(" ") + 1);
			}
		}
		
		Iterator<String> it = posPhoneParts.iterator();
		while(it.hasNext()) {
			if(front)
				extraPhone.add(it.next() + remaingPart);
			else
				extraPhone.add(remaingPart + it.next());
		}
		
		return extraPhone;
	}
	
	private Set<String> possiblePhoneParts(String phonePart, Parameter param) {
		Set<String> allParts = new HashSet<>();
		
		if(phonePart.length() == 2) {
			char[] array = phonePart.toCharArray();
			int i = 0;
			for(; i < array.length; i++) {
				char ch = array[i];
				if(!Alphabets.VOWELS.contains("" + ch)) {
					phonePart = "" + ch;
					break;
				}
			}
			
			if(i == array.length)
				return Collections.emptySet();
		}

		if (param.isDuplicateFlag())
			allParts.add(phonePart + phonePart);
		
		if (param.isFrontVowelFlag()) {
			Iterator<String> it = Alphabets.VOWELS.iterator();
			while (it.hasNext()) {
				allParts.add(it.next() + phonePart);
			}
		}
		
		if (param.isRearVowelFlag()) {
			Iterator<String> it = Alphabets.VOWELS.iterator();
			while (it.hasNext()) {
				allParts.add(phonePart + it.next());
			}
		}
		
		return allParts;
	}

	/*
	 * Starting Phones :- Starting Required, Non-Vowel, single, duplicate, Front Vowel, Rear Vowel; 
	 * 					  Vowel, single, duplicate, Front Vowel, Rear Vowel;
	 * Ending Phones :- Ending Required, Non-Vowel, single, duplicate, Front Vowel, Rear Vowel; 
	 * 					Vowel, single, duplicate, Front Vowel, Rear Vowel;
	 */
	public static class Parameter {
		String name = null;
		List<Boolean> flags = new ArrayList<>();
		public Parameter(String name, boolean ...flags) {
			this.name = name;
			for(boolean f : flags) {
				this.flags.add(f);
			}
		}
		
		public boolean isRequiredFlag() {
			return this.flags.get(0);
		}
		
		public boolean isSingleFlag() {
			return this.flags.get(1);
		}
		
		public boolean isDuplicateFlag() {
			return this.flags.get(2);
		}
		
		public boolean isFrontVowelFlag() {
			return this.flags.get(3);
		}
		
		public boolean isRearVowelFlag() {
			return this.flags.get(4);
		}
		
	}
}
