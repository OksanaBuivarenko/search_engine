package searchengine.services;

import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface LemmaService {
    HashMap<String, Integer> getLemmasCountMap(String text) throws IOException;

    String deletePunctuationInText(String text);

    String[] splitTextIntoWords(String text);

    List<String> deleteAuxiliaryPartOfSpeech(String[] words) throws IOException;

    String deleteTags(String text);

    Lemma addLemma(String word, Site site);

    Lemma createLemma(String word, Site site);

    void deleteLemma(Lemma lemma);

    void decrementLemma(Lemma lemma);

    List <Page> getLemmaPageList(Lemma lemma);

    //Lemma getLemmaByStringLemma (String lemma);

    String getWordFromTextByLemma(String[] words, Lemma lemma) throws IOException;

    List<Lemma> getLemmaListByStringLemma(String stringLemma);
}
