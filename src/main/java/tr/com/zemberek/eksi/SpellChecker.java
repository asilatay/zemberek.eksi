package tr.com.zemberek.eksi;

import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.normalization.TurkishSpellChecker;

import java.io.IOException;
import java.util.List;

public class SpellChecker {

    public static void main(String[] args) throws IOException {
    	//ÖRNEK KISIM
        TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
        TurkishSpellChecker spellChecker = new TurkishSpellChecker(morphology);

        System.out.println("Check if written correctly.");
        String[] words = {"Ankara'ya", "Ankar'aya", "yapbileceksen", "yapabileceğinizden"};
        for (String word : words) {
            System.out.println(word + " -> " + spellChecker.check(word));
        }
        System.out.println();
        System.out.println("Give suggestions.");
        String[] toSuggest = {"Kraamanda", "okumuştk", "yapbileceksen", "oukyamıyorum"};
        for (String s : toSuggest) {
            System.out.println(s + " -> " + spellChecker.suggestForWord(s));
        }
    }
    
    /**
     * 
     * @param word : Düzeltilecek kelime
     * @param spellChecker : Gerekli kütüphane import
     * Eğer kelimenin kontrolünden yanlış bilgisi dönerse önerileri döndür.
     * Kelime doğruysa null döndür
     * @return
     */
    public List<String> suggestionsForWord(String word, TurkishSpellChecker spellChecker) {
    	if (!spellChecker.check(word)) {    		
    		return spellChecker.suggestForWord(word);
    	}
    	return null;
        
    }
}
