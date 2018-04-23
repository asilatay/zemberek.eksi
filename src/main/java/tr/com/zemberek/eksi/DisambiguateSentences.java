package tr.com.zemberek.eksi;

import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisambiguateSentences {

    TurkishSentenceAnalyzer sentenceAnalyzer;

    public DisambiguateSentences(TurkishSentenceAnalyzer sentenceAnalyzer) {
        this.sentenceAnalyzer = sentenceAnalyzer;
    }
    

	Map<Integer, String> analyzeSentenceAndOperationPOSTagging(Map<Integer, String> sentenceSequenceSentenceMap ,String sentence, Integer keyNumber) {
        System.out.println("Sentence  = " + sentence);
        SentenceAnalysis result = sentenceAnalyzer.analyze(sentence);
        sentenceSequenceSentenceMap = getRootOfWords(sentenceSequenceSentenceMap, result, keyNumber);
        return sentenceSequenceSentenceMap;
    }
    
    public List<String> getWordsFromSentence(String sentence) {
    	if (sentence != null) {    		
    		SentenceAnalysis sentenceAnalysis = sentenceAnalyzer.analyze(sentence);
    		List<String> words = new ArrayList<String>();
    		for (SentenceAnalysis.Entry entry : sentenceAnalysis) {
    			words.add(entry.input);
    		}
    		return words;
    	}
    	return null;
    }

    private Map<Integer, String> getRootOfWords(Map<Integer, String> sentenceSequenceSentenceMap
    		, SentenceAnalysis sentenceAnalysis, Integer keyNumber) {
    	String fullSentence = "";
        for (SentenceAnalysis.Entry entry : sentenceAnalysis) {
            System.out.println("Word = " + entry.input);
            for (WordAnalysis analysis : entry.parses) {
                System.out.println("Root = " + analysis.root);
                //Noktalama işaretlerini alma
                if (analysis.getDictionaryItem().getId().equals("._Punc")) {
                	break;
                }
                
                //Her kelimenin root unu al
                fullSentence += analysis.root + " ";
                
                break;
            }
        }
        sentenceSequenceSentenceMap.put(keyNumber, fullSentence);
        return sentenceSequenceSentenceMap;
    }
}
