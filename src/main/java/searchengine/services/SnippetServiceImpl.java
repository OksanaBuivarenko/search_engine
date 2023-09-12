package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SnippetServiceImpl implements SnippetService{
    private final LemmaService lemmaService;
    private int  maxSnippetLength = 30;
    private int  oneLemmaSnippetLength;

    public String getSnippet (Page page, List<Lemma> lemmaList) throws IOException {
        String snippet = "";
        String text = lemmaService.deleteTags(page.getContent());
        oneLemmaSnippetLength = maxSnippetLength/lemmaList.size();
        String[] words = lemmaService.splitTextIntoWords(text);
        List<String> searchWordsList = getSearchWordsList(lemmaList, text);
        for (String word: searchWordsList){
            String regex ="\\p{P}*" + word + "\\p{P}*";
            snippet += getSnippetByOneSearchWord(regex,words, searchWordsList);
        }
        return snippet;
    }

   private String getSnippetByOneSearchWord(String regex, String[] words, List<String> searchWordsList){
        String snippetByOneSearchWord = "";
        int lemmaStartIndex = getLemmaStartIndex(regex, words);
        int startSubstring = getStartSubstring(lemmaStartIndex);
        int endSubstring = getEndSubstring(lemmaStartIndex, words);

        for (int i=startSubstring; i<=endSubstring; i++ ){
            if (words[i].matches(regex) || isSearchWordsListContainsWord(searchWordsList, words[i])){
                words[i] = "<b>" + words[i] + "</b>";
            }
            snippetByOneSearchWord += words[i] + " ";
        }
        return snippetByOneSearchWord;
   }

    private boolean isSearchWordsListContainsWord(List<String> searchWordsList, String word){
        return searchWordsList.contains(word);
    }

    private int getEndSubstring(int lemmaStartIndex, String[] words) {
        int endSubstring = lemmaStartIndex + (oneLemmaSnippetLength/2);
        if(endSubstring>words.length-1) {
            endSubstring = words.length-1;
        }
        return endSubstring;
    }

    private int getStartSubstring(int lemmaStartIndex) {
        int startSubstring = lemmaStartIndex - (oneLemmaSnippetLength/2);
        if(startSubstring<0){
            startSubstring=0;
        }
        return startSubstring;
    }

    private int getLemmaStartIndex(String regex, String[] words) {
        int lemmaStartIndex = 0;
        for(String w : words){
            if (w.matches(regex)){
                lemmaStartIndex = Arrays.asList(words).indexOf(w);
            }
        }
        return lemmaStartIndex;
    }

    private List<String> getSearchWordsList(List<Lemma> lemmaList, String text) throws IOException {
        List<String> wordsList = new ArrayList<>();
        for (Lemma lemma: lemmaList){
            String word  = lemmaService.getWordFromTextByLemma(lemmaService
                    .splitTextIntoWords(lemmaService.deletePunctuationInText(text)), lemma);
            wordsList.add(word);
        }
        return wordsList;
    }
}
