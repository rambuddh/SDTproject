package com.sdt.dictionary.collate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sdt.common.file.ReadDirectory;
import com.sdt.dictionary.DataPrep;
import com.sdt.dictionary.UpgradeDictionary;
import com.sdt.dictionary.DataPrep.WordPhones;

public class CombineDictionaries {

	private ReadDirectory rd = new ReadDirectory();
	
	public CombineDictionaries() {
		rd = new ReadDirectory();
	}
	
	public CombineDictionaries(String inputDir, String outputDir) {
		super();
		this.rd = new ReadDirectory(inputDir, outputDir);
	}
	
	public static void main(String[] args) {
		CombineDictionaries cd = new CombineDictionaries();
		List<WordPhones> dictionaries = cd.combineDicts();
		UpgradeDictionary.writeDict(dictionaries, cd.rd);
	}

	public List<WordPhones> combineDicts() {
		DataPrep dp = new DataPrep(rd);
		List<WordPhones> dictionaries = dp.loadDictionaries();
		this.combineData(dictionaries);
		return dictionaries;
	}
	
	private void combineData(List<WordPhones> dictionaries) {
		Map<String, Integer> wordIndexMap = new HashMap<>();
		int counter = 0;
		Iterator<WordPhones> dictIter = dictionaries.iterator();
		while(dictIter.hasNext()) {
			WordPhones newWPh = dictIter.next();
			Integer index = wordIndexMap.get(newWPh.getWord());
			if(index == null) {
				wordIndexMap.put(newWPh.getWord(), counter++);
				continue;
			}
			
			WordPhones prevWPh = dictionaries.get(index);
			Set<String> prevPhoneSet = new HashSet<>(prevWPh.getPhones());
			List<String> newPhones = newWPh.getPhones();
			
			Iterator<String> it = newPhones.iterator();
			while(it.hasNext()) {
				String phone = it.next();
				if(prevPhoneSet.contains(phone)) {
					it.remove();
				}
			}
			
			prevWPh.getPhones().addAll(newPhones);
			dictIter.remove();
		}
	}
}
