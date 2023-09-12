package searchengine.services;

import searchengine.model.Lemma;
import searchengine.model.Page;

import java.io.IOException;
import java.util.List;

public interface SnippetService {
    String getSnippet(Page page, List<Lemma> lemmaList) throws IOException;
}
