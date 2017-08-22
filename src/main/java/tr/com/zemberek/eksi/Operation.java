package tr.com.zemberek.eksi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;
import zemberek.normalization.TurkishSpellChecker;
import zemberek.tokenization.TurkishSentenceExtractor;

/**
 * @author asilatay
 *
 */
public class Operation 
{
	private static final String taggedSentencesFileName = "taggedSentences.txt";
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
    
    /**
     * Bu metod EksiSozluk projesinden çıkan input u okur ve
     * memory e kaydeder.
     * @return String listesi(Paragraflar)
     */
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
    
    /**
     * 
     * @param entryParagraph : Paragraflar
     * Paragraf şeklinde alınan liste, sırasıyla operasyonlara sokulur. 
     * - Paragrafları cümlelerine ayır.
     * - Kelimeleri POS Tagging yap
     * - Kelimeleri TXT ye yazdır.
     * @return
     */
    private static void allOperationMethod(List<String> entryParagraph) {
    	for (String paragraph : entryParagraph) {
    		Map<Integer,String> newSentenceBySentenceList = new HashMap<>();
    		int totalCount = 0;
    		//Cümlelerine ayır.
    		newSentenceBySentenceList = splitToSentenceTheParagraph(paragraph, newSentenceBySentenceList, totalCount);
            Map<Integer, String> sentenceSequenceSentenceMap = new HashMap<>();
            //Yanlış kelimeleri düzelt.
            sentenceSequenceSentenceMap = checkAndFixWords(newSentenceBySentenceList);
            //POSTagging
            sentenceSequenceSentenceMap = tagVocabs(sentenceSequenceSentenceMap);
            //Çıktı oluştur
            readFileAndWriteOnThis(sentenceSequenceSentenceMap);
    	}
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
    
    /**
     * 
     * @param newSentenceBySentenceList : Cümle Sırası - Cümle Map i
     * TAG operasyonu için gerekli olan kütüphanelerin yüklendiği ve
     * operasyonu yapacak olan metodun çağrıldığı metoddur.
     * @return Cümle Sırası - Cümle Map i
     */
    private static Map<Integer, String> tagVocabs(Map<Integer,String> newSentenceBySentenceList) {
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
        Map<Integer, String> sentenceSequenceSentenceMap = new HashMap<Integer, String>();
		for (Map.Entry<Integer, String> entry : newSentenceBySentenceList.entrySet()) {
			sentenceSequenceSentenceMap = new DisambiguateSentences(sentenceAnalyzer).analyzeSentenceAndOperationPOSTagging(sentenceSequenceSentenceMap, entry.getValue(), entry.getKey());
		}
		return sentenceSequenceSentenceMap;
    }
    
    /**
     * Map deki değerleri okuyup, halihazırda olan dokümana append ile yazan
     * metoddur.
     * @param sentenceSequenceSentenceMap
     */
    private static void readFileAndWriteOnThis(Map<Integer, String> sentenceSequenceSentenceMap) {
    	Map<Integer, String> sortedMapByKey = new HashMap<Integer, String>();
    	SortedSet<Integer> keys = new TreeSet<Integer>(sentenceSequenceSentenceMap.keySet());
    	for (Integer key : keys) {
    		sortedMapByKey.put(key, sentenceSequenceSentenceMap.get(key));
    	}
    	try {
    		FileWriter fw = new FileWriter(taggedSentencesFileName,true); //the true will append the new data
			for (Map.Entry<Integer, String> e : sortedMapByKey.entrySet()) {
				fw.write(e.getValue());//appends the string to the file
			}
			fw.close();
			System.out.println("TXT oluşturuldu.!");
		} catch (IOException e) {
			System.err.println("TXT oluşturulurken hata oluştu!");
			e.printStackTrace();
		}
    }
    
    private static Map<Integer, String> checkAndFixWords(Map<Integer, String> newSentenceBySentenceList) {
    	SpellChecker sc = new SpellChecker();
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
    	DisambiguateSentences disAmb = new DisambiguateSentences(sentenceAnalyzer);
    	TurkishSpellChecker spellChecker = callSpellCheckerLibraries();
    	for (Map.Entry<Integer, String> entry : newSentenceBySentenceList.entrySet()) {
    		String sentence = entry.getValue();
    		String checkedSentence = "";
    		List<String> wordsForOneSentence = disAmb.getWordsFromSentence(sentence);
    		if (wordsForOneSentence != null) {    			
    			for (String word : wordsForOneSentence) {
    				List<String> suggestions = sc.suggestionsForWord(word, spellChecker);
    				if (suggestions != null) {
    					//önerileri değerlendir
    					for (String suggestion : suggestions) {
    						checkedSentence += suggestion +" ";
    						break;
    					}
    					
    				} else {
    					checkedSentence += word + " ";
    				}
    			}
    		}
    		if (!checkedSentence.equals("")) {    			
    			newSentenceBySentenceList.put(entry.getKey(), checkedSentence);
    		}
    	}
    	return newSentenceBySentenceList;
    }

	private static TurkishSpellChecker callSpellCheckerLibraries() {
		TurkishMorphology morphology = null;
		try {
			morphology = TurkishMorphology.createWithDefaults();
		} catch (IOException e) {
			System.err.println("Kritik bir hata oluştu. Kontrol ediniz.");
		}
		TurkishSpellChecker spellChecker = null;
        try {
        	spellChecker = new TurkishSpellChecker(morphology);
		} catch (IOException e) {
			System.err.println("Kritik bir hata oluştu. Kontrol ediniz.");
		}
		return spellChecker;
	}
}
