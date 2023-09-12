package searchengine.services;

import searchengine.model.IndexEntity;
import searchengine.model.Lemma;
import searchengine.model.Page;

public interface IndexEntityService {
    IndexEntity addIndexEntity(Page page, Lemma lemma, Integer value);
    void deleteIndexEntity(IndexEntity index);
    IndexEntity getIndexEntityByPageAndLemma(Page page, Lemma lemma);
}
