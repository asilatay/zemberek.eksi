package tr.com.zemberek.eksi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

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
	
	/**
	 * Başlıklarına göre gruplanmış dosyaların olduğu dizin
	 */
	private static final String titlePathFiles = "D:\\YL_DATA\\titles\\";
	
	/**
	 * Kullanıcılara göre gruplanmış dosyaların olduğu dizin
	 */
	private static final String userPathFiles = "D:\\YL_DATA\\users\\";
	
	/**
	 * Başlıklar düzeltildikten sonra hangi dizine kaydedileceğini gösterir. 
	 * @param args
	 */
	private static final String titlePathFilesAfterZemberek = "D:\\YL_DATA\\Zemberek\\titles\\";
	
	/**
	 * Kullanıcılara göre gruplanmış dosyalar düzeltildikten sonra hangi dizine kaydedileceğini gösterir.
	 */
	private static final String userPathFilesAfterZemberek = "D:\\YL_DATA\\Zemberek\\users\\";
	
	/**
	 * Türkçe stop words lerin durduğu path dir 
	 */
	private static final String stopWordsPath = "D:\\Yüksek Lisans\\Tez\\ZEMBEREK\\stopwords-tr.txt";
	
    public static void main( String[] args ) throws UnsupportedEncodingException
    {
    	String operationSelect = "-1";
    	Scanner scanIn = new Scanner(System.in);
    	List<String> allInMemory = new ArrayList<String>();
        do {
        	System.out.println("Çık      -> Press 0");
        	System.out.println("Txt oku ve Memory Al -> Press 1");
        	System.out.println("Ayıklama Operasyonuna Başla -> Press 2");
        	System.out.println("Başlık dosyalarını oku ve düzelt -> Press 3");
        	
        	operationSelect = scanIn.nextLine();
        	if (operationSelect.equals("1")) {
        		allInMemory = readTxtToMemory("eksi.txt");
        	} else if (operationSelect.equals("2")) {
        		if (allInMemory.size() > 0) {
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
        			
        			List<String> stopWords = readTxtToMemory(stopWordsPath);
        			
        			allOperationMethod(allInMemory, "test.txt", morphology, disambiguator, stopWords);
        		} else {
        			System.err.println("Önce veri okumalısın");
        		}
        	} else if (operationSelect.equals("3")) {
        		readTitleFilesAndProcess();
        	}
        } while(!operationSelect.equals("0"));
        
        scanIn.close();
    }
    
    private static void readTitleFilesAndProcess() throws UnsupportedEncodingException {
		// Directory içinde ne kadar dosya varsa bunların path ini bir listeye doldurur
		List<Path> filesInDirectory = new ArrayList<Path>();
		
		try (Stream<Path> paths = Files.walk(Paths.get(titlePathFiles))) 
		{
			paths.filter(Files::isRegularFile).forEach(filesInDirectory::add);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Veri okunurken problem oluştu");
		}
		
		int fileCount = filesInDirectory.size();
		System.out.println("Sistemdeki toplam dosya sayısı -> " + fileCount);
		
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
		
		List<String> stopWords = readTxtToMemory(stopWordsPath);
		
		int logCount = 0;
		for (Path p : filesInDirectory) {
			List<String> entryList = readTxtToMemory(p.toString());
			
			String newFileName = p.toString().substring(18, p.toString().length());
			
			allOperationMethod(entryList, newFileName, morphology, disambiguator, stopWords);
			
			logCount++;
			
			if (logCount % 20 == 0) {
				System.out.println("Toplam Dosya Sayısı -> " + fileCount + "---- Kalan Dosya Sayısı -> " + (fileCount - logCount));
			}
		}
    }
    
    /**
     * Bu metod EksiSozluk projesinden çıkan input u okur ve
     * memory e kaydeder.
     * @return String listesi(Paragraflar)
     * @throws UnsupportedEncodingException 
     */
    private static List<String> readTxtToMemory(String path) throws UnsupportedEncodingException {
    	//new FileReader(path), "Cp1252"
    	BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "Cp1252"));
		} catch (FileNotFoundException e1) {
			System.err.println("Dosya okunurken hata oluştu.");
		}
    	String line;
    	
    	List<String> allInMemory = new ArrayList<String>();
    	
    	try {
			while((line = in.readLine()) != null) {
				
				allInMemory.add(line);
				
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
    private static void allOperationMethod(List<String> entryParagraph, String newFileName, TurkishMorphology morphology, Z3MarkovModelDisambiguator disambiguator, List<String> stopWords) {
    	for (String paragraph : entryParagraph) {
    		Map<Integer,String> newSentenceBySentenceList = new HashMap<>();
    		//Cümlelerine ayır.
    		newSentenceBySentenceList = splitToSentenceTheParagraph(paragraph, newSentenceBySentenceList);
    		// Encoding harflerini düzelt
    		newSentenceBySentenceList = correctEncoding(newSentenceBySentenceList, morphology, disambiguator);
    		//Stopwords ayıkla
    		newSentenceBySentenceList = removeStopWords(newSentenceBySentenceList, morphology, disambiguator, stopWords);
    		
            Map<Integer, String> sentenceSequenceSentenceMap = new HashMap<>();
            //Yanlış kelimeleri düzelt.
            sentenceSequenceSentenceMap = checkAndFixWords(newSentenceBySentenceList, morphology, disambiguator);
            //POSTagging
            sentenceSequenceSentenceMap = tagVocabs(sentenceSequenceSentenceMap);
            //Çıktı oluştur
            readFileAndWriteOnThis(sentenceSequenceSentenceMap, newFileName);
    	}
    }
    
    private static Map<Integer, String> removeStopWords(Map<Integer, String> newSentenceBySentenceList,
			TurkishMorphology morphology, Z3MarkovModelDisambiguator disambiguator, List<String> stopWords) {
    	TurkishSentenceAnalyzer sentenceAnalyzer = new TurkishSentenceAnalyzer(
                morphology,
                disambiguator
        );
    	DisambiguateSentences disAmb = new DisambiguateSentences(sentenceAnalyzer);
    	
    	System.out.println("Yeni dosya için stop words removing işlemi başlıyor");
    	
    	int totalFoundStopWords = 0;
    	for (Map.Entry<Integer, String> entry : newSentenceBySentenceList.entrySet()) {
    		List<String> wordsForOneSentence = disAmb.getWordsFromSentence(entry.getValue());
    		String checkedSentence = "";
    		for (String word : wordsForOneSentence) {
    			boolean isStopWord = stopWords.stream().filter(a -> a.equalsIgnoreCase(word)).findFirst().isPresent();
    			if (! isStopWord) {
    				checkedSentence += word + " ";
    			} else {
    				totalFoundStopWords++;
    			}
    		}
    		
    		if (!checkedSentence.equals("")) {    			
    			newSentenceBySentenceList.put(entry.getKey(), checkedSentence);
    		}
    	}
    	
    	System.out.println("Yeni dosya için stop words removing işlemi bitti. Toplam bulunan stop word sayısı -> " + totalFoundStopWords);
    	
    	return newSentenceBySentenceList;
	}

	private static Map<Integer, String> correctEncoding(Map<Integer, String> newSentenceBySentenceList, TurkishMorphology morphology, Z3MarkovModelDisambiguator disambiguator) {
    	
    	TurkishSentenceAnalyzer sentenceAnalyzer = new TurkishSentenceAnalyzer(
                morphology,
                disambiguator
        );
    	DisambiguateSentences disAmb = new DisambiguateSentences(sentenceAnalyzer);
    	
    	for (Map.Entry<Integer, String> entry : newSentenceBySentenceList.entrySet()) {
    		List<String> wordsForOneSentence = disAmb.getWordsFromSentence(entry.getValue());
    		String checkedSentence = "";
    		for (String word : wordsForOneSentence) {
    			if (word.contains("ý")) {
    				word = word.replace("ý", "ı");
    			}
    			if (word.contains("þ")) {
    				word = word.replace("þ", "ş");
    			}
    			if (word.contains("ð")) {
    				word = word.replace("ð", "ğ");
    			}
    			
    			checkedSentence += word + " ";
    		}
    		
    		if (!checkedSentence.equals("")) {    			
    			newSentenceBySentenceList.put(entry.getKey(), checkedSentence);
    		}
    	}
    	
    	return newSentenceBySentenceList;
	}

	/**
     * 
     * @param paragraph
     * @param newSentenceBySentenceList
     * @param totalCount
     * @return
     * Bu metod karışık halde duran paragrafı cümle cümle ayırır.
     */
    private static Map<Integer, String> splitToSentenceTheParagraph(String paragraph, Map<Integer,String> newSentenceBySentenceList) {
    	int totalCount = 0;
    	
    	System.out.println("Paragraph = " + paragraph);
        TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
        List<String> sentences = extractor.fromParagraph(paragraph);
        for (String sentence : sentences) {
        	newSentenceBySentenceList.put(totalCount, sentence);
        	totalCount++;
        }
        System.out.println("Paragraf cümlelerine ayrıldı! Toplam Cümle Sayısı -> " + totalCount);
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
    private static void readFileAndWriteOnThis(Map<Integer, String> sentenceSequenceSentenceMap, String newFileName) {
    	Map<Integer, String> sortedMapByKey = new HashMap<Integer, String>();
    	SortedSet<Integer> keys = new TreeSet<Integer>(sentenceSequenceSentenceMap.keySet());
    	for (Integer key : keys) {
    		sortedMapByKey.put(key, sentenceSequenceSentenceMap.get(key));
    	}
    	try {
    		FileWriter fw = new FileWriter(titlePathFilesAfterZemberek + newFileName, true); //the true will append the new data
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
    
    private static Map<Integer, String> checkAndFixWords(Map<Integer, String> newSentenceBySentenceList, TurkishMorphology morphology, Z3MarkovModelDisambiguator disambiguator) {
    	SpellChecker sc = new SpellChecker();
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
