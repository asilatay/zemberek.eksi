package tr.com.zemberek.eksi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;

import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;
import zemberek.tokenization.TurkishSentenceExtractor;

/**
 * Hello world!
 *
 */
public class Operation 
{
    public static void main( String[] args )
    {
    	String operationSelect = "-1";
    	Scanner scanIn = new Scanner(System.in);
    	List<String> allInMemory = new ArrayList<String>();
        do {
        	System.out.println("Çık      -> Press 0");
        	System.out.println("Txt oku ve Memory Al -> Press 1");
        	System.out.println("Ayıklama Operasyonuna Başla -> Press 2");
        	
        	operationSelect = scanIn.nextLine();
        	if (operationSelect.equals("1")) {
        		allInMemory = readTxtToMemory();
        	} else if (operationSelect.equals("2")) {
        		if (allInMemory.size() > 0) {
        			allOperationMethod(allInMemory);
        		} else {
        			System.err.println("Önce veri okumalısın");
        		}
        	}
        } while(!operationSelect.equals("0"));
        
        scanIn.close();
    }
    
    private static List<String> readTxtToMemory() {
    	BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("eksi.txt"));
		} catch (FileNotFoundException e1) {
			System.err.println("Dosya okunurken hata oluştu.");
		}
    	String line;
    	List<String> allInMemory = new ArrayList<String>();
    	try {
			while((line = in.readLine()) != null) {
				allInMemory.add(line);
			    System.out.println(line);
			}
		} catch (IOException e) {
			System.err.println("Txt okurken hata oluştu");
		}
    	try {
			in.close();
		} catch (IOException e) {
			System.err.println("Dosya kapatılırken hata oluştu");
		}
    	System.out.println("Memory e alım başarılı ! ");
    	return allInMemory;
    }
    
    private static List<String> allOperationMethod(List<String> entryParagraph) {
    	Map<Integer,String> newSentenceBySentenceList = new HashMap<>();
    	int totalCount = 0;
    	for (String paragraph : entryParagraph) {
    		//Cümlelerine ayır.
    		newSentenceBySentenceList = splitToSentenceTheParagraph(paragraph, newSentenceBySentenceList, totalCount);
            totalCount = Collections.max(newSentenceBySentenceList.entrySet(), Map.Entry.comparingByValue()).getKey();
            Map<Integer, String> removedProperNouns = new HashMap<>();
            removedProperNouns = removeProperNounsFromSentences(newSentenceBySentenceList);
    	}
    	return null;
    }
    
    /**
     * 
     * @param paragraph
     * @param newSentenceBySentenceList
     * @param totalCount
     * @return
     * Bu metod karışık halde duran paragrafı cümle cümle ayırır.
     */
    private static Map<Integer, String> splitToSentenceTheParagraph(String paragraph, Map<Integer,String> newSentenceBySentenceList
    		, int totalCount) {
    	System.out.println("Paragraph = " + paragraph);
        TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
        List<String> sentences = extractor.fromParagraph(paragraph);
        for (String sentence : sentences) {
        	newSentenceBySentenceList.put(totalCount, sentence);
        	totalCount++;
        }
        System.out.println("Paragraf cümlelerine ayrıldı!");
        return newSentenceBySentenceList;
    }
    
    private static Map<Integer, String> removeProperNounsFromSentences(Map<Integer,String> newSentenceBySentenceList) {
    	TurkishMorphology morphology = null;
		try {
			morphology = TurkishMorphology.createWithDefaults();
		} catch (Exception e) {
			System.err.println("Kritik bir hata oluştu");
		}
        Z3MarkovModelDisambiguator disambiguator = null;
		try {
			disambiguator = new Z3MarkovModelDisambiguator();
		} catch (Exception e) {
			System.err.println("Kritik bir hata oluştu");;
		}
        TurkishSentenceAnalyzer sentenceAnalyzer = new TurkishSentenceAnalyzer(
                morphology,
                disambiguator
        );
        Map<Integer, String> removedProperNouns = new HashMap<Integer, String>();
		for (Map.Entry<Integer, String> entry : newSentenceBySentenceList.entrySet()) {
			removedProperNouns = new DisambiguateSentences(sentenceAnalyzer).analyzeSentenceAndRemoveProperNouns(removedProperNouns, entry.getValue(), entry.getKey());
		}
		return removedProperNouns;
    }
}
