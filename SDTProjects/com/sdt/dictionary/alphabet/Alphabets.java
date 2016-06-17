package com.sdt.dictionary.alphabet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Alphabets {

	public static final String VOWEL = "vowel";
	public static final String NON_VOWEL = "non_vowel";
	public static final int TOTAL_ALPHABETS = 26;
	public static final Set<String> VOWELS = new HashSet<>();
	public static final List<Alphabet> ALPHABETS = new ArrayList<>();
	
	static {
		VOWELS.add("A");
		VOWELS.add("E");
		VOWELS.add("I");
		VOWELS.add("O");
		VOWELS.add("U");
		
		int asciiStartVal = 65;
		for(int i = asciiStartVal; i <  asciiStartVal + TOTAL_ALPHABETS; i++) {
			String ch =  "" + (char) i;
			ALPHABETS.add(new Alphabet(ch, (VOWELS.contains(ch) ? true : false)));
		}
	}
	
	public static class Alphabet {
		String ch;
		boolean isVowel;
		
		public Alphabet(String ch, boolean isVowel) {
			super();
			this.ch = ch;
			this.isVowel = isVowel;
		}
		
		public String getChar() {
			return ch;
		}
		
		public boolean isVowel() {
			return isVowel;
		}

		@Override
		public String toString() {
			return "ch=" + ch + ", isVowel=" + isVowel;
		}
		
	}
}
