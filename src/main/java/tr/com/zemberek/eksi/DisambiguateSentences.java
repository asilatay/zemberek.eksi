package tr.com.zemberek.eksi;

import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;

import java.util.Map;

public class DisambiguateSentences {

    TurkishSentenceAnalyzer sentenceAnalyzer;

    public DisambiguateSentences(TurkishSentenceAnalyzer sentenceAnalyzer) {
        this.sentenceAnalyzer = sentenceAnalyzer;
    }

    Map<Integer, String> analyzeSentenceAndRemoveProperNouns(Map<Integer, String> removedProperNouns ,String sentence, Integer keyNumber) {
        System.out.println("Sentence  = " + sentence);
        SentenceAnalysis result = sentenceAnalyzer.analyze(sentence);
        removedProperNouns = analyzing(removedProperNouns, result, keyNumber);
        return removedProperNouns;
    }

    private Map<Integer, String> analyzing(Map<Integer, String> removedProperNouns, SentenceAnalysis sentenceAnalysis, Integer keyNumber) {
    	String fullSentence = "";
        for (SentenceAnalysis.Entry entry : sentenceAnalysis) {
            System.out.println("Word = " + entry.input);
            boolean foundProperNoun = false;
            for (WordAnalysis analysis : entry.parses) {
                System.out.println(analysis.formatLong());
                if (analysis.formatLong().contains("Prop")) {
                	foundProperNoun = true;
                	break;
                }
            }
            if (!foundProperNoun) {
            	fullSentence += entry.input+ " ";
            }
        }
        removedProperNouns.put(keyNumber, fullSentence);
        return removedProperNouns;
    }
}
