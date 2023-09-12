package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import searchengine.model.IndexEntity;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.LemmaRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LemmaServiceImpl implements LemmaService {
    private final LemmaRepository lemmaRepository;
    private static final String WORD_TYPE_REGEX = "[а-яА-Яё\\s]+";

    @Override
    public HashMap<String, Integer> getLemmasCountMap(String text) throws IOException {
        HashMap<String, Integer> lemmasCountMap = new HashMap<>();
        String[] words = splitTextIntoWords(deletePunctuationInText(text));

        List<String> correctWords = deleteAuxiliaryPartOfSpeech(words);
        for (String word : correctWords) {
            if (lemmasCountMap.containsKey(word)){
                lemmasCountMap.put(word,lemmasCountMap.get(word) + 1);
            }
            else {
                lemmasCountMap.put(word, 1);
            }
        }
        return lemmasCountMap;
    }

    @Override
    public String deletePunctuationInText(String text) {
        return text.replaceAll("\\p{P}", " ");
    }

    @Override
    public  String[] splitTextIntoWords(String text) {
        return  text.split("[\\s]+");
    }

    public String getWordFromTextByLemma(String[] words, Lemma lemma) throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        for (String word:words) {
            if (word.matches(WORD_TYPE_REGEX)) {
                List<String> wordBaseForms = luceneMorph.getMorphInfo(word.toLowerCase());
                for (String symbol : wordBaseForms) {
                    String wordLemma = symbol.substring(0, symbol.indexOf("|"));
                    if (wordLemma.equals(lemma.getLemma())) {
                        return word;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Lemma> getLemmaListByStringLemma(String stringLemma) {
        return lemmaRepository.findByLemma(stringLemma);
    }

    @Override
    public List<String> deleteAuxiliaryPartOfSpeech(String[] words) throws IOException {
        List<String> correctWords = new ArrayList<>();
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        for (String word:words) {
            if (word.matches(WORD_TYPE_REGEX)){
            List<String> wordBaseForms = luceneMorph.getMorphInfo(word.toLowerCase());
            List<String> wordNormalForm = new ArrayList<>();
            for (String symbol : wordBaseForms) {
                if (!symbol.contains("ПРЕДЛ") && !symbol.contains("СОЮЗ") && !symbol.contains("МЕЖД")) {
                    wordNormalForm.add(symbol.substring(0, symbol.indexOf("|")));
                }
            }
            if (!wordNormalForm.isEmpty()){
                correctWords.add(wordNormalForm.get(0));
            }
        }}
        return correctWords;
    }

    @Override
    public String deleteTags(String text) {
        return Jsoup.parse(text).text();
    }

    @Override
    public synchronized Lemma addLemma(String word, Site site) {
       Lemma lemma =  lemmaRepository.findByLemmaAndSite(word, site);
       if (lemma == null){
            lemma = createLemma(word, site);
       }
       else {
           lemma.setFrequency(lemma.getFrequency() + 1);
           lemmaRepository.save(lemma);
       }
       return lemma;
    }

    @Override
    public Lemma createLemma(String word, Site site) {
        Lemma lemma = new Lemma();
        lemma.setLemma(word);
        lemma.setSite(site);
        lemma.setFrequency(1);
        lemmaRepository.save(lemma);
        return lemma;
    }

    @Override
    public void deleteLemma(Lemma lemma) {
        lemmaRepository.delete(lemma);
    }

    @Override
    public void decrementLemma(Lemma lemma) {
        lemma.setFrequency(lemma.getFrequency()-1);
        lemmaRepository.save(lemma);
        if (lemma.getFrequency()==0){
            deleteLemma(lemma);
        }
    }

    @Override
    public List<Page> getLemmaPageList(Lemma lemma) {
        List<Page> pageList = new ArrayList<>();
        for (IndexEntity index: lemma.getIndexes()){
            pageList.add(index.getPage());
        }
        return pageList;
    }

//    @Override
//    public Lemma getLemmaByStringLemma(String lemma) {
//        return lemmaRepository.findByLemma(lemma);
//    }

}
